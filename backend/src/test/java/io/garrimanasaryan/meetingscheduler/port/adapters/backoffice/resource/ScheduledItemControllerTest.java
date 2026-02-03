package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.resource;

import io.garrimanasaryan.meetingscheduler.application.ScheduledItemService;
import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItem;
import io.garrimanasaryan.meetingscheduler.domain.calendar.item.definition.FocusTimeDefinition;
import io.garrimanasaryan.meetingscheduler.domain.common.TitleDescription;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.SingleEventTiming;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.mapper.ScheduledItemMapper;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class ScheduledItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ScheduledItemService scheduledItemService;

    private ScheduledItemMapper mapper = new ScheduledItemMapper();

    @BeforeEach
    void setUp() {
        var controller = new ScheduledItemController(scheduledItemService, mapper);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new RestControllerAdvice())
                .build();
    }

    @Test
    void create_scheduled_item_maps_request_correctly() throws Exception {
        var request = """
                {
                  "by": "user-1",
                  "organizerUserId": "user-1",
                  "calendarId": "calendar-1",
                  "title": "Focus session",
                  "description": "Deep work",
                  "isCancelled": false,
                  "calendarEventTiming": {
                    "type": "SINGLE",
                    "startAt": "2026-02-10T10:00:00Z",
                    "duration": "PT1H"
                  },
                  "itemDetails": {
                    "type": "FOCUS_TIME",
                    "focusLink": "link"
                  }
                }
                """;

        mockMvc.perform(
                        post("/backoffice/scheduled-items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk());

        ArgumentCaptor<ScheduledItem> captor =
                ArgumentCaptor.forClass(ScheduledItem.class);

        verify(scheduledItemService).save(captor.capture());

        ScheduledItem item = captor.getValue();

        assertThat(item.calendarId()).isEqualTo("calendar-1");
        assertThat(item.organizerUserId()).isEqualTo("user-1");
        assertThat(item.titleDescription().title()).isEqualTo("Focus session");
        assertThat(item.titleDescription().description())
                .contains("Deep work");

        assertThat(item.isCancelled()).isFalse();
        assertThat(item.calendarEventTiming())
                .isInstanceOf(SingleEventTiming.class);
    }

    @Test
    void create_scheduled_item_fail_on_invalid_timing_type() throws Exception {
        var request = """
                {
                  "by": "user-1",
                  "organizerUserId": "user-1",
                  "calendarId": "calendar-1",
                  "title": "Broken",
                  "isCancelled": false,
                  "calendarEventTiming": {
                    "type": "INVALID",
                    "startAt": "2026-02-10T10:00:00Z",
                    "duration": "PT1H"
                  },
                  "itemDetails": {
                    "type": "FOCUS_TIME",
                    "focusLink": "link"
                  }
                }
                """;

        mockMvc.perform(
                        post("/backoffice/scheduled-items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(scheduledItemService);
    }

    @Test
    void get_scheduled_item_by_id_maps_correctly() throws Exception {
        var timing = new SingleEventTiming(
                OffsetDateTime.parse("2026-02-10T10:00:00Z"),
                Duration.ofHours(1)
        );

        var item = ScheduledItem.of(
                "user-1",
                "calendar-1",
                "user-1",
                new TitleDescription("Focus", Optional.of("Deep work")),
                timing,
                new FocusTimeDefinition("link"),
                false
        );

        when(scheduledItemService.ofId("sci-1"))
                .thenReturn(Optional.of(item));

        mockMvc.perform(
                        get("/backoffice/scheduled-items/sci-1")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.id()))
                .andExpect(jsonPath("$.calendarId").value("calendar-1"))
                .andExpect(jsonPath("$.organizerUserId").value("user-1"))
                .andExpect(jsonPath("$.title").value("Focus"))
                .andExpect(jsonPath("$.description").value("Deep work"))
                .andExpect(jsonPath("$.isCancelled").value(false))
                .andExpect(jsonPath("$.calendarEventTiming.type").value("SINGLE"))
                .andExpect(jsonPath("$.calendarEventTiming.duration").value("PT1H"));

        verify(scheduledItemService).ofId("sci-1");
    }
}

