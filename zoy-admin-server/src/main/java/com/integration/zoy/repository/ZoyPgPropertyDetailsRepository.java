package com.integration.zoy.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgPropertyDetails;

@Repository
public interface ZoyPgPropertyDetailsRepository extends JpaRepository<ZoyPgPropertyDetails, String> {
	@Query(value ="SELECT DISTINCT zppd.property_city FROM pgowners.zoy_pg_property_details zppd", nativeQuery = true)
	String[] findDistinctCities();

	
}