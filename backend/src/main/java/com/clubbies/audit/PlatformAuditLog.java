package com.clubbies.audit;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "platform_audit_logs")
public class PlatformAuditLog {
    @Id @GeneratedValue(strategy = GenerationType.UUID) public UUID id;
    @Column(name = "actor_id") public UUID actorId;
    @Column(nullable = false, length = 100) public String action;
    @Column(name = "target_type", length = 80) public String targetType;
    @Column(name = "target_id") public UUID targetId;
    @Column(nullable = false, length = 24) public String outcome;
    @Column(name = "created_at", nullable = false) public Instant createdAt;
    @PrePersist void createDate() { createdAt = Instant.now(); }
}
