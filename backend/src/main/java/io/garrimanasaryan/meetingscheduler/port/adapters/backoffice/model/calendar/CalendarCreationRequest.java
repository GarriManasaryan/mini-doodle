package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar;

import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseCreationRequest;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.TitleDescriptionBackofficeModel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CalendarCreationRequest(
        @NotNull String by,
        @NotNull String managedByUserId,
        @NotNull String subjectUserId,
        @Valid @NotNull TitleDescriptionBackofficeModel titleDescription,
        @NotNull CalendarTypeBackoffice type,
        @Valid @NotNull List<WorkingHourBackofficeModel> workingHours,
        @NotNull boolean allowOverlap
) implements BaseCreationRequest {
}
