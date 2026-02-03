package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.item;

import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.item.definition.ScheduledItemDefinitionBff;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseCreationRequest;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.timing.CalendarEventTimingBff;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record ScheduledItemCreationRequest(
        @NotNull String by,
        @NotNull String calendarId,
        @NotNull String organizerUserId,
        @NotNull String title,
        @Nullable String description,
        @NotNull CalendarEventTimingBff calendarEventTiming,
        @NotNull ScheduledItemDefinitionBff itemDetails,
        @NotNull boolean isCancelled
) implements BaseCreationRequest {}
