package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.mapper;

import io.garrimanasaryan.meetingscheduler.domain.calendar.item.ScheduledItemType;
import io.garrimanasaryan.meetingscheduler.domain.calendar.timeslot.TimeSlot;
import io.garrimanasaryan.meetingscheduler.domain.common.TitleDescription;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.timeslot.ScheduledItemTypeBff;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.timeslot.TimeSlotBackofficeModel;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.timeslot.TimeSlotCreationRequest;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.timeslot.TimeSlotUpdateRequest;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.MetadataBackofficeModel;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TimeslotMapper implements BaseMapper<
        TimeSlotBackofficeModel,
        TimeSlot,
        TimeSlotCreationRequest,
        TimeSlotUpdateRequest
        > {

    @Override
    public TimeSlotBackofficeModel toBackofficeModel(@NotNull TimeSlot x) {
        return new TimeSlotBackofficeModel(
                x.id(),
                x.calendarId(),
                x.titleDescription().title(),
                x.titleDescription().description().orElse(null),
                x.allowedScheduledItemType().stream()
                        .map(y -> ScheduledItemTypeBff.valueOf(y.name()))
                        .collect(Collectors.toSet()),
                x.isBusyByUser(),
                toTimingBff(x.calendarEventTiming()),
                new MetadataBackofficeModel(
                        x.metadata().createdAt(),
                        x.metadata().updatedAt(),
                        x.metadata().createdBy(),
                        x.metadata().updatedBy()
                )
        );
    }

    @Override
    public TimeSlot toDomain(@NotNull TimeSlotCreationRequest x) {
        return TimeSlot.of(
                x.by(),
                x.calendarId(),
                new TitleDescription(
                        x.title(),
                        Optional.ofNullable(x.description())
                ),
                x.allowedScheduledItemType().stream()
                        .map(y -> ScheduledItemType.valueOf(y.name()))
                        .collect(Collectors.toSet()),
                x.isBusyByUser(),
                toTimingDomain(x.calendarEventTiming())
        );
    }

    @Override
    public TimeSlot toDomain(@NotNull TimeSlotUpdateRequest x, @NotNull TimeSlot entity) {
        return entity.update(
                x.by(),
                x.title() == null && x.description() == null ? entity.titleDescription() :
                    new TitleDescription(
                            x.title() != null ? x.title() : entity.titleDescription().title(),
                            x.description() != null ? Optional.of(x.description()) : entity.titleDescription().description()
                ),
                x.allowedScheduledItemType() != null ? x.allowedScheduledItemType().stream()
                        .map(y -> ScheduledItemType.valueOf(y.name()))
                        .collect(Collectors.toSet())
                : entity.allowedScheduledItemType(),
                x.isBusyByUser(),
                x.calendarEventTiming() != null ? toTimingDomain(x.calendarEventTiming()) : entity.calendarEventTiming()
        );
    }

    @Override
    public TimeSlot toDomain(String by, TimeSlot entity) {
        return entity.delete(by);
    }
}
