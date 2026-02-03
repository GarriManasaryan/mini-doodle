package io.garrimanasaryan.meetingscheduler.port.adapters.persistence.query;

import io.garrimanasaryan.meetingscheduler.application.query.availability.AvailabilityPreview;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.common.JdbcExecutor;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.common.PgJsonOperations;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.dto.AvailabilityContributorType;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.dto.AvailabilityPreviewDto;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.dto.RecurringAvailabilityRuleDto;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.dto.SingleAvailabilityEventDto;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.mappers.CalendarEventTimingMapper;
import jakarta.validation.constraints.NotNull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Component
public class PgAvailabilityPreviewStorage implements AvailabilityPreview {

    private final JdbcExecutor jdbcExecutor;
    private final PgJsonOperations jsonOperations;

    public PgAvailabilityPreviewStorage(JdbcExecutor jdbcExecutor, PgJsonOperations jsonOperations) {
        this.jdbcExecutor = jdbcExecutor;
        this.jsonOperations = jsonOperations;
    }

    private RowMapper<SingleAvailabilityEventDto> singleEventMapper(){
        return (rs, rowNum) -> new SingleAvailabilityEventDto(
                AvailabilityContributorType.valueOf(
                        rs.getString("contributor_type")
                ),
                rs.getString("source_id"),
                rs.getTimestamp("start_at").toInstant(),
                rs.getTimestamp("end_at").toInstant(),
                jsonOperations.deserialize(
                        rs.getString("allowed_types"),
                        new TypeReference<>() {}
                )
        );
    }

    private RowMapper<RecurringAvailabilityRuleDto> recurringEventMapper(){
        return (rs, rowNum) -> new RecurringAvailabilityRuleDto(
                AvailabilityContributorType.valueOf(
                        rs.getString("contributor_type")
                ),
                rs.getString("source_id"),
                CalendarEventTimingMapper.toCalendarEventTiming(
                        jsonOperations.deserialize(
                                rs.getString("timing"),
                                new TypeReference<>() {}
                        )
                ),
                jsonOperations.deserialize(
                        rs.getString("allowed_types"),
                        new TypeReference<>() {}
                )
        );
    }

    @Override
    public AvailabilityPreviewDto query(
            @NotNull String calendarId,
            @NotNull Instant rangeStart,
            @NotNull Instant rangeEnd
            ) {
        return new AvailabilityPreviewDto(
                getSingleEvents(calendarId, rangeStart, rangeEnd),
                getRecurringEvents(calendarId, rangeEnd)
        );
    }

