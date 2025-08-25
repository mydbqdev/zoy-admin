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

	@Query(value = "select concat(um.user_first_name,' ',um.user_last_name) as tenant_name, "
			+ "ud.user_personal_permanant_address, "
			+ "zpod.pg_owner_name,zpod.pg_owner_mobile , "
			+ "zppd.property_house_area,zppd.property_city,  "
			+ "zpsm.share_type , "
			+ "zpfm.floor_name,zprd.room_name, "
			+ "zpobd.security_deposit, "
			+ "zpobd.fixed_rent, "
			+ "to_char(zpobd.in_date ,'dd-mm-yyyy') as indate, "
			+ "to_char(zpobd.out_date ,'dd-mm-yyyy') as outdate, "
			+ "INITCAP(up.user_payment_result_method) as paymentMode, "
			+ "CONCAT(SPLIT_PART(zprcm.cycle_name, '-', 1), "
			+ "CASE WHEN CAST(SPLIT_PART(zprcm.cycle_name, '-', 1) AS INT) % 100 BETWEEN 11 AND 13 THEN 'th' "
			+ "WHEN CAST(SPLIT_PART(zprcm.cycle_name, '-', 1) AS INT) % 10 = 1 THEN 'st' "
			+ "WHEN CAST(SPLIT_PART(zprcm.cycle_name, '-', 1) AS INT) % 10 = 2 THEN 'nd' "
			+ "WHEN CAST(SPLIT_PART(zprcm.cycle_name, '-', 1) AS INT) % 10 = 3 THEN 'rd' ELSE 'th' end ) AS rentCycle, "
			+ "CONCAT(case WHEN EXTRACT(YEAR FROM age(zpobd.out_date, zpobd.in_date)) * 12 + EXTRACT(MONTH FROM age(zpobd.out_date, zpobd.in_date)) > 0  "
			+ "THEN EXTRACT(YEAR FROM age(zpobd.out_date, zpobd.in_date)) * 12 + EXTRACT(MONTH FROM age(zpobd.out_date, zpobd.in_date)) || ' month ' ELSE '' END, "
			+ "case WHEN EXTRACT(DAY FROM age(zpobd.out_date,zpobd.in_date)) > 0 THEN EXTRACT(DAY FROM age(zpobd.out_date, zpobd.in_date)) || ' days' ELSE '' END) AS duration, "
			+ "(zcpm.address_line_one ||','||zcpm.address_line_two ||','||zcpm.address_line_three||','||zcpm.pin_code) as office_address,"
			+ "um.user_mobile ,zpod.pg_owner_email  "
			+ "from pgowners.zoy_pg_owner_booking_details zpobd  "
			+ "join pgowners.zoy_pg_property_details zppd on zpobd.property_id =zppd.property_id  "
			+ "join pgowners.zoy_pg_owner_details zpod on zppd.pg_owner_id =zpod.pg_owner_id  "
			+ "join pgowners.zoy_pg_share_master zpsm on zpsm.share_id =zpobd.\"share\" "
			+ "join pgowners.zoy_pg_property_floor_details zppfd on zppfd.floor_id =zpobd.floor  "
			+ "join pgowners.zoy_pg_floor_master zpfm on zppfd.master_floor_id =zpfm.floor_id  "
			+ "join pgusers.user_master um on um.user_id =zpobd.tenant_id  "
			+ "join pgusers.user_details ud on um.user_id =ud.user_id "
			+ "join pgowners.zoy_pg_rent_cycle_master zprcm on zprcm.cycle_id =zpobd.lock_in_period  "
			+ "join pgowners.zoy_pg_room_details zprd on zprd.room_id =zpobd.room  "
			+ "left join pgusers.user_dues ud2 on ud2.user_booking_id =zpobd.booking_id and ud2.user_money_due_description ='Security Deposit' "
			+ "left join pgusers.user_payment_due upd on upd.user_money_due_id =ud2.user_money_due_id  "
			+ "left join pgusers.user_payments up on upd.user_payment_id =up.user_payment_id  "
			+ "cross join pgadmin.zoy_company_profile_master zcpm  "
			+ "where zpobd.booking_id =:bookingId  and zcpm.\"type\" ='Head Office'",nativeQuery = true)
	List<String[]> getRentalAgreementDetails(String bookingId);

}
