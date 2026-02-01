package io.garrimanasaryan.meetingscheduler.domain.common.timing;

import io.garrimanasaryan.meetingscheduler.domain.common.recurring.RecurrenceRule;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

public record RecurringEventTiming(
        @NotNull LocalDate startDate,
        @NotNull LocalTime startTime,
        @NotNull ZoneId zoneId,
        @NotNull Duration duration,
        @NotNull RecurrenceRule recurrenceRule
) implements CalendarEventTiming {
}
