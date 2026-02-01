package io.garrimanasaryan.meetingscheduler.port.adapters.persistence.common;

import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.exception.DatabaseException;
import jakarta.validation.constraints.NotNull;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
public class JdbcExecutor {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcExecutor(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public <T> List<T> query(
            @NotNull String sql,
            @NotNull MapSqlParameterSource params,
            @NotNull RowMapper<T> rowMapper
    ){
        return jdbcTemplate.query(sql, params, rowMapper);
    }

    public void update(@NotNull String sql, @NotNull MapSqlParameterSource parameterSource){
        try{
            jdbcTemplate.update(sql, parameterSource);
        } catch (DataAccessException e){
            throw new DatabaseException("DB error", e);
        }
    }

}
