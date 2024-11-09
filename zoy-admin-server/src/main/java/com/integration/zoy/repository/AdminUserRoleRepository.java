package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.AdminUserRole;

@Repository
public interface AdminUserRoleRepository extends JpaRepository<AdminUserRole, Long> {
}