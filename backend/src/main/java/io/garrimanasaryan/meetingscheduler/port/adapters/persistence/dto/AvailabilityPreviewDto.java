package io.garrimanasaryan.meetingscheduler.port.adapters.persistence.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AvailabilityPreviewDto(
        @NotNull List<SingleAvailabilityEventDto> singleEvents,
        @NotNull List<RecurringAvailabilityRuleDto> recurringRules
) {}

