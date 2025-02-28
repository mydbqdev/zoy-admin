package com.integration.zoy.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.PgOwnerPropertyStatus;
import com.integration.zoy.entity.PgOwnerPropertyStatusId;

@Repository
public interface PgOwnerPropertyStatusRepository extends JpaRepository<PgOwnerPropertyStatus,PgOwnerPropertyStatusId>{

	
	@Query(value = "SELECT CASE WHEN COUNT(*) = 0 THEN true WHEN MAX(CASE WHEN status = true THEN 1 ELSE 0 END) = 1 THEN true ELSE false END AS overall_status "
			+ "FROM pgcommon.pg_owner_property_status WHERE pg_owner_id =:ownerId",nativeQuery = true)
	boolean findOwnerPropertyStatus(String ownerId);

	
	@Query(value = "select bd.property_id from pgowners.zoy_pg_owner_booking_details bd where bd.property_id=:propertyID",nativeQuery = true)
	Optional<String[]> findPropertyById(String propertyID);
	
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE pgcommon.pg_owner_property_status\r\n"
			+ "	SET \r\n"
			+ "	    status_type = :type,\r\n"
			+ "	    status_reason = :reason,\r\n"
			+ "	    status = CASE \r\n"
			+ "	                WHEN :type = 'suspend' OR :type = 'inactive' THEN false\r\n"
			+ "	                ELSE true\r\n"
			+ "	             END\r\n"
			+ "	WHERE property_id = :propertyID",nativeQuery = true)
	void updatePropertyById(String type,String reason,String propertyID);
	

	@Query(value="select property_id from pgcommon.pg_owner_property_status where property_id=:propertyId",nativeQuery=true)
	String findTheProperty(String propertyId);
}
