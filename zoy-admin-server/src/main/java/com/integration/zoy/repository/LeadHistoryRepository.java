package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.LeadHistory;

@Repository
public interface LeadHistoryRepository extends JpaRepository<LeadHistory, String>{
	@Query(value = "SELECT history_data FROM pgadmin.lead_history WHERE inquiry_number = :inquiryNumber ORDER BY created_on DESC", nativeQuery = true)
    List<String> findHistoryDataByInquiryNumber(@Param("inquiryNumber") String inquiryNumber);
}
