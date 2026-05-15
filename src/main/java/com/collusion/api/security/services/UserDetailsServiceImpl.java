package com.collusion.api.security.services;

import com.collusion.api.model.User;
import com.collusion.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implements Spring Security's UserDetailsService.
 *
 * This is the single hook Spring Security calls during username/password
 * authentication.  It receives a username string from the login request,
 * queries the database, and returns a UserDetails object that Spring
 * Security uses to verify the password and build the Authentication token.
 *
 * @Transactional is required here because loading a User also lazily fetches
 * the roles collection.  Without a live transaction, accessing user.getRoles()
 * inside UserDetailsImpl.build() would throw a LazyInitializationException.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // Spring Security calls this with whatever string was passed as
    // "username" in the UsernamePasswordAuthenticationToken — which in
    // your AuthController is loginRequest.getEmail().
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No account found for email: " + email));

        return UserDetailsImpl.build(user);
    }
}