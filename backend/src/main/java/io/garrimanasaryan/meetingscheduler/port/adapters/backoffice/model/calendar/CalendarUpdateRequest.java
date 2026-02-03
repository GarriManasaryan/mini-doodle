package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar;

import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseUpdateRequest;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CalendarUpdateRequest(
        @NotNull String by,
        @Nullable String title,
        @Nullable String description,
        @Nullable CalendarTypeBackoffice type,
        @Nullable List<WorkingHourBackofficeModel> workingHours
) implements BaseUpdateRequest {
}
