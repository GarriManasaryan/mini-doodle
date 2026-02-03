package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.resource;

import io.garrimanasaryan.meetingscheduler.application.ScheduledItemService;
import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItem;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.mapper.ScheduledItemMapper;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.item.ScheduledItemBff;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.item.ScheduledItemCreationRequest;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.item.ScheduledItemUpdateRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backoffice/scheduled-items")
public class ScheduledItemController extends BaseController<
        ScheduledItem,
        ScheduledItemService,
        ScheduledItemBff,
        ScheduledItemCreationRequest,
        ScheduledItemUpdateRequest,
        ScheduledItemMapper
        > {

    public ScheduledItemController(ScheduledItemService service, ScheduledItemMapper mapper) {
        super(service, mapper);
    }
}
