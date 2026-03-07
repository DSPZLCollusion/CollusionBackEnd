package com.collusion.api.domain.user;

import com.collusion.api.dto.User.UserRequest;
import com.collusion.api.dto.User.UserResponse;
import com.collusion.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository UserRepository;
    private final RoleRepository roleRepository;
   // private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getAllUsers() {
        return UserRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse getUserById(Integer UserId) {
        User User = UserRepository.findById(UserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + UserId));
        return UserResponse.from(User);
    }

//    @Transactional
//    public UserResponse createUser(UserRequest request) {
//        if (UserRepository.existsByEmail(request.email())) {
//            throw new IllegalArgumentException("Email already in use: " + request.email());
//        }
//
//        Role role = roleRepository.findById(request.roleId())
//                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + request.roleId()));
//
//        // Salt is generated and stored separately so it can be re-used for verification
//        String salt = generateSalt();
//        String hash = passwordEncoder.encode(salt + request.password());
//
//        User User = User.builder()
//                .firstName(request.firstName())
//                .lastName(request.lastName())
//                .email(request.email())
//                .passwordSalt(salt)
//                .passwordHash(hash)
//                .role(role)
//                .build();
//
//        return UserResponse.from(UserRepository.save(User));
//    }

    @Transactional
    public UserResponse updateUser(Integer UserId, UserRequest request) {
        User User = UserRepository.findById(UserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + UserId));

        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + request.roleId()));

        User.setFirstName(request.firstName());
        User.setLastName(request.lastName());
        User.setEmail(request.email());
        User.setRole(role);

        return UserResponse.from(UserRepository.save(User));
    }

    private String generateSalt() {
        byte[] salt = new byte[16];
        new java.security.SecureRandom().nextBytes(salt);
        return java.util.Base64.getEncoder().encodeToString(salt);
    }
}