package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgPropertyDues;
import com.integration.zoy.entity.ZoyPgPropertyDuesId;

@Repository
public interface ZoyPgPropertyDuesRepository extends JpaRepository<ZoyPgPropertyDues, ZoyPgPropertyDuesId> {

	@Query(value = "select zppd.due_id ,zppd.factor_id from pgowners.zoy_pg_property_dues zppd  "
			+ "join pgowners.zoy_pg_due_type_master zpdtm on zpdtm.due_id =zppd.due_id  "
			+ "join pgowners.zoy_pg_due_master zpdm on zpdm.due_type_id =zpdtm.due_type  "
			+ "join pgowners.zoy_pg_due_factor_master zpdfm on zpdfm.factor_id =zppd.factor_id  "
			+ "where zppd.property_id =:propertyId and zpdm.due_name =:dueName", 
			nativeQuery = true)
	List<String[]> getPropertyDueDetails(String propertyId, String dueName);
}
