package io.garrimanasaryan.meetingscheduler.application;

import io.garrimanasaryan.meetingscheduler.application.validators.ScheduledItemTimeslotValidator;
import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItem;
import io.garrimanasaryan.meetingscheduler.domain.calendar.timeslot.TimeSlot;
import io.garrimanasaryan.meetingscheduler.domain.exception.NotFoundException;
import io.garrimanasaryan.meetingscheduler.domain.repo.ScheduledItemRepo;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@Service
@Transactional
public class ScheduledItemService extends BaseService<ScheduledItem, ScheduledItemRepo>{
    private final ScheduledItemTimeslotValidator validator;
    private final CalendarLockInterface locker;

    public ScheduledItemService(
            ScheduledItemRepo repo,
            ScheduledItemTimeslotValidator validator,
            CalendarLockInterface locker
    ) {
        super(repo);
        this.validator = validator;
        this.locker = locker;
    }

    @Override
    public void save(@NotNull ScheduledItem entity) {
        locker.lockCalendar(entity.calendarId());
        validator.validate(entity);
        repo.update(entity);
    }

    @Override
    public void update(@NotNull String id, @NotNull Function<ScheduledItem, ScheduledItem> mapper) {
        repo.ofId(id).ifPresentOrElse(scheduledItem -> {
            locker.lockCalendar(scheduledItem.calendarId());
            var updated = mapper.apply(scheduledItem);
            validator.validate(updated);
            repo.update(updated);
        }, () -> {
            throw new NotFoundException("Entity by provided id not found: " + id);
        });

    }

}
