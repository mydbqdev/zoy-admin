package com.integration.zoy.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgSalesMaster;

@Repository
public interface ZoyPgSalesMasterRepository extends JpaRepository<ZoyPgSalesMaster, String>  {

	boolean existsByEmailId(String emailId);
	
	@Query(value = "SELECT " +
	        "s.email_id AS emailId, " +
	        "s.employee_id AS employeeId, " +
	        "s.first_name AS firstName, " +
	        "s.middle_name AS middleName, " +
	        "s.last_name AS lastName, " +
	        "s.mobile_no AS mobileNo, " +
	        "s.created_at AS createdAt, " +
	        "'Active' AS status " + // Placeholder status for now
	        "FROM pgsales.pg_sales_master s " +
	        "WHERE :searchText IS NULL OR " +
	        "LOWER(s.first_name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(s.middle_name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(s.last_name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(s.email_id) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(s.mobile_no) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(s.employee_id) LIKE LOWER(CONCAT('%', :searchText, '%')) ",

	    countQuery = "SELECT COUNT(*) FROM pgsales.pg_sales_master s " +
	        "WHERE :searchText IS NULL OR " +
	        "LOWER(s.first_name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(s.middle_name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(s.last_name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(s.email_id) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(s.mobile_no) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(s.employee_id) LIKE LOWER(CONCAT('%', :searchText, '%')) ",

	    nativeQuery = true)
	Page<Object[]> findAllSalesPeople(Pageable pageable, @Param("searchText") String searchText);

	Optional<ZoyPgSalesMaster> findByEmailId(String emailId);

	boolean existsByMobileNo(String mobileNo);
}
