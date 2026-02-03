package io.garrimanasaryan.meetingscheduler.port.adapters.persistence;

import io.garrimanasaryan.meetingscheduler.application.ScheduledItemService;
import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItem;
import io.garrimanasaryan.meetingscheduler.domain.calendar.item.definition.FocusTimeDefinition;
import io.garrimanasaryan.meetingscheduler.domain.calendar.item.definition.MeetingDefinition;
import io.garrimanasaryan.meetingscheduler.domain.common.TitleDescription;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Daily;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.RecurringEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.SingleEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.exception.ValidationException;
import io.garrimanasaryan.meetingscheduler.domain.repo.ScheduledItemRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql({
        "/db/test-fixtures/base.sql",
        "/db/test-fixtures/scheduled-item-init.sql"
})
class ScheduledItemRepositoryIT extends DatabaseBaseTest {

    @Autowired
    private ScheduledItemService scheduledItemService;

    @Autowired
    private PgTimeslotRepo timeslotRepo;

    @Autowired
    private ScheduledItemRepo scheduledItemRepo;

    @Test
    void create_and_load_single_scheduled_item() {
        var timing = new SingleEventTiming(
                OffsetDateTime.parse("2026-02-03T10:00:00Z"),
                Duration.ofHours(1)
        );

        var item = ScheduledItem.of(
                "user-1",
                "calendar-1",
                "user-1",
                new TitleDescription("Team meeting", Optional.of("Discuss roadmap")),
                timing,
                new MeetingDefinition(Set.of("user-2"), Optional.empty()),
                false
        );

        scheduledItemService.save(item);

        var loaded = scheduledItemRepo.ofId(item.id());

        assertThat(loaded).isPresent();
        assertThat(loaded.get().calendarId()).isEqualTo("calendar-1");
        assertThat(loaded.get().organizerUserId()).isEqualTo("user-1");
        assertThat(loaded.get().calendarEventTiming())
                .isInstanceOf(SingleEventTiming.class);
    }

    @Test
    void scheduled_item_fitting_timeslot_is_saved() {
        var timing = new SingleEventTiming(
                OffsetDateTime.parse("2026-02-03T11:00:00Z"),
                Duration.ofMinutes(30)
        );

        var item = ScheduledItem.of(
                "user-1",
                "calendar-1",
                "user-1",
                new TitleDescription("Valid meeting", Optional.of("aca")),
                timing,
                new MeetingDefinition(Set.of("user-2"), Optional.empty()),
                false
        );

        scheduledItemService.save(item);

        assertThat(scheduledItemRepo.ofId(item.id())).isPresent();
    }

    @Test
    void scheduled_item_with_unallowed_type_is_rejected() {
        var timing = new SingleEventTiming(
                OffsetDateTime.parse("2026-02-05T10:00:00Z").plusDays(1).withHour(11),
                Duration.ofMinutes(30)
        );

        var item = ScheduledItem.of(
                "user-1",
                "calendar-1",
                "user-1",
                new TitleDescription("Valid meeting", Optional.of("aca")),
                timing,
                new FocusTimeDefinition("focus-link"),
                false
        );

        assertThatThrownBy(() -> scheduledItemService.save(item))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void scheduled_item_fails_when_timeslot_is_busy() {
        timeslotRepo.ofId("ts-1").ifPresent(ts ->
                timeslotRepo.update(ts.update(
                        "user-1",
                        ts.titleDescription(),
                        ts.allowedScheduledItemType(),
                        true,
                        ts.calendarEventTiming()
                ))
        );

        var timing = new SingleEventTiming(
                OffsetDateTime.parse("2026-02-05T10:00:00Z").plusDays(1).withHour(11),
                Duration.ofMinutes(30)
        );

        var item = ScheduledItem.of(
                "user-1",
                "calendar-1",
                "user-1",
                new TitleDescription("Busy slot", Optional.of("acasc")),
                timing,
                new MeetingDefinition(Set.of("user-2"), Optional.empty()),
                false
        );

        assertThatThrownBy(() -> scheduledItemService.save(item))
                .isInstanceOf(ValidationException.class);
    }

}

