package com.clubbies.presidentcup;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "competitions")
public class Competition {
    @Id @GeneratedValue(strategy = GenerationType.UUID) public UUID id;
    @Column(name = "sport_id", nullable = false) public UUID sportId;
    @Column(nullable = false, length = 160) public String name;
    @Column(name = "participation_type", nullable = false, length = 16) public String participationType = "INDIVIDUAL";
    @Column(name = "min_team_size") public Integer minTeamSize;
    @Column(name = "max_team_size") public Integer maxTeamSize;
    @Column(name = "participant_limit") public Integer participantLimit;
    @Column(name = "opens_at") public Instant opensAt;
    @Column(name = "closes_at") public Instant closesAt;
    @Column(nullable = false, length = 24) public String status = "DRAFT";
}
