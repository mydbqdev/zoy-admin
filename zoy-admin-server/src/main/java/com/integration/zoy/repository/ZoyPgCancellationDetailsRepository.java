package com.integration.zoy.repository;

import com.integration.zoy.entity.ZoyPgCancellationDetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoyPgCancellationDetailsRepository extends JpaRepository<ZoyPgCancellationDetails, String> {
    // Additional custom query methods (if needed) can be added here
}