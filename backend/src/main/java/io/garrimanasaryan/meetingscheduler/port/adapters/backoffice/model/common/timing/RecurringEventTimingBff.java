package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.timing;

import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.recurring.RecurrenceRuleBff;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

public record RecurringEventTimingBff(
        @NotNull LocalDate startDate,
        @NotNull LocalTime startTime,
        @NotNull ZoneId zoneId,
        @NotNull Duration duration,
        @Valid @NotNull RecurrenceRuleBff ruleDetails
) implements CalendarEventTimingBff {
}
