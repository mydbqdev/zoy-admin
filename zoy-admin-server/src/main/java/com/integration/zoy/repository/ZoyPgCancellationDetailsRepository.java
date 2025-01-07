package com.integration.zoy.repository;

import com.integration.zoy.entity.ZoyPgCancellationDetails;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoyPgCancellationDetailsRepository extends JpaRepository<ZoyPgCancellationDetails, String> {

	@Query(value = "select * from pgowners.zoy_pg_cancellation_details zpcd order by priority asc", nativeQuery = true)
	List<ZoyPgCancellationDetails> findAllByOrderByCreatedAtDesc();
}