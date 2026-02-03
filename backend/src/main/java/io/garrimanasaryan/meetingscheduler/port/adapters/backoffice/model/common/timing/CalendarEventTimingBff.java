package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.timing;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SingleEventTimingBff.class, name = "SINGLE"),
        @JsonSubTypes.Type(value = RecurringEventTimingBff.class, name = "RECURRING"),
})
public sealed interface CalendarEventTimingBff permits SingleEventTimingBff, RecurringEventTimingBff {
    @NotNull Duration duration();
}
