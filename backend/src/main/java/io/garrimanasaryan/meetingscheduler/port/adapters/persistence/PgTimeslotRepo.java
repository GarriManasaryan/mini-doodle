package io.garrimanasaryan.meetingscheduler.port.adapters.persistence;

import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItemType;
import io.garrimanasaryan.meetingscheduler.domain.calendar.timeslot.TimeSlot;
import io.garrimanasaryan.meetingscheduler.domain.common.TitleDescription;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.RecurringEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.SingleEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.repo.TimeslotRepo;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.common.GeneralSerializer;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.common.JdbcExecutor;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.common.MetadataMapper;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.common.PgJsonOperations;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.common.TimingOperations;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.mappers.CalendarEventTimingMapper;
import jakarta.validation.constraints.NotNull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Optional;

@Repository
public class PgTimeslotRepo implements TimeslotRepo {
    private final JdbcExecutor jdbcExecutor;
    private final PgJsonOperations jsonOperations;
    private final TimingOperations timingOperations;

    public PgTimeslotRepo(
            JdbcExecutor jdbcExecutor,
            PgJsonOperations jsonOperations,
            TimingOperations timingOperations
    ) {
        this.jdbcExecutor = jdbcExecutor;
        this.jsonOperations = jsonOperations;
        this.timingOperations = timingOperations;
    }

    private static MapSqlParameterSource allowedTypeParams(
            @NotNull ScheduledItemType scheduledItemType,
            @NotNull TimeSlot entity
    ) {
        return new MapSqlParameterSource()
                .addValue("scheduled_timeslot_id", entity.id())
                .addValue("allowed_item_type", scheduledItemType.name());
    }

    private RowMapper<TimeSlot> timeSlotRowMapper() {
        return (rs, row) -> {
            var titleDesc = new TitleDescription(
                    rs.getString("title"),
                    Optional.ofNullable(rs.getString("description"))
            );
            return new TimeSlot(
                    rs.getString("id"),
                    rs.getString("calendar_id"),
                    titleDesc,
                    jsonOperations.deserialize(
                            rs.getString("allowed_types"),
                            new TypeReference<>() {
                            }
                    ),
                    rs.getBoolean("is_busy_by_user"),
                    CalendarEventTimingMapper.toCalendarEventTiming(
                            jsonOperations.deserialize(
                                    rs.getString("timing"),
                                    new TypeReference<>() {})
                    ),
                    MetadataMapper.metadataMapper(rs)
            );

        };
    }

    private void updateTimeSlots(@NotNull TimeSlot entity){
        var timeSlotInsert = """
        insert into dm_timeslot
        (
          id, calendar_id, title, description, is_busy_by_user,
          created_at, created_by, updated_at, updated_by, is_deleted
        )
        values
        (
          :id, :calendar_id, :title, :description, :is_busy_by_user,
          :created_at, :created_by, :updated_at, :updated_by, :is_deleted
        )
        on conflict (id) do
          update set
            title = excluded.title,
            description = excluded.description,
            is_busy_by_user = excluded.is_busy_by_user,
            updated_at = excluded.updated_at,
            is_deleted = excluded.is_deleted,
            updated_by = excluded.updated_by
        """;
        var params = new MapSqlParameterSource()
                .addValue("id", entity.id())
                .addValue("calendar_id", entity.calendarId())
                .addValue("title", entity.titleDescription().title())
                .addValue("description", entity.titleDescription().description().orElse(null))
                .addValue("is_busy_by_user", entity.isBusyByUser());

        jdbcExecutor.update(timeSlotInsert, GeneralSerializer.metadataAdder(params, entity));
    }

