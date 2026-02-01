package io.garrimanasaryan.meetingscheduler.port.adapters.persistence;

import io.garrimanasaryan.meetingscheduler.domain.calendar.Calendar;
import io.garrimanasaryan.meetingscheduler.domain.repo.UserRepo;
import io.garrimanasaryan.meetingscheduler.domain.user.User;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.common.GeneralSerializer;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.common.JdbcExecutor;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.common.MetadataMapper;
import jakarta.validation.constraints.NotNull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Repository
public class PgUserRepo implements UserRepo {
    private final JdbcExecutor jdbcExecutor;

    public PgUserRepo(JdbcExecutor jdbcExecutor) {
        this.jdbcExecutor = jdbcExecutor;
    }

    private static MapSqlParameterSource generateParams(@NotNull User entity) {
        var params = new MapSqlParameterSource()
                .addValue("id", entity.id())
                .addValue("name", entity.name())
                .addValue("email", entity.email())
                .addValue("zone_id", entity.zoneId().toString());

        return GeneralSerializer.metadataAdder(params, entity);
    }

    private RowMapper<User> userRowMapper() {
        return (rs, row) -> new User(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("email"),
                ZoneId.of(rs.getString("zone_id")),
                MetadataMapper.metadataMapper(rs)
        );
    }

    @Override
    public void update(@NotNull User entity) {
        var insert = """
        insert into dm_user
          (id, name, email, zone_id, created_at, updated_at, created_by, updated_by, is_deleted)
        values
          (:id, :name, :email, :zone_id, :created_at, :updated_at, :created_by, :updated_by, :is_deleted)
        on conflict (id) do
          update set
            name = excluded.name,
            updated_at = excluded.updated_at,
            is_deleted = excluded.is_deleted,
            updated_by = excluded.updated_by,
            zone_id = excluded.zone_id
        """;
        jdbcExecutor.update(insert, generateParams(entity));
    }

    @Override
    public Optional<User> ofId(@NotNull String id) {
        var select = """
        select
          c.id, c.name, c.email, c.zone_id, c.created_at,
          c.updated_at, c.created_by, c.updated_by, c.is_deleted
        from dm_user c
        where c.id = :id
        """;
        var params = new MapSqlParameterSource().addValue("id", id);
        return jdbcExecutor.query(select, params, userRowMapper()).stream().findFirst();
    }

    @Override
    public List<User> all(@NotNull String userId) {
        var select = """
        select
          c.id, c.name, c.email, c.zone_id, c.created_at,
          c.updated_at, c.created_by, c.updated_by, c.is_deleted
        from dm_user c
        where c.id = :user_id and c.is_deleted = false
        """;
        var params = new MapSqlParameterSource().addValue("user_id", userId);
        return jdbcExecutor.query(select, params, userRowMapper());
    }
}
