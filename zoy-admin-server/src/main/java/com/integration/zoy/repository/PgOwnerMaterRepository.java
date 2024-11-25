package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.integration.zoy.entity.PgOwnerMaster;

public interface PgOwnerMaterRepository extends JpaRepository<PgOwnerMaster, String> {
	
	@Query(value = "select email_id, zoy_code from pg_owner_master where email_id = :emailId", nativeQuery = true)
    List<Object[]> findEmailAndZoyCodeByEmailId(String emailId);
	
	@Query(value="  SELECT \r\n"
			+ "    pom.zoy_code, \r\n"
			+ "    CONCAT(pom.first_name, ' ', pom.last_name) AS username,pom.email_id, pom.mobile_no, pom.created_at, \r\n"
			+ "    CASE WHEN up.zoy_code IS NOT NULL THEN 'registered' ELSE 'pending' END AS status\r\n"
			+ "    FROM pg_owner_master pom LEFT JOIN pgcommon.user_profile up ON pom.zoy_code = up.zoy_code",nativeQuery = true)
	List<Object[]> getAllPgOwnerDetails();

@Query(value = "SELECT * " +
	       "FROM pg_owner_master " +
	       "WHERE COALESCE(:searchTerm, '') = '' OR (" +
	       "  COALESCE(:searchTerm, '') != '' AND (" +
	       "    email_id ILIKE '%' || :searchTerm || '%' OR " +
	       "    first_name ILIKE '%' || :searchTerm || '%' OR " +
	       "    last_name ILIKE '%' || :searchTerm || '%' OR " +
	       "    mobile_no ILIKE '%' || :searchTerm || '%' OR " +
	       "    zoy_code ILIKE '%' || :searchTerm || '%' " +
	       "  )" +
	       ")" +
	       "LIMIT :pageSize OFFSET :offset", 
	       nativeQuery = true)
	List<Object[]> PgOwnerDetailsBasedONSearchWithPagination(@Param("searchTerm") String searchTerm, 
	                                                         @Param("pageSize") int pageSize, 
	                                                         @Param("offset") int offset);
	
	@Query(value="select * from pg_owner_master po where po.email_id = :email_id",nativeQuery = true)
	PgOwnerMaster getOwnerDetails(String email_id);
}
