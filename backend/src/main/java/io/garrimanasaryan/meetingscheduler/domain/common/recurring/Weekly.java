package io.garrimanasaryan.meetingscheduler.domain.common.recurring;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.util.Set;

public record Weekly(
        @NotNull int every,
        @NotNull Set<DayOfWeek> dayOfWeeks
) implements RecurrenceRule {

    public RecurrenceFrequency frequency() {
        return RecurrenceFrequency.WEEKLY;
    }

}
