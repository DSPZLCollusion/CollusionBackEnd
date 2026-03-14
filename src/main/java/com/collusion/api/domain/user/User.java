package com.collusion.api.domain.user;

import com.collusion.api.domain.role.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 62)
    private String email;

    // Never serialized to the client — excluded from UserResponse
    @Column(name = "password_salt", nullable = false)
    private String passwordSalt;

    // Never serialized to the client — excluded from UserResponse
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // Mapped via UserRole entity (not @ManyToMany) because
    // user_roles has extra columns: assigned_at and assigned_by
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserRole> userRoles = new ArrayList<>();

    // ----------------------------------------------------------------
    // Convenience helpers — keeps role management readable in UserService
    // ----------------------------------------------------------------

    public List<String> getRoleNames() {
        return userRoles.stream()
                .map(ur -> ur.getRole().getRoleName())
                .toList();
    }

    public boolean hasRole(String roleName) {
        return getRoleNames().contains(roleName);
    }
}