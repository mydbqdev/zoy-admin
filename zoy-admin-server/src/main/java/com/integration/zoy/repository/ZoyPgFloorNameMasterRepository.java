package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgFloorNameMaster;
@Repository
public interface ZoyPgFloorNameMasterRepository extends JpaRepository<ZoyPgFloorNameMaster, String>{

//	@Query(value = "SELECT room_type_id FROM pgowners.zoy_pg_room_type_master WHERE room_type_name = :roomTypeName", nativeQuery = true)
//    String findRoomTypeIdByName(@Param("roomTypeName") String roomTypeName);
	 @Query(value = "select * from pgowners.zoy_pg_floor_master order by floor_name", nativeQuery = true)
	  List<ZoyPgFloorNameMaster> findAllFloorNameData();
}
