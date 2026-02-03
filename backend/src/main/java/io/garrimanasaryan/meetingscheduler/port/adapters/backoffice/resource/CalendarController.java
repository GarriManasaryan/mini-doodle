package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.resource;

import io.garrimanasaryan.meetingscheduler.application.CalendarService;
import io.garrimanasaryan.meetingscheduler.application.query.availability.AvailabilityPreviewService;
import io.garrimanasaryan.meetingscheduler.domain.calendar.Calendar;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.mapper.CalendarMapper;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.CalendarBackofficeModel;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.CalendarCreationRequest;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.CalendarUpdateRequest;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.query.availability.AvailabilityResultDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/backoffice/calendars")
public class CalendarController extends BaseController<
        Calendar,
        CalendarService,
        CalendarBackofficeModel,
        CalendarCreationRequest,
        CalendarUpdateRequest,
        CalendarMapper
        > {

    private final AvailabilityPreviewService availabilityPreviewService;

    public CalendarController(
            CalendarService service,
            CalendarMapper mapper,
            AvailabilityPreviewService availabilityPreviewService
    ) {
        super(service, mapper);
        this.availabilityPreviewService = availabilityPreviewService;
    }


    @GetMapping("/{calendarId}/availability")
    public AvailabilityResultDto get(
            @PathVariable @NotNull String calendarId,
            @RequestParam("from") @NotNull Instant from,
            @RequestParam("to") @NotNull Instant to
    ) {
        return availabilityPreviewService.getPreview(calendarId, from, to);
    }

}
