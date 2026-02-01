package io.garrimanasaryan.meetingscheduler.domain.calendar.item.definition;

import jakarta.validation.constraints.NotNull;

public record MeetingParticipant(
        @NotNull String scheduledItemId,
        @NotNull String userId,
        @NotNull boolean accepted

) {
}
