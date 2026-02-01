package io.garrimanasaryan.meetingscheduler.port.adapters.persistence.common;

import io.garrimanasaryan.meetingscheduler.domain.common.Domain;
import jakarta.validation.constraints.NotNull;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

public class GeneralSerializer {
    public static Timestamp toTimestamp(@NotNull OffsetDateTime dateTime) {
        return dateTime != null ? new Timestamp(1000 * dateTime.toEpochSecond()) : null;
    }

    public static MapSqlParameterSource metadataAdder(@NotNull MapSqlParameterSource p, Domain entity) {
        p
            .addValue("created_at", toTimestamp(entity.metadata().createdAt()))
            .addValue("updated_at", toTimestamp(entity.metadata().updatedAt()))
            .addValue("created_by", entity.metadata().createdBy())
            .addValue("is_deleted", entity.metadata().isDeleted())
            .addValue("updated_by", entity.metadata().updatedBy());
        return p;
    }
}
