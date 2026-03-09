package com.collusion.api.domain.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(
                name = "create_user",
                procedureName = "create_user",
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_first_name",    type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_last_name",     type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_email",         type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_password_hash", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_password_salt", type = String.class)
                }
        ),
        @NamedStoredProcedureQuery(
                name = "assign_role_to_user",
                procedureName = "assign_role_to_user",
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_user_id",     type = Integer.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_role_id",     type = Integer.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_assigned_by", type = Integer.class)
                }
        )
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password_salt")
    private String passwordSalt;

    @Column(name = "password_hash")
    private String passwordHash;
}