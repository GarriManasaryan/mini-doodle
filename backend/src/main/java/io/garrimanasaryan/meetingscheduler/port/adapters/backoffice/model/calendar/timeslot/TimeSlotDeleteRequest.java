package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.timeslot;

import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseUpdateRequest;
import jakarta.validation.constraints.NotNull;

public record TimeSlotDeleteRequest(
        @NotNull String by
) implements BaseUpdateRequest {
}
