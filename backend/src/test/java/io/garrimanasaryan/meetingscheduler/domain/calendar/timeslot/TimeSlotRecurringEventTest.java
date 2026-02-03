package io.garrimanasaryan.meetingscheduler.domain.calendar.timeslot;

import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItemType;
import io.garrimanasaryan.meetingscheduler.domain.common.TitleDescription;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.*;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.RecurringEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.exception.TimeSlotDomainException;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class TimeSlotRecurringEventTest {

    @Test
    void create_weekly_recurring_timeslot_success() {
        var timing = new RecurringEventTiming(
                OffsetDateTime.parse("2026-02-05T10:00:00Z").toLocalDate(),
                LocalTime.of(9, 0),
                ZoneId.of("UTC"),
                Duration.ofMinutes(60),
                new Weekly(1, Set.of(DayOfWeek.MONDAY))
        );

        var slot = TimeSlot.of(
                "user-1",
                "cal-1",
                new TitleDescription("Weekly Sync", null),
                Set.of(ScheduledItemType.MEETING),
                false,
                timing
        );

        assertThat(slot.calendarEventTiming()).isInstanceOf(RecurringEventTiming.class);
    }

    @Test
    void fail_when_weekly_has_no_days() {
        var timing = new RecurringEventTiming(
                OffsetDateTime.parse("2026-02-05T10:00:00Z").toLocalDate(),
                LocalTime.NOON,
                ZoneId.of("UTC"),
                Duration.ofMinutes(30),
                new Weekly(1, Set.of())
        );

        assertThatThrownBy(() ->
                TimeSlot.of(
                        "user-1",
                        "cal-1",
                        new TitleDescription("Invalid", null),
                        Set.of(ScheduledItemType.MEETING),
                        false,
                        timing
                )
        )
                .isInstanceOf(TimeSlotDomainException.class);
    }

    @Test
    void fail_when_monthly_day_invalid() {
        var timing = new RecurringEventTiming(
                OffsetDateTime.parse("2026-02-05T10:00:00Z").toLocalDate(),
                LocalTime.NOON,
                ZoneId.of("UTC"),
                Duration.ofMinutes(30),
                new Monthly(1, 32)
        );

        assertThatThrownBy(() ->
                TimeSlot.of(
                        "user-1",
                        "cal-1",
                        new TitleDescription("Invalid", null),
                        Set.of(ScheduledItemType.MEETING),
                        false,
                        timing
                )
        )
                .isInstanceOf(TimeSlotDomainException.class)
                .hasMessageContaining("between 1 and 31");
    }

    @Test
    void fail_when_yearly_has_no_days() {
        var timing = new RecurringEventTiming(
                OffsetDateTime.parse("2026-02-05T10:00:00Z").toLocalDate(),
                LocalTime.NOON,
                ZoneId.of("UTC"),
                Duration.ofMinutes(30),
                new Yearly(1, Set.of())
        );

        assertThatThrownBy(() ->
                TimeSlot.of(
                        "user-1",
                        "cal-1",
                        new TitleDescription("Invalid", null),
                        Set.of(ScheduledItemType.MEETING),
                        false,
                        timing
                )
        )
                .isInstanceOf(TimeSlotDomainException.class);
    }

    @Test
    void fail_when_recurrence_interval_not_positive() {
        var timing = new RecurringEventTiming(
                OffsetDateTime.parse("2026-02-05T10:00:00Z").toLocalDate(),
                LocalTime.NOON,
                ZoneId.of("UTC"),
                Duration.ofMinutes(30),
                new Daily(0)
        );

        assertThatThrownBy(() ->
                TimeSlot.of(
                        "user-1",
                        "cal-1",
                        new TitleDescription("Invalid", null),
                        Set.of(ScheduledItemType.MEETING),
                        false,
                        timing
                )
        )
                .isInstanceOf(TimeSlotDomainException.class)
                .hasMessageContaining("interval must be positive");
    }
}
