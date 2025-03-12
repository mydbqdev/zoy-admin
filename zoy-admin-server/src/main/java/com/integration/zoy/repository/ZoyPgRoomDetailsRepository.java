package com.integration.zoy.repository;

import java.util.List;

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
			+ "where zppd.property_id =:propertyId and zprd.room_name =:roomName and zppfd.floor_name =:floorName",nativeQuery = true)
	ZoyPgRoomDetails findRoomDetails(String propertyId, String roomName,String floorName);
	
	@Query(value = "SELECT rd.room_id " +
            "FROM pgowners.zoy_pg_room_details rd " +
            "JOIN pgowners.zoy_pg_floor_rooms fr ON rd.room_id = fr.room_id " +
            "JOIN pgowners.zoy_pg_property_floors pf ON fr.floor_id = pf.floor_id " +
            "WHERE pf.property_id = :propertyId", nativeQuery = true)
List<String> findAllRoomIdsByPropertyId(@Param("propertyId") String propertyId);
	
	 @Modifying
	    @Transactional
	    @Query(value = "DELETE FROM pgowners.zoy_pg_room_details WHERE room_id IN :roomIds", nativeQuery = true)
	    void deleteByRoomIds(@Param("roomIds") List<String> roomIds);
	 
	 @Query(value = "select pgowners.check_dup_room_names(:roomName,:floorId,:propertyId)",nativeQuery = true)
		String checkDuplicateRoomName(String roomName, String floorId, String propertyId);
	 
	 @Query(value = "SELECT * FROM pgowners.zoy_pg_room_details WHERE room_id = :roomId", nativeQuery = true)
	 ZoyPgRoomDetails findRoomNameByRoomId(String roomId);
}
