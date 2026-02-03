package io.garrimanasaryan.meetingscheduler.domain.common.recurring;

import jakarta.validation.constraints.NotNull;

public sealed interface RecurrenceRule permits Daily, Weekly, Monthly, Yearly {

    @NotNull int every();
    @NotNull RecurrenceFrequency frequency();

}
