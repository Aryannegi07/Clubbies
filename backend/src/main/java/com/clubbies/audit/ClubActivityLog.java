package com.clubbies.audit;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "club_activity_logs")
public class ClubActivityLog {
    @Id @GeneratedValue(strategy = GenerationType.UUID) public UUID id;
    @Column(name = "club_id", nullable = false) public UUID clubId;
    @Column(name = "actor_id", nullable = false) public UUID actorId;
    @Column(nullable = false, length = 100) public String action;
    @Column(name = "target_id") public UUID targetId;
    @Column(name = "created_at", nullable = false) public Instant createdAt;
    @PrePersist void createDate() { createdAt = Instant.now(); }
}
