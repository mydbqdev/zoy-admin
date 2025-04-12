package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgSalesUserLoginDetails;

@Repository
public interface ZoyPgSalesUserLoginDetailsRepository extends JpaRepository<ZoyPgSalesUserLoginDetails, String>{
	
	boolean existsByUserEmail(String userEmail);

}
