package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.mapper;

import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItem;
import io.garrimanasaryan.meetingscheduler.domain.calendar.item.definition.FocusTimeDefinition;
import io.garrimanasaryan.meetingscheduler.domain.calendar.item.definition.MeetingDefinition;
import io.garrimanasaryan.meetingscheduler.domain.common.TitleDescription;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.item.ScheduledItemBff;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.item.ScheduledItemCreationRequest;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.item.ScheduledItemStatusBff;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.item.ScheduledItemUpdateRequest;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.item.definition.FocusTimeDefinitionBff;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.item.definition.MeetingDefinitionBff;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.MetadataBackofficeModel;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ScheduledItemMapper implements BaseMapper<
        ScheduledItemBff,
        ScheduledItem,
        ScheduledItemCreationRequest,
        ScheduledItemUpdateRequest
        > {

    @Override
    public ScheduledItemBff toBackofficeModel(@NotNull ScheduledItem x) {
        return new ScheduledItemBff(
                x.id(),
                x.calendarId(),
                x.organizerUserId(),
                x.titleDescription().title(),
                x.titleDescription().description().orElse(null),
                ScheduledItemStatusBff.valueOf(x.status().name()),
                toTimingBff(x.calendarEventTiming()),
                switch (x.scheduledItemDefinition()) {
                    case MeetingDefinition m -> new MeetingDefinitionBff(
                            m.meetingParticipantIds(),
                            m.zoomLink()
                    );
                    case FocusTimeDefinition f -> new FocusTimeDefinitionBff(
                            f.focusLink()
                    );
                },
                x.isCancelled(),
                new MetadataBackofficeModel(
                        x.metadata().createdAt(),
                        x.metadata().updatedAt(),
                        x.metadata().createdBy(),
                        x.metadata().updatedBy()
                )
        );
    }

    @Override
    public ScheduledItem toDomain(@NotNull ScheduledItemCreationRequest x) {
        return ScheduledItem.of(
                x.by(),
                x.calendarId(),
                x.organizerUserId(),
                new TitleDescription(x.title(), Optional.ofNullable(x.description())),
                toTimingDomain(x.calendarEventTiming()),
                switch (x.itemDetails()) {
                    case MeetingDefinitionBff m -> new MeetingDefinition(
                            m.meetingParticipantIds(),
                            m.zoomLink()
                    );
                    case FocusTimeDefinitionBff f -> new FocusTimeDefinition(
                            f.focusLink()
                    );
                },
                x.isCancelled()
        );
    }

    @Override
    public ScheduledItem toDomain(@NotNull ScheduledItemUpdateRequest x, @NotNull ScheduledItem entity) {
        return entity.update(
                x.by(),
                x.title() == null && x.description() == null ? entity.titleDescription() :
                        new TitleDescription(
                                x.title() != null ? x.title() : entity.titleDescription().title(),
                                x.description() != null ? Optional.of(x.description()) : entity.titleDescription().description()
                        ),
                x.calendarEventTiming() != null ? toTimingDomain(x.calendarEventTiming()) : entity.calendarEventTiming(),
                x.itemDetails() != null ? switch (x.itemDetails()) {
                    case MeetingDefinitionBff m -> new MeetingDefinition(
                            m.meetingParticipantIds(),
                            m.zoomLink()
                    );
                    case FocusTimeDefinitionBff f -> new FocusTimeDefinition(
                            f.focusLink()
                    );
                } : entity.scheduledItemDefinition(),
                x.isCancelled()
        );
    }

    @Override
    public ScheduledItem toDomain(String by, ScheduledItem entity) {
        return entity.delete(by);
    }
}
