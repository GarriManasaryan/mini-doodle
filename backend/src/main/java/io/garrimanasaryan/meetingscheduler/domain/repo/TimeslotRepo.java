package io.garrimanasaryan.meetingscheduler.domain.repo;

import io.garrimanasaryan.meetingscheduler.domain.calendar.timeslot.TimeSlot;
import io.garrimanasaryan.meetingscheduler.domain.common.BaseRepo;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface TimeslotRepo extends BaseRepo<TimeSlot> {
    List<TimeSlot> findByCalendarId(@NotNull String calendarId);
}
