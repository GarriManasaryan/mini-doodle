package io.garrimanasaryan.meetingscheduler.domain.calendar.item.definition;

import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItemType;
import io.garrimanasaryan.meetingscheduler.domain.common.TitleDescription;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.Set;

public record MeetingDefinition(
        @NotNull String scheduledItemId,
        @NotNull String organizerUserId,
        @NotNull Set<String> meetingParticipantIds,
        @NotNull TitleDescription titleDescription,
        @NotNull Optional<String> zoomLink
) implements ScheduledItemDefinition {

    public ScheduledItemType type() {
        return ScheduledItemType.MEETING;
    }

}
