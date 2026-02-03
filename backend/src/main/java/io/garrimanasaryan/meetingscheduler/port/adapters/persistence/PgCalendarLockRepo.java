package io.garrimanasaryan.meetingscheduler.port.adapters.persistence;

import io.garrimanasaryan.meetingscheduler.application.CalendarLockInterface;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.common.JdbcExecutor;
import jakarta.validation.constraints.NotNull;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

@Component
public class PgCalendarLockRepo implements CalendarLockInterface {

    private final JdbcExecutor jdbcExecutor;

    public PgCalendarLockRepo(JdbcExecutor jdbcExecutor) {
        this.jdbcExecutor = jdbcExecutor;
    }


    @Override
    public void lockCalendar(@NotNull String calendarId) {

        jdbcExecutor.update("set local lock_timeout = '60s'", new MapSqlParameterSource());

        var lockSql = "select id from dm_calendar where id = :id for update";
        var params = new MapSqlParameterSource().addValue("id", calendarId);
        jdbcExecutor.query(lockSql, params, (rs, rowNum) -> null);
    }
}
