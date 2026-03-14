package com.collusion.api.domain.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

    List<UserRole> findByIdUserId(Long userId);

    boolean existsByIdUserIdAndIdRoleId(Long userId, Long roleId);

    void deleteByIdUserIdAndIdRoleId(Long userId, Long roleId);
}