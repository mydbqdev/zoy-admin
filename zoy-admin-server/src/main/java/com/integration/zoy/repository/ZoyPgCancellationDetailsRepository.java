package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgCancellationDetails;

@Repository
public interface ZoyPgCancellationDetailsRepository extends JpaRepository<ZoyPgCancellationDetails, String> {
	List<ZoyPgCancellationDetails> findAllByOrderByCreatedAtDesc();
    void deleteByCancellationId(String cancellationId);
}