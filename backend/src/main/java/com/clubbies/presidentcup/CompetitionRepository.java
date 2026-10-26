package com.clubbies.presidentcup;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CompetitionRepository extends JpaRepository<Competition, UUID> { List<Competition> findBySportIdOrderByNameAsc(UUID sportId); }
