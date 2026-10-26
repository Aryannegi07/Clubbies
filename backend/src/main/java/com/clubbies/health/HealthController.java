package com.clubbies.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.Instant;

@RestController @RequestMapping("/api/health")
public class HealthController {
    @GetMapping public Object health() { return java.util.Map.of("status", "ok", "service", "clubbies-api", "time", Instant.now()); }
}
