package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.UserMaster;
@Repository
public interface UserMasterRepo extends JpaRepository<UserMaster,String> {

	@Query(value = "SELECT " +
            "(SELECT COUNT(*) FROM pgusers.user_master WHERE user_pin IS NOT NULL) AS activeTenants, " +
            "(SELECT COUNT(DISTINCT pg_owner_id) FROM pgcommon.pg_owner_property_status WHERE status = true) AS activeOwnersCount, " +
            "(SELECT COUNT(*) FROM pgcommon.pg_owner_property_status WHERE status = true) AS activePropertiesCount", 
            nativeQuery = true)
	List<Object[]> getUsersWithNonNullPinAndActiveOwnersPropertiesCount();
}
