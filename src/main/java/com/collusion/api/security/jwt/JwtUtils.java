package com.collusion.api.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utility component for JWT lifecycle management.
 *
 * Key changes from jjwt 0.11.x → 0.12.x used here:
 *
 *  1. Jwts.builder() / Jwts.parser() — parserBuilder() is removed in 0.12.
 *  2. .subject() replaces deprecated .setSubject()  (new fluent claim setters)
 *  3. .issuedAt() / .expiration() replace setIssuedAt / setExpiration
 *  4. .signWith(key) — algorithm is inferred from the key type; the explicit
 *     SignatureAlgorithm enum argument is no longer needed and is removed.
 *  5. .verifyWith(key) replaces .setSigningKey(key) on the parser.
 *  6. io.jsonwebtoken.security.SignatureException is now its own top-level
 *     class rather than a sub-type of JwtException.
 *
 * The secret key is injected from application.properties (which itself reads
 * an environment variable in production) — never hardcoded.
 */
@Component
@Slf4j
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationMs;

    /**
     * Generate a signed JWT for the authenticated user.
     * Subject is set to username; expiry is controlled by the property above.
     */
    public String generateJwtToken(Authentication authentication) {
        var userPrincipal = (com.collusion.api.security.services.UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .subject(userPrincipal.getUsername())           // jjwt 0.12: .subject() not .setSubject()
                .issuedAt(new Date())                           // jjwt 0.12: .issuedAt() not .setIssuedAt()
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(secretKey())                          // algorithm inferred from SecretKey type
                .compact();
    }

    /** Extract username (subject claim) from a validated token. */
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey())                        // jjwt 0.12: verifyWith() not setSigningKey()
                .build()
                .parseSignedClaims(token)                       // jjwt 0.12: parseSignedClaims() not parseClaimsJws()
                .getPayload()
                .getSubject();
    }

    /**
     * Validate a token, returning true only if the signature is valid and
     * the token is not expired.  Each failure case is logged separately so
     * that ops teams can distinguish between attacks and clock drift.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Build a SecretKey from the Base64-encoded secret string.
     * Keys.hmacShaKeyFor() selects HS256/384/512 based on key length.
     * A 64-char Base64 string → 48 bytes → HS384; ≥86 chars → HS512.
     * Minimum 32 bytes (43 chars) required for HS256.
     */
    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}