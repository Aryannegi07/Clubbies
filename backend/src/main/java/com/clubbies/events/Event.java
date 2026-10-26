package com.clubbies.events;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "events")
public class Event {
    @Id @GeneratedValue(strategy = GenerationType.UUID) public UUID id;
    @Column(name = "club_id", nullable = false) public UUID clubId;
    @Column(nullable = false, length = 180) public String title;
    @Column(nullable = false, length = 4000) public String description;
    @Column(length = 80) public String category;
    @Column(name = "starts_at", nullable = false) public Instant startsAt;
    @Column(name = "ends_at", nullable = false) public Instant endsAt;
    @Column(length = 180) public String venue;
    @Column(name = "expected_attendance") public Integer expectedAttendance;
    @Column(name = "cover_image_url", length = 1000) public String coverImageUrl;
    @Column(name = "registration_capacity") public Integer registrationCapacity;
    @Column(nullable = false, length = 24) public String status = "DRAFT";
    @Column(name = "approval_status", nullable = false, length = 24) public String approvalStatus = "PENDING";
    @Column(name = "created_by", nullable = false) public UUID createdBy;
    @Column(name = "created_at", nullable = false) public Instant createdAt;
    @Column(name = "updated_at", nullable = false) public Instant updatedAt;
    @PrePersist void createDates() { createdAt = updatedAt = Instant.now(); }
    @PreUpdate void updateDate() { updatedAt = Instant.now(); }
}
