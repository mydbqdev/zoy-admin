package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgRoomTypeMaster;

@Repository
public interface ZoyPgRoomTypeMasterRepository extends JpaRepository<ZoyPgRoomTypeMaster, String> {


	@Query(value = "SELECT room_type_id FROM pgowners.zoy_pg_room_type_master WHERE room_type_name = :roomTypeName", nativeQuery = true)
    String findRoomTypeIdByName(@Param("roomTypeName") String roomTypeName);
}
