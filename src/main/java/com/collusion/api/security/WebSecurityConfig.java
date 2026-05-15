package com.collusion.api.security;

import com.collusion.api.security.jwt.AuthEntryPointJwt;
import com.collusion.api.security.jwt.AuthTokenFilter;
import com.collusion.api.security.jwt.JwtUtils;
import com.collusion.api.security.services.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Central Spring Security configuration — Spring Security 6 / Spring Boot 3 style.
 *
 * Key architectural decisions:
 *
 * 1. NO WebSecurityConfigurerAdapter.
 *    That class was deprecated in Spring Security 5.7 and removed in 6.0.
 *    The modern approach is to expose @Bean methods that return specific
 *    security objects.  This is more composable and easier to test.
 *
 * 2. SecurityFilterChain @Bean instead of configure(HttpSecurity).
 *    Multiple SecurityFilterChain beans can coexist (useful for separate
 *    API vs actuator security).
 *
 * 3. @EnableMethodSecurity (not @EnableGlobalMethodSecurity).
 *    @EnableGlobalMethodSecurity was deprecated in Spring Security 5.8
 *    and removed in 6.x. @EnableMethodSecurity defaults prePostEnabled=true
 *    and uses AuthorizationManager under the hood (more flexible, supports
 *    custom authorization logic without subclassing).
 *
 * 4. Lambda DSL throughout (e.g. csrf(AbstractHttpConfigurer::disable)).
 *    The old method-chaining style (.and().csrf().disable()) is deprecated.
 *    Lambda DSL is cleaner and the compiler can type-check each configurer.
 *
 * 5. CSRF disabled.
 *    Correct for stateless REST APIs using Bearer tokens.  CSRF protection
 *    guards cookie/session-based flows.  Since we never issue cookies and
 *    every request must include an explicit Authorization header (which
 *    cross-origin scripts cannot set on behalf of the user), disabling CSRF
 *    is safe here.
 *
 * 6. SessionCreationPolicy.STATELESS.
 *    No HttpSession is created or consulted.  Authentication state lives
 *    exclusively in the JWT, which is what makes horizontal scaling trivial.
 *
 * 7. AuthTokenFilter registered via addFilterBefore.
 *    It runs before UsernamePasswordAuthenticationFilter so the
 *    SecurityContext is populated by the time Spring Security checks
 *    authorization rules.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity          // enables @PreAuthorize / @PostAuthorize
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPointJwt      unauthorizedHandler;
    private final JwtUtils               jwtUtils;

    /** Instantiate our filter here (not as @Component) so Spring doesn't
     *  register it in the default filter chain BEFORE our explicit placement. */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(jwtUtils, userDetailsService);
    }

    /**
     * DaoAuthenticationProvider wires together:
     *   - How to load a user (UserDetailsService)
     *   - How to verify a password (PasswordEncoder)
     * Spring Security's AuthenticationManager delegates to this provider
     * during username/password login.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Expose AuthenticationManager as a bean so AuthController can inject it
     * to manually trigger authentication during the /signin flow.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * BCrypt with default strength (10 rounds).
     * Never store or compare passwords as plain text.
     * BCrypt is intentionally slow to defeat brute-force attacks.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ── CSRF ────────────────────────────────────────────────────────
                .csrf(AbstractHttpConfigurer::disable)

                // ── 401 handler for unauthenticated requests ─────────────────────
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(unauthorizedHandler))

                // ── Stateless sessions — no cookies, no HttpSession ──────────────
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ── Route-level authorization rules ──────────────────────────────
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()   // public auth endpoints
                        .requestMatchers("/api/test/all").permitAll()  // demo public endpoint
                        .anyRequest().authenticated()                  // everything else needs a token
                )

                // ── Wire in our JWT filter ────────────────────────────────────────
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(
                        authenticationJwtTokenFilter(),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}