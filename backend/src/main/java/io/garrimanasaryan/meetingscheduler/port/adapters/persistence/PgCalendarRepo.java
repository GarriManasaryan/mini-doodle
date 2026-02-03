package io.garrimanasaryan.meetingscheduler.port.adapters.persistence;

import io.garrimanasaryan.meetingscheduler.domain.calendar.Calendar;
import io.garrimanasaryan.meetingscheduler.domain.calendar.CalendarType;
import io.garrimanasaryan.meetingscheduler.domain.calendar.WorkingHour;
import io.garrimanasaryan.meetingscheduler.domain.common.TitleDescription;
import io.garrimanasaryan.meetingscheduler.domain.repo.CalendarRepo;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.common.GeneralSerializer;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.common.JdbcExecutor;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.common.MetadataMapper;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.common.PgJsonOperations;
import jakarta.validation.constraints.NotNull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Optional;

@Repository
public class PgCalendarRepo implements CalendarRepo {
    private final JdbcExecutor jdbcExecutor;
    private final PgJsonOperations jsonOperations;

    public PgCalendarRepo(JdbcExecutor jdbcExecutor, PgJsonOperations jsonOperations) {
        this.jdbcExecutor = jdbcExecutor;
        this.jsonOperations = jsonOperations;
    }

    private static MapSqlParameterSource calendarBaseParams(@NotNull Calendar entity) {
        var params = new MapSqlParameterSource()
                .addValue("id", entity.id())
                .addValue("managed_by_user_id", entity.managedByUserId())
                .addValue("subject_user_id", entity.subjectUserId())
                .addValue("title", entity.titleDescription().title())
                .addValue("description", entity.titleDescription().description().orElse(null))
                .addValue("type", entity.type().name());

        return GeneralSerializer.metadataAdder(params, entity);
    }

    private static MapSqlParameterSource calendarWorkingHours(
            @NotNull WorkingHour workingHour,
            @NotNull Calendar entity
    ) {
        var params = new MapSqlParameterSource()
                .addValue("calendar_id", entity.id())
                .addValue("day_of_week", workingHour.dayOfWeek().getValue())
                .addValue("start_at", workingHour.startAt())
                .addValue("duration", workingHour.duration().toString());

        return GeneralSerializer.metadataAdder(params, entity);
    }

    private RowMapper<Calendar> calendarRowMapper() {
        return (rs, row) -> new Calendar(
                rs.getString("id"),
                rs.getString("managed_by_user_id"),
                rs.getString("subject_user_id"),
                new TitleDescription(
                        rs.getString("title"),
                        Optional.ofNullable(rs.getString("description"))
                ),
                CalendarType.valueOf(rs.getString("type")),
                jsonOperations.deserialize(
                        rs.getString("working_hours"),
                        new TypeReference<>() {}
                ),
                MetadataMapper.metadataMapper(rs)
        );
    }

    @Override
    public void update(@NotNull Calendar entity) {
        var insert = """
        insert into dm_calendar
          (
            id, managed_by_user_id, subject_user_id, title, description,
            type, created_at, updated_at, created_by, updated_by, is_deleted
          )
        values
          (
            :id, :managed_by_user_id, :subject_user_id, :title, :description, :type,
            :created_at, :updated_at, :created_by, :updated_by, :is_deleted
          )
        on conflict (id) do
          update set
            title = excluded.title,
            description = excluded.description,
            type = excluded.type,
            updated_at = excluded.updated_at,
            is_deleted = excluded.is_deleted,
            updated_by = excluded.updated_by
        """;
        jdbcExecutor.update(insert, calendarBaseParams(entity));

        entity.workingHours().forEach(
workingHour -> {
                var workHours = """
                insert into dm_calendar_working_hour
                  (calendar_id, day_of_week, start_at, duration)
                values
                  (:calendar_id, :day_of_week, :start_at, :duration::interval)
                on conflict (calendar_id, day_of_week, start_at) do
                  update set
                    day_of_week = excluded.day_of_week,
                    start_at = excluded.start_at,
                    duration = excluded.duration
                """;
                jdbcExecutor.update(workHours, calendarWorkingHours(workingHour, entity));

            }
        );

    }

    @Override
    public Optional<Calendar> ofId(@NotNull String id) {
        var select = """
        select
          c.id,
          c.managed_by_user_id,
          c.subject_user_id,
          c.title,
          c.description,
          c.type,
          c.created_at,
          c.updated_at,
          c.created_by,
          c.updated_by,
          c.is_deleted,
          coalesce(
            jsonb_agg(
              jsonb_build_object(
                'dayOfWeek',
                 case cw.day_of_week
                   when 1 then 'MONDAY'
                   when 2 then 'TUESDAY'
                   when 3 then 'WEDNESDAY'
                   when 4 then 'THURSDAY'
                   when 5 then 'FRIDAY'
                   when 6 then 'SATURDAY'
                   when 7 then 'SUNDAY'
                 end,
                'startAt', cw.start_at,
                'duration',
                  (
                   'PT' ||
                   extract(hour from cw.duration)::int || 'H' ||
                   extract(minute from cw.duration)::int || 'M' ||
                   extract(second from cw.duration)::int || 'S'
                  )
              )
            ) filter (where cw.calendar_id is not null),
            '[]'::jsonb
          ) as working_hours
        from dm_calendar c
        left join dm_calendar_working_hour cw on cw.calendar_id = c.id
        where c.id = :id
        group by
          c.id,
          c.managed_by_user_id,
          c.subject_user_id,
          c.title,
          c.description,
          c.type,
          c.created_at,
          c.updated_at,
          c.created_by,
          c.is_deleted,
          c.updated_by
        """;
        var params = new MapSqlParameterSource().addValue("id", id);
        return jdbcExecutor.query(select, params, calendarRowMapper()).stream().findFirst();
    }

}
