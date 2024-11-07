package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgTypeMaster;

@Repository
public interface ZoyPgTypeMasterRepository extends JpaRepository<ZoyPgTypeMaster, String> {
	
}
