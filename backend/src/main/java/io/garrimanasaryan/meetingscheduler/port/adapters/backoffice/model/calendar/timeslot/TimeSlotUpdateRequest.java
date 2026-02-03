package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.timeslot;

import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseUpdateRequest;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.timing.CalendarEventTimingBff;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record TimeSlotUpdateRequest(
        @NotNull String by,
        @Nullable String title,
        @Nullable String description,
        @Nullable Set<ScheduledItemTypeBff> allowedScheduledItemType,
        @NotNull boolean isBusyByUser,
        @Nullable CalendarEventTimingBff calendarEventTiming
) implements BaseUpdateRequest {
}
