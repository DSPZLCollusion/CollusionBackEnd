package com.collusion.api.domain.user;

import com.collusion.api.dto.user.UserRequest;
import com.collusion.api.dto.user.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ----------------------------------------------------------------
    // GET /api/users
    // ----------------------------------------------------------------

    @GetMapping
    @PreAuthorize("hasAnyRole('DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // ----------------------------------------------------------------
    // GET /api/users/{id}
    // ----------------------------------------------------------------

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTOR', 'ADMIN')")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // ----------------------------------------------------------------
    // POST /api/users
    // ----------------------------------------------------------------

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserRequest request) {
        userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ----------------------------------------------------------------
    // PUT /api/users/{id}
    // ----------------------------------------------------------------

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateUser(@PathVariable Long id,
                                           @Valid @RequestBody UserRequest request) {
        userService.updateUser(id, request);
        return ResponseEntity.noContent().build();
    }

    // ----------------------------------------------------------------
    // POST /api/users/{id}/roles
    // assignedBy is extracted from the JWT token via @AuthenticationPrincipal
    // and resolved to a Long in the service — never trusted from the client
    // ----------------------------------------------------------------

//    @PostMapping("/{id}/roles")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Void> assignRole(
//            @PathVariable Long id,
//            @RequestParam Long roleId,
//            @AuthenticationPrincipal UserDetails principal) {
//
//        // principal.getUsername() returns the email set during JWT authentication
//        // UserService resolves the email to a userId for the assignedBy field
//        userService.assignRole(id, roleId, principal);
//        return ResponseEntity.ok().build();
//    }
//
//    // ----------------------------------------------------------------
//    // DELETE /api/users/{id}/roles
//    // ----------------------------------------------------------------
//
//    @DeleteMapping("/{id}/roles")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Void> revokeRole(@PathVariable Long id,
//                                           @RequestParam Long roleId) {
//        userService.revokeRole(id, roleId);
//        return ResponseEntity.noContent().build();
//    }
}