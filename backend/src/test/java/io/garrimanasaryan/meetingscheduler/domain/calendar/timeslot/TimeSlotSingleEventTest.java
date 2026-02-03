package io.garrimanasaryan.meetingscheduler.domain.calendar.timeslot;

import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItemType;
import io.garrimanasaryan.meetingscheduler.domain.common.TitleDescription;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.SingleEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.exception.TimeSlotDomainException;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class TimeSlotSingleEventTest {

    @Test
    void create_single_event_timeslot_success() {
        var timing = new SingleEventTiming(
                OffsetDateTime.parse("2026-02-05T10:00:00Z").plusDays(1),
                Duration.ofMinutes(30)
        );

        var slot = TimeSlot.of(
                "user-1",
                "cal-1",
                new TitleDescription("Focus", null),
                Set.of(ScheduledItemType.MEETING),
                false,
                timing
        );

        assertThat(slot.id()).startsWith("tms");
        assertThat(slot.calendarEventTiming()).isEqualTo(timing);
        assertThat(slot.metadata().createdBy()).isEqualTo("user-1");
        assertThat(slot.metadata().isDeleted()).isFalse();
    }

    @Test
    void fail_when_single_event_duration_is_zero() {
        var timing = new SingleEventTiming(
                OffsetDateTime.parse("2026-02-05T10:00:00Z"),
                Duration.ZERO
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
                .hasMessageContaining("duration must be positive");
    }

    @Test
    void fail_when_single_event_duration_is_negative() {
        var timing = new SingleEventTiming(
                OffsetDateTime.parse("2026-02-05T10:00:00Z"),
                Duration.ofMinutes(-10)
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
}
