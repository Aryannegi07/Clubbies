package com.clubbies.rooms;

import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name = "rooms")
public class Room {
    @Id @GeneratedValue(strategy = GenerationType.UUID) public UUID id;
    @Column(nullable = false, unique = true, length = 80) public String name;
    @Column(name = "room_type", length = 60) public String roomType;
    @Column(nullable = false) public int capacity;
    @Column(length = 1000) public String facilities;
    @Column(nullable = false) public boolean active = true;
}
