package io.garrimanasaryan.meetingscheduler.domain.user;

import io.garrimanasaryan.meetingscheduler.application.IdGenerator;
import io.garrimanasaryan.meetingscheduler.domain.common.Domain;
import io.garrimanasaryan.meetingscheduler.domain.common.Metadata;
import jakarta.validation.constraints.NotNull;

import java.time.ZoneId;

public record User(
        @NotNull String id,
        @NotNull String name,
        @NotNull String email,
        @NotNull ZoneId zoneId,
        @NotNull Metadata metadata
) implements Domain {

    public static User of(
            @NotNull String by,
            @NotNull String name,
            @NotNull String email,
            @NotNull ZoneId zoneId
    ){
        return new User(
                IdGenerator.generate("usr"),
                name,
                email,
                zoneId,
                Metadata.create(by)
        );
    }

    public User update(
            @NotNull String by,
            @NotNull String name,
            @NotNull ZoneId zoneId
    ){
        return new User(
                id,
                name,
                email,
                zoneId,
                metadata.update(by)
        );
    }

    public User delete(String by) {
        return new User(
                id,
                name,
                email,
                zoneId,
                metadata.delete(by)
        );
    }
}
