package com.clubbies.bookings;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "booking_requests")
public class BookingRequest {
    @Id @GeneratedValue(strategy = GenerationType.UUID) public UUID id;
    @Column(name = "room_id", nullable = false) public UUID roomId;
    @Column(name = "event_id") public UUID eventId;
    @Column(name = "club_id", nullable = false) public UUID clubId;
    @Column(name = "requested_by", nullable = false) public UUID requestedBy;
    @Column(name = "starts_at", nullable = false) public Instant startsAt;
    @Column(name = "ends_at", nullable = false) public Instant endsAt;
    @Column(nullable = false, length = 24) public String status = "PENDING";
    @Column(length = 1200) public String notes;
    @Column(name = "reviewed_by") public UUID reviewedBy;
    @Column(name = "reviewed_at") public Instant reviewedAt;
    @Column(name = "created_at", nullable = false) public Instant createdAt;
    @PrePersist void createDate() { createdAt = Instant.now(); }
}
