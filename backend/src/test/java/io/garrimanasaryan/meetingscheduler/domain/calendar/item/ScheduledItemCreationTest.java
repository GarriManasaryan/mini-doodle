package io.garrimanasaryan.meetingscheduler.domain.calendar.item;

import io.garrimanasaryan.meetingscheduler.domain.calendar.item.definition.FocusTimeDefinition;
import io.garrimanasaryan.meetingscheduler.domain.calendar.item.definition.MeetingDefinition;
import io.garrimanasaryan.meetingscheduler.domain.common.TitleDescription;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.SingleEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.exception.TimeSlotDomainException;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ScheduledItemCreationTest {

    @Test
    void create_single_event_scheduled_item_success() {
        var timing = new SingleEventTiming(
                OffsetDateTime.parse("2026-02-03T10:00:00Z"),
                Duration.ofMinutes(30)
        );

        var item = ScheduledItem.of(
                "creator",
                "cal-1",
                "organizer-1",
                new TitleDescription("Meeting", null),
                timing,
                new MeetingDefinition(Set.of("user-2"), Optional.empty()),
                false
        );

        assertThat(item.id()).startsWith("sci");
        assertThat(item.calendarId()).isEqualTo("cal-1");
        assertThat(item.organizerUserId()).isEqualTo("organizer-1");
        assertThat(item.metadata().createdBy()).isEqualTo("creator");
        assertThat(item.isCancelled()).isFalse();
    }

    @Test
    void fail_when_single_event_duration_is_invalid() {
        var timing = new SingleEventTiming(
                OffsetDateTime.parse("2026-02-03T10:00:00Z"),
                Duration.ZERO
        );

        assertThatThrownBy(() ->
                ScheduledItem.of(
                        "creator",
                        "cal-1",
                        "organizer-1",
                        new TitleDescription("Invalid", null),
                        timing,
                        new FocusTimeDefinition("link"),
                        false
                )
        )
                .isInstanceOf(TimeSlotDomainException.class);
    }
}
