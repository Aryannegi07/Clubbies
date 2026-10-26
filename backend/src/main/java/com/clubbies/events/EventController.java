package com.clubbies.events;

import com.clubbies.clubs.ClubCoreAssignmentRepository;
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

@RestController @RequestMapping("/api/events")
public class EventController {
    private final EventRepository events; private final UserRepository users; private final ClubCoreAssignmentRepository assignments;
    public EventController(EventRepository events, UserRepository users, ClubCoreAssignmentRepository assignments) { this.events = events; this.users = users; this.assignments = assignments; }
    public record EventRequest(@NotNull UUID clubId, @NotBlank @Size(max = 180) String title, @NotBlank @Size(max = 4000) String description, @Size(max = 80) String category, @NotNull Instant startsAt, @NotNull Instant endsAt, @Size(max = 180) String venue, Integer expectedAttendance, @Size(max = 1000) String coverImageUrl, Integer registrationCapacity) {}
    public record EventView(UUID id, UUID clubId, String title, String description, String category, Instant startsAt, Instant endsAt, String venue, Integer expectedAttendance, String coverImageUrl, Integer registrationCapacity, String status, String approvalStatus) {}
    @GetMapping public List<EventView> list(@RequestParam(required = false) UUID clubId) { return (clubId == null ? events.findByStatusAndStartsAtAfterOrderByStartsAtAsc("PUBLISHED", Instant.now().minusSeconds(3600)) : events.findByClubIdOrderByStartsAtDesc(clubId).stream().filter(e -> "PUBLISHED".equals(e.status)).toList()).stream().map(this::view).toList(); }
    @GetMapping("/{id}") public EventView get(@PathVariable UUID id) { Event e = events.findById(id).orElseThrow(() -> new NotFoundException("Event not found")); if (!"PUBLISHED".equals(e.status)) throw new NotFoundException("Event not found"); return view(e); }
    @PostMapping @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CLUB_CORE')") public EventView create(@Valid @RequestBody EventRequest input, Authentication auth) { access(input.clubId(), auth); validateTime(input.startsAt(), input.endsAt()); Event e = new Event(); e.clubId = input.clubId(); e.title = input.title().trim(); e.description = input.description().trim(); e.category = input.category(); e.startsAt = input.startsAt(); e.endsAt = input.endsAt(); e.venue = input.venue(); e.expectedAttendance = input.expectedAttendance(); e.coverImageUrl = input.coverImageUrl(); e.registrationCapacity = input.registrationCapacity(); e.createdBy = current(auth).id; e.status = "SUBMITTED"; return view(events.save(e)); }
    @PatchMapping("/{id}") @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CLUB_CORE')") public EventView update(@PathVariable UUID id, @Valid @RequestBody EventRequest input, Authentication auth) { Event e = events.findById(id).orElseThrow(() -> new NotFoundException("Event not found")); access(e.clubId, auth); validateTime(input.startsAt(), input.endsAt()); e.title = input.title().trim(); e.description = input.description().trim(); e.category = input.category(); e.startsAt = input.startsAt(); e.endsAt = input.endsAt(); e.venue = input.venue(); e.expectedAttendance = input.expectedAttendance(); e.coverImageUrl = input.coverImageUrl(); e.registrationCapacity = input.registrationCapacity(); return view(events.save(e)); }
    @PostMapping("/{id}/submit") @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CLUB_CORE')") public EventView submit(@PathVariable UUID id, Authentication auth) { Event e = events.findById(id).orElseThrow(() -> new NotFoundException("Event not found")); access(e.clubId, auth); e.status = "SUBMITTED"; return view(events.save(e)); }
    @PostMapping("/{id}/approve") @PreAuthorize("hasRole('SUPER_ADMIN')") public EventView approve(@PathVariable UUID id) { Event e = events.findById(id).orElseThrow(() -> new NotFoundException("Event not found")); e.status = "PUBLISHED"; e.approvalStatus = "APPROVED"; return view(events.save(e)); }
    @PostMapping("/{id}/reject") @PreAuthorize("hasRole('SUPER_ADMIN')") public EventView reject(@PathVariable UUID id) { Event e = events.findById(id).orElseThrow(() -> new NotFoundException("Event not found")); e.status = "REJECTED"; e.approvalStatus = "REJECTED"; return view(events.save(e)); }
    private void validateTime(Instant starts, Instant ends) { if (!ends.isAfter(starts)) throw new IllegalArgumentException("Event end time must be after start time."); }
    private void access(UUID clubId, Authentication auth) { if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"))) return; if (!assignments.existsByIdClubIdAndIdUserId(clubId, current(auth).id)) throw new ForbiddenException("You are not assigned to this club."); }
    private User current(Authentication auth) { return users.findByEmailIgnoreCase(auth.getName()).orElseThrow(() -> new NotFoundException("Account not found")); }
    private EventView view(Event e) { return new EventView(e.id, e.clubId, e.title, e.description, e.category, e.startsAt, e.endsAt, e.venue, e.expectedAttendance, e.coverImageUrl, e.registrationCapacity, e.status, e.approvalStatus); }
}
