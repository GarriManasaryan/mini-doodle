package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.recurring;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "frequency",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DailyBff.class, name = "DAILY"),
        @JsonSubTypes.Type(value = WeeklyBff.class, name = "WEEKLY"),
        @JsonSubTypes.Type(value = MonthlyBff.class, name = "MONTHLY"),
        @JsonSubTypes.Type(value = YearlyBff.class, name = "YEARLY")
})
public sealed interface RecurrenceRuleBff permits DailyBff, WeeklyBff, MonthlyBff, YearlyBff {

    @NotNull @Positive int every();
    @NotNull RecurrenceFrequencyBff frequency();

}
