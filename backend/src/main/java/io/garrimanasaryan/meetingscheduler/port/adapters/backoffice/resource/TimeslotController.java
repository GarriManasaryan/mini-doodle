package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.resource;

import io.garrimanasaryan.meetingscheduler.application.TimeslotService;
import io.garrimanasaryan.meetingscheduler.domain.calendar.timeslot.TimeSlot;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.mapper.TimeslotMapper;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.timeslot.TimeSlotBackofficeModel;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.timeslot.TimeSlotCreationRequest;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.timeslot.TimeSlotUpdateRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backoffice/timeslots")
public class TimeslotController extends BaseController<
        TimeSlot,
        TimeslotService,
        TimeSlotBackofficeModel,
        TimeSlotCreationRequest,
        TimeSlotUpdateRequest,
        TimeslotMapper
        > {

    public TimeslotController(TimeslotService service, TimeslotMapper mapper) {
        super(service, mapper);
    }
}
