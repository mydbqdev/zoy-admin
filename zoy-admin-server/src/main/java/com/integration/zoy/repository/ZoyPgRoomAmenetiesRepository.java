package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgRoomAmeneties;

@Repository
public interface ZoyPgRoomAmenetiesRepository extends JpaRepository<ZoyPgRoomAmeneties, ZoyPgRoomAmenetiesId> {
	
}
