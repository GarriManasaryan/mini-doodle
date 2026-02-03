package io.garrimanasaryan.meetingscheduler.domain.calendar.item.definition;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItemType;
import jakarta.validation.constraints.NotNull;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MeetingDefinition.class, name = "MEETING"),
        @JsonSubTypes.Type(value = FocusTimeDefinition.class, name = "FOCUS_TIME"),
})
public sealed interface ScheduledItemDefinition permits MeetingDefinition, FocusTimeDefinition{
    @NotNull ScheduledItemType type();
}
