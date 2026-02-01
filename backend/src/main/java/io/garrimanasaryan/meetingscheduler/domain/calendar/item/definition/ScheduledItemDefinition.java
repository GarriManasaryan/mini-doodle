package io.garrimanasaryan.meetingscheduler.domain.calendar.item.definition;

import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItemType;
import io.garrimanasaryan.meetingscheduler.domain.common.TitleDescription;
import jakarta.validation.constraints.NotNull;

public interface ScheduledItemDefinition {
    @NotNull String scheduledItemId();
    @NotNull String organizerUserId();
    @NotNull TitleDescription titleDescription();
    @NotNull ScheduledItemType type();
}
