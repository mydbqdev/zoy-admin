package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgDueFactorMaster;

@Repository
public interface ZoyPgDueFactorMasterRepository extends JpaRepository<ZoyPgDueFactorMaster, String> {
   
	  @Query(value = "SELECT factor_id FROM pgowners.zoy_pg_due_factor_master WHERE factor_name = :factorName", nativeQuery = true)
	    String findFactorIdByFactorName(@Param("factorName") String factorName);
	  
	  @Query(value = "select * from pgowners.zoy_pg_due_factor_master order by factor_name", nativeQuery = true)
	  	List<ZoyPgDueFactorMaster> findAllDueFactorData();
	  
}
