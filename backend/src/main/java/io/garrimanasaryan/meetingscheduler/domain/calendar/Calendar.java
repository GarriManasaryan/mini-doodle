package io.garrimanasaryan.meetingscheduler.domain.calendar;

import io.garrimanasaryan.meetingscheduler.application.IdGenerator;
import io.garrimanasaryan.meetingscheduler.domain.common.Domain;
import io.garrimanasaryan.meetingscheduler.domain.common.Metadata;
import io.garrimanasaryan.meetingscheduler.domain.common.TitleDescription;
import io.garrimanasaryan.meetingscheduler.domain.policy.calendar.WorkingHourPolicy;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record Calendar(
        @NotNull String id,
        @NotNull String managedByUserId,
        @NotNull String subjectUserId,
        @NotNull TitleDescription titleDescription,
        @NotNull CalendarType type,
        @NotNull List<WorkingHour> workingHours,
        @NotNull boolean allowOverlap,
        @NotNull Metadata metadata
) implements Domain {

    public static Calendar of(
            @NotNull String by,
            @NotNull String managedByUserId,
            @NotNull String subjectUserId,
            @NotNull TitleDescription titleDescription,
            @NotNull CalendarType type,
            @NotNull List<WorkingHour> workingHours,
            @NotNull boolean allowOverlap
    ) {
        WorkingHourPolicy.validate(workingHours);
        return new Calendar(
                IdGenerator.generate("cld"),
                managedByUserId,
                subjectUserId,
                titleDescription,
                type,
                workingHours,
                allowOverlap,
                Metadata.create(by)
        );
    }

    public Calendar update(
            @NotNull String by,
            @NotNull TitleDescription titleDescription,
            @NotNull CalendarType type,
            @NotNull List<WorkingHour> workingHours
    ) {
        WorkingHourPolicy.validate(workingHours);
        return new Calendar(
                id,
                managedByUserId,
                subjectUserId,
                titleDescription,
                type,
                workingHours,
                allowOverlap,
                metadata.update(by)
        );
    }

    public Calendar delete(@NotNull String by){
        return new Calendar(
                id,
                managedByUserId,
                subjectUserId,
                titleDescription,
                type,
                workingHours,
                allowOverlap,
                metadata.delete(by)
        );
    }

    public boolean myCalendar() {
        return managedByUserId.equals(subjectUserId);
    }

}
