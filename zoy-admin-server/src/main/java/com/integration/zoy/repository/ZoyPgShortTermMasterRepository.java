package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.integration.zoy.entity.ZoyPgShortTermMaster;

public interface ZoyPgShortTermMasterRepository extends JpaRepository<ZoyPgShortTermMaster, String> {
	

	@Query(value="DELETE FROM pgowners.zoy_pg_short_term_master zpstm  WHERE zpstm.zoy_pg_short_term_master_id IN :deleteDetails",nativeQuery = true)
	void deleteShortTermDetailsbyIds(String [] deleteDetails);
	

	@Query(value = "SELECT *  FROM pgowners.zoy_pg_short_term_master zpstm order by zpstm.effective_date desc,start_day asc", nativeQuery = true)
	List<ZoyPgShortTermMaster> findAllShortTermDetails();
}



