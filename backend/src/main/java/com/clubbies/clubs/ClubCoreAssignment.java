package com.clubbies.clubs;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "club_core_assignments")
public class ClubCoreAssignment {
    @EmbeddedId public Key id;
    @Column(name = "assigned_at", nullable = false) public Instant assignedAt;
    @Column(name = "assigned_by") public UUID assignedBy;
    public ClubCoreAssignment() {}
    public ClubCoreAssignment(UUID clubId, UUID userId, UUID assignedBy) { this.id = new Key(clubId, userId); this.assignedBy = assignedBy; }
    @PrePersist void createDate() { assignedAt = Instant.now(); }
    @Embeddable public static class Key implements java.io.Serializable {
        @Column(name = "club_id") public UUID clubId;
        @Column(name = "user_id") public UUID userId;
        public Key() {}
        public Key(UUID clubId, UUID userId) { this.clubId = clubId; this.userId = userId; }
        @Override public boolean equals(Object o) { return o instanceof Key k && java.util.Objects.equals(clubId, k.clubId) && java.util.Objects.equals(userId, k.userId); }
        @Override public int hashCode() { return java.util.Objects.hash(clubId, userId); }
    }
}
