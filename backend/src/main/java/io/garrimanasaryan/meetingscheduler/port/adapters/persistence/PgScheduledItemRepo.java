package io.garrimanasaryan.meetingscheduler.port.adapters.persistence;

import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItem;
import io.garrimanasaryan.meetingscheduler.domain.calendar.item.definition.FocusTimeDefinition;
import io.garrimanasaryan.meetingscheduler.domain.calendar.item.definition.MeetingDefinition;
import io.garrimanasaryan.meetingscheduler.domain.common.TitleDescription;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.RecurringEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.SingleEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.repo.ScheduledItemRepo;
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

import java.util.Optional;

@Repository
public class PgScheduledItemRepo implements ScheduledItemRepo {
    private final JdbcExecutor jdbcExecutor;
    private final PgJsonOperations jsonOperations;
    private final TimingOperations timingOperations;

    public PgScheduledItemRepo(
            JdbcExecutor jdbcExecutor,
            PgJsonOperations jsonOperations,
            TimingOperations timingOperations
    ) {
        this.jdbcExecutor = jdbcExecutor;
        this.jsonOperations = jsonOperations;
        this.timingOperations = timingOperations;
    }

    private RowMapper<ScheduledItem> scheduledItemRowMapper() {
        return (rs, row) -> new ScheduledItem(
                    rs.getString("id"),
                    rs.getString("calendar_id"),
                    rs.getString("organizer_user_id"),
                    new TitleDescription(
                            rs.getString("title"),
                            Optional.ofNullable(rs.getString("description"))
                    ),
                    CalendarEventTimingMapper.toCalendarEventTiming(
                            jsonOperations.deserialize(
                                    rs.getString("timing"),
                                    new TypeReference<>() {})
                    ),
                    jsonOperations.deserialize(
                            rs.getString("definition"),
                            new TypeReference<>() {
                            }
                    ),
                    rs.getBoolean("is_cancelled"),
                    MetadataMapper.metadataMapper(rs)
            );

    }

    private void updateScheduledItem(@NotNull ScheduledItem entity){
        var scheduledItemInsert = """
        insert into dm_scheduled_item
        (
          id, calendar_id, title, description, organizer_user_id,
          is_cancelled, created_at, created_by, updated_at, 
          updated_by, is_deleted
        )
        values
        (
          :id, :calendar_id, :title, :description, :organizer_user_id,
          :is_cancelled, :created_at, :created_by, :updated_at, 
          :updated_by, :is_deleted
        )
        on conflict (id) do
          update set
            title = excluded.title,
            description = excluded.description,
            is_cancelled = excluded.is_cancelled,
            updated_at = excluded.updated_at,
            is_deleted = excluded.is_deleted,
            updated_by = excluded.updated_by
        """;

        var params = new MapSqlParameterSource()
                .addValue("id", entity.id())
                .addValue("calendar_id", entity.calendarId())
                .addValue("title", entity.titleDescription().title())
                .addValue("description", entity.titleDescription().description().orElse(null))
                .addValue("is_cancelled", entity.isCancelled())
                .addValue("organizer_user_id", entity.organizerUserId())
                ;
        jdbcExecutor.update(scheduledItemInsert, GeneralSerializer.metadataAdder(params, entity));

    }

    private void updateScheduleDefinitions(@NotNull ScheduledItem entity){
        switch (entity.scheduledItemDefinition()){
            case MeetingDefinition m -> updateMeetingDefinitions(m, entity);
            case FocusTimeDefinition f -> updateFocusTimeDefinitions(f, entity);
        }
    }

    private void updateMeetingDefinitions(@NotNull MeetingDefinition m, @NotNull ScheduledItem entity){
        var mInsert = """
                insert into dm_meeting_details
                (scheduled_item_id, zoom_link)
                values
                (:scheduled_item_id, :zoom_link)
                on conflict (scheduled_item_id) do
                  update set
                    zoom_link = excluded.zoom_link
                """;

        var mParam = new MapSqlParameterSource()
                .addValue("scheduled_item_id", entity.id())
                .addValue("zoom_link", m.zoomLink().orElse(null))
                ;
        jdbcExecutor.update(mInsert, mParam);

        // participants
        m.meetingParticipantIds().forEach(
                participantId -> {
                    var pInsert = """
                                insert into dm_meeting_participant
                                (scheduled_item_id, user_id, accepted)
                                values
                                (:scheduled_item_id, :user_id, :accepted)
                                on conflict (scheduled_item_id, user_id) do
                                  update set
                                    accepted = excluded.accepted
                            """;

                    var pParam = new MapSqlParameterSource()
                            .addValue("scheduled_item_id", entity.id())
                            .addValue("user_id", participantId)
                            .addValue("accepted", false)
                            ;
                    jdbcExecutor.update(pInsert, pParam);
                }
        );
    }
    private void updateFocusTimeDefinitions(@NotNull FocusTimeDefinition f, @NotNull ScheduledItem entity){
        var fInsert = """
                insert into dm_focus_time_details
                (scheduled_item_id, focus_link)
                values
                (:scheduled_item_id, :focus_link)
                on conflict (scheduled_item_id) do
                  update set
                    focus_link = excluded.focus_link
                """;

        var fParam = new MapSqlParameterSource()
                .addValue("scheduled_item_id", entity.id())
                .addValue("focus_link", f.focusLink())
                ;
        jdbcExecutor.update(fInsert, fParam);
    }
    private void updateTimings(@NotNull ScheduledItem entity){
        switch (entity.calendarEventTiming()){
            case SingleEventTiming s -> updateSingleEventTiming(s, entity);
            case RecurringEventTiming r -> updateRecurringEventTiming(r, entity);
        }
    }

