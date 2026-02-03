package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.recurring;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.util.Set;

public record WeeklyBff(
        @NotNull int every,
        @NotNull Set<DayOfWeek> dayOfWeeks
) implements RecurrenceRuleBff {

    public RecurrenceFrequencyBff frequency() {
        return RecurrenceFrequencyBff.WEEKLY;
    }

}
