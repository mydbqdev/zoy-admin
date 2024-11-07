package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgShareMaster;

@Repository
public interface ZoyPgShareMasterRepository extends JpaRepository<ZoyPgShareMaster, String> {

	
	  @Query(value = "SELECT share_id FROM pgowners.zoy_pg_share_master WHERE share_type = :shareType LIMIT 1", nativeQuery = true)
	    String findShareIdByShareType(@Param("shareType") String shareType);
}
