package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.mapper;

import io.garrimanasaryan.meetingscheduler.domain.user.User;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.MetadataBackofficeModel;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.user.UserBackofficeModel;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.user.UserCreationRequest;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.user.UserUpdateRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements BaseMapper<
        UserBackofficeModel,
        User,
        UserCreationRequest,
        UserUpdateRequest
        > {

    @Override
    public UserBackofficeModel toBackofficeModel(@NotNull User x) {
        return new UserBackofficeModel(
                x.id(),
                x.name(),
                x.email(),
                x.zoneId(),
                new MetadataBackofficeModel(
                        x.metadata().createdAt(),
                        x.metadata().updatedAt(),
                        x.metadata().createdBy(),
                        x.metadata().updatedBy()
                )
        );
    }

    @Override
    public User toDomain(@NotNull UserCreationRequest x) {
        return User.of(
                x.by(),
                x.name(),
                x.email(),
                x.zoneId()
        );
    }

    @Override
    public User toDomain(@NotNull UserUpdateRequest x, @NotNull User entity) {
        return entity.update(
                x.by(),
                x.name() != null ? x.name() : entity.name(),
                x.zoneId() != null ? x.zoneId() : entity.zoneId()
        );
    }

    @Override
    public User toDomain(String by, User entity) {
        return entity.delete(by);
    }
}
