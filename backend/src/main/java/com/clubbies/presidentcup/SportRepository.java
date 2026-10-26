package com.clubbies.presidentcup;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SportRepository extends JpaRepository<Sport, UUID> { List<Sport> findByEditionIdAndActiveTrueOrderByNameAsc(UUID editionId); }
