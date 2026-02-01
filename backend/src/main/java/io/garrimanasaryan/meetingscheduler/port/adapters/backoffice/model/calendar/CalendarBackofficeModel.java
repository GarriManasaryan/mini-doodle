package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar;

import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseBackofficeModel;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.MetadataBackofficeModel;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.TitleDescriptionBackofficeModel;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CalendarBackofficeModel(
        @NotNull String id,
        @NotNull String managedByUserId,
        @NotNull String subjectUserId,
        @NotNull TitleDescriptionBackofficeModel titleDescription,
        @NotNull CalendarTypeBackoffice type,
        @NotNull List<WorkingHourBackofficeModel> workingHours,
        @NotNull boolean allowOverlap,
        @NotNull MetadataBackofficeModel metadata
) implements BaseBackofficeModel {
}
