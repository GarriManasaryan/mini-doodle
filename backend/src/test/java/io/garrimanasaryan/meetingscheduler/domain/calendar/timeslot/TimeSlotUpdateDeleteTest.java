package io.garrimanasaryan.meetingscheduler.domain.calendar.timeslot;

import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItemType;
import io.garrimanasaryan.meetingscheduler.domain.common.TitleDescription;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.SingleEventTiming;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class TimeSlotUpdateDeleteTest {

    @Test
    void update_preserves_identity_and_calendar() {
        var original = TimeSlot.of(
                "creator",
                "cal-1",
                new TitleDescription("Initial", null),
                Set.of(ScheduledItemType.MEETING),
                false,
                new SingleEventTiming(
                        OffsetDateTime.parse("2026-02-05T10:00:00Z").plusDays(1),
                        Duration.ofMinutes(30)
                )
        );

        var updated = original.update(
                "editor",
                new TitleDescription("Updated", null),
                Set.of(ScheduledItemType.FOCUS_TIME),
                true,
                original.calendarEventTiming()
        );

        assertThat(updated.id()).isEqualTo(original.id());
        assertThat(updated.calendarId()).isEqualTo(original.calendarId());
        assertThat(updated.metadata().updatedBy()).isEqualTo("editor");
        assertThat(updated.isBusyByUser()).isTrue();
    }

    @Test
    void delete_marks_metadata_as_deleted() {
        var slot = TimeSlot.of(
                "creator",
                "cal-1",
                new TitleDescription("To delete", null),
                Set.of(ScheduledItemType.MEETING),
                false,
                new SingleEventTiming(
                        OffsetDateTime.parse("2026-02-05T10:00:00Z").plusDays(1),
                        Duration.ofMinutes(30)
                )
        );

        var deleted = slot.delete("deleter");

        assertThat(deleted.metadata().isDeleted()).isTrue();
        assertThat(deleted.metadata().updatedBy()).isEqualTo("deleter");
    }
}

