package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar;

import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseUpdateRequest;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.TitleDescriptionBackofficeModel;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CalendarUpdateRequest(
        @NotNull String by,
        @Nullable TitleDescriptionBackofficeModel titleDescription,
        @Nullable CalendarTypeBackoffice type,
        @Nullable List<WorkingHourBackofficeModel> workingHours
) implements BaseUpdateRequest {
}
