package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgDueMaster;

@Repository
public interface ZoyPgDueMasterRepository extends JpaRepository<ZoyPgDueMaster, String> {

	@Query(value = "select * from pgowners.zoy_pg_due_master zpdm where due_name =:data", nativeQuery = true)
	ZoyPgDueMaster findDueMaster(String data);
}