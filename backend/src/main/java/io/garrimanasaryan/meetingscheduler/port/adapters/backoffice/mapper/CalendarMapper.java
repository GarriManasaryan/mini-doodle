package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.mapper;

import io.garrimanasaryan.meetingscheduler.domain.calendar.Calendar;
import io.garrimanasaryan.meetingscheduler.domain.calendar.CalendarType;
import io.garrimanasaryan.meetingscheduler.domain.calendar.WorkingHour;
import io.garrimanasaryan.meetingscheduler.domain.common.TitleDescription;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.CalendarBackofficeModel;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.CalendarCreationRequest;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.CalendarTypeBackoffice;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.CalendarUpdateRequest;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.calendar.WorkingHourBackofficeModel;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.MetadataBackofficeModel;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CalendarMapper implements BaseMapper<
        CalendarBackofficeModel,
        Calendar,
        CalendarCreationRequest,
        CalendarUpdateRequest
        > {

    @Override
    public CalendarBackofficeModel toBackofficeModel(@NotNull Calendar x) {
        return new CalendarBackofficeModel(
                x.id(),
                x.managedByUserId(),
                x.subjectUserId(),
                x.titleDescription().title(),
                x.titleDescription().description().orElse(null),
                CalendarTypeBackoffice.valueOf(x.type().name()),
                x.workingHours().stream().map(
                        w -> new WorkingHourBackofficeModel(
                                w.dayOfWeek(),
                                w.startAt(),
                                w.duration()
                        )
                ).toList(),
                new MetadataBackofficeModel(
                        x.metadata().createdAt(),
                        x.metadata().updatedAt(),
                        x.metadata().createdBy(),
                        x.metadata().updatedBy()
                )
        );
    }

    @Override
    public Calendar toDomain(@NotNull CalendarCreationRequest x) {
        return Calendar.of(
                x.by(),
                x.managedByUserId(),
                x.subjectUserId(),
                new TitleDescription(
                    x.title(),
                    Optional.ofNullable(x.description())
                ),
                CalendarType.valueOf(x.type().name()),
                x.workingHours().stream().map(
                        w -> new WorkingHour(
                                w.dayOfWeek(),
                                w.startAt(),
                                w.duration()
                        )
                ).toList()
        );
    }

    @Override
    public Calendar toDomain(@NotNull CalendarUpdateRequest x, @NotNull Calendar calendar) {
        return calendar.update(
                x.by(),
                x.title() == null && x.description() == null ? calendar.titleDescription() :
                        new TitleDescription(
                        x.title() != null ? x.title() : calendar.titleDescription().title(),
                        x.description() != null ? Optional.of(x.description()) : calendar.titleDescription().description()
                ),
                x.type() != null ? CalendarType.valueOf(x.type().name()) : calendar.type(),
                x.workingHours() != null ? x.workingHours().stream().map(
                        w -> new WorkingHour(
                                w.dayOfWeek(),
                                w.startAt(),
                                w.duration()
                        )
                ).toList() : calendar.workingHours()
        );
    }

    @Override
    public Calendar toDomain(String by, Calendar entity) {
        return entity.delete(by);
    }
}
