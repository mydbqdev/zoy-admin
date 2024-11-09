package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.AdminUserLoginDetails;

@Repository
public interface AdminUserLoginDetailsRepository extends JpaRepository<AdminUserLoginDetails, String> {
}