    private void updateSingleEventTiming(@NotNull SingleEventTiming single, @NotNull ScheduledItem entity){
        var singleInsert = """
                insert into dm_scheduled_item_single_timing
                (scheduled_item_id, start_at, duration)
                values
                (:scheduled_item_id, :start_at, :duration::interval)
                on conflict (scheduled_item_id) do
                  update set
                    start_at = excluded.start_at,
                    duration = excluded.duration
                """;

        var singleParams = new MapSqlParameterSource()
                .addValue("scheduled_item_id", entity.id())
                .addValue("start_at", GeneralSerializer.toTimestamp(single.startAt()))
                .addValue("duration", single.duration().toString())
                ;
        jdbcExecutor.update(singleInsert, singleParams);

        // remove opposite (recurring events)
        jdbcExecutor.update(
                "delete from dm_scheduled_item_recurred_timing where scheduled_item_id = :id",
                new MapSqlParameterSource("id", entity.id())
        );
    }

    private void updateRecurringEventTiming(@NotNull RecurringEventTiming recurring, @NotNull ScheduledItem entity){
        var recurringInsert = """
                insert into dm_scheduled_item_recurred_timing
                (
                  scheduled_item_id, start_date, start_time, duration, zone_id,
                  recurrence_frequency, recurrence_every, rule_details
                )
                values
                (
                  :scheduled_item_id, :start_date, :start_time, :duration::interval, :zone_id,
                  :recurrence_frequency, :recurrence_every, :rule_details::jsonb
                )
                on conflict (scheduled_item_id) do
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
                .addValue("scheduled_item_id", entity.id())
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
                "delete from dm_scheduled_item_single_timing where scheduled_item_id = :id",
                new MapSqlParameterSource("id", entity.id())
        );
    }


    @Override
    public void update(@NotNull ScheduledItem entity) {
        updateScheduledItem(entity);
        updateScheduleDefinitions(entity);
        updateTimings(entity);

    }

    @Override
    public Optional<ScheduledItem> ofId(@NotNull String id) {
        var select = """
        select
          c.id,
          c.calendar_id,
          c.title,
          c.description,
          c.organizer_user_id,
          c.is_cancelled,
          c.created_at,
          c.updated_at,
          c.created_by,
          c.updated_by,
          c.is_deleted,

          coalesce(
            (
              jsonb_agg(
                jsonb_build_object(
                  'type', 'MEETING',
                  'scheduledItemId', c.id,
                  'meetingParticipantIds',
                    coalesce(mp.participant_ids, '[]'::jsonb),
                  'zoomLink', md.zoom_link
                )
              ) filter (where md.scheduled_item_id is not null)
            )->0,

            (
              jsonb_agg(
                jsonb_build_object(
                  'type', 'FOCUS_TIME',
                  'scheduledItemId', c.id,
                  'focusLink', fd.focus_link
                )
              ) filter (where fd.scheduled_item_id is not null)
            )->0
          ) as definition,

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
              ) filter (where st.scheduled_item_id is not null)
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
                      'details', rt.rule_details
                    )
                )
              ) filter (where rt.scheduled_item_id is not null)
            )->0
          ) as timing

        from dm_scheduled_item c

        left join dm_scheduled_item_single_timing st
          on st.scheduled_item_id = c.id
        left join dm_scheduled_item_recurred_timing rt
          on rt.scheduled_item_id = c.id

        left join dm_meeting_details md
          on md.scheduled_item_id = c.id

        left join (
          select
            scheduled_item_id,
            jsonb_agg(distinct user_id) as participant_ids
          from dm_meeting_participant
          group by scheduled_item_id
        ) mp on mp.scheduled_item_id = c.id

        left join dm_focus_time_details fd
          on fd.scheduled_item_id = c.id

        where c.id = :id
          and c.is_deleted = false

        group by
          c.id,
          c.calendar_id,
          c.title,
          c.description,
          c.organizer_user_id,
          c.is_cancelled,
          c.created_at,
          c.updated_at,
          c.created_by,
          c.updated_by,
          c.is_deleted,
          md.zoom_link,
          fd.focus_link,
          mp.participant_ids
        """;

        var params = new MapSqlParameterSource().addValue("id", id);
        return jdbcExecutor.query(select, params, scheduledItemRowMapper())
                .stream()
                .findFirst();
    }

}
