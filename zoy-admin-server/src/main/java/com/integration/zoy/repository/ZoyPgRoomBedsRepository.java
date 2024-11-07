package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgRoomBeds;
import com.integration.zoy.entity.ZoyPgRoomBedsId;

@Repository
public interface ZoyPgRoomBedsRepository extends JpaRepository<ZoyPgRoomBeds, ZoyPgRoomBedsId> {
	
}
