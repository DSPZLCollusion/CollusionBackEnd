package com.collusion.api.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // name must match the @NamedStoredProcedureQuery name defined on User.java
    @Procedure(name = "create_user")
    void createUser(@Param("p_first_name")    String firstName,
                    @Param("p_last_name")     String lastName,
                    @Param("p_email")         String email,
                    @Param("p_password_hash") String passwordHash,
                    @Param("p_password_salt") String passwordSalt);

    @Procedure(name = "assign_role_to_user")
    void assignRoleToUser(@Param("p_user_id")     Integer userId,
                          @Param("p_role_id")     Integer roleId,
                          @Param("p_assigned_by") Integer assignedBy);
}