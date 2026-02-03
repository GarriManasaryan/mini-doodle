package io.garrimanasaryan.meetingscheduler.port.adapters.persistence;

import io.garrimanasaryan.meetingscheduler.application.TimeslotService;
import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItemType;
import io.garrimanasaryan.meetingscheduler.domain.calendar.timeslot.TimeSlot;
import io.garrimanasaryan.meetingscheduler.domain.common.TitleDescription;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Monthly;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Weekly;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Yearly;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.RecurringEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.SingleEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql("/db/test-fixtures/base.sql")
class TimeslotRepositoryIT extends DatabaseBaseTest {

    @Autowired
    private PgTimeslotRepo timeslotRepo;

    @Autowired
    private TimeslotService service;

    @Test
    void save_and_load_single_event_timeslot() {
        var slot = TimeSlot.of(
                "user-1",
                "calendar-1",
                new TitleDescription("Focus time", Optional.of("aca")),
                Set.of(ScheduledItemType.FOCUS_TIME),
                false,
                new SingleEventTiming(
                        OffsetDateTime.parse("2026-02-05T10:00:00Z").plusDays(1),
                        Duration.ofMinutes(45)
                )
        );

        timeslotRepo.update(slot);

        var loaded = timeslotRepo.ofId(slot.id());

        assertThat(loaded).isPresent();
        assertThat(loaded.get().id()).isEqualTo(slot.id());
        assertThat(loaded.get().calendarId()).isEqualTo("calendar-1");
        assertThat(loaded.get().allowedScheduledItemType())
                .containsExactly(ScheduledItemType.FOCUS_TIME);

        assertThat(loaded.get().calendarEventTiming()).isInstanceOf(SingleEventTiming.class);

        var single = (SingleEventTiming) loaded.get().calendarEventTiming();
        assertThat(single.duration()).isEqualTo(Duration.ofMinutes(45));
    }

    @Test
    void single_event_within_working_hours_is_saved() {
        var mondayAt10 = OffsetDateTime.parse("2026-02-05T10:00:00Z")
                .with(DayOfWeek.MONDAY)
                .withHour(10)
                .withMinute(0);

        var slot = TimeSlot.of(
                "user-1",
                "calendar-1",
                new TitleDescription("Valid single", Optional.empty()),
                Set.of(ScheduledItemType.MEETING),
                false,
                new SingleEventTiming(
                        mondayAt10,
                        Duration.ofHours(1)
                )
        );

        timeslotRepo.update(slot);

        assertThat(timeslotRepo.ofId(slot.id())).isPresent();
    }

    @Test
    void single_event_outside_working_hours_is_rejected() {
        var sunday = OffsetDateTime.parse("2026-02-05T10:00:00Z")
                .with(DayOfWeek.SUNDAY)
                .withHour(22);

        var slot = TimeSlot.of(
                "user-1",
                "calendar-1",
                new TitleDescription("Invalid single", Optional.empty()),
                Set.of(ScheduledItemType.MEETING),
                false,
                new SingleEventTiming(
                        sunday,
                        Duration.ofHours(1)
                )
        );

        assertThatThrownBy(() -> service.save(slot))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void weekly_recurring_within_working_hours_is_saved() {
        var timing = new RecurringEventTiming(
                LocalDate.of(2026, 2, 5),
                LocalTime.of(10, 0),
                ZoneId.of("UTC"),
                Duration.ofHours(1),
                new Weekly(1, Set.of(DayOfWeek.MONDAY))
        );

        var slot = TimeSlot.of(
                "user-1",
                "calendar-1",
                new TitleDescription("Weekly valid", Optional.empty()),
                Set.of(ScheduledItemType.MEETING),
                false,
                timing
        );

        timeslotRepo.update(slot);

        assertThat(timeslotRepo.ofId(slot.id())).isPresent();
    }

    @Test
    void yearly_recurring_invalid_date_is_rejected() {
        var timing = new RecurringEventTiming(
                LocalDate.of(2025, 1, 1),
                LocalTime.of(2, 0),
                ZoneId.of("UTC"),
                Duration.ofHours(1),
                new Yearly(1, Set.of(MonthDay.of(2, 1)))
        );

        var slot = TimeSlot.of(
                "user-1",
                "calendar-1",
                new TitleDescription("Yearly invalid", Optional.empty()),
                Set.of(ScheduledItemType.MEETING),
                false,
                timing
        );

        assertThatThrownBy(() -> service.save(slot)).isInstanceOf(ValidationException.class);
    }


}

