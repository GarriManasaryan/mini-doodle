package io.garrimanasaryan.meetingscheduler.application.validators;

import io.garrimanasaryan.meetingscheduler.domain.calendar.WorkingHour;
import io.garrimanasaryan.meetingscheduler.domain.calendar.timeslot.TimeSlot;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Daily;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Monthly;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Weekly;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Yearly;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.RecurringEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.SingleEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.exception.ValidationException;
import io.garrimanasaryan.meetingscheduler.domain.repo.CalendarRepo;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TimeslotCalendarWorkingHoursValidator implements Validator<TimeSlot> {
    private static final int VALIDATION_DAYS = 14;
    private final CalendarRepo calendarRepo;

    public TimeslotCalendarWorkingHoursValidator(CalendarRepo calendarRepo) {
        this.calendarRepo = calendarRepo;
    }

    @Override
    public void validate(@NotNull TimeSlot entity) {
        calendarRepo.ofId(entity.calendarId())
                .ifPresentOrElse(
                        calendar -> {
                            switch (entity.calendarEventTiming()) {
                                case SingleEventTiming singleEventTiming ->
                                        validateSingle(singleEventTiming, calendar.workingHours());
                                case RecurringEventTiming recurringEventTiming ->
                                        validateRecurring(recurringEventTiming, calendar.workingHours());
                            }
                        }
                        ,
                        () -> {
                            throw new ValidationException(
                                    "Calendar ID for the provided Timeslot not found: " + entity.id()
                            );
                        }
                );
    }

    private void validateRecurring(
            @NotNull RecurringEventTiming recurringEventTiming,
            @NotNull List<WorkingHour> workingHours
    ) {
        switch (recurringEventTiming.recurrenceRule()){
            case Daily d -> validateDailyRecurring(d, recurringEventTiming, workingHours);
            case Weekly w -> validateWeeklyRecurring(w, recurringEventTiming, workingHours);
            case Monthly m -> validateMonthlyRecurring(m, recurringEventTiming, workingHours);
            case Yearly y -> validateYearlyRecurring(y, recurringEventTiming, workingHours);
        }

    }

    private static void validateYearlyRecurring(
            @NotNull Yearly y,
            @NotNull RecurringEventTiming t,
            @NotNull List<WorkingHour> workingHours
    ) {
        int year = t.startDate().getYear() + y.every();

        for (MonthDay md : y.days()) {
            if (!md.isValidYear(year)) {
                throw new ValidationException("Yearly recurrence date is invalid for year " + year);
            }

            LocalDate date = md.atYear(year);

            assertWithinWorkingHours(
                    date,
                    t.startTime(),
                    t.duration(),
                    workingHours
            );
        }

    }

    private static void validateMonthlyRecurring(
            Monthly m,
            RecurringEventTiming t,
            List<WorkingHour> workingHours
    ) {
        LocalDate base = t.startDate().plusMonths(m.every());

        if (m.dayOfMonth() > base.lengthOfMonth()) {
            throw new ValidationException("Monthly recurrence day does not exist in target month");
        }

        LocalDate date = base.withDayOfMonth(m.dayOfMonth());

        assertWithinWorkingHours(
                date,
                t.startTime(),
                t.duration(),
                workingHours
        );
    }


    private static void validateWeeklyRecurring(
            @NotNull Weekly w,
            @NotNull RecurringEventTiming t,
            @NotNull List<WorkingHour> workingHours
    ) {
        if (w.dayOfWeeks().isEmpty()) {
            throw new ValidationException("Weekly recurrence must specify at least one day");
        }

        LocalDate start = t.startDate();
        LocalDate end = start.plusWeeks(w.every());

        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            if (w.dayOfWeeks().contains(date.getDayOfWeek())) {
                assertWithinWorkingHours(
                        date,
                        t.startTime(),
                        t.duration(),
                        workingHours
                );
            }
        }
    }

    private static void validateDailyRecurring(
            @NotNull Daily d,
            @NotNull RecurringEventTiming t,
            @NotNull List<WorkingHour> workingHours
    ) {
        LocalDate date = t.startDate();

        for (int i = 0; i < VALIDATION_DAYS; i += d.every()) {
            assertWithinWorkingHours(
                    date.plusDays(i),
                    t.startTime(),
                    t.duration(),
                    workingHours
            );
        }
    }


    private static void validateSingle(
            @NotNull SingleEventTiming singleEventTiming,
            @NotNull List<WorkingHour> workingHours
    ) {
        var tmsStart = singleEventTiming.startAt().toZonedDateTime();

        assertWithinWorkingHours(
                tmsStart.toLocalDate(),
                tmsStart.toLocalTime(),
                singleEventTiming.duration(),
                workingHours
        );

    }

    private static void assertWithinWorkingHours(
            LocalDate date,
            LocalTime startTime,
            Duration duration,
            List<WorkingHour> workingHours
    ) {
        var endTime = startTime.plus(duration);
        var dayOfWeek = date.getDayOfWeek();

        boolean valid = workingHours.stream()
                .filter(wh -> wh.dayOfWeek().equals(dayOfWeek))
                .anyMatch(wh ->
                        !startTime.isBefore(wh.startAt()) &&
                                !endTime.isAfter(wh.endAt())
                );

        if (!valid) {
            throw new ValidationException(
                    String.format(
                            "Recurring timeslot violates calendar working hours on %s (%s) %s–%s. " +
                                    "Allowed working hours: %s",
                            date,
                            dayOfWeek,
                            startTime,
                            endTime,
                            formatWorkingHours(workingHours)
                    )
            );
        }

    }

    private static String formatWorkingHours(List<WorkingHour> workingHours) {
        return workingHours.stream()
                .sorted(
                        Comparator
                                .comparing(WorkingHour::dayOfWeek)
                                .thenComparing(WorkingHour::startAt)
                )
                .map(wh -> String.format(
                        "%s %s–%s",
                        wh.dayOfWeek(),
                        wh.startAt(),
                        wh.endAt()
                ))
                .distinct()
                .collect(Collectors.joining(", "));
    }


}
