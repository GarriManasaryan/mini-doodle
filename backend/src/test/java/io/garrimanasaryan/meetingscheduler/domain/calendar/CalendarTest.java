package io.garrimanasaryan.meetingscheduler.domain.calendar;

import io.garrimanasaryan.meetingscheduler.domain.common.TitleDescription;
import io.garrimanasaryan.meetingscheduler.domain.exception.CalendarDomainException;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CalendarTest {

    @Test
    void fail_when_working_hours_overlap_within_same_day() {
        var hours = List.of(
                new WorkingHour(
                        DayOfWeek.MONDAY,
                        LocalTime.of(9, 0),
                        Duration.ofHours(4)
                ),
                new WorkingHour(
                        DayOfWeek.MONDAY,
                        LocalTime.of(12, 0),
                        Duration.ofHours(2)
                )
        );

        assertThatThrownBy(() ->
                Calendar.of(
                        "user-1",
                        "user-1",
                        "user-1",
                        new TitleDescription("My calendar", null),
                        CalendarType.PERSONAL,
                        hours
                )
        )
                .isInstanceOf(CalendarDomainException.class)
                .hasMessageContaining("overlap");
    }

    @Test
    void fail_when_working_hour_exceeds_24h_day() {
        var invalidHours = List.of(
                new WorkingHour(
                        DayOfWeek.MONDAY,
                        LocalTime.of(23, 0),
                        Duration.ofHours(2)
                )
        );

        assertThatThrownBy(() ->
                Calendar.of(
                        "user-1",
                        "user-1",
                        "user-1",
                        new TitleDescription("calendar", Optional.of("aca")),
                        CalendarType.PERSONAL,
                        invalidHours
                )
        )
                .isInstanceOf(CalendarDomainException.class);
    }
}