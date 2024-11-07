package com.integration.zoy.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgTermsMaster;

@Repository
public interface ZoyPgTermsMasterRepository extends JpaRepository<ZoyPgTermsMaster, String> {
	
}