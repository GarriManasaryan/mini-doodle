package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.item.definition;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.item.ScheduledItemTypeBff;
import jakarta.validation.constraints.NotNull;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MeetingDefinitionBff.class, name = "MEETING"),
        @JsonSubTypes.Type(value = FocusTimeDefinitionBff.class, name = "FOCUS_TIME"),
})
public sealed interface ScheduledItemDefinitionBff permits MeetingDefinitionBff, FocusTimeDefinitionBff {
    @NotNull ScheduledItemTypeBff type();
}
