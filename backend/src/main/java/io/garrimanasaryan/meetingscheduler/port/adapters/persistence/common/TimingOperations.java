package io.garrimanasaryan.meetingscheduler.port.adapters.persistence.common;

import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Daily;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Monthly;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.RecurrenceRule;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Weekly;
import io.garrimanasaryan.meetingscheduler.domain.common.recurring.Yearly;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.time.MonthDay;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class TimingOperations {

    private final PgJsonOperations jsonOperations;

    public TimingOperations(PgJsonOperations jsonOperations) {
        this.jsonOperations = jsonOperations;
    }

    public String ruleDetailsJson(@NotNull RecurrenceRule rule) {
        return switch (rule) {
            case Daily d -> "{}";
            case Weekly w -> jsonOperations.serialize(Map.of("days", w.dayOfWeeks()));
            case Monthly m -> jsonOperations.serialize(Map.of("dayOfMonth", m.dayOfMonth()));
            case Yearly y -> jsonOperations.serialize(
                    Map.of(
                            "days",
                            y.days().stream()
                                    .map(MonthDay::toString)
                                    .toList()
                    )
            );
        };
    }
}
