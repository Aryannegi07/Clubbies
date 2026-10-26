package com.clubbies.auth;

import com.clubbies.users.User;
import com.clubbies.users.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final UserRepository users;
    public DatabaseUserDetailsService(UserRepository users) { this.users = users; }
    @Override public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = users.findByEmailIgnoreCase(email).orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
        return org.springframework.security.core.userdetails.User.withUsername(user.email)
                .password(user.passwordHash)
                .authorities(user.roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).toList())
                .disabled(!"ACTIVE".equals(user.status)).build();
    }
}
