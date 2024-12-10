package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgFloorRooms;
import com.integration.zoy.entity.ZoyPgFloorRoomsId;

@Repository
public interface ZoyPgFloorRoomsRepository extends JpaRepository<ZoyPgFloorRooms, ZoyPgFloorRoomsId> {
	
}