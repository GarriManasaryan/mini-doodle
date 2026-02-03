package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.resource;

import io.garrimanasaryan.meetingscheduler.application.TimeslotService;
import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItemType;
import io.garrimanasaryan.meetingscheduler.domain.calendar.timeslot.TimeSlot;
import io.garrimanasaryan.meetingscheduler.domain.common.TitleDescription;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.SingleEventTiming;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.mapper.TimeslotMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class TimeSlotControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TimeslotService timeslotService;

    private TimeslotMapper mapper = new TimeslotMapper();

    @BeforeEach
    void setUp() {
        var controller = new TimeslotController(timeslotService, mapper);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new RestControllerAdvice())
                .build();
    }

    @Test
    void create_timeslot_maps_request_correctly() throws Exception {
        var request = """
        {
          "by": "user-1",
          "calendarId": "calendar-1",
          "title": "Focus",
          "description": "Deep work",
          "allowedScheduledItemType": ["FOCUS_TIME"],
          "isBusyByUser": true,
          "calendarEventTiming": {
            "type": "SINGLE",
            "startAt": "2026-02-10T10:00:00Z",
            "duration": "PT1H"
          }
        }
        """;

        mockMvc.perform(
                        post("/backoffice/timeslots")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk());

        ArgumentCaptor<TimeSlot> captor =
                ArgumentCaptor.forClass(TimeSlot.class);

        verify(timeslotService).save(captor.capture());

        TimeSlot slot = captor.getValue();

        assertThat(slot.calendarId()).isEqualTo("calendar-1");
        assertThat(slot.titleDescription().title()).isEqualTo("Focus");
        assertThat(slot.titleDescription().description())
                .contains("Deep work");
        assertThat(slot.isBusyByUser()).isTrue();
        assertThat(slot.allowedScheduledItemType())
                .containsExactly(ScheduledItemType.FOCUS_TIME);

        assertThat(slot.calendarEventTiming())
                .isInstanceOf(SingleEventTiming.class);
    }


    @Test
    void create_timeslot_fail_on_wrong_type() throws Exception {
        var request = """
        {
          "by": "user-1",
          "calendarId": "calendar-1",
          "title": "Focus",
          "allowedScheduledItemType": ["FOCUS_TIME"],
          "isBusyByUser": false,
          "calendarEventTiming": {
            "type": "type",
            "startAt": "2026-02-10T10:00:00Z",
            "duration": "PT1H"
          }
        }
        """;

        mockMvc.perform(
                        post("/backoffice/timeslots")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(timeslotService);
    }

    @Test
    void get_timeslot_by_id_maps_correctly() throws Exception {
        var timing = new SingleEventTiming(
                OffsetDateTime.parse("2026-02-10T10:00:00Z"),
                Duration.ofHours(1)
        );

        var slot = TimeSlot.of(
                "user-1",
                "calendar-1",
                new TitleDescription("Focus", Optional.of("Deep work")),
                Set.of(ScheduledItemType.FOCUS_TIME),
                false,
                timing
        );

        when(timeslotService.ofId("ts-1"))
                .thenReturn(Optional.of(slot));

        mockMvc.perform(
                        get("/backoffice/timeslots/ts-1")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(slot.id()))
                .andExpect(jsonPath("$.calendarId").value("calendar-1"))
                .andExpect(jsonPath("$.title").value("Focus"))
                .andExpect(jsonPath("$.description").value("Deep work"))
                .andExpect(jsonPath("$.isBusyByUser").value(false))
                .andExpect(jsonPath("$.calendarEventTiming.type").value("SINGLE"))
                .andExpect(jsonPath("$.calendarEventTiming.duration").value("PT1H"));

        verify(timeslotService).ofId("ts-1");
    }

}