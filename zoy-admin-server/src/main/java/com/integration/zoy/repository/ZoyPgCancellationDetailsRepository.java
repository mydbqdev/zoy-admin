package com.integration.zoy.repository;

import com.integration.zoy.entity.ZoyPgCancellationDetails;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoyPgCancellationDetailsRepository extends JpaRepository<ZoyPgCancellationDetails, String> {

	@Query(value = "select * from pgowners.zoy_pg_cancellation_details zpcd where zpcd.pg_type=:pgtype order by effective_date DESC,priority ASC", nativeQuery = true)
	List<ZoyPgCancellationDetails> findAllByOrderByCreatedAtDesc1(String pgtype);
	
	@Query(value = "select * from pgowners.zoy_pg_cancellation_details zpcd order by priority asc", nativeQuery = true)
	List<ZoyPgCancellationDetails> findAllByOrderByCreatedAtDesc();
	
	@Query(value="DELETE FROM pgowners.zoy_pg_cancellation_details zpcd  WHERE zpcd.cancellation_id IN :deleteDetails",nativeQuery = true)
	void deleteBeforeCheckInCancellationbyIds(String [] deleteDetails);
}