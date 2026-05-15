package com.collusion.api.security.jwt;

import com.collusion.api.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT authentication filter — runs exactly once per HTTP request.
 *
 * Why OncePerRequestFilter?
 * Servlet filters can be invoked multiple times per request (e.g., during
 * a RequestDispatcher.forward()).  OncePerRequestFilter guarantees a single
 * execution regardless of the dispatch type.
 *
 * Flow:
 *   1. Extract the Bearer token from the Authorization header.
 *   2. Validate it (signature + expiry).
 *   3. Load the UserDetails from the DB (we need fresh roles on every request).
 *   4. Set an Authentication object into the SecurityContextHolder so that
 *      downstream filters and @PreAuthorize annotations can read it.
 *
 * If no token is present (e.g., hitting a public endpoint), the filter simply
 * passes the request through without setting any authentication — public
 * endpoints work normally, and protected ones will be blocked by the
 * authorizeHttpRequests rules in WebSecurityConfig.
 *
 * @RequiredArgsConstructor + @Component is intentionally NOT used here.
 * This filter is registered as a @Bean in WebSecurityConfig so that
 * Spring Security controls its placement in the filter chain.
 */
@Slf4j
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String jwt = parseJwt(request);

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUsernameFromJwtToken(jwt);

                var userDetails = userDetailsService.loadUserByUsername(username);

                // UsernamePasswordAuthenticationToken with authorities = authenticated
                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // Attach request metadata (IP, session id) to the token
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract the JWT string from the Authorization header.
     * Expected format: "Bearer <token>"
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}