package com.integration.zoy.repository;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgDueTypeMaster;

@Repository
public interface ZoyPgDueTypeMasterRepository extends JpaRepository<ZoyPgDueTypeMaster, String> {
	
}
