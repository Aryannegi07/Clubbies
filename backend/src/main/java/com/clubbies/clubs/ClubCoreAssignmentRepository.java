package com.clubbies.clubs;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ClubCoreAssignmentRepository extends JpaRepository<ClubCoreAssignment, ClubCoreAssignment.Key> {
    boolean existsByIdClubIdAndIdUserId(UUID clubId, UUID userId);
}
