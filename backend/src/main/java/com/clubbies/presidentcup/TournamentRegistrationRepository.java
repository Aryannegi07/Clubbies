package com.clubbies.presidentcup;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TournamentRegistrationRepository extends JpaRepository<TournamentRegistration, UUID> {
    Optional<TournamentRegistration> findByCompetitionIdAndParticipantId(UUID competitionId, UUID participantId);
    List<TournamentRegistration> findByParticipantIdOrderByCreatedAtDesc(UUID participantId);
}
