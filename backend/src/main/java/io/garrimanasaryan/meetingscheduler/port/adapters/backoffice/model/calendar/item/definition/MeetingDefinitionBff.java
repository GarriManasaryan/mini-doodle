package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.item.definition;

import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.item.ScheduledItemTypeBff;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.Set;

public record MeetingDefinitionBff(
        @NotNull Set<String> meetingParticipantIds,
        @NotNull Optional<String> zoomLink
) implements ScheduledItemDefinitionBff {

    public ScheduledItemTypeBff type() {
        return ScheduledItemTypeBff.MEETING;
    }

}
