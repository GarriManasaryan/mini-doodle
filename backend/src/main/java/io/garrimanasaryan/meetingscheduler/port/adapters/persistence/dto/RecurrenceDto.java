package io.garrimanasaryan.meetingscheduler.port.adapters.persistence.dto;

import io.garrimanasaryan.meetingscheduler.domain.common.recurring.RecurrenceFrequency;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record RecurrenceDto(
        @NotNull RecurrenceFrequency frequency,
        @NotNull int every,
        Map<String, Object> details
) {
}
