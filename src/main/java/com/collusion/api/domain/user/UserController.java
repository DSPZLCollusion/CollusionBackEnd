package com.collusion.api.domain.user;

import com.collusion.api.dto.User.UserRequest;
import com.collusion.api.dto.User.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Users")
@RequiredArgsConstructor
public class UserController {

    private final UserService UserService;

    @GetMapping
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(UserService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public ResponseEntity<UserResponse> getUser(@PathVariable Integer id) {
        return ResponseEntity.ok(UserService.getUserById(id));
    }

//    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(UserService.createUser(request));
//    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(UserService.updateUser(id, request));
    }
}