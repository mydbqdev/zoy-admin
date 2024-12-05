package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.integration.zoy.entity.PgOwnerMaster;

public interface PgOwnerMaterRepository extends JpaRepository<PgOwnerMaster, String> {

	@Query(value = "select email_id, zoy_code from pg_owner_master where email_id = :emailId", nativeQuery = true)
	List<Object[]> findEmailAndZoyCodeByEmailId(String emailId);

	@Query(value = "SELECT\r\n"
			+ "    pzm.zoy_code,\r\n"
			+ "    pzm.first_name || ' ' || pzm.last_name AS owner_name,\r\n"
			+ "    up.email_id,\r\n"
			+ "    up.mobile_no AS contact,\r\n"
			+ "    pzm.created_at,\r\n"
			+ "    'pending' AS status\r\n"
			+ "FROM\r\n"
			+ "    pgadmin.pg_owner_master pzm\r\n"
			+ "JOIN\r\n"
			+ "    pgcommon.user_profile up ON pzm.email_id = up.email_id\r\n"
			+ "WHERE\r\n"
			+ "    up.enabled = false\r\n"
			+ "ORDER BY\r\n"
			+ "    pzm.created_at DESC", nativeQuery = true)
	List<Object[]> getAllPgOwnerDetails();

	@Query(value = "SELECT * " +
			"FROM pg_owner_master " +
			"WHERE COALESCE(:searchTerm, '') = '' OR (" +
			"  COALESCE(:searchTerm, '') != '' AND (" +
			"    email_id ILIKE '%' || :searchTerm || '%' OR " +
			"    first_name ILIKE '%' || :searchTerm || '%' OR " +
			"    last_name ILIKE '%' || :searchTerm || '%' OR " +
			"    mobile_no ILIKE '%' || :searchTerm || '%' OR " +
			"    zoy_code ILIKE '%' || :searchTerm || '%' " +
			"  )" +
			")" +
			"LIMIT :pageSize OFFSET :offset", 
			nativeQuery = true)
	List<Object[]> PgOwnerDetailsBasedONSearchWithPagination(@Param("searchTerm") String searchTerm, 
			@Param("pageSize") int pageSize, 
			@Param("offset") int offset);

	@Query(value="select * from pg_owner_master po where po.email_id = :email_id",nativeQuery = true)
	PgOwnerMaster getOwnerDetails(String email_id);




	@Query(value = "SELECT\r\n"
			+ "    SPLIT_PART(pg_owner_name, ' ', 1) AS first_name,\r\n"
			+ "    SPLIT_PART(pg_owner_name, ' ', 2) AS last_name,\r\n"
			+ "    pg_owner_email AS email_id,\r\n"
			+ "    pg_owner_mobile AS contact_number,\r\n"
			+ "    ekyc.user_ekyc_masked_aadhaar AS aadhaar_number,\r\n"
			+ "    CONCAT(\r\n"
			+ "        COALESCE(ekyc.user_ekyc_house, ''), ' ',\r\n"
			+ "        COALESCE(ekyc.user_ekyc_street, ''), ' ',\r\n"
			+ "        COALESCE(ekyc.user_ekyc_locality, ''), ' ',\r\n"
			+ "        COALESCE(ekyc.user_ekyc_district, ''), ' ',\r\n"
			+ "        COALESCE(ekyc.user_ekyc_state, ''), ' ',\r\n"
			+ "        COALESCE(ekyc.user_ekyc_country, '')\r\n"
			+ "    ) AS address\r\n"
			+ "FROM \r\n"
			+ "    pgowners.zoy_pg_owner_details owner\r\n"
			+ "JOIN \r\n"
			+ "    pgcommon.ekyc_details ekyc\r\n"
			+ "    ON owner.pg_owner_encrypted_aadhar = ekyc.enocded_aadhaar\r\n"
			+ "WHERE \r\n"
			+ "    pg_owner_id = :pgownerid", nativeQuery = true)
	List<Object[]> getPgOwnerBasicInfo(String pgownerid);



	@Query(value = "SELECT zpod.pg_owner_id,\r\n" +
			"       zpod.pg_owner_name,\r\n" +
			"       CASE \r\n" +
			"           WHEN up.enabled IS TRUE AND EXISTS (\r\n" +
			"               SELECT 1 FROM pgcommon.pg_owner_property_status pops\r\n" +
			"               WHERE pops.pg_owner_id = zpod.pg_owner_id\r\n" +
			"               AND pops.status = TRUE\r\n" + 
			"           ) THEN 'Active'\r\n" +
			"           WHEN up.enabled IS TRUE THEN 'Inactive'\r\n" + 
			"           ELSE 'Pending'\r\n" +
			"       END AS status,\r\n" +
			"       zpod.pg_owner_profile_image\r\n" +
			"FROM pgowners.zoy_pg_owner_details zpod\r\n" +
			"LEFT JOIN pgcommon.user_profile up\r\n" +
			"    ON zpod.pg_owner_email = up.email_id\r\n" +
			"WHERE zpod.pg_owner_id = :pgownerid", nativeQuery = true)
	List<Object[]> getPgOwnerProfileDetails(String pgownerid);





