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

	@Query(value = "SELECT rd.* "
			+ "FROM pgowners.zoy_pg_property_floor_details fd "
			+ "LEFT JOIN pgowners.zoy_pg_property_floors pf ON fd.floor_id = pf.floor_id "
			+ "LEFT JOIN pgowners.zoy_pg_floor_rooms fr ON fd.floor_id = fr.floor_id "
			+ "LEFT JOIN pgowners.zoy_pg_room_details rd ON rd.room_id = fr.room_id "
			+ "WHERE pf.property_id = :propId AND fd.floor_id = :floorId AND rd.room_id = :roomName", nativeQuery = true)
	Optional<ZoyPgRoomDetails> findByRoomName(String propId, String floorId, String roomName);


	@Query(value = "SELECT  "
			+ "    STRING_AGG(CASE WHEN bda.bed_available = 'available' THEN bda.bed_name END, ', ') AS available_beds, "
			+ "    STRING_AGG(CASE WHEN bda.bed_available = 'occupied' THEN bda.bed_name END, ', ') AS occupied_beds, "
			+ "    rd.room_monthly_rent  "
			+ "FROM pgowners.zoy_pg_property_floor_details fd "
			+ "LEFT JOIN pgowners.zoy_pg_property_floors pf ON fd.floor_id = pf.floor_id "
			+ "LEFT JOIN pgowners.zoy_pg_floor_rooms fr ON fd.floor_id = fr.floor_id "
			+ "LEFT JOIN pgowners.zoy_pg_room_details rd ON rd.room_id = fr.room_id "
			+ "LEFT JOIN pgowners.zoy_pg_room_beds rb ON rb.room_id = rd.room_id "
			+ "LEFT JOIN pgowners.zoy_pg_bed_details bda ON bda.bed_id = rb.bed_id  "
			+ "WHERE pf.property_id = :propId AND fd.floor_name = :floorId AND rd.room_name = :roomName  "
			+ "GROUP BY rd.room_monthly_rent, rd.room_id; ", nativeQuery = true)
	List<String[]> getAllBedsAvailabilityByRoomId(String propId, String floorId, String roomName);


	@Modifying
	@Transactional
	@Query(value = "UPDATE pgowners.zoy_pg_bed_details  "
			+ "SET bed_available = 'available'  "
			+ "WHERE bed_available = 'occupied'  "
			+ "AND bed_id IN ( "
			+ "    SELECT bdo.bed_id  "
			+ "    FROM pgowners.zoy_pg_property_floor_details fd  "
			+ "    LEFT JOIN pgowners.zoy_pg_property_floors pf ON fd.floor_id = pf.floor_id  "
			+ "    LEFT JOIN pgowners.zoy_pg_floor_rooms fr ON fd.floor_id = fr.floor_id  "
			+ "    LEFT JOIN pgowners.zoy_pg_room_details rd ON rd.room_id = fr.room_id  "
			+ "    LEFT JOIN pgowners.zoy_pg_room_beds rb ON rb.room_id = rd.room_id  "
			+ "    LEFT JOIN pgowners.zoy_pg_bed_details bdo ON bdo.bed_id = rb.bed_id   "
			+ "    WHERE  "
			+ " pf.property_id = :propId  "
			+ " AND fd.floor_name = :floorId  "
			+ " AND rd.room_name = :roomName  "
			+ " AND bdo.bed_name = :bed     "
			+ "); ", nativeQuery = true)
	void updateBedAvailabilityByRoomId(String floorId, String propId , String roomName , String bed);

	@Query(value = "SELECT rd.room_id, rd.room_name, rd.room_type, rd.share_id, rd.room_area, rd.room_available, " +
			"rd.room_daily_rent, rd.room_type_id, rd.room_monthly_rent, rd.room_remarks, rd.customer_id " +
			"FROM pgowners.zoy_pg_room_details rd " +
			"LEFT JOIN pgowners.zoy_pg_room_ameneties ra ON rd.room_id = ra.room_id " +
			"LEFT JOIN pgowners.zoy_pg_ameneties_master am ON ra.ameneties_id = am.ameneties_id " +
			"LEFT JOIN pgowners.zoy_pg_property_ameneties pa ON am.ameneties_id = pa.ameneties_id " +
			"LEFT JOIN pgowners.zoy_pg_property_details pd ON pa.property_id = pd.property_id " +
			"WHERE pd.property_id = :propertyId", 
			nativeQuery = true)
	Optional<ZoyPgRoomDetails> findRoomDetailsByPropertyId(@Param("propertyId") String propertyId);



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

	@Query(value = "SELECT rd.room_id, rd.room_name, rd.room_type, rd.room_area, rd.room_daily_rent, rd.room_monthly_rent, rd.room_remarks, " +
			"STRING_AGG(amen.ameneties_id, ',') AS amenities " +
			"FROM pgowners.zoy_pg_room_details rd " +
			"LEFT JOIN pgowners.zoy_pg_room_ameneties amen ON rd.room_id = amen.room_id " +
			"WHERE rd.room_id = :roomId " +
			"GROUP BY rd.room_id", nativeQuery = true)
	List<Object[]> findRoomDetailsWithAmenities(@Param("roomId") String roomId);


	@Query(value = "SELECT  "
			+ "    STRING_AGG(DISTINCT CASE WHEN zpbd.bed_available = 'available' THEN zpbd.bed_id ||'|'||zpbd.bed_name END, ',') AS available_beds, "
			+ "    STRING_AGG(DISTINCT CASE WHEN zpbd.bed_available = 'occupied' THEN zpbd.bed_id ||'|'||zpbd.bed_name END, ',') AS occupied_beds, "
			+ "    coalesce (cast(zprd.room_monthly_rent as numeric),0), "
			+ "    zpsm.share_type , zpsm.share_id "
			+ "FROM pgowners.zoy_pg_floor_rooms zpfr "
			+ "JOIN pgowners.zoy_pg_room_beds zprb ON zprb.room_id = zpfr.room_id "
			+ "JOIN pgowners.zoy_pg_room_details zprd ON zpfr.room_id = zprd.room_id "
			+ "JOIN pgowners.zoy_pg_share_master zpsm ON zpsm.share_id = zprd.share_id "
			+ "JOIN pgowners.zoy_pg_bed_details zpbd ON zprb.bed_id = zpbd.bed_id "
			+ "WHERE zpfr.room_id = :roomId "
			+ "GROUP BY zprd.room_monthly_rent, zpsm.share_type,zpsm.share_id ", nativeQuery = true)
	List<String[]> findRoomBedsDetails(String roomId);
}
