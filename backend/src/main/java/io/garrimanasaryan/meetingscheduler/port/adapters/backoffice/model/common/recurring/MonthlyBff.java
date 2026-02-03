package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.recurring;

import jakarta.validation.constraints.NotNull;

public record MonthlyBff(
        @NotNull int every,
        @NotNull int dayOfMonth
) implements RecurrenceRuleBff {

    public RecurrenceFrequencyBff frequency() {
        return RecurrenceFrequencyBff.MONTHLY;
    }

}
