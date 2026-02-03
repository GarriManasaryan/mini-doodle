package io.garrimanasaryan.meetingscheduler.domain.calendar.timeslot;

import io.garrimanasaryan.meetingscheduler.application.IdGenerator;
import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItemType;
import io.garrimanasaryan.meetingscheduler.domain.common.Domain;
import io.garrimanasaryan.meetingscheduler.domain.common.Metadata;
import io.garrimanasaryan.meetingscheduler.domain.common.TitleDescription;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.CalendarEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.RecurringEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.SingleEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.policy.timeslot.RecurringEventTimingPolicy;
import io.garrimanasaryan.meetingscheduler.domain.policy.timeslot.SingleEventTimingPolicy;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record TimeSlot(
        @NotNull String id,
        @NotNull String calendarId,
        @NotNull TitleDescription titleDescription,
        @NotNull Set<ScheduledItemType> allowedScheduledItemType,
        @NotNull boolean isBusyByUser,
        @NotNull CalendarEventTiming calendarEventTiming,
        @NotNull Metadata metadata
) implements Domain {

    public static TimeSlot of(
            @NotNull String by,
            @NotNull String calendarId,
            @NotNull TitleDescription titleDescription,
            @NotNull Set<ScheduledItemType> allowedScheduledItemType,
            @NotNull boolean isBusyByUser,
            @NotNull CalendarEventTiming calendarEventTiming
    ){
        validate(calendarEventTiming);
        return new TimeSlot(
                IdGenerator.generate("tms"),
                calendarId,
                titleDescription,
                allowedScheduledItemType,
                isBusyByUser,
                calendarEventTiming,
                Metadata.create(by)
        );
    }

    public TimeSlot update(
            @NotNull String by,
            @NotNull TitleDescription titleDescription,
            @NotNull Set<ScheduledItemType> allowedScheduledItemType,
            @NotNull boolean isBusyByUser,
            @NotNull CalendarEventTiming calendarEventTiming
    ){
        validate(calendarEventTiming);
        return new TimeSlot(
                id,
                calendarId,
                titleDescription,
                allowedScheduledItemType,
                isBusyByUser,
                calendarEventTiming,
                metadata.update(by)
        );
    }

    public TimeSlot delete(String by) {
        return new TimeSlot(
                id,
                calendarId,
                titleDescription,
                allowedScheduledItemType,
                isBusyByUser,
                calendarEventTiming,
                metadata.delete(by)
        );
    }

    private static void validate(@NotNull CalendarEventTiming calendarEventTiming){
        switch (calendarEventTiming){
            case SingleEventTiming singleEventTiming ->
                    SingleEventTimingPolicy.validate(singleEventTiming);
            case RecurringEventTiming recurringEventTiming ->
                    RecurringEventTimingPolicy.validate(recurringEventTiming);
        }
    }
}
