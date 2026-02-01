package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.mapper;

import io.garrimanasaryan.meetingscheduler.domain.calendar.Calendar;
import io.garrimanasaryan.meetingscheduler.domain.common.Domain;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseBackofficeModel;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseCreationRequest;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseUpdateRequest;
import jakarta.validation.constraints.NotNull;

public interface BaseMapper<
        B extends BaseBackofficeModel,
        D extends Domain,
        C extends BaseCreationRequest,
        U extends BaseUpdateRequest
        > {
    B toBackofficeModel(@NotNull D entity);
    D toDomain(@NotNull C request);
    D toDomain(@NotNull U request, @NotNull D entity);
    D toDomain(@NotNull String by, @NotNull D entity);

}
