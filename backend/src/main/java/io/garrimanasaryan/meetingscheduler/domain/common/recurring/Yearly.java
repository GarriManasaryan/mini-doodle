package io.garrimanasaryan.meetingscheduler.domain.common.recurring;

import jakarta.validation.constraints.NotNull;

import java.time.MonthDay;
import java.util.Set;

public record Yearly(
        @NotNull int every,
        @NotNull Set<MonthDay> days
) implements RecurrenceRule {

    public RecurrenceFrequency frequency() {
        return RecurrenceFrequency.YEARLY;
    }

}
