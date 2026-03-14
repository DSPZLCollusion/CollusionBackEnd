package com.collusion.api.domain.role;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

/**
 * Represents a row in the user_roles join table.
 * Modeled as its own @Entity (rather than a simple @ManyToMany)
 * because the table has extra columns: assigned_at and assigned_by.
 * A plain @ManyToMany cannot map extra join table columns.
 */
@Entity
@Table(name = "user_roles")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRole {

    @EmbeddedId
    private UserRoleId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private com.collusion.api.domain.user.User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "assigned_at", updatable = false)
    @CreatedDate
    private OffsetDateTime assignedAt;

    // The id of the member who performed the assignment.
    // Nullable — null means seeded/system assigned.
    @Column(name = "assigned_by")
    private Long assignedBy;
}