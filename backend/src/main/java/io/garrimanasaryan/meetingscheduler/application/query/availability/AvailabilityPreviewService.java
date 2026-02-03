package io.garrimanasaryan.meetingscheduler.application.query.availability;

import io.garrimanasaryan.meetingscheduler.application.CalendarService;
import io.garrimanasaryan.meetingscheduler.domain.exception.NotFoundException;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.query.availability.AvailabilityEventInstanceDto;
import io.garrimanasaryan.meetingscheduler.port.adapters.backoffice.model.query.availability.AvailabilityResultDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static io.garrimanasaryan.meetingscheduler.application.query.availability.RecurrenceExpander.expand;

@Service
public class AvailabilityPreviewService {

    private final AvailabilityPreview availabilityPreview;
    private final CalendarService calendarService;

    public AvailabilityPreviewService(AvailabilityPreview availabilityPreview, CalendarService calendarService) {
        this.availabilityPreview = availabilityPreview;
        this.calendarService = calendarService;
    }

    @Transactional(readOnly = true)
    public AvailabilityResultDto getPreview(
            @NotNull String calendarId,
            @NotNull Instant rangeStart,
            @NotNull Instant rangeEnd
    ){
        calendarService.ofId(calendarId)
                .orElseThrow(() -> new NotFoundException("Calendar by provided ID not found"));

        if (!rangeStart.isBefore(rangeEnd)) {
            throw new IllegalArgumentException("rangeStart must be before rangeEnd");
        }

        var preview = availabilityPreview.query(calendarId, rangeStart, rangeEnd);

        List<EventInstance> instances = new ArrayList<>();

        preview.singleEvents().forEach(e ->
                instances.add(new EventInstance(
                        e.type(),
                        e.sourceId(),
                        e.start(),
                        e.end(),
                        e.allowedScheduledItemTypes()
                ))
        );

        preview.recurringRules().forEach(rule ->
                instances.addAll(expand(rule, rangeStart, rangeEnd))
        );

        instances.sort(Comparator.comparing(EventInstance::start).thenComparing(EventInstance::end));

        List<AvailabilityEventInstanceDto> result =
                instances.stream()
                        .map(e -> new AvailabilityEventInstanceDto(
                                e.type(),
                                e.sourceId(),
                                e.start(),
                                e.end(),
                                e.allowedTypes()
                        ))
                        .toList();

        return new AvailabilityResultDto(rangeStart, rangeEnd, result);

    }

}
