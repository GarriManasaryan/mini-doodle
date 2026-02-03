package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common;

import jakarta.validation.constraints.NotNull;

public interface BaseCreationRequest {
    @NotNull String by();
}