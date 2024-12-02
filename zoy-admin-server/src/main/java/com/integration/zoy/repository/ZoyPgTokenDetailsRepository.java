package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgTokenDetails;

@Repository
public interface ZoyPgTokenDetailsRepository extends JpaRepository<ZoyPgTokenDetails, String> {
}