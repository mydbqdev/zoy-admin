package com.integration.zoy.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgPropertyFloors;
import com.integration.zoy.entity.ZoyPgPropertyFloorsId;

@Repository
public interface ZoyPgPropertyFloorsRepository extends JpaRepository<ZoyPgPropertyFloors, ZoyPgPropertyFloorsId> {
	@Query(value = "SELECT fd.floor_id " +
            "FROM pgowners.zoy_pg_property_floors pf " +
            "JOIN pgowners.zoy_pg_property_floor_details fd ON pf.floor_id = fd.floor_id " +
            "WHERE pf.property_id = :propertyId", nativeQuery = true)
   List<String> findAllFloorIdsByPropertyId(@Param("propertyId") String propertyId);
	
	 @Modifying
	    @Transactional
	    @Query(value = "DELETE FROM pgowners.zoy_pg_property_floors WHERE floor_id IN :floorIds", nativeQuery = true)
	    void deleteByFloorIds(@Param("floorIds") List<String> floorIds);
}





