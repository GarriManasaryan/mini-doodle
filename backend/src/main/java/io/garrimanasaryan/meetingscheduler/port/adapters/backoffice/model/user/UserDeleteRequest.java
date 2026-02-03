package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.user;

import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseUpdateRequest;
import jakarta.validation.constraints.NotNull;

public record UserDeleteRequest(
        @NotNull String by
) implements BaseUpdateRequest {

}
