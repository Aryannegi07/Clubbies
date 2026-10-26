package com.clubbies.presidentcup;

import com.clubbies.common.ConflictException;
import com.clubbies.common.NotFoundException;
import com.clubbies.users.User;
import com.clubbies.users.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/president-cup")
public class PresidentCupController {
    private final TournamentEditionRepository editions; private final SportRepository sports; private final CompetitionRepository competitions; private final TournamentRegistrationRepository registrations; private final UserRepository users; private final TeamRepository teams;
    public PresidentCupController(TournamentEditionRepository editions, SportRepository sports, CompetitionRepository competitions, TournamentRegistrationRepository registrations, UserRepository users, TeamRepository teams) { this.editions = editions; this.sports = sports; this.competitions = competitions; this.registrations = registrations; this.users = users; this.teams = teams; }
    public record EditionView(UUID id, String name, String introduction, String rules, Instant registrationOpensAt, Instant registrationClosesAt, String status) {}
    public record SportView(UUID id, UUID editionId, String name, String description) {}
    public record CompetitionView(UUID id, UUID sportId, String name, String participationType, Integer minTeamSize, Integer maxTeamSize, Integer participantLimit, Instant opensAt, Instant closesAt, String status) {}
    public record RegistrationView(UUID id, UUID competitionId, UUID participantId, UUID teamId, String status, Instant createdAt) {}
    public record RegistrationInput(UUID teamId) {}
    @GetMapping("/active") public EditionView active() { return editions.findFirstByStatusOrderByCreatedAtDesc("ACTIVE").map(this::edition).orElse(null); }
    @GetMapping("/editions/{id}") public EditionView edition(@PathVariable UUID id) { return edition(editions.findById(id).orElseThrow(() -> new NotFoundException("Tournament edition not found"))); }
    @GetMapping("/editions/{id}/sports") public List<SportView> sports(@PathVariable UUID id) { return sports.findByEditionIdAndActiveTrueOrderByNameAsc(id).stream().map(s -> new SportView(s.id, s.editionId, s.name, s.description)).toList(); }
    @GetMapping("/sports/{id}/competitions") public List<CompetitionView> competitions(@PathVariable UUID id) { return competitions.findBySportIdOrderByNameAsc(id).stream().map(this::competition).toList(); }
    @PostMapping("/competitions/{id}/registrations") @PreAuthorize("isAuthenticated()") public RegistrationView register(@PathVariable UUID id, @RequestBody RegistrationInput input, Authentication auth) { Competition c = competitions.findById(id).orElseThrow(() -> new NotFoundException("Competition not found")); Instant now = Instant.now(); if (!"OPEN".equals(c.status) || (c.opensAt != null && now.isBefore(c.opensAt)) || (c.closesAt != null && now.isAfter(c.closesAt))) throw new ConflictException("This competition is not accepting registrations."); User user = current(auth); if (registrations.findByCompetitionIdAndParticipantId(id, user.id).isPresent()) throw new ConflictException("You are already registered for this competition."); if ("TEAM".equals(c.participationType)) { if (input.teamId() == null) throw new ConflictException("A team is required for this competition."); Team team = teams.findById(input.teamId()).orElseThrow(() -> new NotFoundException("Team not found")); if (!team.competitionId.equals(id) || !team.memberIds.contains(user.id)) throw new com.clubbies.common.ForbiddenException("You are not a member of that competition team."); if (c.minTeamSize != null && team.memberIds.size() < c.minTeamSize) throw new ConflictException("The team has not reached its minimum roster size."); } TournamentRegistration r = new TournamentRegistration(); r.competitionId = id; r.participantId = user.id; r.teamId = input.teamId(); return view(registrations.save(r)); }
    @GetMapping("/registrations/me") @PreAuthorize("isAuthenticated()") public List<RegistrationView> mine(Authentication auth) { return registrations.findByParticipantIdOrderByCreatedAtDesc(current(auth).id).stream().map(this::view).toList(); }
    private User current(Authentication auth) { return users.findByEmailIgnoreCase(auth.getName()).orElseThrow(() -> new NotFoundException("Account not found")); }
    private EditionView edition(TournamentEdition e) { return new EditionView(e.id, e.name, e.introduction, e.rules, e.registrationOpensAt, e.registrationClosesAt, e.status); }
    private CompetitionView competition(Competition c) { return new CompetitionView(c.id, c.sportId, c.name, c.participationType, c.minTeamSize, c.maxTeamSize, c.participantLimit, c.opensAt, c.closesAt, c.status); }
    private RegistrationView view(TournamentRegistration r) { return new RegistrationView(r.id, r.competitionId, r.participantId, r.teamId, r.status, r.createdAt); }
}
