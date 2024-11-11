package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.AdminUserTemporary;

@Repository
public interface AdminUserTemporaryRepository extends JpaRepository<AdminUserTemporary, Long> {
}