package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyShareMaster;

@Repository
public interface ZoyShareMasterRepository extends JpaRepository<ZoyShareMaster, String> {
}