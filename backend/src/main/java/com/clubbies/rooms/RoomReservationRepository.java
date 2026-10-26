package com.clubbies.rooms;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.UUID;

public interface RoomReservationRepository extends JpaRepository<RoomReservation, UUID> {
    boolean existsByRoomIdAndStartsAtLessThanAndEndsAtGreaterThan(UUID roomId, Instant endsAt, Instant startsAt);
}
