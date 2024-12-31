package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.integration.zoy.entity.ZoyPgOwnerBookingDetails;

public interface ZoyPgOwnerBookingDetailsRepository extends JpaRepository<ZoyPgOwnerBookingDetails, String> {
	@Query(value = "select zppf.floor_id,zppfd.floor_name ,zprd.room_id,zprd.room_name ,zpbd.bed_id ,zpbd.bed_name ,zpsm.share_id , "
			+ "zpsm.share_type ,zprcm.cycle_id ,zprcm.cycle_name "
			+ "from pgowners.zoy_pg_property_details zppd "
			+ "join pgowners.zoy_pg_property_floors zppf on zppd.property_id =zppf.property_id  "
			+ "join pgowners.zoy_pg_property_floor_details zppfd on zppfd.floor_id =zppf.floor_id "
			+ "join pgowners.zoy_pg_floor_rooms zpfr on zppf.floor_id =zpfr.floor_id  "
			+ "join pgowners.zoy_pg_room_details zprd on zprd.room_id =zpfr.room_id  "
			+ "join pgowners.zoy_pg_room_beds zprb on zprb.room_id =zpfr.room_id  "
			+ "join pgowners.zoy_pg_bed_details zpbd on zpbd.bed_id =zprb.bed_id "
			+ "join pgowners.zoy_pg_share_master zpsm on zpsm.share_id =zprd.share_id "
			+ "join pgowners.zoy_pg_rent_cycle_master zprcm on zprcm.cycle_name =:lockInPeriod "
			+ "where zprd.room_name=:room and zpbd.bed_name = :selectedBed and zppd.property_id =:propertyId  "
			+ "group by zppf.floor_id,zprd.room_id,zprd.room_name ,zpbd.bed_id ,zpbd.bed_name,zppfd.floor_name ,zpsm.share_id ,zprcm.cycle_id", nativeQuery = true)
	List<String[]> findFloorRoomBedIdsByPropertyName(String propertyId, String room, String selectedBed,String lockInPeriod);

	@Query(value = "select * from pgowners.zoy_pg_owner_booking_details zpobd where property_id =:propertyId",nativeQuery = true)
	List<ZoyPgOwnerBookingDetails> findAllBookingByPropertyId(String propertyId);
	
	@Query(value = "select zppf.floor_id,zppfd.floor_name ,zprd.room_id,zprd.room_name ,zpbd.bed_id ,zpbd.bed_name ,zpsm.share_id , "
			+ "zpsm.share_type ,zprcm.cycle_id ,zprcm.cycle_name "
			+ "from pgowners.zoy_pg_property_details zppd "
			+ "join pgowners.zoy_pg_property_floors zppf on zppd.property_id =zppf.property_id  "
			+ "join pgowners.zoy_pg_property_floor_details zppfd on zppfd.floor_id =zppf.floor_id "
			+ "join pgowners.zoy_pg_floor_rooms zpfr on zppf.floor_id =zpfr.floor_id  "
			+ "join pgowners.zoy_pg_room_details zprd on zprd.room_id =zpfr.room_id  "
			+ "join pgowners.zoy_pg_room_beds zprb on zprb.room_id =zpfr.room_id  "
			+ "join pgowners.zoy_pg_bed_details zpbd on zpbd.bed_id =zprb.bed_id "
			+ "join pgowners.zoy_pg_share_master zpsm on zpsm.share_id =zprd.share_id "
			+ "join pgowners.zoy_pg_rent_cycle_master zprcm on zprcm.cycle_id =:lockInPeriod "
			+ "where zprd.room_id=:room and zpbd.bed_id = :selectedBed and zppd.property_id =:propertyId  "
			+ "group by zppf.floor_id,zprd.room_id,zprd.room_name ,zpbd.bed_id ,zpbd.bed_name,zppfd.floor_name ,zpsm.share_id ,zprcm.cycle_id", nativeQuery = true)
	List<String[]> findFloorRoomBedNameByPropertyName(String propertyId, String room, String selectedBed,String lockInPeriod);
	
	
	@Query(value = "select zppf.floor_id,zppfd.floor_name ,zprd.room_id,zprd.room_name ,zpbd.bed_id ,zpbd.bed_name ,zpsm.share_id ,  "
			+ "zpsm.share_type from pgowners.zoy_pg_property_details zppd  "
			+ "join pgowners.zoy_pg_property_floors zppf on zppd.property_id =zppf.property_id   "
			+ "join pgowners.zoy_pg_property_floor_details zppfd on zppfd.floor_id =zppf.floor_id  "
			+ "join pgowners.zoy_pg_floor_rooms zpfr on zppf.floor_id =zpfr.floor_id   "
			+ "join pgowners.zoy_pg_room_details zprd on zprd.room_id =zpfr.room_id   "
			+ "join pgowners.zoy_pg_room_beds zprb on zprb.room_id =zpfr.room_id   "
			+ "join pgowners.zoy_pg_bed_details zpbd on zpbd.bed_id =zprb.bed_id  "
			+ "join pgowners.zoy_pg_share_master zpsm on zpsm.share_id =zprd.share_id  "
			+ "where zprd.room_name=:room and zpbd.bed_name = :selectedBed and zppd.property_id =:propertyId   "
			+ "group by zppf.floor_id,zprd.room_id,zprd.room_name ,zpbd.bed_id ,zpbd.bed_name,zppfd.floor_name ,zpsm.share_id ", nativeQuery = true)
	List<String[]> findFloorRoomBedIdsByPropertyName(String propertyId, String room, String selectedBed);

}
