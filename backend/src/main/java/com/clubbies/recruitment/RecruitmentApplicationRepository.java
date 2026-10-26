package com.clubbies.recruitment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecruitmentApplicationRepository extends JpaRepository<RecruitmentApplication, UUID> {
    Optional<RecruitmentApplication> findByDriveIdAndApplicantId(UUID driveId, UUID applicantId);
    List<RecruitmentApplication> findByApplicantIdOrderBySubmittedAtDesc(UUID applicantId);
    List<RecruitmentApplication> findByDriveIdOrderBySubmittedAtDesc(UUID driveId);
}
