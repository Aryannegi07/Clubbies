package com.clubbies.config;

import com.clubbies.auth.DatabaseUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.AuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Value("${app.frontend-origin}") private String frontendOrigin;

    @Bean PasswordEncoder passwordEncoder() { return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8(); }

    @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception { return configuration.getAuthenticationManager(); }

    @Bean SecurityFilterChain filterChain(HttpSecurity http, DatabaseUserDetailsService users) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .ignoringRequestMatchers("/api/auth/register", "/api/auth/login"))
            .userDetailsService(users)
            .exceptionHandling(exception -> exception
                    .authenticationEntryPoint(jsonAuthenticationEntryPoint())
                    .accessDeniedHandler((request, response, accessDenied) -> {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"status\":403,\"code\":\"FORBIDDEN\",\"message\":\"You do not have permission for this resource.\"}");
                    }))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/health", "/actuator/health", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/clubs/**", "/api/events/**", "/api/recruitment/drives/**", "/api/president-cup/**").permitAll()
                    .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/csrf").permitAll()
                    .anyRequest().authenticated())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).sessionFixation().migrateSession())
            .logout(logout -> logout.logoutUrl("/api/auth/logout").invalidateHttpSession(true).clearAuthentication(true)
                    .deleteCookies("CLUBBIES_SESSION", "JSESSIONID", "XSRF-TOKEN")
                    .logoutSuccessHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_NO_CONTENT)));
        return http.build();
    }

    @Bean AuthenticationEntryPoint jsonAuthenticationEntryPoint() {
        return (request, response, authenticationException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":401,\"code\":\"UNAUTHENTICATED\",\"message\":\"Authentication is required for this resource.\"}");
        };
    }

    @Bean CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(frontendOrigin));
        config.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Content-Type", "X-XSRF-TOKEN", "X-Requested-With"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
