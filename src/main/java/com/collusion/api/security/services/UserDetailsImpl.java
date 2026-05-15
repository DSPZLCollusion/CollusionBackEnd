package com.collusion.api.security.services;

import com.collusion.api.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

/**
 * Adapter that bridges our User entity and Spring Security's UserDetails contract.
 *
 * Why a separate class instead of implementing UserDetails on User directly?
 * - User is a JPA entity (persistence concern).
 * - UserDetails is a security concern.
 * - Mixing them creates a class with two reasons to change (SRP violation)
 *   and makes it harder to evolve either independently.
 *
 * The static build() factory converts a User + its Roles into the
 * GrantedAuthority list Spring Security uses for @PreAuthorize checks.
 *
 * @JsonIgnore on password prevents accidental serialisation into API responses
 * if this object ever ends up in a response body.
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDetailsImpl implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    @EqualsAndHashCode.Include
    private final Long id;

    private final String firstName;
    private final String lastName;

    // Email is the login identifier in your schema — this is what
    // Spring Security calls "username" in its UserDetails contract.
    // The method is still called getUsername() to satisfy the interface,
    // but it returns the email string.
    private final String email;

    @JsonIgnore
    private final String password;

    private final Collection<? extends GrantedAuthority> authorities;

    private UserDetailsImpl(Long id,
                            String firstName,
                            String lastName,
                            String email,
                            String password,
                            Collection<? extends GrantedAuthority> authorities) {
        this.id          = id;
        this.firstName   = firstName;
        this.lastName    = lastName;
        this.email       = email;
        this.password    = password;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .toList();

        return new UserDetailsImpl(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPasswordHash(),
                authorities
        );
    }

    // UserDetails contract — getUsername() must return the login identifier.
    // In your app that's email, not a separate username field.
    @Override
    public String getUsername() {
        return email;
    }

    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }
}