    private List<RecurringAvailabilityRuleDto> getRecurringEvents(
            @NotNull String calendarId,
            @NotNull Instant rangeEnd
    ){
        var selectRecurringEvents = """
            select
                ts.id as source_id,
                ts.calendar_id,
                case
                    when ts.is_busy_by_user then 'BUSY_TIMESLOT'
                    else 'FREE_TIMESLOT'
                    end as contributor_type,
                jsonb_build_object(
                        'type', 'RECURRING',
                        'startDate', rt.start_date,
                        'startTime', rt.start_time,
                        'zoneId', rt.zone_id,
                        'duration',
                        'PT' ||
                        extract(hour from rt.duration)::int || 'H' ||
                        extract(minute from rt.duration)::int || 'M' ||
                        extract(second from rt.duration)::int || 'S',
                        'recurrence',
                        jsonb_build_object(
                                'frequency', rt.recurrence_frequency,
                                'every', rt.recurrence_every,
                                'details', rt.rule_details
                        )
                ) as timing,
                coalesce(
                    jsonb_agg(distinct at.allowed_item_type)
                    filter (where at.allowed_item_type is not null),
                    '[]'::jsonb
                ) as allowed_types
            from dm_timeslot ts
                     join dm_timeslot_recurred_timing rt
                          on rt.scheduled_timeslot_id = ts.id
                     left join dm_timeslot_allowed_type at
                               on at.scheduled_timeslot_id = ts.id
            where ts.calendar_id = :calendarId
              and ts.is_deleted = false
              and rt.start_date <= (:rangeEnd at time zone 'UTC')::date
            group by ts.id, ts.calendar_id, ts.is_busy_by_user,
                     rt.start_date, rt.start_time, rt.zone_id,
                     rt.duration, rt.recurrence_frequency,
                     rt.recurrence_every, rt.rule_details
            
            union all
            
            select
             si.id                 as source_id,
             si.calendar_id,
             'SCHEDULED_ITEM'      as contributor_type,
             jsonb_build_object(
                     'type', 'RECURRING',
                     'startDate', rt.start_date,
                     'startTime', rt.start_time,
                     'zoneId', rt.zone_id,
                     'duration',
                     'PT' ||
                     extract(hour from rt.duration)::int || 'H' ||
                     extract(minute from rt.duration)::int || 'M' ||
                     extract(second from rt.duration)::int || 'S',
                     'recurrence',
                     jsonb_build_object(
                             'frequency', rt.recurrence_frequency,
                             'every', rt.recurrence_every,
                             'details', rt.rule_details
                     )
             ) as timing,
             '[]'::jsonb as allowed_types
            from dm_scheduled_item si
                  join dm_scheduled_item_recurred_timing rt
                       on rt.scheduled_item_id = si.id
            where si.calendar_id = :calendarId
            and si.is_deleted = false
            and si.is_cancelled = false
            and rt.start_date <= (:rangeEnd at time zone 'UTC')::date
    
        """;
        var paramsRecurring = new MapSqlParameterSource()
                .addValue("calendarId", calendarId)
                .addValue("rangeEnd", Timestamp.from(rangeEnd));

        return jdbcExecutor.query(selectRecurringEvents, paramsRecurring, recurringEventMapper());

    }

    private List<SingleAvailabilityEventDto> getSingleEvents(
            @NotNull String calendarId,
            @NotNull Instant rangeStart,
            @NotNull Instant rangeEnd
    ){
        var selectSingleEvents = """
            select
                ts.id as source_id,
                ts.calendar_id,
                case
                    when ts.is_busy_by_user then 'BUSY_TIMESLOT'
                    else 'FREE_TIMESLOT'
                    end as contributor_type,
                st.start_at as start_at,
                st.start_at + st.duration as end_at,
                coalesce(
                    jsonb_agg(distinct at.allowed_item_type)
                    filter (where at.allowed_item_type is not null),
                    '[]'::jsonb
                ) as allowed_types
            from dm_timeslot ts
                     join dm_timeslot_single_timing st
                          on st.scheduled_timeslot_id = ts.id
                     left join dm_timeslot_allowed_type at
                               on at.scheduled_timeslot_id = ts.id
            where ts.calendar_id = :calendarId
              and ts.is_deleted = false
              and st.start_at < :rangeEnd
              and (st.start_at + st.duration) > :rangeStart
            group by ts.id, ts.calendar_id, ts.is_busy_by_user, st.start_at, st.duration
            
            union all
            
            select
                si.id as source_id,
                si.calendar_id,
                'SCHEDULED_ITEM' as contributor_type,
                st.start_at as start_at,
                st.start_at + st.duration as end_at,
                '[]'::jsonb as allowed_types
            from dm_scheduled_item si
                  join dm_scheduled_item_single_timing st
                       on st.scheduled_item_id = si.id
            where si.calendar_id = :calendarId
            and si.is_deleted = false
            and si.is_cancelled = false
            and st.start_at < :rangeEnd
            and (st.start_at + st.duration) > :rangeStart
        """;

        var paramsSingle = new MapSqlParameterSource()
                .addValue("calendarId", calendarId)
                .addValue("rangeStart", Timestamp.from(rangeStart))
                .addValue("rangeEnd", Timestamp.from(rangeEnd));

        return jdbcExecutor.query(selectSingleEvents, paramsSingle, singleEventMapper());

    }
}
