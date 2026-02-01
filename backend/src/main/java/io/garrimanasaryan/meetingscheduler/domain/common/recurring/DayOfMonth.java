package io.garrimanasaryan.meetingscheduler.domain.common.recurring;

import jakarta.validation.constraints.NotNull;

import java.time.Month;

public record DayOfMonth(
        @NotNull Month month,
        @NotNull int day
) {
}
