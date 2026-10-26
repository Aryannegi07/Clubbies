package com.clubbies.auth;

import com.clubbies.common.RoleName;
import com.clubbies.users.User;
import com.clubbies.users.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminBootstrapRunner implements CommandLineRunner {
    private final UserRepository users; private final PasswordEncoder encoder;
    @Value("${app.admin-bootstrap.email:}") private String email;
    @Value("${app.admin-bootstrap.password:}") private String password;
    @Value("${app.admin-bootstrap.name:Platform Administrator}") private String name;
    public AdminBootstrapRunner(UserRepository users, PasswordEncoder encoder) { this.users = users; this.encoder = encoder; }
    @Override @Transactional public void run(String... args) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) return;
        String normalized = email.trim().toLowerCase();
        User user = users.findByEmailIgnoreCase(normalized).orElseGet(() -> { User u = new User(); u.email = normalized; u.displayName = name; u.passwordHash = encoder.encode(password); return u; });
        user.roles.add(RoleName.SUPER_ADMIN); user.status = "ACTIVE"; users.save(user);
    }
}
