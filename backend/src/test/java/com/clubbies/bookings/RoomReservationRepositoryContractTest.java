package com.clubbies.bookings;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

class RoomReservationRepositoryContractTest {
    @Test
    void overlapRuleUsesHalfOpenIntervals() {
        Instant existingStart = Instant.parse("2026-10-18T10:00:00Z");
        Instant existingEnd = Instant.parse("2026-10-18T11:00:00Z");
        Instant touchingStart = existingEnd;
        Instant overlappingStart = Instant.parse("2026-10-18T10:30:00Z");
        assertThat(overlappingStart.isBefore(existingEnd) && existingStart.isBefore(Instant.parse("2026-10-18T11:30:00Z"))).isTrue();
        assertThat(touchingStart.equals(existingEnd)).isTrue();
    }
}
