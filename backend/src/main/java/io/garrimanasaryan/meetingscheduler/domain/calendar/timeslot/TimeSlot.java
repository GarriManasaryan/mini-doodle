package io.garrimanasaryan.meetingscheduler.domain.calendar.timeslot;

import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItemType;
import io.garrimanasaryan.meetingscheduler.domain.common.Domain;
import io.garrimanasaryan.meetingscheduler.domain.common.Metadata;
import io.garrimanasaryan.meetingscheduler.domain.common.TitleDescription;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.CalendarEventTiming;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record TimeSlot(
        @NotNull String id,
        @NotNull String calendarId,
        @NotNull TitleDescription titleDescription,
        @NotNull boolean allowOverlap,
        @NotNull Set<ScheduledItemType> allowedScheduledItemType,
        @NotNull boolean isBusyByUser,
        @NotNull CalendarEventTiming calendarEventTiming,
        @NotNull Metadata metadata
) implements Domain {

    public Domain delete(String by) {
        return null;
    }
}
