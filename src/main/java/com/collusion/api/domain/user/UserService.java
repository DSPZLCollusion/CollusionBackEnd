package com.collusion.api.domain.user;

import com.collusion.api.dto.User.UserRequest;
import com.collusion.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public void createUser(UserRequest userRequest) {
        roleRepository.findById(userRequest.roleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + userRequest.roleId()));

        // Generate a random salt
        byte[] saltBytes = new byte[16];
        new SecureRandom().nextBytes(saltBytes);
        String salt = Base64.getEncoder().encodeToString(saltBytes);

        // Hash the password combined with the salt
        String hashedPassword = passwordEncoder.encode(userRequest.password() + salt);

        userRepository.createUser(
                userRequest.firstName(),
                userRequest.lastName(),
                userRequest.email(),
                hashedPassword,
                salt
        );
    }

    @Transactional
    public void assignRoleToUser(Integer userId, Integer roleId, Integer assignedBy) {
        roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        userRepository.assignRoleToUser(userId, roleId, assignedBy);
    }
}