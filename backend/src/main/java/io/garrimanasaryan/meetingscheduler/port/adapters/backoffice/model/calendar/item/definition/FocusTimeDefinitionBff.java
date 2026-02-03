package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.item.definition;

import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.item.ScheduledItemTypeBff;
import jakarta.validation.constraints.NotNull;

public record FocusTimeDefinitionBff(
        @NotNull String focusLink
) implements ScheduledItemDefinitionBff {

    public ScheduledItemTypeBff type() {
        return ScheduledItemTypeBff.FOCUS_TIME;
    }

}
