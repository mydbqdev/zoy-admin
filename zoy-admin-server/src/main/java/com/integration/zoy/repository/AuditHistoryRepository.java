package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.AuditHistory;

@Repository
public interface AuditHistoryRepository extends JpaRepository<AuditHistory, String> {
}