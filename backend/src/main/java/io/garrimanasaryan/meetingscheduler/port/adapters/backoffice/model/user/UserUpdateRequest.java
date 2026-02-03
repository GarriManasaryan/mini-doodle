package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.user;

import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseUpdateRequest;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.ZoneId;

public record UserUpdateRequest(
        @NotNull String by,
        @Nullable String name,
        @Nullable ZoneId zoneId
) implements BaseUpdateRequest {

}