	@Query(value = "   \r\n"
			+ "   SELECT\r\n"
			+ "    pd.property_name AS property_name,\r\n "
			+ "    CASE \r\n"
			+ "        WHEN pops.status THEN 'Active' \r\n"
			+ "        ELSE 'Inactive' \r\n"
			+ "    END AS status,\r\n"
			+ "    zpm.pg_type_name AS pgtype,\r\n"
			+ "    CONCAT(pd.property_locality, ', ', pd.property_city, ', ', pd.property_state, ' - ', pd.property_pincode) AS pgaddress,\r\n"
			+ "    pd.property_manager_name AS managername,\r\n"
			+ "    pd.property_contact_number AS managercontact,\r\n"
			+ "    pd.property_pg_email AS manageremailid,\r\n"
			+ "    COUNT(DISTINCT CASE WHEN pops.status = TRUE THEN pf.floor_id ELSE NULL END) AS numberoffloors,\r\n"
			+ "    COALESCE(SUM(sm.share_occupancy_count), 0) AS total_occupanc,\r\n"
			+ "    pd.property_gst_number AS gstnumber,\r\n"
			+ "    pd.property_cin_numer AS CIN,\r\n"
			+ "    NULL AS ownershiproof,\r\n"
			+ "    COUNT(DISTINCT pf.floor_id) AS totalfloors,\r\n"
			+ "    COUNT(DISTINCT rd.room_id) AS totalroom,\r\n"
			+ "    COALESCE(SUM(sm.share_occupancy_count), 0) AS total_occupie,\r\n"
			+ "    COALESCE(SUM(CASE WHEN rd.room_available THEN 1 ELSE 0 END), 0) AS vacan,\r\n"
			+ "    COALESCE(SUM(CASE WHEN NOT rd.room_available THEN 1 ELSE 0 END), 0) AS occupie,\r\n"
			+ "    rd.room_name AS roomno,\r\n"
			+ "    COUNT(DISTINCT rb.bed_id) AS numberofbeds,\r\n"
			+ "    COALESCE(SUM(CASE WHEN bd.bed_available = 'available' THEN 1 ELSE 0 END), 0) AS numberofbedsavailable,\r\n"
			+ "    tm.security_deposit,\r\n"
			+ "    tm.notice_period,\r\n"
			+ "    rcm.cycle_name AS rent_cycle,\r\n"
			+ "    CASE \r\n"
			+ "        WHEN tm.is_late_payment_fee = TRUE THEN tm.late_payment_fee \r\n"
			+ "        ELSE '0.00' \r\n"
			+ "    END AS late_payment_fee,\r\n"
			+ "    CASE \r\n"
			+ "        WHEN tm.is_late_payment_fee = TRUE THEN tm.grace_period \r\n"
			+ "        ELSE '0' \r\n"
			+ "    END AS grace_period,\r\n"
			+ "    pd.cancellation_fixed_charges AS additional_charges,\r\n"
			+ "    pd.property_agreement_charges AS agreement_charges,\r\n"
			+ "    pd.property_ekyc_charges AS ekyc_charges,\r\n"
			+ "    pd.property_description,\r\n"
			+ "    pd.property_id AS propertyId,\r\n"
			+ "    pf.floor_id AS floorId,\r\n"
			+ "    fr.floor_name AS floorName, \r\n"
			+ "    COUNT(DISTINCT rd.room_id) AS total_rooms,\r\n"
			+ "    COUNT(DISTINCT rb.bed_id) AS total_occupancy,\r\n"
			+ "    COALESCE(SUM(CASE WHEN bd.bed_available = 'occupied' THEN 1 ELSE 0 END), 0) AS occupied,\r\n"
			+ "    COALESCE(SUM(CASE WHEN bd.bed_available = 'available' THEN 1 ELSE 0 END), 0) AS vacant \r\n"
			+ "FROM \r\n"
			+ "    pgowners.zoy_pg_owner_details o\r\n"
			+ "LEFT JOIN \r\n"
			+ "    pgowners.zoy_pg_property_details pd ON o.pg_owner_id = pd.pg_owner_id\r\n"
			+ "LEFT JOIN \r\n"
			+ "    pgowners.zoy_pg_property_floors pf ON pd.property_id = pf.property_id\r\n"
			+ "LEFT JOIN \r\n"
			+ "    pgowners.zoy_pg_property_floor_details fr ON pf.floor_id = fr.floor_id\r\n"
			+ "LEFT JOIN \r\n"
			+ "    pgowners.zoy_pg_floor_rooms frs ON fr.floor_id = frs.floor_id\r\n"
			+ "LEFT JOIN \r\n"
			+ "    pgowners.zoy_pg_room_details rd ON frs.room_id = rd.room_id\r\n"
			+ "LEFT JOIN \r\n"
			+ "    pgowners.zoy_pg_room_beds rb ON rd.room_id = rb.room_id\r\n"
			+ "LEFT JOIN \r\n"
			+ "    pgowners.zoy_pg_bed_details bd ON rb.bed_id = bd.bed_id\r\n"
			+ "LEFT JOIN \r\n"
			+ "    pgowners.zoy_pg_share_master sm ON rd.share_id = sm.share_id\r\n"
			+ "LEFT JOIN \r\n"
			+ "    pgowners.zoy_pg_property_terms_conditions ptc ON pd.property_id = ptc.property_id\r\n"
			+ "LEFT JOIN \r\n"
			+ "    pgowners.zoy_pg_terms_master tm ON ptc.term_id = tm.term_id\r\n"
			+ "LEFT JOIN\r\n"
			+ "    pgowners.zoy_pg_type_master zpm ON pd.pg_type_id = zpm.pg_type_id "
            + "LEFT JOIN\r\n"
            + "    pgowners.zoy_pg_propety_rent_cycle prc ON pd.property_id = prc.property_id "
            + "LEFT JOIN\r\n"
            + "    pgowners.zoy_pg_rent_cycle_master rcm ON prc.cycle_id = rcm.cycle_id "
			+ "LEFT JOIN \r\n"
			+ "    pgcommon.pg_owner_property_status pops ON pd.property_id = pops.property_id\r\n"  
			+ "WHERE \r\n"
			+ "    o.pg_owner_id = :pgownerid\r\n"
			+ "GROUP BY \r\n"
			+ "    pd.property_name,pops.status, pd.property_charge_status, pd.property_locality, pd.property_city, \r\n"
			+ "    pd.property_state, pd.property_pincode, pd.property_manager_name, pd.property_contact_number, \r\n"
			+ "    pd.property_pg_email, pd.property_gst_number, pd.property_cin_numer, rd.room_id, \r\n"
			+ "    tm.security_deposit, tm.notice_period, tm.agreement_duration, tm.late_payment_fee, \r\n"
			+ "    tm.grace_period, pd.cancellation_fixed_charges, pd.property_agreement_charges, \r\n"
			+ "    pd.property_ekyc_charges, pd.property_description,pd.property_id,zpm.pg_type_name, pf.floor_id,fr.floor_name,rcm.cycle_name,tm.is_late_payment_fee", nativeQuery = true)
	List<Object[]> getPropertyDetailsByOwnerId(String pgownerid);


