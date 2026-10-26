package com.clubbies.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ClubActivityLogRepository extends JpaRepository<ClubActivityLog, UUID> { List<ClubActivityLog> findByClubIdOrderByCreatedAtDesc(UUID clubId); }
