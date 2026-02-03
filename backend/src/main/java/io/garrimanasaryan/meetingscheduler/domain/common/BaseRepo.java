package io.garrimanasaryan.meetingscheduler.domain.common;

import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public interface BaseRepo<D extends Domain> {
    void update(@NotNull D entity);
    Optional<D> ofId(@NotNull String id);
}
