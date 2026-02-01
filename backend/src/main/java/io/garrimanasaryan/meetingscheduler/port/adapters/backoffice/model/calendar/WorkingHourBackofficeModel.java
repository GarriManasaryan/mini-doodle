package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar;

import jakarta.annotation.Nullable;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;

public record WorkingHourBackofficeModel(
        @Nullable DayOfWeek dayOfWeek,
        @Nullable LocalTime startAt,
        @Nullable Duration duration
) {

}
