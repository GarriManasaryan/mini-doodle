package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.item;

import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.item.definition.ScheduledItemDefinitionBff;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseBackofficeModel;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.MetadataBackofficeModel;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.timing.CalendarEventTimingBff;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record ScheduledItemBff(
        @NotNull String id,
        @NotNull String calendarId,
        @NotNull String organizerUserId,
        @NotNull String title,
        @Nullable String description,
        @NotNull ScheduledItemStatusBff status,
        @NotNull CalendarEventTimingBff calendarEventTiming,
        @NotNull ScheduledItemDefinitionBff itemDetails,
        @NotNull boolean isCancelled,
        @NotNull MetadataBackofficeModel metadata
) implements BaseBackofficeModel {


}
