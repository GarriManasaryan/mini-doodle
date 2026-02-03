package io.garrimanasaryan.meetingscheduler.application.query.availability;

import io.garrimanasaryan.meetingscheduler.application.CalendarService;
import io.garrimanasaryan.meetingscheduler.domain.calendar.Calendar;
import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItemType;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Daily;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.CalendarEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.RecurringEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.exception.NotFoundException;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.query.availability.AvailabilityEventInstanceDto;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.dto.AvailabilityContributorType;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.dto.AvailabilityPreviewDto;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.dto.RecurringAvailabilityRuleDto;
import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.dto.SingleAvailabilityEventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityPreviewServiceTest {

    @Mock
    AvailabilityPreview availabilityPreview;

    @Mock
    CalendarService calendarService;

    AvailabilityPreviewService service;

    @BeforeEach
    void setUp() {
        service = new AvailabilityPreviewService(
                availabilityPreview,
                calendarService
        );
    }

    @Test
    void fails_when_calendar_not_found() {
        when(calendarService.ofId("cal-1"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.getPreview(
                        "cal-1",
                        Instant.parse("2026-02-03T10:00:00Z"),
                        Instant.parse("2026-02-03T12:00:00Z")
                )
        )
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void fails_when_range_is_invalid() {
        when(calendarService.ofId("cal-1"))
                .thenReturn(Optional.of(mock(Calendar.class)));

        assertThatThrownBy(() ->
                service.getPreview(
                        "cal-1",
                        Instant.parse("2026-02-03T12:00:00Z"),
                        Instant.parse("2026-02-03T10:00:00Z")
                )
        )
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void returns_single_event_instances() {
        when(calendarService.ofId("cal-1"))
                .thenReturn(Optional.of(mock(Calendar.class)));

        var single = new SingleAvailabilityEventDto(
                AvailabilityContributorType.FREE_TIMESLOT,
                "ts-1",
                Instant.parse("2026-02-03T10:00:00Z"),
                Instant.parse("2026-02-03T11:00:00Z"),
                Set.of(ScheduledItemType.MEETING)
        );

        when(availabilityPreview.query(any(), any(), any()))
                .thenReturn(new AvailabilityPreviewDto(
                        List.of(single),
                        List.of()
                ));

        var result = service.getPreview(
                "cal-1",
                Instant.parse("2026-02-03T09:00:00Z"),
                Instant.parse("2026-02-03T12:00:00Z")
        );

        assertThat(result.events()).hasSize(1);
        assertThat(result.events().getFirst().sourceId()).isEqualTo("ts-1");
    }

    @Test
    void expands_recurring_events_and_sorts_all_instances() {
        when(calendarService.ofId("cal-1"))
                .thenReturn(Optional.of(mock(Calendar.class)));

        var recurringRule = new RecurringAvailabilityRuleDto(
                AvailabilityContributorType.FREE_TIMESLOT,
                "ts-r",
                recurringTimingDailyAt10(),
                Set.of(ScheduledItemType.MEETING)
        );

        when(availabilityPreview.query(any(), any(), any()))
                .thenReturn(new AvailabilityPreviewDto(
                        List.of(),
                        List.of(recurringRule)
                ));

        var result = service.getPreview(
                "cal-1",
                Instant.parse("2026-02-03T00:00:00Z"),
                Instant.parse("2026-02-05T00:00:00Z")
        );

        assertThat(result.events()).hasSize(2);
        assertThat(result.events())
                .isSortedAccordingTo(
                        Comparator.comparing(AvailabilityEventInstanceDto::start)
                );
    }

    private CalendarEventTiming recurringTimingDailyAt10() {
        return new RecurringEventTiming(
                LocalDate.of(2026, 2, 3),
                LocalTime.of(10, 0),
                ZoneId.of("UTC"),
                Duration.ofHours(1),
                new Daily(1)
        );
    }
}
