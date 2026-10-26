package com.clubbies.recruitment;

import com.clubbies.clubs.ClubCoreAssignmentRepository;
import com.clubbies.common.ConflictException;
import com.clubbies.common.ForbiddenException;
import com.clubbies.common.NotFoundException;
import com.clubbies.users.User;
import com.clubbies.users.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/recruitment")
public class RecruitmentController {
    private final RecruitmentDriveRepository drives; private final RecruitmentApplicationRepository applications; private final UserRepository users; private final ClubCoreAssignmentRepository assignments;
    public RecruitmentController(RecruitmentDriveRepository drives, RecruitmentApplicationRepository applications, UserRepository users, ClubCoreAssignmentRepository assignments) { this.drives = drives; this.applications = applications; this.users = users; this.assignments = assignments; }
    public record DriveRequest(@NotNull UUID clubId, @NotBlank @Size(max = 160) String title, @NotBlank @Size(max = 3000) String description, @NotBlank @Size(max = 1200) String positions, @Size(max = 1800) String eligibility, @NotNull Instant opensAt, @NotNull Instant closesAt, @NotBlank String status) {}
    public record DriveView(UUID id, UUID clubId, String title, String description, String positions, String eligibility, Instant opensAt, Instant closesAt, String status) {}
    public record ApplicationRequest(@NotBlank String answersJson) {}
    public record ApplicationView(UUID id, UUID driveId, UUID applicantId, String answersJson, String status, Instant submittedAt, String reviewerNotes) {}
    @GetMapping("/drives") public List<DriveView> openDrives() { return drives.findByStatusOrderByClosesAtAsc("OPEN").stream().filter(d -> d.closesAt.isAfter(Instant.now())).map(this::view).toList(); }
    @GetMapping("/drives/{id}") public DriveView drive(@PathVariable UUID id) { return view(drives.findById(id).orElseThrow(() -> new NotFoundException("Recruitment drive not found"))); }
    @PostMapping("/drives") @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CLUB_CORE')") public DriveView create(@Valid @RequestBody DriveRequest request, Authentication auth) { access(request.clubId(), auth); if (!request.closesAt().isAfter(request.opensAt())) throw new IllegalArgumentException("Closing time must be after opening time"); RecruitmentDrive d = new RecruitmentDrive(); d.clubId = request.clubId(); d.title = request.title().trim(); d.description = request.description().trim(); d.positions = request.positions(); d.eligibility = request.eligibility(); d.opensAt = request.opensAt(); d.closesAt = request.closesAt(); d.status = request.status(); d.createdBy = current(auth).id; return view(drives.save(d)); }
    @PostMapping("/drives/{id}/applications") @PreAuthorize("isAuthenticated()") public ApplicationView apply(@PathVariable UUID id, @Valid @RequestBody ApplicationRequest request, Authentication auth) { RecruitmentDrive d = drives.findById(id).orElseThrow(() -> new NotFoundException("Recruitment drive not found")); if (!"OPEN".equals(d.status) || Instant.now().isBefore(d.opensAt) || Instant.now().isAfter(d.closesAt)) throw new ConflictException("This recruitment drive is not accepting applications."); User user = current(auth); if (applications.findByDriveIdAndApplicantId(id, user.id).isPresent()) throw new ConflictException("You already applied to this drive."); RecruitmentApplication a = new RecruitmentApplication(); a.driveId = id; a.applicantId = user.id; a.answersJson = request.answersJson(); return view(applications.save(a)); }
    @GetMapping("/applications/me") @PreAuthorize("isAuthenticated()") public List<ApplicationView> mine(Authentication auth) { return applications.findByApplicantIdOrderBySubmittedAtDesc(current(auth).id).stream().map(this::studentView).toList(); }
    @GetMapping("/drives/{id}/applications") @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CLUB_CORE')") public List<ApplicationView> review(@PathVariable UUID id, Authentication auth) { RecruitmentDrive d = drives.findById(id).orElseThrow(() -> new NotFoundException("Recruitment drive not found")); access(d.clubId, auth); return applications.findByDriveIdOrderBySubmittedAtDesc(id).stream().map(this::view).toList(); }
    @PatchMapping("/applications/{id}") @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CLUB_CORE')") public ApplicationView decide(@PathVariable UUID id, @RequestParam String status, @RequestParam(required = false) String reviewerNotes, Authentication auth) { RecruitmentApplication a = applications.findById(id).orElseThrow(() -> new NotFoundException("Application not found")); RecruitmentDrive d = drives.findById(a.driveId).orElseThrow(() -> new NotFoundException("Recruitment drive not found")); access(d.clubId, auth); a.status = status; a.reviewerNotes = reviewerNotes; a.reviewedBy = current(auth).id; a.reviewedAt = Instant.now(); return view(applications.save(a)); }
    private void access(UUID clubId, Authentication auth) { if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"))) return; if (!assignments.existsByIdClubIdAndIdUserId(clubId, current(auth).id)) throw new ForbiddenException("You are not assigned to this club."); }
    private User current(Authentication auth) { return users.findByEmailIgnoreCase(auth.getName()).orElseThrow(() -> new NotFoundException("Account not found")); }
    private DriveView view(RecruitmentDrive d) { return new DriveView(d.id, d.clubId, d.title, d.description, d.positions, d.eligibility, d.opensAt, d.closesAt, d.status); }
    private ApplicationView studentView(RecruitmentApplication a) { return new ApplicationView(a.id, a.driveId, a.applicantId, a.answersJson, a.status, a.submittedAt, null); }
    private ApplicationView view(RecruitmentApplication a) { return new ApplicationView(a.id, a.driveId, a.applicantId, a.answersJson, a.status, a.submittedAt, a.reviewerNotes); }
}
