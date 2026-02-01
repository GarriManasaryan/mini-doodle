package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.resource;

import io.garrimanasaryan.meetingscheduler.application.CalendarService;
import io.garrimanasaryan.meetingscheduler.domain.calendar.Calendar;
import io.garrimanasaryan.meetingscheduler.domain.repo.CalendarRepo;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.mapper.CalendarMapper;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.CalendarBackofficeModel;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.CalendarCreationRequest;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.CalendarUpdateRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    public CalendarController(CalendarService service, CalendarMapper mapper) {
        super(service, mapper);
    }
}
