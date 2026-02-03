package io.garrimanasaryan.meetingscheduler.application.query.availability;

import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItemType;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.dto.AvailabilityContributorType;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Set;

public record EventInstance(
        @NotNull AvailabilityContributorType type,
        @NotNull String sourceId,
        @NotNull Instant start,
        @NotNull Instant end,
        @NotNull Set<ScheduledItemType> allowedTypes
) {}

