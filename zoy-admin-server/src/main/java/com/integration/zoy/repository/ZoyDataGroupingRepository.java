package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyDataGrouping;

@Repository
public interface ZoyDataGroupingRepository extends JpaRepository<ZoyDataGrouping, String> {
}