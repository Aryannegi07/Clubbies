package com.clubbies.admin;

import com.clubbies.audit.PlatformAuditLog;
import com.clubbies.audit.PlatformAuditLogRepository;
import com.clubbies.clubs.*;
import com.clubbies.common.NotFoundException;
import com.clubbies.common.RoleName;
import com.clubbies.presidentcup.*;
import com.clubbies.users.User;
import com.clubbies.users.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/admin") @PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminController {
    private final ClubRepository clubs; private final UserRepository users; private final ClubCoreAssignmentRepository assignments; private final TournamentEditionRepository editions; private final SportRepository sports; private final CompetitionRepository competitions; private final PlatformAuditLogRepository audit;
    public AdminController(ClubRepository clubs, UserRepository users, ClubCoreAssignmentRepository assignments, TournamentEditionRepository editions, SportRepository sports, CompetitionRepository competitions, PlatformAuditLogRepository audit) { this.clubs = clubs; this.users = users; this.assignments = assignments; this.editions = editions; this.sports = sports; this.competitions = competitions; this.audit = audit; }
    public record ClubCreate(@NotBlank @Size(max = 80) String name, @Size(max = 180) String fullName, @NotBlank @Size(max = 1800) String description, @Size(max = 80) String category) {}
    public record AssignmentRequest(@NotBlank String email) {}
    public record EditionRequest(@NotBlank String name, String introduction, String rules, Instant registrationOpensAt, Instant registrationClosesAt, @NotBlank String status) {}
    public record SportRequest(@NotBlank String name, String description) {}
    public record CompetitionRequest(@NotBlank String name, @NotBlank String participationType, Integer minTeamSize, Integer maxTeamSize, Integer participantLimit, Instant opensAt, Instant closesAt, @NotBlank String status) {}
    public record ClubView(UUID id, String name, String fullName, String description, String category, boolean active) {}
    public record AuditView(UUID id, UUID actorId, String action, String targetType, UUID targetId, String outcome, Instant createdAt) {}
    @PostMapping("/clubs") public ClubView createClub(@Valid @RequestBody ClubCreate input, Authentication auth) { Club c = new Club(); c.name = input.name().trim(); c.fullName = input.fullName(); c.description = input.description().trim(); c.category = input.category(); Club saved = clubs.save(c); log(auth, "CLUB_CREATED", "CLUB", saved.id); return new ClubView(saved.id, saved.name, saved.fullName, saved.description, saved.category, saved.active); }
    @PostMapping("/clubs/{clubId}/core-members") public void assign(@PathVariable UUID clubId, @Valid @RequestBody AssignmentRequest input, Authentication auth) { Club club = clubs.findById(clubId).orElseThrow(() -> new NotFoundException("Club not found")); User user = users.findByEmailIgnoreCase(input.email()).orElseThrow(() -> new NotFoundException("User not found")); user.roles.add(RoleName.CLUB_CORE); users.save(user); if (!assignments.existsByIdClubIdAndIdUserId(clubId, user.id)) assignments.save(new ClubCoreAssignment(club.id, user.id, current(auth).id)); log(auth, "CORE_MEMBER_ASSIGNED", "CLUB", clubId); }
    @DeleteMapping("/clubs/{clubId}/core-members/{userId}") public void revoke(@PathVariable UUID clubId, @PathVariable UUID userId, Authentication auth) { assignments.deleteById(new ClubCoreAssignment.Key(clubId, userId)); log(auth, "CORE_MEMBER_REVOKED", "CLUB", clubId); }
    @PostMapping("/president-cup/editions") public TournamentEdition createEdition(@Valid @RequestBody EditionRequest input, Authentication auth) { TournamentEdition e = new TournamentEdition(); e.name = input.name(); e.introduction = input.introduction(); e.rules = input.rules(); e.registrationOpensAt = input.registrationOpensAt(); e.registrationClosesAt = input.registrationClosesAt(); e.status = input.status(); TournamentEdition saved = editions.save(e); log(auth, "TOURNAMENT_EDITION_CREATED", "EDITION", saved.id); return saved; }
    @PostMapping("/president-cup/editions/{editionId}/sports") public Sport createSport(@PathVariable UUID editionId, @Valid @RequestBody SportRequest input, Authentication auth) { editions.findById(editionId).orElseThrow(() -> new NotFoundException("Edition not found")); Sport s = new Sport(); s.editionId = editionId; s.name = input.name(); s.description = input.description(); Sport saved = sports.save(s); log(auth, "SPORT_CREATED", "SPORT", saved.id); return saved; }
    @PostMapping("/president-cup/sports/{sportId}/competitions") public Competition createCompetition(@PathVariable UUID sportId, @Valid @RequestBody CompetitionRequest input, Authentication auth) { sports.findById(sportId).orElseThrow(() -> new NotFoundException("Sport not found")); Competition c = new Competition(); c.sportId = sportId; c.name = input.name(); c.participationType = input.participationType(); c.minTeamSize = input.minTeamSize(); c.maxTeamSize = input.maxTeamSize(); c.participantLimit = input.participantLimit(); c.opensAt = input.opensAt(); c.closesAt = input.closesAt(); c.status = input.status(); Competition saved = competitions.save(c); log(auth, "COMPETITION_CREATED", "COMPETITION", saved.id); return saved; }
    @GetMapping("/audit-logs") public List<AuditView> auditLogs() { return audit.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt")).stream().map(l -> new AuditView(l.id, l.actorId, l.action, l.targetType, l.targetId, l.outcome, l.createdAt)).toList(); }
    private User current(Authentication auth) { return users.findByEmailIgnoreCase(auth.getName()).orElseThrow(() -> new NotFoundException("Account not found")); }
    private void log(Authentication auth, String action, String type, UUID target) { PlatformAuditLog l = new PlatformAuditLog(); l.actorId = current(auth).id; l.action = action; l.targetType = type; l.targetId = target; l.outcome = "SUCCESS"; audit.save(l); }
}
