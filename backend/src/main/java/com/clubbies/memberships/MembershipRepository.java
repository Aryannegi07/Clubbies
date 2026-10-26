package com.clubbies.memberships;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembershipRepository extends JpaRepository<Membership, UUID> {
    Optional<Membership> findByUserIdAndClubId(UUID userId, UUID clubId);
    List<Membership> findByUserIdOrderByAppliedAtDesc(UUID userId);
    List<Membership> findByClubIdOrderByAppliedAtDesc(UUID clubId);
}