	@Query(value =" SELECT \r\n"
			+ "    b.user_account_number AS account_number,\r\n"
			+ "    b.user_bank_name AS bank_name,\r\n"
			+ "    b.user_bank_branch AS bank_branch,\r\n"
			+ "    b.user_ifsc_code AS ifsc_code,\r\n"
			+ "    CASE \r\n"
			+ "        WHEN b.user_is_primary = TRUE THEN 'Primary Account'\r\n"
			+ "        ELSE 'Secondary Account'\r\n"
			+ "    END AS account_type\r\n"
			+ "FROM \r\n"
			+ "    pgowners.zoy_pg_owner_details o\r\n"
			+ "JOIN \r\n"
			+ "    pgcommon.bank_master bm ON o.pg_owner_id = bm.user_id\r\n"
			+ "JOIN \r\n"
			+ " pgcommon.bank_details b ON bm.user_bank_id = b.user_bank_id\r\n"
			+ "WHERE \r\n"
			+ "    o.pg_owner_id = :pgownerid", nativeQuery = true)
	List<Object[]>getPgOwnerBusinessInfo(String pgownerid);

	@Query(value ="SELECT \r\n"
			+ "    tm.security_deposit,\r\n"
			+ "    tm.notice_period,\r\n"
			+ "    tm.agreement_duration AS rent_cycle,\r\n"
			+ "    tm.late_payment_fee,\r\n"
			+ "    tm.grace_period,\r\n"
			+ "    pd.cancellation_fixed_charges AS additional_charges,\r\n"
			+ "    pd.property_agreement_charges AS agreement_charges,\r\n"
			+ "    pd.property_ekyc_charges AS ekyc_charges,\r\n"
			+ "    pd.property_description,\r\n"
			+ "    pd.property_name\r\n"
			+ "FROM \r\n"
			+ "    pgowners.zoy_pg_owner_details o\r\n"
			+ "LEFT JOIN \r\n"
			+ "    pgowners.zoy_pg_property_details pd ON o.pg_owner_id = pd.pg_owner_id\r\n"
			+ "LEFT JOIN \r\n"
			+ "    pgowners.zoy_pg_property_terms_conditions ptc ON pd.property_id = ptc.property_id\r\n"
			+ "LEFT JOIN \r\n"
			+ "    pgowners.zoy_pg_terms_master tm ON ptc.term_id = tm.term_id\r\n"
			+ "WHERE \r\n"
			+ "    o.pg_owner_id = :pgownerid", nativeQuery = true)
	List<Object[]>getPgOwnerAdditionalInfo(String pgownerid);


