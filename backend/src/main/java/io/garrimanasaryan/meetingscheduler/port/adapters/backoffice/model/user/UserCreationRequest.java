package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.user;

import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseCreationRequest;
import jakarta.validation.constraints.NotNull;

import java.time.ZoneId;

public record UserCreationRequest(
        @NotNull String by,
        @NotNull String name,
        @NotNull String email,
        @NotNull ZoneId zoneId
) implements BaseCreationRequest {

}
