package io.garrimanasaryan.meetingscheduler.domain.common;

import jakarta.validation.constraints.NotNull;

public interface Domain {
    @NotNull String id();
    @NotNull Metadata metadata();

}
