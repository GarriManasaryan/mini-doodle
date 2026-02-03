package io.garrimanasaryan.meetingscheduler.domain.common.recurring;

import jakarta.validation.constraints.NotNull;

public record Daily(
        @NotNull int every
) implements RecurrenceRule {

    public RecurrenceFrequency frequency() {
        return RecurrenceFrequency.DAILY;
    }

}
