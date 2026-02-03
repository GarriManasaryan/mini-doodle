package io.garrimanasaryan.meetingscheduler.application.query.availability;

import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Daily;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Monthly;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.RecurrenceRule;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Weekly;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Yearly;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.RecurringEventTiming;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.dto.RecurringAvailabilityRuleDto;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.MonthDay;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RecurrenceExpander {

    public static List<EventInstance> expand(
            @NotNull RecurringAvailabilityRuleDto rule,
            @NotNull Instant rangeStart,
            @NotNull Instant rangeEnd
    ) {
        var timing = (RecurringEventTiming) rule.timing();
        var zoneId = timing.zoneId();

        Instant cursor = timing.startDate()
                .atTime(timing.startTime())
                .atZone(zoneId)
                .toInstant();

        List<EventInstance> result = new ArrayList<>();

        while (!cursor.isAfter(rangeEnd)) {
            Instant end = cursor.plus(timing.duration());

            if (!end.isBefore(rangeStart)) {
                result.add(new EventInstance(
                        rule.type(),
                        rule.sourceId(),
                        cursor,
                        end,
                        rule.allowedScheduledItemTypes()
                ));
            }

            cursor = advance(cursor, timing.recurrenceRule(), zoneId);
        }

        return result;
    }

    private static Instant advance(
            @NotNull Instant current,
            @NotNull RecurrenceRule rule,
            @NotNull ZoneId zoneId
    ) {
        ZonedDateTime zdt = current.atZone(zoneId);

        return switch (rule) {
            case Daily daily -> advanceDaily(zdt, daily).toInstant();
            case Weekly weekly -> advanceWeekly(zdt, weekly).toInstant();
            case Monthly monthly -> advanceMonthly(zdt, monthly).toInstant();
            case Yearly yearly -> advanceYearly(zdt, yearly).toInstant();
        };
    }

    private static ZonedDateTime advanceDaily(ZonedDateTime zdt, Daily rule) {
        return zdt.plusDays(rule.every());
    }

    private static ZonedDateTime advanceWeekly(ZonedDateTime zdt, Weekly rule) {
        List<DayOfWeek> sorted = rule.dayOfWeeks().stream()
                .sorted()
                .toList();

        DayOfWeek current = zdt.getDayOfWeek();

        for (DayOfWeek d : sorted) {
            if (d.getValue() > current.getValue()) {
                return zdt.plusDays(d.getValue() - current.getValue());
            }
        }
        int daysUntilNextWeek =
                7 * rule.every() - (current.getValue() - sorted.getFirst().getValue());

        return zdt.plusDays(daysUntilNextWeek);
    }


    private static ZonedDateTime advanceMonthly(ZonedDateTime zdt, Monthly rule) {
        int targetDay = rule.dayOfMonth();

        ZonedDateTime base = zdt.withDayOfMonth(1).plusMonths(rule.every());

        int maxDay = base.getMonth().length(base.toLocalDate().isLeapYear());
        return base.withDayOfMonth(Math.min(targetDay, maxDay));
    }


    private static ZonedDateTime advanceYearly(ZonedDateTime zdt, Yearly rule) {
        List<MonthDay> sorted = rule.days().stream()
                .sorted(Comparator
                        .comparing(MonthDay::getMonthValue)
                        .thenComparing(MonthDay::getDayOfMonth))
                .toList();

        MonthDay current = MonthDay.from(zdt);

        for (MonthDay d : sorted) {
            if (d.isAfter(current)) {
                return zdt.withMonth(d.getMonthValue())
                        .withDayOfMonth(
                                Math.min(
                                        d.getDayOfMonth(),
                                        zdt.getMonth().length(zdt.toLocalDate().isLeapYear())
                                )
                        );
            }
        }

        ZonedDateTime nextYear = zdt.plusYears(rule.every());
        MonthDay first = sorted.getFirst();

        int maxDay = first.getMonth().length(nextYear.toLocalDate().isLeapYear());

        return nextYear
                .withMonth(first.getMonthValue())
                .withDayOfMonth(Math.min(first.getDayOfMonth(), maxDay));
    }

}
