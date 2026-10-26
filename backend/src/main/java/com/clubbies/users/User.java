package com.clubbies.users;

import com.clubbies.common.RoleName;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity @Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.UUID) public UUID id;
    @Column(nullable = false, unique = true, length = 320) public String email;
    @Column(name = "password_hash", nullable = false) public String passwordHash;
    @Column(name = "display_name", nullable = false, length = 120) public String displayName;
    @Column(nullable = false, length = 24) public String status = "ACTIVE";
    @ElementCollection(fetch = FetchType.EAGER) @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING) @Column(name = "role", nullable = false, length = 24)
    public Set<RoleName> roles = new HashSet<>();
    @Column(name = "created_at", nullable = false) public Instant createdAt;
    @Column(name = "updated_at", nullable = false) public Instant updatedAt;
    @PrePersist void createDates() { createdAt = updatedAt = Instant.now(); }
    @PreUpdate void updateDate() { updatedAt = Instant.now(); }
    public boolean hasRole(RoleName role) { return roles.contains(role); }
}
