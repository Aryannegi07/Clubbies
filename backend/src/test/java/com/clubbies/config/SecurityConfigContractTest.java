package com.clubbies.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigContractTest {
    @Test
    void unauthenticatedApiRequestsReturnJsonInsteadOfBasicChallenge() throws Exception {
        var handler = new SecurityConfig().jsonAuthenticationEntryPoint();
        var response = new MockHttpServletResponse();
        handler.commence(new MockHttpServletRequest("GET", "/api/me"), response, new org.springframework.security.authentication.BadCredentialsException("missing"));
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getHeader("WWW-Authenticate")).isNull();
        assertThat(response.getContentType()).isEqualTo("application/json");
        assertThat(response.getContentAsString()).contains("UNAUTHENTICATED");
    }
}
