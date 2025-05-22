package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.PgLocationCode;


@Repository
public interface PgLocationCodeRepository extends JpaRepository<PgLocationCode, String> {
    
    PgLocationCode findByLocationShortName(String shortName);
    PgLocationCode findByLocationName(String locationName);
}
