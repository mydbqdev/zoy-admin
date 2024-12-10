package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgPropertyTermsConditions;
import com.integration.zoy.entity.ZoyPgPropertyTermsConditionsId;

@Repository
public interface ZoyPgPropertyTermsConditionsRepository extends JpaRepository<ZoyPgPropertyTermsConditions, ZoyPgPropertyTermsConditionsId> {
	
}
