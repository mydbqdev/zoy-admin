package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgDueMaster;

@Repository
public interface ZoyPgDueMasterRepository extends JpaRepository<ZoyPgDueMaster, String> {

	@Query(value = "select * from pgowners.zoy_pg_due_master zpdm where due_name =:data", nativeQuery = true)
	ZoyPgDueMaster findDueMaster(String data);

	@Query(value = "select zppd.due_id ,zppd.factor_id from pgowners.zoy_pg_property_dues zppd "
			+ "join pgowners.zoy_pg_due_type_master zpdtm on zppd.due_id =zpdtm.due_id "
			+ "join pgowners.zoy_pg_due_master zpdm on zpdm.due_type_id =zpdtm.due_type "
			+ "and zpdm.due_name ='Rent' where zppd.property_id =:propertyId ", nativeQuery = true)
	List<String[]> findRentDue(String propertyId);
	
	 @Query(value = "select * from pgowners.zoy_pg_due_master order by due_name", nativeQuery = true)
	  List<ZoyPgDueMaster> findAllPgDueData();
}