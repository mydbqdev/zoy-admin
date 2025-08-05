package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.PgAreaCode;


@Repository
public interface PgAreaCodeRepository extends JpaRepository<PgAreaCode, String> {
	boolean existsByAreaShortName(String shortName);
	PgAreaCode findByAreaShortName(String shortName);
	PgAreaCode findByAreaName(String areaName);
}
