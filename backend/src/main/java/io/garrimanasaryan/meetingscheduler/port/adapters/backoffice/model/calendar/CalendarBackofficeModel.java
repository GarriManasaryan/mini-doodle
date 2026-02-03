package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar;

import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseBackofficeModel;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.MetadataBackofficeModel;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CalendarBackofficeModel(
        @NotNull String id,
        @NotNull String managedByUserId,
        @NotNull String subjectUserId,
        @NotNull String title,
        @Nullable String description,
        @NotNull CalendarTypeBackoffice type,
        @NotNull List<WorkingHourBackofficeModel> workingHours,
        @NotNull MetadataBackofficeModel metadata
) implements BaseBackofficeModel {
}
