package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.BulkUploadDetails;

@Repository
public interface BulkUploadDetailsRepository extends JpaRepository<BulkUploadDetails, Long> {

}