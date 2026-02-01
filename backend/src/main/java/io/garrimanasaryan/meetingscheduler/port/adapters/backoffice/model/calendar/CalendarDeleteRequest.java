package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar;

import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseUpdateRequest;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.TitleDescriptionBackofficeModel;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CalendarDeleteRequest(
        @NotNull String by
) implements BaseUpdateRequest {
}
