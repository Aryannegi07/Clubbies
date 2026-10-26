package com.clubbies.notifications;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "notifications")
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.UUID) public UUID id;
    @Column(name = "user_id", nullable = false) public UUID userId;
    @Column(nullable = false, length = 180) public String title;
    @Column(nullable = false, length = 1200) public String message;
    @Column(name = "target_url", length = 500) public String targetUrl;
    @Column(name = "read_at") public Instant readAt;
    @Column(name = "created_at", nullable = false) public Instant createdAt;
    @PrePersist void createDate() { createdAt = Instant.now(); }
}
