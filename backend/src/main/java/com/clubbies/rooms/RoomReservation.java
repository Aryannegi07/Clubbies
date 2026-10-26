package com.clubbies.rooms;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "room_reservations")
public class RoomReservation {
    @Id @GeneratedValue(strategy = GenerationType.UUID) public UUID id;
    @Column(name = "room_id", nullable = false) public UUID roomId;
    @Column(name = "booking_request_id", nullable = false, unique = true) public UUID bookingRequestId;
    @Column(name = "starts_at", nullable = false) public Instant startsAt;
    @Column(name = "ends_at", nullable = false) public Instant endsAt;
}
