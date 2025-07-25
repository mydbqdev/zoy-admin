package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.integration.zoy.entity.ZoyPgRentGst;

public interface ZoyPgRentGstRepository extends JpaRepository<ZoyPgRentGst, String> {

	@Query(value = "select * from pgowners.zoy_pg_rent_gst zprg where is_approved =true "
			+ "ORDER BY CASE WHEN effective_date\\:\\:date <= CURRENT_DATE THEN 0 ELSE 1 end, effective_date desc LIMIT 1", nativeQuery = true)
	ZoyPgRentGst findPgRentGst();

	
}
