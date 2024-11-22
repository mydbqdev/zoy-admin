package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.AdminUserPasswordHistory;

@Repository
public interface AdminUserPasswordHistoryRepository extends JpaRepository<AdminUserPasswordHistory, Long> {
	List<AdminUserPasswordHistory> findTop3ByUserEmailOrderByTsDesc(String userEmail);
}
