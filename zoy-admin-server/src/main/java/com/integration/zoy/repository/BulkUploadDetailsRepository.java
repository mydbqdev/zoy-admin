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

}