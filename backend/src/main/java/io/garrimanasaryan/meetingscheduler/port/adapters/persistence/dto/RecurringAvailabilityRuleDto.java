package io.garrimanasaryan.meetingscheduler.port.adapters.persistence.dto;

import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItemType;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.CalendarEventTiming;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record RecurringAvailabilityRuleDto(
        @NotNull AvailabilityContributorType type,
        @NotNull String sourceId,
        @NotNull CalendarEventTiming timing,
        @NotNull Set<ScheduledItemType> allowedScheduledItemTypes
) {}

