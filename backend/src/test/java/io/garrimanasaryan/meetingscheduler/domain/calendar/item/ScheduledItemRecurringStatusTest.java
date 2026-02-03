package io.garrimanasaryan.meetingscheduler.domain.calendar.item;

import io.garrimanasaryan.meetingscheduler.domain.calendar.item.definition.FocusTimeDefinition;
import io.garrimanasaryan.meetingscheduler.domain.common.TitleDescription;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Daily;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Weekly;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.RecurringEventTiming;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ScheduledItemRecurringStatusTest {

    @Test
    void recurring_event_upcoming_before_first_occurrence() {
        var timing = new RecurringEventTiming(
                OffsetDateTime.parse("2026-02-05T10:00:00Z").toLocalDate(),
                LocalTime.of(10, 0),
                ZoneId.of("UTC"),
                Duration.ofHours(1),
                new Daily(1)
        );

        var item = ScheduledItem.of(
                "creator",
                "cal-1",
                "org-1",
                new TitleDescription("Daily", Optional.of("asc")),
                timing,
                new FocusTimeDefinition("link"),
                false
        );

        assertThat(item.status()).isEqualTo(ScheduledItemStatus.UPCOMING);
    }

    @Test
    void recurring_event_upcoming_between_occurrences() {
        var timing = new RecurringEventTiming(
                OffsetDateTime.parse("2026-02-03T10:00:00Z").toLocalDate().minusDays(10),
                LocalTime.of(9, 0),
                ZoneId.of("UTC"),
                Duration.ofMinutes(30),
                new Weekly(1, Set.of(DayOfWeek.MONDAY))
        );

        var item = ScheduledItem.of(
                "creator",
                "cal-1",
                "org-1",
                new TitleDescription("Weekly", null),
                timing,
                new FocusTimeDefinition("link"),
                false
        );

        assertThat(item.status()).isEqualTo(ScheduledItemStatus.UPCOMING);
    }
}