    private void updateAllowedEventTypesPerTimeSlot(@NotNull TimeSlot entity){
        jdbcExecutor.update(
                "delete from dm_timeslot_allowed_type where scheduled_timeslot_id = :id",
                new MapSqlParameterSource("id", entity.id())
        );
        entity.allowedScheduledItemType().forEach(type -> {
                var allowedTypeParams = new MapSqlParameterSource()
                        .addValue("scheduled_timeslot_id", entity.id())
                        .addValue("allowed_item_type", type.name());

                var insertAllowed = """
                    insert into dm_timeslot_allowed_type
                    (scheduled_timeslot_id, allowed_item_type)
                    values
                    (:scheduled_timeslot_id, :allowed_item_type)
                """;
                jdbcExecutor.update(insertAllowed, allowedTypeParams);
            }
        );
    }
    private void updateTimings(@NotNull TimeSlot entity){
        switch (entity.calendarEventTiming()){
            case SingleEventTiming single -> updateSingleTiming(single, entity);
            case RecurringEventTiming recurring -> updateRecurringTiming(recurring, entity);
        }
    }
    private void updateSingleTiming(@NotNull SingleEventTiming single, @NotNull TimeSlot entity){
        var singleInsert = """
                insert into dm_timeslot_single_timing
                (scheduled_timeslot_id, start_at, duration)
                values
                (:scheduled_timeslot_id, :start_at, :duration::interval)
                on conflict (scheduled_timeslot_id) do
                  update set
                    start_at = excluded.start_at,
                    duration = excluded.duration
                """;

        var singleParams = new MapSqlParameterSource()
                .addValue("scheduled_timeslot_id", entity.id())
                .addValue("start_at", GeneralSerializer.toTimestamp(single.startAt()))
                .addValue("duration", single.duration().toString())
                ;
        jdbcExecutor.update(singleInsert, singleParams);

        // remove opposite (recurring events)
        jdbcExecutor.update(
                "delete from dm_timeslot_recurred_timing where scheduled_timeslot_id = :id",
                new MapSqlParameterSource("id", entity.id())
        );
    }
    private void updateRecurringTiming(@NotNull RecurringEventTiming recurring, @NotNull TimeSlot entity){
        var recurringInsert = """
                insert into dm_timeslot_recurred_timing
                (
                  scheduled_timeslot_id, start_date, start_time, duration, zone_id,
                  recurrence_frequency, recurrence_every, rule_details
                )
                values
                (
                  :scheduled_timeslot_id, :start_date, :start_time, :duration::interval, :zone_id,
                  :recurrence_frequency, :recurrence_every, :rule_details::jsonb
                )
                on conflict (scheduled_timeslot_id) do
                  update set
                    start_date = excluded.start_date,
                    start_time = excluded.start_time,
                    duration = excluded.duration,
                    zone_id = excluded.zone_id,
                    recurrence_frequency = excluded.recurrence_frequency,
                    recurrence_every = excluded.recurrence_every,
                    rule_details = excluded.rule_details
                """;

        var recurringParams = new MapSqlParameterSource()
                .addValue("scheduled_timeslot_id", entity.id())
                .addValue("start_date", recurring.startDate())
                .addValue("start_time", recurring.startTime())
                .addValue("duration", recurring.duration().toString())
                .addValue("zone_id", recurring.zoneId().toString())
                .addValue("recurrence_frequency", recurring.recurrenceRule().frequency().name())
                .addValue("recurrence_every", recurring.recurrenceRule().every())
                .addValue("rule_details", timingOperations.ruleDetailsJson(recurring.recurrenceRule()))
                ;
        jdbcExecutor.update(recurringInsert, recurringParams);

        // remove opposite (single events)
        jdbcExecutor.update(
                "delete from dm_timeslot_single_timing where scheduled_timeslot_id = :id",
                new MapSqlParameterSource("id", entity.id())
        );
    }

    @Override
    public void update(@NotNull TimeSlot entity) {
        updateTimeSlots(entity);
        updateAllowedEventTypesPerTimeSlot(entity);
        updateTimings(entity);

    }

