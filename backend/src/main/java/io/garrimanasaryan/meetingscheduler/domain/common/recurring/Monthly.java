package io.garrimanasaryan.meetingscheduler.domain.common.recurring;

import jakarta.validation.constraints.NotNull;

public record Monthly(
        @NotNull int every,
        @NotNull int dayOfMonth
) implements RecurrenceRule {

    public RecurrenceFrequency frequency() {
        return RecurrenceFrequency.MONTHLY;
    }

}
