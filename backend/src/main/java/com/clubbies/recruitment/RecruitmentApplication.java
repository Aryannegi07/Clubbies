package com.clubbies.recruitment;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "recruitment_applications", uniqueConstraints = @UniqueConstraint(name = "uq_recruitment_applicant_drive", columnNames = {"drive_id", "applicant_id"}))
public class RecruitmentApplication {
    @Id @GeneratedValue(strategy = GenerationType.UUID) public UUID id;
    @Column(name = "drive_id", nullable = false) public UUID driveId;
    @Column(name = "applicant_id", nullable = false) public UUID applicantId;
    @Column(name = "answers_json", nullable = false, length = 10000) public String answersJson = "{}";
    @Column(name = "reviewer_notes", length = 4000) public String reviewerNotes;
    @Column(nullable = false, length = 24) public String status = "SUBMITTED";
    @Column(name = "submitted_at", nullable = false) public Instant submittedAt;
    @Column(name = "reviewed_at") public Instant reviewedAt;
    @Column(name = "reviewed_by") public UUID reviewedBy;
    @PrePersist void createDate() { submittedAt = Instant.now(); }
}
