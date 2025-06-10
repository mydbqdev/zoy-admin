package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgGenderMaster;

@Repository
public interface ZoyPgGenderMasterRepository extends JpaRepository<ZoyPgGenderMaster, String> {
	
}
