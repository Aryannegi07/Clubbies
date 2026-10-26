package com.clubbies.presidentcup;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface TournamentEditionRepository extends JpaRepository<TournamentEdition, UUID> {
    Optional<TournamentEdition> findFirstByStatusOrderByCreatedAtDesc(String status);
}
