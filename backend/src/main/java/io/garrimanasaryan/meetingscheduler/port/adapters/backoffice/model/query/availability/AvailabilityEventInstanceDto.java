package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.query.availability;

import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItemType;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.dto.AvailabilityContributorType;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Set;

public record AvailabilityEventInstanceDto(
        @NotNull AvailabilityContributorType contributorType,
        @NotNull String sourceId,
        @NotNull Instant start,
        @NotNull Instant end,
        @NotNull Set<ScheduledItemType> allowedScheduledItemTypes
) {
}
