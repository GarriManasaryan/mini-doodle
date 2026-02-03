package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.recurring;

import jakarta.validation.constraints.NotNull;

public record DailyBff(
        @NotNull int every
) implements RecurrenceRuleBff {

    public RecurrenceFrequencyBff frequency() {
        return RecurrenceFrequencyBff.DAILY;
    }

}
