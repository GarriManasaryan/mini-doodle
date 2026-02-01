package io.garrimanasaryan.meetingscheduler.domain.repo;

import io.garrimanasaryan.meetingscheduler.domain.calendar.Calendar;
import io.garrimanasaryan.meetingscheduler.domain.common.BaseRepo;

import java.util.List;

public interface CalendarRepo extends BaseRepo<Calendar> {
    List<Calendar> all(String userId);
}
