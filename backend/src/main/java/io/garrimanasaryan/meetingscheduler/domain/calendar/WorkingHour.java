package io.garrimanasaryan.meetingscheduler.domain.calendar;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;

public record WorkingHour(
        @NotNull DayOfWeek dayOfWeek,
        @NotNull LocalTime startAt,
        @NotNull Duration duration
) {

    public LocalTime endAt() {
        return startAt.plus(duration);
    }

}
