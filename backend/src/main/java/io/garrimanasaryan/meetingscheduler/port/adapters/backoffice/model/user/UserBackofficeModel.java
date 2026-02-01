package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.user;

import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseBackofficeModel;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.MetadataBackofficeModel;
import jakarta.validation.constraints.NotNull;

import java.time.ZoneId;

public record UserBackofficeModel(
        @NotNull String id,
        @NotNull String name,
        @NotNull String email,
        @NotNull ZoneId zoneId,
        @NotNull MetadataBackofficeModel metadata
) implements BaseBackofficeModel {

}
