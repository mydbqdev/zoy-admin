package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgCancellationDetails;

@Repository
public interface ZoyPgCancellationDetailsRepository extends JpaRepository<ZoyPgCancellationDetails, String> {
    // Additional custom query methods (if needed) can be added here
}