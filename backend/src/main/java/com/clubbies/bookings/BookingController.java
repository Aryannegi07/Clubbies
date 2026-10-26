package com.clubbies.bookings;

import com.clubbies.clubs.ClubCoreAssignmentRepository;
import com.clubbies.common.ConflictException;
import com.clubbies.common.ForbiddenException;
import com.clubbies.common.NotFoundException;
import com.clubbies.rooms.Room;
import com.clubbies.rooms.RoomRepository;
import com.clubbies.rooms.RoomReservation;
import com.clubbies.rooms.RoomReservationRepository;
import com.clubbies.users.User;
import com.clubbies.users.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/bookings")
public class BookingController {
    private final BookingRequestRepository bookings; private final RoomRepository rooms; private final RoomReservationRepository reservations; private final UserRepository users; private final ClubCoreAssignmentRepository assignments;
    public BookingController(BookingRequestRepository bookings, RoomRepository rooms, RoomReservationRepository reservations, UserRepository users, ClubCoreAssignmentRepository assignments) { this.bookings = bookings; this.rooms = rooms; this.reservations = reservations; this.users = users; this.assignments = assignments; }
    public record BookingRequestInput(@NotNull UUID roomId, @NotNull UUID clubId, UUID eventId, @NotNull Instant startsAt, @NotNull Instant endsAt, String notes) {}
    public record BookingView(UUID id, UUID roomId, UUID clubId, UUID eventId, Instant startsAt, Instant endsAt, String status, String notes) {}
    public record RoomAvailability(UUID id, String name, String roomType, int capacity, String facilities) {}
    @GetMapping("/availability") public List<RoomAvailability> availability(@RequestParam Instant startsAt, @RequestParam Instant endsAt, @RequestParam(defaultValue = "0") int capacity) { if (!endsAt.isAfter(startsAt)) throw new IllegalArgumentException("End time must be after start time."); return rooms.findByActiveTrueAndCapacityGreaterThanEqualOrderByCapacityAsc(capacity).stream().filter(r -> !reservations.existsByRoomIdAndStartsAtLessThanAndEndsAtGreaterThan(r.id, endsAt, startsAt)).map(r -> new RoomAvailability(r.id, r.name, r.roomType, r.capacity, r.facilities)).toList(); }
    @PostMapping @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CLUB_CORE')") public BookingView request(@Valid @RequestBody BookingRequestInput input, Authentication auth) { access(input.clubId(), auth); Room room = rooms.findById(input.roomId()).orElseThrow(() -> new NotFoundException("Room not found")); if (room.capacity < 1) throw new ConflictException("Room is not usable."); if (!input.endsAt().isAfter(input.startsAt())) throw new IllegalArgumentException("End time must be after start time."); BookingRequest b = new BookingRequest(); b.roomId = input.roomId(); b.clubId = input.clubId(); b.eventId = input.eventId(); b.requestedBy = current(auth).id; b.startsAt = input.startsAt(); b.endsAt = input.endsAt(); b.notes = input.notes(); return view(bookings.save(b)); }
    @GetMapping("/mine") @PreAuthorize("isAuthenticated()") public List<BookingView> mine(Authentication auth) { return bookings.findByRequestedByOrderByCreatedAtDesc(current(auth).id).stream().map(this::view).toList(); }
    @GetMapping("/pending") @PreAuthorize("hasRole('SUPER_ADMIN')") public List<BookingView> pending() { return bookings.findByStatusOrderByCreatedAtAsc("PENDING").stream().map(this::view).toList(); }
    @PostMapping("/{id}/approve") @PreAuthorize("hasRole('SUPER_ADMIN')") @Transactional public BookingView approve(@PathVariable UUID id, Authentication auth) { BookingRequest b = bookings.findById(id).orElseThrow(() -> new NotFoundException("Booking request not found")); if (!"PENDING".equals(b.status)) throw new ConflictException("Booking request is already decided."); if (reservations.existsByRoomIdAndStartsAtLessThanAndEndsAtGreaterThan(b.roomId, b.endsAt, b.startsAt)) throw new ConflictException("The room is no longer available for this time."); RoomReservation r = new RoomReservation(); r.roomId = b.roomId; r.bookingRequestId = b.id; r.startsAt = b.startsAt; r.endsAt = b.endsAt; reservations.saveAndFlush(r); b.status = "APPROVED"; b.reviewedBy = current(auth).id; b.reviewedAt = Instant.now(); return view(bookings.save(b)); }
    @PostMapping("/{id}/reject") @PreAuthorize("hasRole('SUPER_ADMIN')") public BookingView reject(@PathVariable UUID id, Authentication auth) { BookingRequest b = bookings.findById(id).orElseThrow(() -> new NotFoundException("Booking request not found")); b.status = "REJECTED"; b.reviewedBy = current(auth).id; b.reviewedAt = Instant.now(); return view(bookings.save(b)); }
    private void access(UUID clubId, Authentication auth) { if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"))) return; if (!assignments.existsByIdClubIdAndIdUserId(clubId, current(auth).id)) throw new ForbiddenException("You are not assigned to this club."); }
    private User current(Authentication auth) { return users.findByEmailIgnoreCase(auth.getName()).orElseThrow(() -> new NotFoundException("Account not found")); }
    private BookingView view(BookingRequest b) { return new BookingView(b.id, b.roomId, b.clubId, b.eventId, b.startsAt, b.endsAt, b.status, b.notes); }
}
