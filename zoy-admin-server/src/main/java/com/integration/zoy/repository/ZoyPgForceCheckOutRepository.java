package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgForceCheckOut;

@Repository
public interface ZoyPgForceCheckOutRepository extends JpaRepository<ZoyPgForceCheckOut, String> {
	
}
