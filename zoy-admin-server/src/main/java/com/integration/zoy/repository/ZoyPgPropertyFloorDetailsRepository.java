package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgPropertyFloorDetails;

@Repository
public interface ZoyPgPropertyFloorDetailsRepository extends JpaRepository<ZoyPgPropertyFloorDetails, String> {
	
}
