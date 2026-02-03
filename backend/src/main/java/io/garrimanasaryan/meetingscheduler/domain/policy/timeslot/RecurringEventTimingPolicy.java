package io.garrimanasaryan.meetingscheduler.domain.policy.timeslot;

import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Daily;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Monthly;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.RecurrenceRule;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Weekly;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Yearly;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.RecurringEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.exception.TimeSlotDomainException;
import jakarta.validation.constraints.NotNull;

public class RecurringEventTimingPolicy {
    public static void validate(@NotNull RecurringEventTiming timing){
        validateBase(timing);
        validateRule(timing.recurrenceRule());

    }

    private static void validateBase(@NotNull RecurringEventTiming timing) {
        if (timing.startDate() == null || timing.startTime() == null ||
                timing.zoneId() == null) {
            throw new TimeSlotDomainException("Recurring event timing is incomplete");
        }

        if (timing.duration().isZero() || timing.duration().isNegative()) {
            throw new TimeSlotDomainException("Recurring event duration must be positive");
        }

        if (timing.recurrenceRule().every() <= 0){
            throw new TimeSlotDomainException("Recurring event interval must be positive");
        }
    }

    private static void validateRule(@NotNull RecurrenceRule rule){
        switch (rule){
            case Daily d -> {
                // later
            }
            case Weekly w -> {
                if (w.dayOfWeeks() == null || w.dayOfWeeks().isEmpty()){
                    throw new TimeSlotDomainException("Weekly recurrence at least one weekday");
                }
            }
            case Monthly m -> {
                if (m.dayOfMonth() < 1 || m.dayOfMonth() > 31){
                    throw new TimeSlotDomainException("Monthly recurrence day must be between 1 and 31");
                }
            }
            case Yearly y -> {
                if (y.days() == null || y.days().isEmpty()){
                    throw new TimeSlotDomainException("Yearly recurrence requires at least one date");
                }
            }
        }
    }

}
