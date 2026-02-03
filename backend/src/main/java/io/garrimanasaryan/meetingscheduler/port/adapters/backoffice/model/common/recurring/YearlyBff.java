package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.recurring;

import jakarta.validation.constraints.NotNull;

import java.time.MonthDay;
import java.util.Set;

public record YearlyBff(
        @NotNull int every,
        @NotNull Set<MonthDay> days
) implements RecurrenceRuleBff {

    public RecurrenceFrequencyBff frequency() {
        return RecurrenceFrequencyBff.YEARLY;
    }

}
