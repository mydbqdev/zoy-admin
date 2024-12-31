package com.integration.zoy.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.UserMaster;

@Repository
public interface UserMasterRepository extends JpaRepository<UserMaster, String>{
	@Query(value = "SELECT * FROM pgusers.user_master WHERE user_mobile = :mobile and user_email=:email", nativeQuery = true)
	Optional<UserMaster> findUserMaster(String mobile, String email);

	@Query(value = "select concat(um.first_name,' ',um.last_name )as userName,um.user_email  from user_master um  order by  LOWER( um.first_name) asc, LOWER(um.last_name) ASC ",nativeQuery = true)
	List<Object[]> getUsersNameList();
	@Query(value = "SELECT " +
	        "CONCAT(um.user_first_name, ' ', um.user_last_name) AS tenantName, " +
	        "um.user_mobile AS contactNumber, " +
	        "um.user_email AS userEmail, " +
	        "CASE " +
	        "    WHEN um.user_pin IS NOT NULL THEN 'Active' " +
	        "    ELSE 'inactive' " +
	        "END AS status, " +
	        "CASE " +
	        "    WHEN um.user_pin IS NOT NULL THEN 'Yes' " +
	        "    ELSE 'No' " +
	        "END AS appUser, " +
	        "CASE " +
	        "    WHEN um.user_ekyc_isekycverified = true AND um.user_ekyc_isvideo_verified = true THEN 'Verified' " +
	        "    WHEN um.user_ekyc_isekycverified = true AND (um.user_ekyc_isvideo_verified = false OR um.user_ekyc_isvideo_verified IS NULL) THEN 'Partially Verified' " +
	        "    ELSE 'Pending' " +
	        "END AS ekycStatus, " +
	        "um.user_created_at AS registeredDate " +
	        "FROM pgusers.user_master um " +
	        "WHERE (:searchText IS NULL OR " +
	        "LOWER(CAST(CONCAT(um.user_first_name, ' ', um.user_last_name) AS text)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(CAST(um.user_mobile AS text)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(CAST(um.user_email AS text)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(CAST(CASE WHEN um.user_pin IS NOT NULL THEN 'active' ELSE 'inactive' END AS text)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(CAST(CASE WHEN um.user_pin IS NOT NULL THEN 'Yes' ELSE 'No' END AS text)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(CAST(CASE WHEN um.user_ekyc_isekycverified = true AND um.user_ekyc_isvideo_verified = true THEN 'Verified' " +
	        "    WHEN um.user_ekyc_isekycverified = true AND (um.user_ekyc_isvideo_verified = false OR um.user_ekyc_isvideo_verified IS NULL) THEN 'Partially Verified' " +
	        "    ELSE 'Pending' END AS text)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	        "LOWER(CAST(um.user_created_at AS text)) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
	        "AND ((:startDate = '1970-01-01 00:00:00' or :endDate = '9999-12-31 23:59:59') or ((:startDate != '1970-01-01 00:00:00' AND :endDate != '9999-12-31 23:59:59') and um.user_created_at BETWEEN CAST(:startDate AS timestamp) AND CAST(:endDate AS timestamp)))" +
	        "AND (:status IS NULL OR LOWER(CAST(CASE WHEN um.user_pin IS NOT NULL THEN 'active' ELSE 'inactive' END AS text)) = LOWER(CAST(:status AS text)))",
	    countQuery = "SELECT COUNT(*) FROM pgusers.user_master um " +
	                 "WHERE (:searchText IS NULL OR " +
	                 "LOWER(CAST(CONCAT(um.user_first_name, ' ', um.user_last_name) AS text)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	                 "LOWER(CAST(um.user_mobile AS text)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	                 "LOWER(CAST(um.user_email AS text)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	                 "LOWER(CAST(CASE WHEN um.user_pin IS NOT NULL THEN 'active' ELSE 'inactive' END AS text)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	                 "LOWER(CAST(CASE WHEN um.user_pin IS NOT NULL THEN 'Yes' ELSE 'No' END AS text)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	                 "LOWER(CAST(CASE WHEN um.user_ekyc_isekycverified = true AND um.user_ekyc_isvideo_verified = true THEN 'Verified' " +
	                 "    WHEN um.user_ekyc_isekycverified = true AND (um.user_ekyc_isvideo_verified = false OR um.user_ekyc_isvideo_verified IS NULL) THEN 'Partially Verified' " +
	                 "    ELSE 'Pending' END AS text)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
	                 "LOWER(CAST(um.user_created_at AS text)) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
	                 "AND  ((:startDate = '1970-01-01 00:00:00' or :endDate = '9999-12-31 23:59:59') or ((:startDate != '1970-01-01 00:00:00' AND :endDate != '9999-12-31 23:59:59') AND um.user_created_at BETWEEN CAST(:startDate AS timestamp) AND CAST(:endDate AS timestamp)))" +
	                "AND (:status IS NULL OR LOWER(CAST(CASE WHEN um.user_pin IS NOT NULL THEN 'active' ELSE 'inactive' END AS text)) = LOWER(CAST(:status AS text)))",
	    nativeQuery = true)
	Page<Object[]> getTenantDetails(Pageable pageable, @Param("searchText") String searchText, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("status") String status);
}
