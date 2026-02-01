package io.garrimanasaryan.meetingscheduler.application;

import io.garrimanasaryan.meetingscheduler.domain.calendar.Calendar;
import io.garrimanasaryan.meetingscheduler.domain.repo.CalendarRepo;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CalendarService extends BaseService<Calendar, CalendarRepo>{

    public CalendarService(CalendarRepo repo) {
        super(repo);
    }

    public void save(@NotNull Calendar entity) {
        repo.update(entity);
    }

}
