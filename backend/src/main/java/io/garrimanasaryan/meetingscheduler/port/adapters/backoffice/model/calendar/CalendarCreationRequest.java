package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar;

import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseCreationRequest;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CalendarCreationRequest(
        @NotNull String by,
        @NotNull String managedByUserId,
        @NotNull String subjectUserId,
        @NotNull String title,
        @Nullable String description,
        @NotNull CalendarTypeBackoffice type,
        @Valid @NotNull List<WorkingHourCreationRequest> workingHours
) implements BaseCreationRequest {
}
