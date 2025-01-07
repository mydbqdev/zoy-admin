package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.TriggeredCond;

@Repository
public interface TriggeredCondRepository extends JpaRepository<TriggeredCond, Long> {
}
