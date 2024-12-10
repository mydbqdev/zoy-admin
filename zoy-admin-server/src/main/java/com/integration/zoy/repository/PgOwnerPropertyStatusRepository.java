package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.PgOwnerPropertyStatus;
import com.integration.zoy.entity.PgOwnerPropertyStatusId;

@Repository
public interface PgOwnerPropertyStatusRepository extends JpaRepository<PgOwnerPropertyStatus,PgOwnerPropertyStatusId>{

	
	@Query(value = "SELECT CASE WHEN COUNT(*) = 0 THEN true WHEN MAX(CASE WHEN status = true THEN 1 ELSE 0 END) = 1 THEN true ELSE false END AS overall_status "
			+ "FROM pgcommon.pg_owner_property_status WHERE pg_owner_id =:ownerId",nativeQuery = true)
	boolean findOwnerPropertyStatus(String ownerId);

}
