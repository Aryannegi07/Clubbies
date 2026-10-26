package com.clubbies.presidentcup;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "tournament_registrations", uniqueConstraints = @UniqueConstraint(name = "uq_competition_participant", columnNames = {"competition_id", "participant_id"}))
public class TournamentRegistration {
    @Id @GeneratedValue(strategy = GenerationType.UUID) public UUID id;
    @Column(name = "competition_id", nullable = false) public UUID competitionId;
    @Column(name = "participant_id", nullable = false) public UUID participantId;
    @Column(name = "team_id") public UUID teamId;
    @Column(nullable = false, length = 24) public String status = "SUBMITTED";
    @Column(name = "created_at", nullable = false) public Instant createdAt;
    @PrePersist void createDate() { createdAt = Instant.now(); }
}