    @Override
    public Optional<TimeSlot> ofId(@NotNull String id) {
        var select = """
            select
              c.id,
              c.calendar_id,
              c.title,
              c.description,
              c.is_busy_by_user,
              c.created_at,
              c.updated_at,
              c.created_by,
              c.updated_by,
              c.is_deleted,

              coalesce(
                jsonb_agg(distinct at.allowed_item_type)
                  filter (where at.allowed_item_type is not null),
                '[]'::jsonb
              ) as allowed_types,

              coalesce(
            
                (
                  jsonb_agg(
                    jsonb_build_object(
                      'type', 'SINGLE',
                      'startAt', st.start_at,
                      'duration',
                        'PT' ||
                        extract(hour from st.duration)::int || 'H' ||
                        extract(minute from st.duration)::int || 'M' ||
                        extract(second from st.duration)::int || 'S'
                    )
                  ) filter (where st.scheduled_timeslot_id is not null)
                )->0,
            
                (
                  jsonb_agg(
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
                          'details',
                            case rt.recurrence_frequency
                              when 'DAILY' then '{}'::jsonb
                              when 'WEEKLY' then
                                jsonb_build_object(
                                  'days', coalesce(rt.rule_details -> 'days', '[]'::jsonb)
                                )
                              when 'MONTHLY' then
                                jsonb_build_object(
                                  'dayOfMonth', (rt.rule_details ->> 'dayOfMonth')::int
                                )
                              when 'YEARLY' then
                                jsonb_build_object(
                                  'days', coalesce(rt.rule_details -> 'days', '[]'::jsonb)
                                )
                              else '{}'::jsonb
                            end
                        )
                    )
                  ) filter (where rt.scheduled_timeslot_id is not null)
                )->0
            
              ) as timing
            
            from dm_timeslot c
            left join dm_timeslot_allowed_type at
              on at.scheduled_timeslot_id = c.id
            left join dm_timeslot_single_timing st
              on st.scheduled_timeslot_id = c.id
            left join dm_timeslot_recurred_timing rt
              on rt.scheduled_timeslot_id = c.id
            
            where c.id = :id
            
            group by
              c.id,
              c.calendar_id,
              c.title,
              c.description,
              c.is_busy_by_user,
              c.created_at,
              c.updated_at,
              c.created_by,
              c.updated_by,
              c.is_deleted
        """;
        var params = new MapSqlParameterSource().addValue("id", id);
        return jdbcExecutor.query(select, params, timeSlotRowMapper()).stream().findFirst();
    }

    @Override
    public List<TimeSlot> findByCalendarId(@NotNull String calendarId) {
        var select = """
            select
              c.id,
              c.calendar_id,
              c.title,
              c.description,
              c.is_busy_by_user,
              c.created_at,
              c.updated_at,
              c.created_by,
              c.updated_by,
              c.is_deleted,

              coalesce(
                jsonb_agg(distinct at.allowed_item_type)
                  filter (where at.allowed_item_type is not null),
                '[]'::jsonb
              ) as allowed_types,

              coalesce(
            
                (
                  jsonb_agg(
                    jsonb_build_object(
                      'type', 'SINGLE',
                      'startAt', st.start_at,
                      'duration',
                        'PT' ||
                        extract(hour from st.duration)::int || 'H' ||
                        extract(minute from st.duration)::int || 'M' ||
                        extract(second from st.duration)::int || 'S'
                    )
                  ) filter (where st.scheduled_timeslot_id is not null)
                )->0,
            
                (
                  jsonb_agg(
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
                          'details',
                            case rt.recurrence_frequency
                              when 'DAILY' then '{}'::jsonb
                              when 'WEEKLY' then
                                jsonb_build_object(
                                  'days', coalesce(rt.rule_details -> 'days', '[]'::jsonb)
                                )
                              when 'MONTHLY' then
                                jsonb_build_object(
                                  'dayOfMonth', (rt.rule_details ->> 'dayOfMonth')::int
                                )
                              when 'YEARLY' then
                                jsonb_build_object(
                                  'days', coalesce(rt.rule_details -> 'days', '[]'::jsonb)
                                )
                              else '{}'::jsonb
                            end
                        )
                    )
                  ) filter (where rt.scheduled_timeslot_id is not null)
                )->0
            
              ) as timing
            
            from dm_timeslot c
            left join dm_timeslot_allowed_type at
              on at.scheduled_timeslot_id = c.id
            left join dm_timeslot_single_timing st
              on st.scheduled_timeslot_id = c.id
            left join dm_timeslot_recurred_timing rt
              on rt.scheduled_timeslot_id = c.id
            
            where c.calendar_id = :id
            
            group by
              c.id,
              c.calendar_id,
              c.title,
              c.description,
              c.is_busy_by_user,
              c.created_at,
              c.updated_at,
              c.created_by,
              c.updated_by,
              c.is_deleted
        """;
        var params = new MapSqlParameterSource().addValue("id", calendarId);
        return jdbcExecutor.query(select, params, timeSlotRowMapper());
    }
}
