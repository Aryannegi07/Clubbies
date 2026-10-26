package com.clubbies.presidentcup;

import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name = "sports")
public class Sport {
    @Id @GeneratedValue(strategy = GenerationType.UUID) public UUID id;
    @Column(name = "edition_id", nullable = false) public UUID editionId;
    @Column(nullable = false, length = 120) public String name;
    @Column(length = 1000) public String description;
    @Column(nullable = false) public boolean active = true;
}
