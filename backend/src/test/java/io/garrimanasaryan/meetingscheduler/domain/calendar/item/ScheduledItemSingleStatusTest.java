package io.garrimanasaryan.meetingscheduler.domain.calendar.item;

import io.garrimanasaryan.meetingscheduler.domain.calendar.item.definition.FocusTimeDefinition;
import io.garrimanasaryan.meetingscheduler.domain.common.TitleDescription;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.SingleEventTiming;
import org.junit.jupiter.api.Test;

import javax.swing.text.html.Option;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ScheduledItemSingleStatusTest {

    @Test
    void single_event_upcoming() {
        var timing = new SingleEventTiming(
                OffsetDateTime.parse("2026-02-05T10:00:00Z").plusHours(2),
                Duration.ofMinutes(30)
        );

        var item = ScheduledItem.of(
                "creator",
                "cal-1",
                "org-1",
                new TitleDescription("Upcoming", null),
                timing,
                new FocusTimeDefinition("link"),
                false
        );

        assertThat(item.status()).isEqualTo(ScheduledItemStatus.UPCOMING);
    }

    @Test
    void single_event_ongoing() {
        var timing = new SingleEventTiming(
                OffsetDateTime.parse("2026-02-05T10:00:00Z").minusMinutes(10),
                Duration.ofMinutes(30)
        );

        var item = ScheduledItem.of(
                "creator",
                "cal-1",
                "org-1",
                new TitleDescription("Ongoing", null),
                timing,
                new FocusTimeDefinition("link"),
                false
        );

        assertThat(item.status()).isEqualTo(ScheduledItemStatus.ONGOING);
    }

    @Test
    void single_event_completed() {
        var timing = new SingleEventTiming(
                OffsetDateTime.parse("2025-02-15T10:00:00Z"),
                Duration.ofMinutes(30)
        );

        var item = ScheduledItem.of(
                "creator",
                "cal-1",
                "org-1",
                new TitleDescription("Completed", Optional.of("acac")),
                timing,
                new FocusTimeDefinition("link"),
                false
        );

        assertThat(item.status()).isEqualTo(ScheduledItemStatus.COMPLETED);
    }

    @Test
    void cancelled_overrides_time_logic() {
        var timing = new SingleEventTiming(
                OffsetDateTime.parse("2026-02-05T10:00:00Z").plusHours(1),
                Duration.ofMinutes(30)
        );

        var item = ScheduledItem.of(
                "creator",
                "cal-1",
                "org-1",
                new TitleDescription("Cancelled", null),
                timing,
                new FocusTimeDefinition("link"),
                true
        );

        assertThat(item.status()).isEqualTo(ScheduledItemStatus.CANCELLED);
    }
}

