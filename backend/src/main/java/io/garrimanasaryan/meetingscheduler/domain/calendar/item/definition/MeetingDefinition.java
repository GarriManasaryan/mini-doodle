package io.garrimanasaryan.meetingscheduler.domain.calendar.item.definition;

import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItemType;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.Set;

public record MeetingDefinition(
        @NotNull Set<String> meetingParticipantIds,
        @NotNull Optional<String> zoomLink
) implements ScheduledItemDefinition {

    public ScheduledItemType type() {
        return ScheduledItemType.MEETING;
    }

}
