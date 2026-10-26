package com.clubbies.memberships;

import com.clubbies.clubs.ClubCoreAssignmentRepository;
import com.clubbies.common.ForbiddenException;
import com.clubbies.common.NotFoundException;
import com.clubbies.users.User;
import com.clubbies.users.UserRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/memberships")
public class MembershipController {
    private final MembershipRepository memberships; private final UserRepository users; private final ClubCoreAssignmentRepository assignments;
    public MembershipController(MembershipRepository memberships, UserRepository users, ClubCoreAssignmentRepository assignments) { this.memberships = memberships; this.users = users; this.assignments = assignments; }
    public record MembershipView(UUID id, UUID userId, UUID clubId, String status, Instant appliedAt, Instant decidedAt) {}
    public record Decision(@NotBlank String status) {}
    @GetMapping("/me") @PreAuthorize("isAuthenticated()") public List<MembershipView> mine(Authentication auth) { return memberships.findByUserIdOrderByAppliedAtDesc(current(auth).id).stream().map(this::view).toList(); }
    @GetMapping("/clubs/{clubId}") @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CLUB_CORE')") public List<MembershipView> byClub(@PathVariable UUID clubId, Authentication auth) { requireClubAccess(clubId, auth); return memberships.findByClubIdOrderByAppliedAtDesc(clubId).stream().map(this::view).toList(); }
    @PatchMapping("/{id}/decision") @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CLUB_CORE')") public MembershipView decide(@PathVariable UUID id, @RequestBody Decision decision, Authentication auth) { Membership m = memberships.findById(id).orElseThrow(() -> new NotFoundException("Membership application not found")); requireClubAccess(m.clubId, auth); if (!List.of("APPROVED", "REJECTED", "INACTIVE").contains(decision.status())) throw new IllegalArgumentException("Unsupported membership status"); m.status = decision.status(); m.decidedAt = Instant.now(); m.decidedBy = current(auth).id; return view(memberships.save(m)); }
    private void requireClubAccess(UUID clubId, Authentication auth) { if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"))) return; if (!assignments.existsByIdClubIdAndIdUserId(clubId, current(auth).id)) throw new ForbiddenException("You are not assigned to this club."); }
    private User current(Authentication auth) { return users.findByEmailIgnoreCase(auth.getName()).orElseThrow(() -> new NotFoundException("Account not found")); }
    private MembershipView view(Membership m) { return new MembershipView(m.id, m.userId, m.clubId, m.status, m.appliedAt, m.decidedAt); }
}
