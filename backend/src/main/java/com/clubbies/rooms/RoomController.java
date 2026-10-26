package com.clubbies.rooms;

import com.clubbies.common.NotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/rooms")
public class RoomController {
    private final RoomRepository rooms;
    public RoomController(RoomRepository rooms) { this.rooms = rooms; }
    public record RoomRequest(@NotBlank String name, String roomType, @Min(1) int capacity, String facilities, boolean active) {}
    public record RoomView(UUID id, String name, String roomType, int capacity, String facilities, boolean active) {}
    @GetMapping public List<RoomView> list() { return rooms.findAll().stream().filter(r -> r.active).map(this::view).toList(); }
    @PostMapping @PreAuthorize("hasRole('SUPER_ADMIN')") public RoomView create(@Valid @RequestBody RoomRequest input) { Room r = new Room(); r.name = input.name().trim(); r.roomType = input.roomType(); r.capacity = input.capacity(); r.facilities = input.facilities(); r.active = input.active(); return view(rooms.save(r)); }
    @PatchMapping("/{id}") @PreAuthorize("hasRole('SUPER_ADMIN')") public RoomView update(@PathVariable UUID id, @Valid @RequestBody RoomRequest input) { Room r = rooms.findById(id).orElseThrow(() -> new NotFoundException("Room not found")); r.name = input.name().trim(); r.roomType = input.roomType(); r.capacity = input.capacity(); r.facilities = input.facilities(); r.active = input.active(); return view(rooms.save(r)); }
    private RoomView view(Room r) { return new RoomView(r.id, r.name, r.roomType, r.capacity, r.facilities, r.active); }
}
