package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.resource;

import io.garrimanasaryan.meetingscheduler.application.BaseService;
import io.garrimanasaryan.meetingscheduler.domain.common.BaseRepo;
import io.garrimanasaryan.meetingscheduler.domain.common.Domain;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.mapper.BaseMapper;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseBackofficeModel;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseCreationRequest;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

public abstract class BaseController<
        D extends Domain,
        S extends BaseService<D, ?>,
        B extends BaseBackofficeModel,
        C extends BaseCreationRequest,
        U extends BaseUpdateRequest,
        M extends BaseMapper<B, D, C, U>
        > {

    protected final S service;
    protected final M mapper;

    public BaseController(S service, M mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<B> all(@RequestParam(name = "userId") @NotNull String userId) {
        return service.all(userId).stream()
                .map(mapper::toBackofficeModel)
                .toList();
    }

    @PostMapping
    public void save(@Valid @RequestBody @NotNull C request) {
        service.save(mapper.toDomain(request));
    }

    @PutMapping("/{id}")
    public void update(
            @PathVariable(name = "id") @NotNull String id,
            @Valid @RequestBody @NotNull U request
    ) {
        service.update(id, existing -> mapper.toDomain(request, existing));
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable(name = "id") @NotNull String id,
            @RequestParam(name = "by") @NotNull String by
    ) {
        service.update(id, existing -> mapper.toDomain(by, existing));
    }


    @GetMapping("/{id}")
    public Optional<B> ofId(@PathVariable(name = "id") @NotNull String id) {
        return service.ofId(id).map(mapper::toBackofficeModel);
    }

}
