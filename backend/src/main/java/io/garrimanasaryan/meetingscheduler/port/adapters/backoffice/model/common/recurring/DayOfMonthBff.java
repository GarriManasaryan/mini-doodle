package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.recurring;

import jakarta.validation.constraints.NotNull;

import java.time.Month;

public record DayOfMonthBff(
        @NotNull Month month,
        @NotNull int day
) {
}
