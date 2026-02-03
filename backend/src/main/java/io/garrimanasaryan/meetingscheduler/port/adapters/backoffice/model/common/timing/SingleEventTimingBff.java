package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.timing;

import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.OffsetDateTime;

public record SingleEventTimingBff(
        @NotNull OffsetDateTime startAt,
        @NotNull Duration duration
) implements CalendarEventTimingBff {
}
