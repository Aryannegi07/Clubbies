package com.clubbies.notifications;

import com.clubbies.common.NotFoundException;
import com.clubbies.users.User;
import com.clubbies.users.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/notifications") @PreAuthorize("isAuthenticated()")
public class NotificationController {
    private final NotificationRepository notifications; private final UserRepository users;
    public NotificationController(NotificationRepository notifications, UserRepository users) { this.notifications = notifications; this.users = users; }
    public record NotificationView(UUID id, String title, String message, String targetUrl, Instant readAt, Instant createdAt) {}
    @GetMapping public List<NotificationView> list(Authentication auth) { return notifications.findByUserIdOrderByCreatedAtDesc(current(auth).id).stream().map(this::view).toList(); }
    @PostMapping("/{id}/read") public NotificationView read(@PathVariable UUID id, Authentication auth) { Notification n = notifications.findById(id).orElseThrow(() -> new NotFoundException("Notification not found")); if (!n.userId.equals(current(auth).id)) throw new NotFoundException("Notification not found"); n.readAt = Instant.now(); return view(notifications.save(n)); }
    private User current(Authentication auth) { return users.findByEmailIgnoreCase(auth.getName()).orElseThrow(() -> new NotFoundException("Account not found")); }
    private NotificationView view(Notification n) { return new NotificationView(n.id, n.title, n.message, n.targetUrl, n.readAt, n.createdAt); }
}
