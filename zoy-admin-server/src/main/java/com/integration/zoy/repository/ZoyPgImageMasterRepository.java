package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgImageMaster;

@Repository
public interface ZoyPgImageMasterRepository extends JpaRepository<ZoyPgImageMaster, String> {
}

