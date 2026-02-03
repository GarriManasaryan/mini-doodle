package io.garrimanasaryan.meetingscheduler.domain.common;

import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public record TitleDescription(
        @NotNull String title,
        @NotNull Optional<String> description
) {
}
