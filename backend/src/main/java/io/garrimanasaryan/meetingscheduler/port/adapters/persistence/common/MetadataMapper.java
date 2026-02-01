package io.garrimanasaryan.meetingscheduler.port.adapters.persistence.common;

import io.garrimanasaryan.meetingscheduler.domain.common.Metadata;
import jakarta.validation.constraints.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class MetadataMapper {
    public static Metadata metadataMapper(@NotNull ResultSet rs) throws SQLException {
        return new Metadata(
                rs.getObject("created_at", OffsetDateTime.class),
                rs.getObject("updated_at", OffsetDateTime.class),
                rs.getString("created_by"),
                rs.getString("updated_by"),
                rs.getBoolean("is_deleted")
        );

    }

}
