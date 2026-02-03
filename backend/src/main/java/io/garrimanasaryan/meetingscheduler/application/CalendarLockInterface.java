package io.garrimanasaryan.meetingscheduler.application;

import jakarta.validation.constraints.NotNull;

public interface CalendarLockInterface {
    void lockCalendar(@NotNull String calendarId);
}
