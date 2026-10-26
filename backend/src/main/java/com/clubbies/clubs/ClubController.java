package com.clubbies.clubs;

import com.clubbies.common.ConflictException;
import com.clubbies.common.NotFoundException;
import com.clubbies.memberships.Membership;
import com.clubbies.memberships.MembershipRepository;
import com.clubbies.users.User;
import com.clubbies.users.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/clubs")
public class ClubController {
    private final ClubRepository clubs; private final MembershipRepository memberships; private final UserRepository users; private final ClubCoreAssignmentRepository assignments;
    public ClubController(ClubRepository clubs, MembershipRepository memberships, UserRepository users, ClubCoreAssignmentRepository assignments) { this.clubs = clubs; this.memberships = memberships; this.users = users; this.assignments = assignments; }
    public record ClubResponse(UUID id, String name, String fullName, String description, String category, String logoUrl, String contactEmail, boolean recruitmentOpen, boolean active) {}
    public record ClubUpdate(@NotBlank @Size(max = 80) String name, @Size(max = 180) String fullName, @NotBlank @Size(max = 1800) String description, @Size(max = 80) String category, @Size(max = 1000) String logoUrl, @Email String contactEmail, boolean recruitmentOpen, boolean active) {}
    public record MembershipResponse(UUID id, UUID clubId, String status) {}

    @GetMapping public List<ClubResponse> list(@RequestParam(required = false) String search, @RequestParam(required = false) String category) {
        String q = search == null ? "" : search.trim().toLowerCase();
        return clubs.findByActiveTrueOrderByNameAsc().stream().filter(c -> q.isBlank() || c.name.toLowerCase().contains(q) || c.description.toLowerCase().contains(q)).filter(c -> category == null || category.isBlank() || category.equalsIgnoreCase(c.category)).map(this::response).toList();
    }
    @GetMapping("/{id}") public ClubResponse get(@PathVariable UUID id) { return response(clubs.findById(id).orElseThrow(() -> new NotFoundException("Club not found"))); }
    @PostMapping("/{id}/membership-applications") @PreAuthorize("isAuthenticated()")
    public MembershipResponse apply(@PathVariable UUID id, Authentication authentication) {
        User user = current(authentication); clubs.findById(id).orElseThrow(() -> new NotFoundException("Club not found"));
        Membership membership = memberships.findByUserIdAndClubId(user.id, id).orElseGet(() -> { Membership m = new Membership(); m.userId = user.id; m.clubId = id; return m; });
        if ("APPROVED".equals(membership.status) || "PENDING".equals(membership.status)) throw new ConflictException("You already have an active membership application for this club.");
        membership.status = "PENDING"; membership.decidedAt = null; membership.decidedBy = null; Membership saved = memberships.save(membership); return new MembershipResponse(saved.id, saved.clubId, saved.status);
    }
    @PatchMapping("/{id}") @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CLUB_CORE')")
    public ClubResponse update(@PathVariable UUID id, @Valid @RequestBody ClubUpdate input, Authentication auth) {
        Club club = clubs.findById(id).orElseThrow(() -> new NotFoundException("Club not found"));
        if (auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN")) && !assignments.existsByIdClubIdAndIdUserId(id, current(auth).id)) throw new com.clubbies.common.ForbiddenException("You are not assigned to this club.");
        club.name = input.name().trim(); club.fullName = input.fullName(); club.description = input.description().trim(); club.category = input.category(); club.logoUrl = input.logoUrl(); club.contactEmail = input.contactEmail(); club.recruitmentOpen = input.recruitmentOpen(); if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"))) club.active = input.active(); return response(clubs.save(club));
    }
    private User current(Authentication authentication) { return users.findByEmailIgnoreCase(authentication.getName()).orElseThrow(() -> new NotFoundException("Account not found")); }
    private ClubResponse response(Club c) { return new ClubResponse(c.id, c.name, c.fullName, c.description, c.category, c.logoUrl, c.contactEmail, c.recruitmentOpen, c.active); }
}
