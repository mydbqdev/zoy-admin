package com.integration.zoy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
	        "COUNT(p.property_id) AS numberOfProperties " +
	        "FROM pgowners.zoy_pg_owner_details o " +
	        "LEFT JOIN pgowners.zoy_pg_property_details p ON o.pg_owner_id = p.pg_owner_id " +
	        "WHERE (:searchText IS NULL OR " +
	        "LOWER(o.pg_owner_name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(o.pg_owner_id) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(o.pg_owner_email) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(o.pg_owner_mobile) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
	        "GROUP BY o.pg_owner_id, o.pg_owner_name, o.pg_owner_email, o.pg_owner_mobile",
	    countQuery = "SELECT COUNT(o) FROM pgowners.zoy_pg_owner_details o " +
	                 "WHERE (:searchText IS NULL OR " +
	                 "LOWER(o.pg_owner_name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	                 "LOWER(o.pg_owner_id) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	                 "LOWER(o.pg_owner_email) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	                 "LOWER(o.pg_owner_mobile) LIKE LOWER(CONCAT('%', :searchText, '%')))",
	    nativeQuery = true)
	Page<Object[]> findAllOwnerWithPropertyCountRaw(Pageable pageable,
	                                                @Param("searchText") String searchText);



}
