package com.integration.zoy.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.PgEmailMobileBlacklisted;

@Repository
public interface PgEmailMobileBlacklistedRepository extends JpaRepository<PgEmailMobileBlacklisted, String> {

	boolean existsByEmail(String email);

	boolean existsByMobile(String mobile);

	@Transactional
	void deleteByIdIn(List<String> ids);

	@Query("SELECT p FROM PgEmailMobileBlacklisted p " +
			"WHERE (:search IS NULL OR p.email LIKE %:search% OR p.mobile LIKE %:search%)")
	Page<PgEmailMobileBlacklisted> search(@Param("search") String search, Pageable pageable);
}
