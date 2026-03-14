package com.collusion.api.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Fetch users with their roles in one query to avoid N+1 problem.
    // Without this, accessing user.getUserRoles() for each user in a
    // list would fire a separate query per user.
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role")
    List<User> findAllWithRoles();
}