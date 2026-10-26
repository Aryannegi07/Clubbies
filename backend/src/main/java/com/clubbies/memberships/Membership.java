package com.clubbies.memberships;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "memberships", uniqueConstraints = @UniqueConstraint(name = "uq_membership_user_club", columnNames = {"user_id", "club_id"}))
public class Membership {
    @Id @GeneratedValue(strategy = GenerationType.UUID) public UUID id;
    @Column(name = "user_id", nullable = false) public UUID userId;
    @Column(name = "club_id", nullable = false) public UUID clubId;
    @Column(nullable = false, length = 24) public String status = "PENDING";
    @Column(name = "applied_at", nullable = false) public Instant appliedAt;
    @Column(name = "decided_at") public Instant decidedAt;
    @Column(name = "decided_by") public UUID decidedBy;
    @PrePersist void createDate() { appliedAt = Instant.now(); }
}
