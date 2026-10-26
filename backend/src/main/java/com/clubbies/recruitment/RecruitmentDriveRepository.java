package com.clubbies.recruitment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface RecruitmentDriveRepository extends JpaRepository<RecruitmentDrive, UUID> {
    List<RecruitmentDrive> findByStatusOrderByClosesAtAsc(String status);
    List<RecruitmentDrive> findByClubIdOrderByCreatedAtDesc(UUID clubId);
}
