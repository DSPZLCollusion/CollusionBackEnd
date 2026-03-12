package com.collusion.api.domain.user;

import com.collusion.api.dto.user.UserRequest;
import com.collusion.api.dto.user.UserResponse;
import com.collusion.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ----------------------------------------------------------------
    // CREATE
    // ----------------------------------------------------------------

    @Transactional
    public Long createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use: " + request.email());
        }

        String salt = generateSalt();
        // Combine password + salt before hashing so both are required to verify
        String hash = passwordEncoder.encode(request.password() + salt);

        return userRepository.createUser(
                request.firstName(),
                request.lastName(),
                request.email(),
                salt,
                hash
        );
    }

    // ----------------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------------

    @Transactional
    public void updateUser(Long userId, UserRequest request) {
        // Confirm user exists before attempting update
        if (userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("User", userId);
        }

        userRepository.updateUser(
                userId,
                request.firstName(),
                request.lastName(),
                request.email()
        );
    }

    @Transactional
    public void updatePassword(Long userId, String newPassword) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("User", userId);
        }

        String salt = generateSalt();
        String hash = passwordEncoder.encode(newPassword + salt);
        userRepository.updateUserPassword(userId, salt, hash);
    }

    // ----------------------------------------------------------------
    // ROLE ASSIGNMENT
    // assignedBy comes from the JWT principal in the controller —
    // never from the request body
    // ----------------------------------------------------------------


    // ----------------------------------------------------------------
    // READS
    // ----------------------------------------------------------------

    public UserResponse getUserById(Long userId) {
        List<UserRoleRow> rows = userRepository.findByIdWithRoles(userId);
        if (rows.isEmpty()) {
            throw new ResourceNotFoundException("User", userId);
        }
        return collapseToResponse(rows);
    }

    public List<UserResponse> getAllUsers() {
        List<UserRoleRow> rows = userRepository.findAllWithRoles();

        // Group flat rows by userId then collapse each group into one UserResponse
        return rows.stream()
                .collect(Collectors.groupingBy(UserRoleRow::userId))
                .values()
                .stream()
                .map(this::collapseToResponse)
                .sorted(Comparator.comparing(UserResponse::lastName)
                        .thenComparing(UserResponse::firstName))
                .toList();
    }

    // ----------------------------------------------------------------
    // PRIVATE HELPERS
    // ----------------------------------------------------------------

    /**
     * Collapses a list of flat user+role rows (one per role)
     * into a single UserResponse with a List<String> roles.
     * All rows in the list must belong to the same user.
     */
    private UserResponse collapseToResponse(List<UserRoleRow> rows) {
        UserRoleRow first = rows.getFirst();
        List<String> roles = rows.stream()
                .map(UserRoleRow::roleName)
                .toList();

        return new UserResponse(
                first.userId(),
                first.firstName(),
                first.lastName(),
                first.email(),
                roles
        );
    }

    private String generateSalt() {
        byte[] saltBytes = new byte[16];
        new SecureRandom().nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }
}