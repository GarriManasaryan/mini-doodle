package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;

public record WorkingHourBackofficeModel(
        @NotNull DayOfWeek dayOfWeek,
        @NotNull LocalTime startAt,
        @NotNull Duration duration
) {

}
