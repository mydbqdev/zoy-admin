package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.TriggeredValue;

@Repository
public interface TriggeredValueRepository extends JpaRepository<TriggeredValue, Long> {
}

