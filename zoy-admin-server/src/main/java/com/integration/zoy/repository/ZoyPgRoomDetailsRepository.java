package com.integration.zoy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.integration.zoy.entity.ZoyPgRoomDetails;

@Repository
public interface ZoyPgRoomDetailsRepository extends JpaRepository<ZoyPgRoomDetails, String> {

	
	@Query(value = "select zprd.* from pgowners.zoy_pg_property_details zppd "
			+ "join pgowners.zoy_pg_property_floors zppf on zppf.property_id =zppd.property_id "
			+ "join pgowners.zoy_pg_property_floor_details zppfd on zppf.floor_id =zppfd.floor_id "
			+ "join pgowners.zoy_pg_floor_rooms zpfr on zpfr.floor_id =zppfd.floor_id "
			+ "join pgowners.zoy_pg_room_details zprd on zprd.room_id =zpfr.room_id "
			+ "where zppd.property_id =:propertyId and zprd.room_name =:roomName",nativeQuery = true)
	ZoyPgRoomDetails findRoomDetails(String propertyId, String roomName);
}
