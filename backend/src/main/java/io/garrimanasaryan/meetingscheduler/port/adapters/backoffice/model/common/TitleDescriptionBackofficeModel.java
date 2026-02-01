package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common;

import jakarta.annotation.Nullable;

public record TitleDescriptionBackofficeModel(
        @Nullable String title,
        @Nullable String description
) {
}
