package com.integration.zoy.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgPropertyFloors;
import com.integration.zoy.entity.ZoyPgPropertyFloorsId;

@Repository
public interface ZoyPgPropertyFloorsRepository extends JpaRepository<ZoyPgPropertyFloors, ZoyPgPropertyFloorsId> {
	
}





