package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.item.definition;

import jakarta.validation.constraints.NotNull;

public record MeetingParticipantBff(
        @NotNull String scheduledItemId,
        @NotNull String userId,
        @NotNull boolean accepted

) {
}
