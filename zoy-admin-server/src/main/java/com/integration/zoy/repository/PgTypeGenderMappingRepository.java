package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.integration.zoy.entity.PgTypeGenderMapping;

@Repository
public interface PgTypeGenderMappingRepository extends JpaRepository<PgTypeGenderMapping, String> {

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM pgowners.zoy_pg_type_gender_master WHERE pg_type_id =:pgTypeId", nativeQuery = true)
	void deleteByPgTypeId(String pgTypeId);

}