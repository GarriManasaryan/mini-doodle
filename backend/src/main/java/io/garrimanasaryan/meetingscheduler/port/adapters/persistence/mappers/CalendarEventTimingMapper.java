package io.garrimanasaryan.meetingscheduler.port.adapters.persistence.mappers;

import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Daily;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Monthly;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Weekly;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Yearly;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.CalendarEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.RecurringEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.SingleEventTiming;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.dto.TimingDto;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.MonthDay;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarEventTimingMapper {

    public static CalendarEventTiming toCalendarEventTiming(@NotNull TimingDto dto){
        return switch (dto.type()){
            case "SINGLE" ->
                new SingleEventTiming(
                        dto.startAt(),
                        Duration.parse(dto.duration())
                );

            case "RECURRING" -> {
                var r = dto.recurrence();
                var rule = switch (r.frequency()){
                    case DAILY -> new Daily(r.every());
                    case WEEKLY -> new Weekly(
                            r.every(),
                            ((List<String>) r.details().getOrDefault("days", List.of()))
                                    .stream()
                                    .map(DayOfWeek::valueOf)
                                    .collect(Collectors.toSet())
                    );
                    case MONTHLY -> new Monthly(
                            r.every(),
                            (int) r.details().get("dayOfMonth")
                    );
                    case YEARLY -> new Yearly(
                            r.every(),
                            ((List<String>) r.details().getOrDefault("days", List.of()))
                                    .stream()
                                    .map(MonthDay::parse)
                                    .collect(Collectors.toSet())
                    );
                };

                yield new RecurringEventTiming(
                        dto.startDate(),
                        dto.startTime(),
                        ZoneId.of(dto.zoneId()),
                        Duration.parse(dto.duration()),
                        rule
                );
            }

            default -> throw new IllegalStateException("Unknown timing type: " + dto.type());

        };

    }

}
