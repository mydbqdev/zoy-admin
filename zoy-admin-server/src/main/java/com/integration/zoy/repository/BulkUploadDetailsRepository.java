package com.integration.zoy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.BulkUploadDetails;

@Repository
public interface BulkUploadDetailsRepository extends JpaRepository<BulkUploadDetails, Long> {
	
	@Query(value = "SELECT * FROM bulk_upload_details WHERE job_execution_id = :jobExeId", nativeQuery = true)
    Optional<BulkUploadDetails> findByJobExecutionId(@Param("jobExeId") String jobExeId);
	
	@Query(value = "select exit_message from bulkupload.batch_job_execution where job_execution_id = :executionId", nativeQuery = true)
    String findErrorMessage(@Param("executionId") long executionId);
	
	@Query(value = "select ur.user_email from \r\n" + "pgadmin.user_role ur\r\n"
			+ "join pgadmin.app_role ar on ur.role_id = ar.id\r\n"
			+ "where ar.role_name='SUPER_ADMIN_ACCESS'", nativeQuery = true)
	String[] allSuperAdmin();
	
}