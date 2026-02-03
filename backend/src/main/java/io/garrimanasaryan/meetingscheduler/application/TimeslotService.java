package io.garrimanasaryan.meetingscheduler.application;

import io.garrimanasaryan.meetingscheduler.application.validators.TimeslotCalendarWorkingHoursValidator;
import io.garrimanasaryan.meetingscheduler.domain.calendar.timeslot.TimeSlot;
import io.garrimanasaryan.meetingscheduler.domain.exception.NotFoundException;
import io.garrimanasaryan.meetingscheduler.domain.repo.TimeslotRepo;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@Service
@Transactional
public class TimeslotService extends BaseService<TimeSlot, TimeslotRepo> {

    private final TimeslotCalendarWorkingHoursValidator validator;
    private final CalendarLockInterface locker;

    public TimeslotService(
            TimeslotRepo repo,
            TimeslotCalendarWorkingHoursValidator validator,
            CalendarLockInterface locker
    ) {
        super(repo);
        this.validator = validator;
        this.locker = locker;
    }

    @Override
    public void save(@NotNull TimeSlot entity) {
        locker.lockCalendar(entity.calendarId());
        validator.validate(entity);
        repo.update(entity);
    }

    @Override
    public void update(@NotNull String id, @NotNull Function<TimeSlot, TimeSlot> mapper) {
        repo.ofId(id).ifPresentOrElse(timeSlot -> {
            locker.lockCalendar(timeSlot.calendarId());
            var updated = mapper.apply(timeSlot);
            validator.validate(updated);
            repo.update(updated);
        }, () -> {
            throw new NotFoundException("Entity by provided id not found: " + id);
        });

    }

}
