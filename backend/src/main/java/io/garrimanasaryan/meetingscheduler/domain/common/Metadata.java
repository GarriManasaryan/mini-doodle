package io.garrimanasaryan.meetingscheduler.domain.common;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record Metadata(
        @NotNull OffsetDateTime createdAt,
        @NotNull OffsetDateTime updatedAt,
        @NotNull String createdBy,
        @NotNull String updatedBy,
        @NotNull boolean isDeleted
) {

    public Metadata update(@NotNull String by){
        return new Metadata(createdAt, OffsetDateTime.now(), createdBy, by, isDeleted);
    }

    public static Metadata create(@NotNull String by){
        return new Metadata(OffsetDateTime.now(), OffsetDateTime.now(), by, by, false);
    }

    public Metadata delete(@NotNull String by){
        return new Metadata(createdAt, OffsetDateTime.now(), createdBy, by, true);
    }

}
