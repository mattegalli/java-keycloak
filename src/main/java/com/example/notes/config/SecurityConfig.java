package com.example.notes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration for a stateless JWT resource server.
 *
 *  1. HTTP request goes through the SecurityFilterChain.
 *  2. BearerTokenAuthenticationFilter extracts the JWT from the
 *     "Authorization: Bearer <token>" header.
 *  3. NimbusJwtDecoder checks the signature against Keycloak's public keys.
 *  4. JwtAuthenticationConverter maps the token's claims to Spring Security
 *     GrantedAuthority objects so @PreAuthorize checks work.
 *  5. The resulting JwtAuthenticationToekn is stored in the SecurityContext and available via @AuthenticationPrincipal.
 *
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // enables @PreAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // CSRF protection is for cookie sessions, not important for jwt
            .csrf(csrf -> csrf.disable())

            // Every request must carry a valid JWT — there are no public endpoints
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )

            // Activate the JWT resource server filter chain.
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }

    /**
     * Maps Keycloak JWT claims to Spring Security GrantedAuthority objects.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter rolesConverter = new JwtGrantedAuthoritiesConverter();

        // Navigate into realm_access.roles in the Keycloak JWT payload
        rolesConverter.setAuthoritiesClaimName("realm_access.roles");

        // Prefix every role with ROLE_ so Spring's hasRole() checks work correctly
        rolesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(rolesConverter);
        return converter;
    }
}
