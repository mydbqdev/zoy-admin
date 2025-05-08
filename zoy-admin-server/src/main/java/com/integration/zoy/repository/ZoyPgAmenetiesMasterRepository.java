package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgAmenetiesMaster;

@Repository
public interface ZoyPgAmenetiesMasterRepository extends JpaRepository<ZoyPgAmenetiesMaster, String> {

	@Query(value = "SELECT ameneties_id FROM pgowners.zoy_pg_ameneties_master WHERE ameneties_name IN (:amenities)", nativeQuery = true)
	List<String> getAmenityIdsByNames(@Param("amenities") List<String> amenities);

	@Query(value = "SELECT ameneties_name FROM pgowners.zoy_pg_ameneties_master WHERE ameneties_name IN (:amenities)", nativeQuery = true)
	List<String> getAmenityNameByNames(@Param("amenities") List<String> amenities);
	
	 @Query(value = "select * from pgowners.zoy_pg_ameneties_master order by ameneties_name", nativeQuery = true)
	  List<ZoyPgAmenetiesMaster> findAllPgShareData();
}
