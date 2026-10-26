package com.clubbies.events;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByStatusAndStartsAtAfterOrderByStartsAtAsc(String status, Instant from);
    List<Event> findByClubIdOrderByStartsAtDesc(UUID clubId);
}
