package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.TriggeredOn;

@Repository
public interface TriggeredOnRepository extends JpaRepository<TriggeredOn, Long> {
}

