package io.garrimanasaryan.meetingscheduler.domain.calendar.item;

import io.garrimanasaryan.meetingscheduler.domain.common.Domain;
import io.garrimanasaryan.meetingscheduler.domain.common.Metadata;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Daily;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Monthly;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Weekly;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Yearly;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.CalendarEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.RecurringEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.SingleEventTiming;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

public record ScheduledItem(
        @NotNull String id,
        @NotNull String calendarId,
        @NotNull CalendarEventTiming calendarEventTiming,
        @NotNull boolean isCancelled,
        @NotNull Metadata metadata
) implements Domain {

    public Domain delete(String by) {
        return null;
    }

    public ScheduledItemStatus status(Instant now){
        if (isCancelled) return ScheduledItemStatus.CANCELLED;

        return switch (calendarEventTiming){
            case SingleEventTiming single -> resolveSingleStatus(now, single);
            case RecurringEventTiming recurring -> resolveRecurringStatus(now, recurring);
        };
    }

    private ScheduledItemStatus resolveSingleStatus(Instant now, SingleEventTiming singleEventTiming){
        var start = singleEventTiming.startAt().toInstant();
        var end = start.plus(singleEventTiming.duration());

        if (now.isBefore(start)) return ScheduledItemStatus.UPCOMING;
        if (now.isAfter(end)) return ScheduledItemStatus.COMPLETED;

        return ScheduledItemStatus.ONGOING;
    }

    private ScheduledItemStatus resolveRecurringStatus(Instant nowInstant, RecurringEventTiming t){
        var now = nowInstant.atZone(t.zoneId());
        var firstStart = ZonedDateTime.of(
                t.startDate(),
                t.startTime(),
                t.zoneId()
        );

        if (now.isBefore(firstStart)) return ScheduledItemStatus.UPCOMING;

        var lastOccurrence = previousOccurrenceZdt(t, now);
        var lastOccurrenceEnd = lastOccurrence.plus(t.duration());

        if (!now.isBefore(lastOccurrence) && now.isBefore(lastOccurrenceEnd)){
            return ScheduledItemStatus.ONGOING;
        }

        return ScheduledItemStatus.UPCOMING;
    }

    private ZonedDateTime previousOccurrenceZdt(
            RecurringEventTiming t,
            ZonedDateTime now
    ){
        return switch (t.recurrenceRule()){
            case Daily d -> previousDaily(t, now, d);
            case Weekly w -> previousWeekly(t, now, w);
            case Monthly m -> previousMonthly(t, now, m);
            case Yearly y -> previousYearly(t, now, y);

        };

    }

    private ZonedDateTime previousDaily(
            RecurringEventTiming t,
            ZonedDateTime now,
            Daily rule
    ){
        var start = ZonedDateTime.of(t.startDate(), t.startTime(), t.zoneId());
        var daysBetween = ChronoUnit.DAYS.between(start.toLocalDate(), now.toLocalDate());

        var interval = rule.every();
        var occurrencesPassed = daysBetween / interval;

        return start.plusDays(occurrencesPassed * interval);

    }

    private ZonedDateTime previousWeekly(
            RecurringEventTiming t,
            ZonedDateTime now,
            Weekly rule
    ){
        var start = ZonedDateTime.of(t.startDate(), t.startTime(), t.zoneId());
        var weeksBetween = ChronoUnit.WEEKS.between(start.toLocalDate(), now.toLocalDate());

        var interval = rule.every();
        var baseWeeks = (weeksBetween / interval) * interval;

        var baseWeekStart = start
                .plusWeeks(baseWeeks)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                ;

        return rule.dayOfWeeks().stream()
                .map(dow -> baseWeekStart.with(TemporalAdjusters.nextOrSame(dow)))
                .filter(dt -> !dt.isAfter(now))
                .max(ZonedDateTime::compareTo)
                .orElse(baseWeekStart)
                ;

    }

    private ZonedDateTime previousMonthly(
            RecurringEventTiming t,
            ZonedDateTime now,
            Monthly rule
    ){
        var start = ZonedDateTime.of(t.startDate(), t.startTime(), t.zoneId());
        var monthsBetween = ChronoUnit.MONTHS.between(
                YearMonth.from(start),
                YearMonth.from(now)
        );

        var interval = rule.every();
        var offset = (monthsBetween / interval) * interval;

        var targetMonth = YearMonth.from(start).plusMonths(offset);

        var day = rule.dayOfMonth();
        int safeDay = Math.min(day, targetMonth.lengthOfMonth());

        return targetMonth
                .atDay(safeDay)
                .atTime(t.startTime())
                .atZone(t.zoneId())
                ;

    }

    private ZonedDateTime previousYearly(
            RecurringEventTiming t,
            ZonedDateTime now,
            Yearly rule
    ){
        var start = ZonedDateTime.of(t.startDate(), t.startTime(), t.zoneId());
        var yearsBetween = now.getYear() - start.getYear();

        var interval = rule.every();
        var offset = (yearsBetween / interval) * interval;

        var year = start.getYear() + offset;

        return rule.days().stream()
                .map(md -> md.atYear(year))
                .map(d -> d.atTime(t.startTime()).atZone(t.zoneId()))
                .filter(dt -> !dt.isAfter(now))
                .max(ZonedDateTime::compareTo)
                .orElseThrow(() -> new IllegalStateException("Unmapped zdt"))
                ;

    }
}
