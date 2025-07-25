package com.integration.zoy.repository;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgOwnerDetails;

@Repository
public interface ZoyPgOwnerDetailsRepository extends JpaRepository<ZoyPgOwnerDetails, String> {	
	@Query(value = "SELECT " +
	        "o.pg_owner_id AS ownerId, " +
	        "o.pg_owner_name AS ownerName, " +
	        "o.pg_owner_email AS ownerEmail, " +
	        "o.pg_owner_mobile AS ownerContact, " +
	        "COUNT(p.property_id) AS numberOfProperties, " +
	        "CASE " +
	        "    WHEN COUNT(p.property_id) > 0 THEN 'Active' " +
	        "    WHEN up.pwd IS NULL THEN 'Not Registered'"+
	        "    ELSE 'Registered' " +
	        "END AS status " +
	        "FROM pgowners.zoy_pg_owner_details o " +
	        "JOIN pgcommon.user_profile up ON o.pg_owner_email = up.email_id " +
	        "LEFT JOIN pgowners.zoy_pg_property_details p ON o.pg_owner_id = p.pg_owner_id " +
	        "WHERE(:searchText IS NULL OR " +
	        "LOWER(o.pg_owner_name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(o.pg_owner_id) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(o.pg_owner_email) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(o.pg_owner_mobile) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
	        "GROUP BY o.pg_owner_id, o.pg_owner_name, o.pg_owner_email, o.pg_owner_mobile ,up.pwd",

	    countQuery = "SELECT COUNT(DISTINCT o.pg_owner_id) " +
	        "FROM pgowners.zoy_pg_owner_details o " +
	        "JOIN pgcommon.user_profile up ON o.pg_owner_email = up.email_id " +
	        "LEFT JOIN pgowners.zoy_pg_property_details p ON o.pg_owner_id = p.pg_owner_id " +
	        "WHERE  (:searchText IS NULL OR " +
	        "LOWER(o.pg_owner_name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(o.pg_owner_id) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(o.pg_owner_email) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(o.pg_owner_mobile) LIKE LOWER(CONCAT('%', :searchText, '%')))",

	    nativeQuery = true)
	Page<Object[]> findAllOwnerWithPropertyCountRaw(Pageable pageable,
	                                                @Param("searchText") String searchText);



//	@Modifying
//	@Transactional
//    @Query(value = "UPDATE pgowners.zoy_pg_owner_details " +
//                   "SET zoy_share = :newZoyShare " +
//                   "WHERE pg_owner_id = :ownerid", nativeQuery = true)
//    int updateZoyShare(@Param("ownerid") String ownerid, @Param("newZoyShare") BigDecimal newZoyShare);
	
	@Modifying
	@Transactional
    @Query(value = "UPDATE pgowners.zoy_pg_property_details SET zoy_variable_share = :zoyVariableShare,zoy_fixed_share = :zoyFixedShare "
    		+ "WHERE property_id = :propertyId", nativeQuery = true)
    int updatePropertyZoyShare(@Param("propertyId") String propertyId, @Param("zoyVariableShare") BigDecimal zoyVariableShare,
    		 @Param("zoyFixedShare") BigDecimal zoyFixedShare);
	
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE pgowners.zoy_pg_registered_owner_details " +
	               "SET status = 'Closed' " +
	               "WHERE status = 'Resolved' " +
	               "AND ts <= (CAST(:timeZone AS date) - interval '48 hours')", nativeQuery = true)
	void updateResolvedRequestsToClosedAfter48Hours(String timeZone);
}
