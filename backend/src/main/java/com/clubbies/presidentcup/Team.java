package com.clubbies.presidentcup;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity @Table(name = "teams")
public class Team {
    @Id @GeneratedValue(strategy = GenerationType.UUID) public UUID id;
    @Column(name = "competition_id", nullable = false) public UUID competitionId;
    @Column(nullable = false, length = 120) public String name;
    @Column(name = "captain_id", nullable = false) public UUID captainId;
    @Column(nullable = false) public boolean locked = false;
    @ElementCollection @CollectionTable(name = "team_members", joinColumns = @JoinColumn(name = "team_id")) @Column(name = "user_id")
    public Set<UUID> memberIds = new HashSet<>();
}
