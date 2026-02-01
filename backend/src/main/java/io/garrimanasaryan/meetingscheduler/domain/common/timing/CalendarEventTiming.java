package io.garrimanasaryan.meetingscheduler.domain.common.timing;

import jakarta.validation.constraints.NotNull;

import java.time.Duration;

public sealed interface CalendarEventTiming permits SingleEventTiming, RecurringEventTiming {
    @NotNull Duration duration();
}
