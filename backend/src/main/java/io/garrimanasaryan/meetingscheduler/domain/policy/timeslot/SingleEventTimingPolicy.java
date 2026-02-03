package io.garrimanasaryan.meetingscheduler.domain.policy.timeslot;

import io.garrimanasaryan.meetingscheduler.domain.common.timing.SingleEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.exception.TimeSlotDomainException;
import jakarta.validation.constraints.NotNull;

public class SingleEventTimingPolicy {
    public static void validate(@NotNull SingleEventTiming timing){
        validateBase(timing);
    }

    private static void validateBase(@NotNull SingleEventTiming timing) {
        if (timing.startAt() == null || timing.duration() == null) {
            throw new TimeSlotDomainException("Single event timing is incomplete");
        }

        if (timing.duration().isZero() || timing.duration().isNegative()) {
            throw new TimeSlotDomainException("Single event duration must be positive");
        }

    }

}
