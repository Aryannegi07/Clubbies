package com.clubbies.clubs;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "clubs")
public class Club {
    @Id @GeneratedValue(strategy = GenerationType.UUID) public UUID id;
    @Column(nullable = false, unique = true, length = 80) public String name;
    @Column(name = "full_name", length = 180) public String fullName;
    @Column(nullable = false, length = 1800) public String description;
    @Column(length = 80) public String category;
    @Column(name = "logo_url", length = 1000) public String logoUrl;
    @Column(name = "contact_email", length = 320) public String contactEmail;
    @Column(name = "recruitment_open", nullable = false) public boolean recruitmentOpen = false;
    @Column(nullable = false) public boolean active = true;
    @Column(name = "created_at", nullable = false) public Instant createdAt;
    @Column(name = "updated_at", nullable = false) public Instant updatedAt;
    @PrePersist void createDates() { createdAt = updatedAt = Instant.now(); }
    @PreUpdate void updateDate() { updatedAt = Instant.now(); }
}
