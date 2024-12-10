package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgDueTypeMaster;

@Repository
public interface ZoyPgDueTypeMasterRepository extends JpaRepository<ZoyPgDueTypeMaster, String> {
	
}
