package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgRoomBeds;
import com.integration.zoy.entity.ZoyPgRoomBedsId;

@Repository
public interface ZoyPgRoomBedsRepository extends JpaRepository<ZoyPgRoomBeds, ZoyPgRoomBedsId> {
	
}
