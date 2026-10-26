package com.clubbies.presidentcup;

import com.clubbies.common.ConflictException;
import com.clubbies.common.ForbiddenException;
import com.clubbies.common.NotFoundException;
import com.clubbies.users.User;
import com.clubbies.users.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController @RequestMapping("/api/president-cup/teams") @PreAuthorize("isAuthenticated()")
public class TeamController {
    private final TeamRepository teams; private final CompetitionRepository competitions; private final TeamInvitationRepository invitations; private final UserRepository users;
    public TeamController(TeamRepository teams, CompetitionRepository competitions, TeamInvitationRepository invitations, UserRepository users) { this.teams = teams; this.competitions = competitions; this.invitations = invitations; this.users = users; }
    public record TeamRequest(@NotNull UUID competitionId, @NotBlank String name) {}
    public record InviteRequest(@NotBlank @Email String email) {}
    public record TeamView(UUID id, UUID competitionId, String name, UUID captainId, boolean locked, java.util.Set<UUID> memberIds) {}
    @PostMapping public TeamView create(@Valid @RequestBody TeamRequest input, Authentication auth) { Competition c = competitions.findById(input.competitionId()).orElseThrow(() -> new NotFoundException("Competition not found")); if (!"TEAM".equals(c.participationType)) throw new ConflictException("This competition is for individual registrations."); User user = current(auth); Team t = new Team(); t.competitionId = c.id; t.name = input.name().trim(); t.captainId = user.id; t.memberIds.add(user.id); return view(teams.save(t)); }
    @GetMapping("/{id}") public TeamView get(@PathVariable UUID id) { return view(teams.findById(id).orElseThrow(() -> new NotFoundException("Team not found"))); }
    @PostMapping("/{id}/invitations") public void invite(@PathVariable UUID id, @Valid @RequestBody InviteRequest input, Authentication auth) { Team team = teams.findById(id).orElseThrow(() -> new NotFoundException("Team not found")); User user = current(auth); if (!team.captainId.equals(user.id)) throw new ForbiddenException("Only the team captain can invite members."); User invitee = users.findByEmailIgnoreCase(input.email()).orElseThrow(() -> new NotFoundException("Student account not found")); if (team.memberIds.contains(invitee.id)) throw new ConflictException("That student is already on the team."); if (invitations.findAll().stream().anyMatch(i -> i.teamId.equals(id) && i.inviteeId.equals(invitee.id) && "PENDING".equals(i.status))) throw new ConflictException("An invitation is already pending."); TeamInvitation invitation = new TeamInvitation(); invitation.teamId = id; invitation.inviteeId = invitee.id; invitations.save(invitation); }
    @PostMapping("/{id}/members/{memberId}") public TeamView accept(@PathVariable UUID id, @PathVariable UUID memberId, Authentication auth) { Team team = teams.findById(id).orElseThrow(() -> new NotFoundException("Team not found")); User user = current(auth); if (!user.id.equals(memberId)) throw new ForbiddenException("You can only accept an invitation for yourself."); if (team.locked) throw new ConflictException("This roster is locked."); TeamInvitation invitation = invitations.findAll().stream().filter(i -> i.teamId.equals(id) && i.inviteeId.equals(memberId) && "PENDING".equals(i.status)).findFirst().orElseThrow(() -> new NotFoundException("Invitation not found")); Competition c = competitions.findById(team.competitionId).orElseThrow(() -> new NotFoundException("Competition not found")); if (c.maxTeamSize != null && team.memberIds.size() >= c.maxTeamSize) throw new ConflictException("This team has reached its roster limit."); team.memberIds.add(memberId); invitation.status = "ACCEPTED"; invitations.save(invitation); return view(teams.save(team)); }
    private User current(Authentication auth) { return users.findByEmailIgnoreCase(auth.getName()).orElseThrow(() -> new NotFoundException("Account not found")); }
    private TeamView view(Team t) { return new TeamView(t.id, t.competitionId, t.name, t.captainId, t.locked, t.memberIds); }
}
