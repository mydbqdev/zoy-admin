package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgRentCycleMaster;

@Repository
public interface ZoyPgRentCycleMasterRepository extends JpaRepository<ZoyPgRentCycleMaster, String> {



	@Query(value = "select zprcm.cycle_id,zprcm.cycle_name from pgowners.zoy_pg_rent_cycle_master zprcm "
			+ "join pgowners.zoy_pg_propety_rent_cycle zpprc on zprcm .cycle_id =zpprc .cycle_id "
			+ "and zpprc.property_id =:propertyId",nativeQuery = true)
	List<String[]> findRentCycleName(String propertyId);

	@Query(value = "SELECT cycle_id FROM pgowners.zoy_pg_rent_cycle_master WHERE cycle_name IN (:cycleNames)", nativeQuery = true)
	List<String> findCycleIdsByCycleNames(@Param("cycleNames") List<String> cycleNames);

}
