package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.timeslot;

import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseCreationRequest;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.timing.CalendarEventTimingBff;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record TimeSlotCreationRequest(
        @NotNull String by,
        @NotNull String calendarId,
        @NotNull String title,
        @Nullable String description,
        @NotNull Set<ScheduledItemTypeBff> allowedScheduledItemType,
        @NotNull boolean isBusyByUser,
        @NotNull CalendarEventTimingBff calendarEventTiming
) implements BaseCreationRequest {
}
