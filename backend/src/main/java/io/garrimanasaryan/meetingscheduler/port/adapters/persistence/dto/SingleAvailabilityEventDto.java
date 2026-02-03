package io.garrimanasaryan.meetingscheduler.port.adapters.persistence.dto;

import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItemType;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Set;

public record SingleAvailabilityEventDto(
        @NotNull AvailabilityContributorType type,
        @NotNull String sourceId,
        @NotNull Instant start,
        @NotNull Instant end,
        @NotNull Set<ScheduledItemType> allowedScheduledItemTypes
) {}

