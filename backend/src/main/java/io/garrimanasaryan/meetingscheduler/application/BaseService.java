package io.garrimanasaryan.meetingscheduler.application;

import io.garrimanasaryan.meetingscheduler.domain.common.BaseRepo;
import io.garrimanasaryan.meetingscheduler.domain.common.Domain;
import io.garrimanasaryan.meetingscheduler.domain.exception.NotFoundException;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.function.Function;

public abstract class BaseService<D extends Domain, R extends BaseRepo<D>> {

    protected final R repo;

    public BaseService(R repo) {
        this.repo = repo;
    }

    // almost always have custom validations
    public abstract void save(@NotNull D entity);

    public Optional<D> ofId(@NotNull String id) {
        return repo.ofId(id);
    }

    public void update(@NotNull String id, @NotNull Function<D, D> mapper) {
        repo.ofId(id).ifPresentOrElse(
                existing -> repo.update(mapper.apply(existing)),
                () -> {
                    throw new NotFoundException("Entity by provided id not found: " + id);
                }

        );
    }
}
