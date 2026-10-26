package com.clubbies.auth;

import com.clubbies.common.ConflictException;
import com.clubbies.common.NotFoundException;
import com.clubbies.common.RoleName;
import com.clubbies.users.User;
import com.clubbies.users.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.web.csrf.CsrfToken;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users; private final PasswordEncoder encoder; private final AuthenticationManager auth;
    private final HttpSessionSecurityContextRepository contextRepository = new HttpSessionSecurityContextRepository();
    public AuthController(UserRepository users, PasswordEncoder encoder, AuthenticationManager auth) { this.users = users; this.encoder = encoder; this.auth = auth; }

    public record RegisterRequest(@NotBlank @Email String email, @NotBlank @Size(min = 10, max = 128) String password, @NotBlank @Size(max = 120) String displayName) {}
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}
    public record ProfileResponse(UUID id, String email, String displayName, String status, java.util.Set<RoleName> roles) {}

    @PostMapping("/register")
    public ProfileResponse register(@Valid @RequestBody RegisterRequest request, HttpServletRequest servletRequest, HttpServletResponse response) {
        String email = request.email().trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(email)) throw new ConflictException("An account with this email already exists.");
        User user = new User(); user.email = email; user.displayName = request.displayName().trim(); user.passwordHash = encoder.encode(request.password()); user.roles.add(RoleName.STUDENT);
        User saved = users.save(user);
        authenticate(new LoginRequest(saved.email, request.password()), servletRequest, response);
        return profile(saved);
    }

    @PostMapping("/login")
    public ProfileResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest, HttpServletResponse response) {
        return authenticate(request, servletRequest, response);
    }

    private ProfileResponse authenticate(LoginRequest request, HttpServletRequest servletRequest, HttpServletResponse response) {
        Authentication authentication = auth.authenticate(new UsernamePasswordAuthenticationToken(request.email().trim().toLowerCase(), request.password()));
        SecurityContext context = SecurityContextHolder.createEmptyContext(); context.setAuthentication(authentication); SecurityContextHolder.setContext(context);
        servletRequest.getSession(true); contextRepository.saveContext(context, servletRequest, response);
        User user = users.findByEmailIgnoreCase(request.email()).orElseThrow(() -> new NotFoundException("Account not found"));
        return profile(user);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request) { SecurityContextHolder.clearContext(); HttpSession session = request.getSession(false); if (session != null) session.invalidate(); }

    @GetMapping("/csrf")
    public java.util.Map<String, String> csrf(CsrfToken token) { return java.util.Map.of("token", token.getToken()); }

    @GetMapping("/me")
    public ProfileResponse me(Authentication authentication) { return profile(users.findByEmailIgnoreCase(authentication.getName()).orElseThrow(() -> new NotFoundException("Account not found"))); }

    private ProfileResponse profile(User user) { return new ProfileResponse(user.id, user.email, user.displayName, user.status, user.roles); }
}
