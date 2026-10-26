package com.clubbies.presidentcup;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "tournament_editions")
public class TournamentEdition {
    @Id @GeneratedValue(strategy = GenerationType.UUID) public UUID id;
    @Column(nullable = false, unique = true, length = 160) public String name;
    @Column(length = 4000) public String introduction;
    @Column(length = 4000) public String rules;
    @Column(name = "registration_opens_at") public Instant registrationOpensAt;
    @Column(name = "registration_closes_at") public Instant registrationClosesAt;
    @Column(nullable = false, length = 24) public String status = "DRAFT";
    @Column(name = "created_at", nullable = false) public Instant createdAt;
    @PrePersist void createDate() { createdAt = Instant.now(); }
}
