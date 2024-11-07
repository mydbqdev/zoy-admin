package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.integration.zoy.entity.ZoyPgImageMaster;

@Repository
public interface ZoyPgImageMasterRepository extends JpaRepository<ZoyPgImageMaster, String> {
}

