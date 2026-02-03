package io.garrimanasaryan.meetingscheduler.application.query.availability;

import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.dto.AvailabilityPreviewDto;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public interface AvailabilityPreview {
    @NotNull AvailabilityPreviewDto query(
            @NotNull String calendarId,
            @NotNull Instant rangeStart,
            @NotNull Instant rangeEnd
    ) ;
}
