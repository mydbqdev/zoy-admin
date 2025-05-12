package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgTypeMaster;

@Repository
public interface ZoyPgTypeMasterRepository extends JpaRepository<ZoyPgTypeMaster, String> {
	
	 @Query(value = "select * from pgowners.zoy_pg_type_master order by pg_type_name", nativeQuery = true)
	  List<ZoyPgTypeMaster> findAllPgTypeData();
}
