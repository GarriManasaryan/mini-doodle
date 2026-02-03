package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record MetadataBackofficeModel(
        @NotNull OffsetDateTime createdAt,
        @NotNull OffsetDateTime updatedAt,
        @NotNull String createdBy,
        @NotNull String updatedBy
) {

    public MetadataBackofficeModel update(@NotNull String by){
        return new MetadataBackofficeModel(createdAt, OffsetDateTime.now(), createdBy, by);
    }

    public static MetadataBackofficeModel create(@NotNull String by){
        return new MetadataBackofficeModel(OffsetDateTime.now(), OffsetDateTime.now(), by, by);
    }

}
