package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.item;

import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.item.definition.ScheduledItemDefinitionBff;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseUpdateRequest;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.timing.CalendarEventTimingBff;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record ScheduledItemUpdateRequest(
        @NotNull String by,
        @Nullable String title,
        @Nullable String description,
        @Nullable CalendarEventTimingBff calendarEventTiming,
        @Nullable ScheduledItemDefinitionBff itemDetails,
        @NotNull boolean isCancelled
) implements BaseUpdateRequest {


}
