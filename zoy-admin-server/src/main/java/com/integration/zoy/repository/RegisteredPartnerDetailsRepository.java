package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.RegisteredPartner;
@Repository
public interface RegisteredPartnerDetailsRepository extends JpaRepository<RegisteredPartner,String>{
	@Query(value = "select (select count(*) from pgowners.zoy_pg_registered_owner_details) as registeredOwners", nativeQuery = true)
	List<Object[]> getOwnerCardsDetails();
}
