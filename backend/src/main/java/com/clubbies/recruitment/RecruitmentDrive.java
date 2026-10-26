package com.clubbies.recruitment;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "recruitment_drives")
public class RecruitmentDrive {
    @Id @GeneratedValue(strategy = GenerationType.UUID) public UUID id;
    @Column(name = "club_id", nullable = false) public UUID clubId;
    @Column(nullable = false, length = 160) public String title;
    @Column(nullable = false, length = 3000) public String description;
    @Column(name = "positions", nullable = false, length = 1200) public String positions;
    @Column(name = "eligibility", length = 1800) public String eligibility;
    @Column(name = "opens_at", nullable = false) public Instant opensAt;
    @Column(name = "closes_at", nullable = false) public Instant closesAt;
    @Column(nullable = false, length = 24) public String status = "DRAFT";
    @Column(name = "created_by", nullable = false) public UUID createdBy;
    @Column(name = "created_at", nullable = false) public Instant createdAt;
    @Column(name = "updated_at", nullable = false) public Instant updatedAt;
    @PrePersist void createDates() { createdAt = updatedAt = Instant.now(); }
    @PreUpdate void updateDate() { updatedAt = Instant.now(); }
}
