package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgPropertyRentCycle;
import com.integration.zoy.entity.ZoyPgPropertyRentCycleId;

@Repository
public interface ZoyPgPropertyRentCycleRepository extends JpaRepository<ZoyPgPropertyRentCycle, ZoyPgPropertyRentCycleId> {
	
}