	@Query(value = "SELECT " +
			"b.bed_id, " +
			"b.bed_name, " +
			"b.bed_available " +
			"FROM pgowners.zoy_pg_room_beds rb " +
			"JOIN pgowners.zoy_pg_bed_details b ON rb.bed_id = b.bed_id " +
			"WHERE rb.room_id = :roomNo " +
			"ORDER BY b.bed_name", nativeQuery = true)
	List<Object[]> getBedDetailsByRoomId(@Param("roomNo") String roomNo);


	@Query(value = "SELECT r.room_id " +
			"FROM pgowners.zoy_pg_room_details r " +
			"JOIN pgowners.zoy_pg_floor_rooms fr ON r.room_id = fr.room_id " +
			"WHERE r.room_name = :roomNo AND fr.floor_id = :floorId", nativeQuery = true)
	String getRoomIdByRoomNumberAndFloor(@Param("roomNo") String roomNo, @Param("floorId") String floorId);


	@Query(value = "SELECT "
	        + "COUNT(DISTINCT rd.room_id) AS total_rooms, "
	        + "COUNT(DISTINCT rb.bed_id) AS total_occupancy, "
	        + "COALESCE(SUM(CASE WHEN bd.bed_available = 'occupied' THEN 1 ELSE 0 END), 0) AS occupied, "
	        + "COALESCE(SUM(CASE WHEN bd.bed_available = 'available' THEN 1 ELSE 0 END), 0) AS vacant "
	        + "FROM pgowners.zoy_pg_property_floors pf "
	        + "LEFT JOIN pgowners.zoy_pg_property_floor_details fr ON pf.floor_id = fr.floor_id "
	        + "LEFT JOIN pgowners.zoy_pg_floor_rooms frs ON fr.floor_id = frs.floor_id "
	        + "LEFT JOIN pgowners.zoy_pg_room_details rd ON frs.room_id = rd.room_id "
	        + "LEFT JOIN pgowners.zoy_pg_room_beds rb ON rd.room_id = rb.room_id "
	        + "LEFT JOIN pgowners.zoy_pg_bed_details bd ON rb.bed_id = bd.bed_id "
	        + "WHERE pf.floor_id = :floorId "
	        + "GROUP BY pf.floor_id", nativeQuery = true)
	List<Object[]> getFloorDetailsByFloorId(String floorId);

	
	@Query(value = "SELECT " +
            "COUNT(DISTINCT CASE WHEN pops.status = TRUE THEN pf.floor_id ELSE NULL END) AS numberoffloors, " +
            "COUNT(DISTINCT rb.bed_id) AS total_beds " +
            "FROM pgowners.zoy_pg_property_floors pf " +
            "JOIN pgcommon.pg_owner_property_status pops ON pf.property_id = pops.property_id " +
            "LEFT JOIN pgowners.zoy_pg_floor_rooms frs ON pf.floor_id = frs.floor_id " +
            "LEFT JOIN pgowners.zoy_pg_room_details rd ON frs.room_id = rd.room_id " +
            "LEFT JOIN pgowners.zoy_pg_room_beds rb ON rd.room_id = rb.room_id " +
            "WHERE pf.property_id = :propertyId", nativeQuery = true)
	List<Object[]> getActiveFloorsAndTotalBedsByPropertyId(String propertyId);
	
	
	@Query(value = "SELECT \r\n"
			+ "    o.pg_owner_id AS ownerId,\r\n"
			+ "    o.pg_owner_name AS ownerName,\r\n"
			+ "    o.pg_owner_email AS ownerEmail\r\n"
			+ "FROM pgowners.zoy_pg_owner_details o\r\n"
			+ "WHERE o.pg_owner_email = :emailId", nativeQuery = true)
    List<Object[]> getDetailsofEncryptedEMail(String emailId);
	


}
