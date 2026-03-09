package com.collusion.api.domain.user;

import com.collusion.api.dto.User.UserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
//@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public String index() {
        return "Hello, World!";
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserRequest userRequest) {
        userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{userId}/roles")
    //@PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public ResponseEntity<Void> assignRole(@PathVariable Integer userId,
                                           @RequestParam Integer roleId,
                                           @RequestParam Integer assignedBy) {
        userService.assignRoleToUser(userId, roleId, assignedBy);
        return ResponseEntity.ok().build();
    }
}