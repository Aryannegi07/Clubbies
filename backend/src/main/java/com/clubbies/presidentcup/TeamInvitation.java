package com.clubbies.presidentcup;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "team_invitations", uniqueConstraints = @UniqueConstraint(name = "uq_team_invitee", columnNames = {"team_id", "invitee_id"}))
public class TeamInvitation {
    @Id @GeneratedValue(strategy = GenerationType.UUID) public UUID id;
    @Column(name = "team_id", nullable = false) public UUID teamId;
    @Column(name = "invitee_id", nullable = false) public UUID inviteeId;
    @Column(nullable = false, length = 20) public String status = "PENDING";
    @Column(name = "created_at", nullable = false) public Instant createdAt;
    @PrePersist void createDate() { createdAt = Instant.now(); }
}
