package io.garrimanasaryan.meetingscheduler.domain.calendar.item.definition;

import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItemType;
import jakarta.validation.constraints.NotNull;

public record FocusTimeDefinition(
        @NotNull String focusLink
) implements ScheduledItemDefinition {
    public ScheduledItemType type() {
        return ScheduledItemType.FOCUS_TIME;
    }
}
