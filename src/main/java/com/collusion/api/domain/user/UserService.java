package com.collusion.api.domain.user;

import com.collusion.api.domain.role.Role;
import com.collusion.api.domain.role.RoleRepository;
import com.collusion.api.domain.role.UserRole;
import com.collusion.api.domain.role.UserRoleId;
import com.collusion.api.domain.role.UserRoleRepository;
import com.collusion.api.dto.user.UserRequest;
import com.collusion.api.dto.user.UserResponse;
import com.collusion.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository     userRepository;
    private final RoleRepository     roleRepository;
    private final UserRoleRepository userRoleRepository;

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
        String hash = passwordEncoder.encode(request.password() + salt);

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .passwordSalt(salt)
                .passwordHash(hash)
                .build();

        return userRepository.save(user).getId();
    }

    // ----------------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------------

    @Transactional
    public void updateUser(Long userId, UserRequest request) {
        User user = findUserOrThrow(userId);

        // Check email uniqueness only if the email is actually changing
        if (!user.getEmail().equals(request.email())
                && userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use: " + request.email());
        }

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());

        userRepository.save(user);
    }

    @Transactional
    public void updatePassword(Long userId, String newPassword) {
        User user = findUserOrThrow(userId);

        String salt = generateSalt();
        String hash = passwordEncoder.encode(newPassword + salt);

        user.setPasswordSalt(salt);
        user.setPasswordHash(hash);

        userRepository.save(user);
    }

    // ----------------------------------------------------------------
    // ROLE ASSIGNMENT
    // assignedBy is resolved from the JWT principal in the controller —
    // never trusted from the request body
    // ----------------------------------------------------------------

    @Transactional
    public void assignRole(Long userId, Long roleId, UserDetails principal) {
        User user = findUserOrThrow(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", roleId));

        if (userRoleRepository.existsByIdUserIdAndIdRoleId(userId, roleId)) {
            throw new IllegalArgumentException(
                    role.getRoleName() + " is already assigned to this user");
        }

        // Resolve the acting admin's id from their email in the JWT
        Long assignedById = userRepository.findByEmail(principal.getUsername())
                .map(User::getId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user not found"));

        UserRole userRole = UserRole.builder()
                .id(new UserRoleId(userId, roleId))
                .user(user)
                .role(role)
                .assignedBy(assignedById)
                .build();

        userRoleRepository.save(userRole);
    }

    @Transactional
    public void revokeRole(Long userId, Long roleId) {
        if (!userRoleRepository.existsByIdUserIdAndIdRoleId(userId, roleId)) {
            throw new IllegalArgumentException("User does not have this role");
        }
        userRoleRepository.deleteByIdUserIdAndIdRoleId(userId, roleId);
    }

    // ----------------------------------------------------------------
    // READS
    // ----------------------------------------------------------------

    public UserResponse getUserById(Long userId) {
        return UserResponse.from(findUserOrThrow(userId));
    }

    public List<UserResponse> getAllUsers() {
        // findAllWithRoles uses JOIN FETCH to avoid N+1 queries
        return userRepository.findAllWithRoles()
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    // ----------------------------------------------------------------
    // PRIVATE HELPERS
    // ----------------------------------------------------------------

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    private String generateSalt() {
        byte[] saltBytes = new byte[16];
        new SecureRandom().nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }
}