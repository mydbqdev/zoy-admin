package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgPropertyDetails;

@Repository
public interface ZoyPgPropertyDetailsRepository extends JpaRepository<ZoyPgPropertyDetails, String> {
	
	@Query(value ="SELECT DISTINCT zppd.property_city FROM pgowners.zoy_pg_property_details zppd", nativeQuery = true)
	String[] findDistinctCities();


	@Query(value = "select zpod.pg_owner_id ||','||zpod.pg_owner_name||','||zpod.pg_owner_mobile , "
			+ "STRING_AGG(DISTINCT zppd.property_id ||'|'|| zppd.property_name , ',') AS propertyDetails  "
			+ "	from pgowners.zoy_pg_owner_details zpod join pgowners.zoy_pg_property_details zppd "
			+ "on zppd.pg_owner_id =zpod.pg_owner_id group by zpod.pg_owner_id ",nativeQuery = true)
	List<String[]> getOwnerPropertyDetails();

}