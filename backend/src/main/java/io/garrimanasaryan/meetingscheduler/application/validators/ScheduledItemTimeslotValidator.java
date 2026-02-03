package io.garrimanasaryan.meetingscheduler.application.validators;

import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItem;
import io.garrimanasaryan.meetingscheduler.domain.calendar.timeslot.TimeSlot;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.*;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.CalendarEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.RecurringEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.SingleEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.exception.ValidationException;
import io.garrimanasaryan.meetingscheduler.domain.repo.TimeslotRepo;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduledItemTimeslotValidator implements Validator<ScheduledItem> {

    private static final int VALIDATION_DAYS = 14;

    private final TimeslotRepo timeslotRepo;

    public ScheduledItemTimeslotValidator(TimeslotRepo timeslotRepo) {
        this.timeslotRepo = timeslotRepo;
    }

    @Override
    public void validate(@NotNull ScheduledItem item) {
        var timeslots = timeslotRepo.findByCalendarId(item.calendarId());

        if (timeslots.isEmpty()) {
            throw new ValidationException(
                    "No timeslots found for calendar " + item.calendarId()
            );
        }

        var itemOccurrences = expandOccurrences(item.calendarEventTiming());

        boolean fitsAnyTimeslot = timeslots.stream()
                .filter(ts -> !ts.isBusyByUser())
                .filter(ts ->
                        ts.allowedScheduledItemType()
                                .contains(item.scheduledItemDefinition().type())
                )
                .anyMatch(ts -> allOccurrencesContained(itemOccurrences, ts));

        if (!fitsAnyTimeslot) {
            throw new ValidationException(
                    "Scheduled item does not fit into any allowed available timeslot"
            );
        }
    }

    private boolean allOccurrencesContained(
            List<TimeRange> itemOccurrences,
            TimeSlot slot
    ) {
        var slotOccurrences = expandOccurrences(slot.calendarEventTiming());

        for (TimeRange itemOcc : itemOccurrences) {
            boolean contained = slotOccurrences.stream()
                    .anyMatch(slotOcc -> slotOcc.contains(itemOcc));

            if (!contained) {
                return false;
            }
        }
        return true;
    }

    private List<TimeRange> expandOccurrences(CalendarEventTiming timing) {
        return switch (timing) {
            case SingleEventTiming s -> List.of(
                    new TimeRange(
                            s.startAt().toInstant(),
                            s.startAt().toInstant().plus(s.duration())
                    )
            );
            case RecurringEventTiming r -> expandRecurring(r);
        };
    }

    private List<TimeRange> expandRecurring(RecurringEventTiming t) {
        List<TimeRange> ranges = new ArrayList<>();

        LocalDate start = t.startDate();
        LocalDate end = start.plusDays(VALIDATION_DAYS);

        switch (t.recurrenceRule()) {
            case Daily d -> {
                for (LocalDate date = start;
                     !date.isAfter(end);
                     date = date.plusDays(d.every())) {
                    ranges.add(toRange(date, t));
                }
            }

            case Weekly w -> {
                for (LocalDate date = start;
                     !date.isAfter(end);
                     date = date.plusDays(1)) {
                    if (w.dayOfWeeks().contains(date.getDayOfWeek())) {
                        ranges.add(toRange(date, t));
                    }
                }
            }

            case Monthly m -> {
                LocalDate base = start;
                while (!base.isAfter(end)) {
                    if (m.dayOfMonth() <= base.lengthOfMonth()) {
                        ranges.add(toRange(base.withDayOfMonth(m.dayOfMonth()), t));
                    }
                    base = base.plusMonths(m.every());
                }
            }

            case Yearly y -> {
                int year = start.getYear();
                while (Year.of(year).atDay(1).isBefore(end)) {
                    for (MonthDay md : y.days()) {
                        if (md.isValidYear(year)) {
                            ranges.add(toRange(md.atYear(year), t));
                        }
                    }
                    year += y.every();
                }
            }
        }

        return ranges;
    }

    private TimeRange toRange(LocalDate date, RecurringEventTiming t) {
        ZonedDateTime zdt = ZonedDateTime.of(
                date,
                t.startTime(),
                t.zoneId()
        );

        Instant start = zdt.toInstant();
        return new TimeRange(start, start.plus(t.duration()));
    }

    private record TimeRange(Instant start, Instant end) {

        boolean contains(TimeRange other) {
            return !other.start.isBefore(this.start)
                    && !other.end.isAfter(this.end);
        }
    }
}
