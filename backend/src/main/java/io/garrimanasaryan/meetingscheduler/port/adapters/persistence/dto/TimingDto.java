package io.garrimanasaryan.meetingscheduler.port.adapters.persistence.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

public record TimingDto(
        @NotNull String type,
        @Nullable OffsetDateTime startAt,
        @Nullable LocalDate startDate,
        @Nullable LocalTime startTime,
        @Nullable String zoneId,
        @NotNull String duration,
        @Nullable RecurrenceDto recurrence
) {
}
