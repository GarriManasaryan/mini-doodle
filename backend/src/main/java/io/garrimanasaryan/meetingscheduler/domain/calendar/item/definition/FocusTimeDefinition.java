package io.garrimanasaryan.meetingscheduler.domain.calendar.item.definition;

import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItemType;
import io.garrimanasaryan.meetingscheduler.domain.common.TitleDescription;
import jakarta.validation.constraints.NotNull;

public record FocusTimeDefinition(
        @NotNull String scheduledItemId,
        @NotNull String organizerUserId,
        @NotNull String focusLink,
        @NotNull TitleDescription titleDescription
) implements ScheduledItemDefinition {

    public ScheduledItemType type() {
        return ScheduledItemType.FOCUS_TIME;
    }

}
