package io.garrimanasaryan.meetingscheduler.domain.common.timing;

import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.OffsetDateTime;

public record SingleEventTiming(
        @NotNull OffsetDateTime startAt,
        @NotNull Duration duration
) implements CalendarEventTiming {
}
