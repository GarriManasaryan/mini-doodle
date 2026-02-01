package io.garrimanasaryan.meetingscheduler.domain.policy.calendar;

import io.garrimanasaryan.meetingscheduler.domain.calendar.WorkingHour;
import io.garrimanasaryan.meetingscheduler.domain.exception.CalendarDomainException;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class WorkingHourPolicy {
    public static void validate(@NotNull List<WorkingHour> hours){
        hasEmptyHours(hours);
        withinHours(hours);
        noOverlapWithinDay(hours);
    }

    private static void hasEmptyHours(@NotNull List<WorkingHour> hours){
        if (hours == null || hours.isEmpty()){
            throw new CalendarDomainException("Calendar must have working hours");
        }
    }
    private static void withinHours(@NotNull List<WorkingHour> hours){
        hours
                .forEach(x -> {
                    LocalTime end = x.startAt().plus(x.duration());
                    if (end.isBefore(x.startAt())){
                        throw new CalendarDomainException("Working hour exceeds 24h day");
                    }
                });
    }

    private static void noOverlapWithinDay(@NotNull List<WorkingHour> hours){
        Map<DayOfWeek, List<WorkingHour>> hoursByDay = hours.stream()
                .collect(Collectors.groupingBy(WorkingHour::dayOfWeek));

        hoursByDay.values().forEach(
                dayHours -> {
                    dayHours.sort(Comparator.comparing(WorkingHour::startAt));
                    for (int i = 0; i < dayHours.size() - 1 ;i++ ){
                        var current = dayHours.get(i);
                        var next = dayHours.get(i+1);
                        if (current.endAt().isAfter(next.startAt())){
                            throw new CalendarDomainException("Working hours overlap; edit existing one");
                        }
                    }
                }
        );
    }
}
