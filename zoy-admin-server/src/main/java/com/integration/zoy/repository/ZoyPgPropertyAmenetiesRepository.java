package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgPropertyAmeneties;
import com.integration.zoy.entity.ZoyPgPropertyAmenetiesId;

@Repository
public interface ZoyPgPropertyAmenetiesRepository extends JpaRepository<ZoyPgPropertyAmeneties, ZoyPgPropertyAmenetiesId> {

	@Query(value = "select zpam.ameneties_name from pgowners.zoy_pg_ameneties_master zpam join "
			+ "pgowners.zoy_pg_property_ameneties zppa on zppa.ameneties_id =zpam.ameneties_id and "
			+ "zppa.property_id =:propertyId",nativeQuery = true)
	List<String> findPropertyAmenetiesName(String propertyId);
	
}
