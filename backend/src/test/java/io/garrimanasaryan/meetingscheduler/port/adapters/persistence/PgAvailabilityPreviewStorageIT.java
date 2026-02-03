package io.garrimanasaryan.meetingscheduler.port.adapters.persistence;

import io.garrimanasaryan.meetingscheduler.port.adapters.persistence.query.PgAvailabilityPreviewStorage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql({
        "/db/test-fixtures/base.sql",
        "/db/test-fixtures/availability-preview.sql"
})
class PgAvailabilityPreviewStorageIT extends DatabaseBaseTest {

    @Autowired
    PgAvailabilityPreviewStorage storage;

    @Test
    void returns_single_timeslot_in_range() {
        var result = storage.query(
                "calendar-1",
                Instant.parse("2026-02-03T08:00:00Z"),
                Instant.parse("2026-02-03T18:00:00Z")
        );

        assertThat(result.singleEvents()).hasSize(1);
        assertThat(result.singleEvents().getFirst().sourceId()).isEqualTo("ts-1");
    }

    @Test
    void includes_recurring_timeslots_before_range_end() {
        var result = storage.query(
                "calendar-1",
                Instant.parse("2026-02-03T00:00:00Z"),
                Instant.parse("2026-02-10T00:00:00Z")
        );

        assertThat(result.recurringRules()).isNotEmpty();
    }

    @Test
    void excludes_cancelled_scheduled_items() {
        var result = storage.query(
                "calendar-1",
                Instant.parse("2026-02-03T00:00:00Z"),
                Instant.parse("2026-02-05T00:00:00Z")
        );

        assertThat(result.singleEvents())
                .noneMatch(e -> e.sourceId().equals("cancelled-item"));
    }
}

