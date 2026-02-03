package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.item;

import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseUpdateRequest;
import jakarta.validation.constraints.NotNull;

public record ScheduledItemDeleteRequest(
        @NotNull String by
) implements BaseUpdateRequest {


}
