package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgTypeMaster;

@Repository
public interface ZoyPgTypeMasterRepository extends JpaRepository<ZoyPgTypeMaster, String> {
	
	 @Query(value = "select * from pgowners.zoy_pg_type_master order by pg_type_name", nativeQuery = true)
	  List<ZoyPgTypeMaster> findAllPgTypeData();
	 
	 @Query(value = "SELECT pg_type_id FROM pgowners.zoy_pg_type_master WHERE pg_type_name = :pgName", nativeQuery = true)
	 String findPgId(@Param("pgName") String pgName);
	 
	 
	 @Query(value="SELECT \r\n"
	 		+ "    zptm.pg_type_id,\r\n"
	 		+ "    zptm.pg_type_name,\r\n"
	 		+ "    STRING_AGG(DISTINCT zpgm.gender_name, ', ') AS gender_names\r\n"
	 		+ "FROM \r\n"
	 		+ "    pgowners.zoy_pg_type_master zptm\r\n"
	 		+ "LEFT JOIN \r\n"
	 		+ "    pgowners.zoy_pg_type_gender_master zptgm ON zptgm.pg_type_id = zptm.pg_type_id\r\n"
	 		+ "LEFT JOIN \r\n"
	 		+ "    pgowners.zoy_pg_gender_master zpgm ON zpgm.gender_id = zptgm.gender_id\r\n"
	 		+ "GROUP BY \r\n"
	 		+ "    zptm.pg_type_id, zptm.pg_type_name\r\n"
	 		+ "ORDER BY \r\n"
	 		+ "    zptm.pg_type_name",nativeQuery=true)
	 List<Object[]> findPgTypeAndGenders();
	 
	 
	 
}
