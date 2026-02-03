package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.timeslot;

import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseBackofficeModel;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.MetadataBackofficeModel;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.timing.CalendarEventTimingBff;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record TimeSlotBackofficeModel(
        @NotNull String id,
        @NotNull String calendarId,
        @NotNull String title,
        @Nullable String description,
        @NotNull Set<ScheduledItemTypeBff> allowedScheduledItemType,
        @NotNull boolean isBusyByUser,
        @NotNull CalendarEventTimingBff calendarEventTiming,
        @NotNull MetadataBackofficeModel metadata
) implements BaseBackofficeModel {
}
