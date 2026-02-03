package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.query.availability;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public record AvailabilityResultDto(
        @NotNull Instant rangeStart,
        @NotNull Instant rangeEnd,
        @NotNull List<AvailabilityEventInstanceDto> events
) {
}
