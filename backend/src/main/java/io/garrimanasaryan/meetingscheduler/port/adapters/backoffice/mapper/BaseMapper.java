package io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.mapper;

import io.garrimanasaryan.meetingscheduler.domain.calendar.Calendar;
import io.garrimanasaryan.meetingscheduler.domain.common.Domain;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Daily;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Monthly;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Weekly;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Yearly;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.CalendarEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.RecurringEventTiming;
import io.garrimanasaryan.meetingscheduler.domain.common.timing.SingleEventTiming;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseBackofficeModel;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseCreationRequest;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.BaseUpdateRequest;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.recurring.DailyBff;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.recurring.MonthlyBff;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.recurring.WeeklyBff;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.recurring.YearlyBff;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.timing.CalendarEventTimingBff;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.timing.RecurringEventTimingBff;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.common.timing.SingleEventTimingBff;
import jakarta.validation.constraints.NotNull;

public interface BaseMapper<
        B extends BaseBackofficeModel,
        D extends Domain,
        C extends BaseCreationRequest,
        U extends BaseUpdateRequest
        > {
    B toBackofficeModel(@NotNull D entity);
    D toDomain(@NotNull C request);
    D toDomain(@NotNull U request, @NotNull D entity);
    D toDomain(@NotNull String by, @NotNull D entity);

    default CalendarEventTiming toTimingDomain(@NotNull CalendarEventTimingBff x){
        return switch (x){
            case SingleEventTimingBff single -> new SingleEventTiming(
                    single.startAt(),
                    single.duration()
            );
            case RecurringEventTimingBff recurring -> new RecurringEventTiming(
                    recurring.startDate(),
                    recurring.startTime(),
                    recurring.zoneId(),
                    recurring.duration(),
                    switch (recurring.ruleDetails()){
                        case DailyBff d -> new Daily(d.every());
                        case WeeklyBff w -> new Weekly(w.every(), w.dayOfWeeks());
                        case MonthlyBff m -> new Monthly(m.every(), m.dayOfMonth());
                        case YearlyBff y -> new Yearly(y.every(), y.days());
                    }
            );
        };
    }

    default CalendarEventTimingBff toTimingBff(@NotNull CalendarEventTiming x){
        return switch (x){
            case SingleEventTiming single -> new SingleEventTimingBff(
                    single.startAt(),
                    single.duration()
            );
            case RecurringEventTiming recurring -> new RecurringEventTimingBff(
                    recurring.startDate(),
                    recurring.startTime(),
                    recurring.zoneId(),
                    recurring.duration(),
                    switch (recurring.recurrenceRule()){
                        case Daily d -> new DailyBff(d.every());
                        case Weekly w -> new WeeklyBff(w.every(), w.dayOfWeeks());
                        case Monthly m -> new MonthlyBff(m.every(), m.dayOfMonth());
                        case Yearly y -> new YearlyBff(y.every(), y.days());
                    }
            );
        };
    }

}
