package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgPropertyFloorDetails;

@Repository
public interface ZoyPgPropertyFloorDetailsRepository extends JpaRepository<ZoyPgPropertyFloorDetails, String> {

	
	@Query(value = "select zppfd.* from pgowners.zoy_pg_property_details zppd "
			+ "join pgowners.zoy_pg_property_floors zppf on zppf.property_id =zppd.property_id "
			+ "join pgowners.zoy_pg_property_floor_details zppfd on zppf.floor_id =zppfd.floor_id "
			+ "where zppd.property_id =:propertyId and zppfd.floor_name =:floorName",nativeQuery = true)
	ZoyPgPropertyFloorDetails findFloorDetails(String propertyId, String floorName);
	
	@Query(value = "select pgowners.check_dup_floor_names(:floorName,:propertyId)",nativeQuery = true)
	String checkDuplicateFloorName(String floorName, String propertyId);
}
