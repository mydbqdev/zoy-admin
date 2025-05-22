package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.integration.zoy.entity.PgOwnerMaster;

public interface PgOwnerMaterRepository extends JpaRepository<PgOwnerMaster, String> {

	@Query(value = "select email_id, zoy_code from pg_owner_master where email_id = :emailId", nativeQuery = true)
	List<Object[]> findEmailAndZoyCodeByEmailId(String emailId);
	
	@Query(value = "select mobile_no from pg_owner_master where mobile_no = :mobile_no", nativeQuery = true)
	String findPhoneNumber(String mobile_no);
	
	@Query(value = " SELECT  "
			+ "    pzm.zoy_code, "
			+ "    pzm.first_name || ' ' || pzm.last_name AS owner_name,  "
			+ "    up.email_id,  "
			+ "    up.mobile_no AS contact,  "
			+ "    pzm.created_at,  "
			+ "    'pending' AS status,  "
			+ "     pzm.zoy_share,  "
			+ "     pzm.register_id "
			+ "FROM  "
			+ "    pgadmin.pg_owner_master pzm  "
			+ "JOIN  "
			+ "    pgcommon.user_profile up ON pzm.email_id = up.email_id  "
			+ "WHERE  "
			+ "    up.enabled = false  "
			+ "ORDER BY  "
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




	@Query(value = "SELECT "
			+ "    SPLIT_PART(pg_owner_name, ' ', 1) AS first_name, "
			+ "    SPLIT_PART(pg_owner_name, ' ', 2) AS last_name, "
			+ "    pg_owner_email AS email_id, "
			+ "    pg_owner_mobile AS contact_number, "
			+ "    ekyc.user_ekyc_masked_aadhaar AS aadhaar_number, "
			+ "    CONCAT( "
			+ "        COALESCE(ekyc.user_ekyc_house, ''), ' ', "
			+ "        COALESCE(ekyc.user_ekyc_street, ''), ' ', "
			+ "        COALESCE(ekyc.user_ekyc_locality, ''), ' ', "
			+ "        COALESCE(ekyc.user_ekyc_district, ''), ' ', "
			+ "        COALESCE(ekyc.user_ekyc_state, ''), ' ', "
			+ "        COALESCE(ekyc.user_ekyc_country, '') "
			+ "    ) AS address "
			+ "FROM  "
			+ "    pgowners.zoy_pg_owner_details owner "
			+ "JOIN  "
			+ "    pgcommon.ekyc_details ekyc "
			+ "    ON owner.pg_owner_encrypted_aadhar = ekyc.enocded_aadhaar "
			+ "WHERE  "
			+ "    pg_owner_id = :pgownerid", nativeQuery = true)
	List<Object[]> getPgOwnerBasicInfo(String pgownerid);



	@Query(value = "SELECT zpod.pg_owner_id, " +
			"       zpod.pg_owner_name, " +
			"       CASE  " +
			"           WHEN up.enabled IS TRUE AND EXISTS ( " +
			"               SELECT 1 FROM pgcommon.pg_owner_property_status pops " +
			"               WHERE pops.pg_owner_id = zpod.pg_owner_id " +
			"               AND pops.status = TRUE " + 
			"           ) THEN 'Active' " +
			"           WHEN up.enabled IS TRUE THEN 'Inactive' " + 
			"           ELSE 'Pending' " +
			"       END AS status, " +
			"       zpod.pg_owner_profile_image " +
			"FROM pgowners.zoy_pg_owner_details zpod " +
			"LEFT JOIN pgcommon.user_profile up " +
			"    ON zpod.pg_owner_email = up.email_id " +
			"WHERE zpod.pg_owner_id = :pgownerid", nativeQuery = true)
	List<Object[]> getPgOwnerProfileDetails(String pgownerid);





	@Query(value = "    "
			+ "   SELECT "
			+ "    pd.property_name AS property_name,  "
			+ "    CASE  "
			+ "        WHEN pops.status THEN 'Active'  "
			+ "        ELSE 'Inactive'  "
			+ "    END AS status, "
			+ "    zpm.pg_type_name AS pgtype, "
			+ "    CONCAT(pd.property_locality, ', ', pd.property_city, ', ', pd.property_state, ' - ', pd.property_pincode) AS pgaddress, "
			+ "    pd.property_manager_name AS managername, "
			+ "    pd.property_contact_number AS managercontact, "
			+ "    pd.property_pg_email AS manageremailid, "
			+ "    COUNT(DISTINCT CASE WHEN pops.status = TRUE THEN pf.floor_id ELSE NULL END) AS numberoffloors, "
			+ "    COALESCE(SUM(sm.share_occupancy_count), 0) AS total_occupanc, "
			+ "    pd.property_gst_number AS gstnumber, "
			+ "    pd.property_cin_numer AS CIN, "
			+ "    NULL AS ownershiproof, "
			+ "    COUNT(DISTINCT pf.floor_id) AS totalfloors, "
			+ "    COUNT(DISTINCT rd.room_id) AS totalroom, "
			+ "    COALESCE(SUM(sm.share_occupancy_count), 0) AS total_occupie, "
			+ "    COALESCE(SUM(CASE WHEN rd.room_available THEN 1 ELSE 0 END), 0) AS vacan, "
			+ "    COALESCE(SUM(CASE WHEN NOT rd.room_available THEN 1 ELSE 0 END), 0) AS occupie, "
			+ "    rd.room_name AS roomno, "
			+ "    COUNT(DISTINCT rb.bed_id) AS numberofbeds, "
			+ "    COALESCE(SUM(CASE WHEN bd.bed_available = 'available' THEN 1 ELSE 0 END), 0) AS numberofbedsavailable, "
			+ "    tm.security_deposit, "
			+ "    tm.notice_period, "
			+ "    rcm.cycle_name AS rent_cycle, "
			+ "    CASE  "
			+ "        WHEN tm.is_late_payment_fee = TRUE THEN tm.late_payment_fee  "
			+ "        ELSE '0.00'  "
			+ "    END AS late_payment_fee, "
			+ "    CASE  "
			+ "        WHEN tm.is_late_payment_fee = TRUE THEN tm.grace_period  "
			+ "        ELSE '0'  "
			+ "    END AS grace_period, "
			+ "    pd.cancellation_fixed_charges AS additional_charges, "
			+ "    pd.property_agreement_charges AS agreement_charges, "
			+ "    pd.property_ekyc_charges AS ekyc_charges, "
			+ "    pd.property_description, "
			+ "    pd.property_id AS propertyId, "
			+ "    pf.floor_id AS floorId, "
			+ "    fr.floor_name AS floorName,  "
			+ "    COUNT(DISTINCT rd.room_id) AS total_rooms, "
			+ "    COUNT(DISTINCT rb.bed_id) AS total_occupancy, "
			+ "    COALESCE(SUM(CASE WHEN bd.bed_available = 'occupied' THEN 1 ELSE 0 END), 0) AS occupied, "
			+ "    COALESCE(SUM(CASE WHEN bd.bed_available = 'available' THEN 1 ELSE 0 END), 0) AS vacant  "
			+ "FROM  "
			+ "    pgowners.zoy_pg_owner_details o "
			+ "LEFT JOIN  "
			+ "    pgowners.zoy_pg_property_details pd ON o.pg_owner_id = pd.pg_owner_id "
			+ "LEFT JOIN  "
			+ "    pgowners.zoy_pg_property_floors pf ON pd.property_id = pf.property_id "
			+ "LEFT JOIN  "
			+ "    pgowners.zoy_pg_property_floor_details fr ON pf.floor_id = fr.floor_id "
			+ "LEFT JOIN  "
			+ "    pgowners.zoy_pg_floor_rooms frs ON fr.floor_id = frs.floor_id "
			+ "LEFT JOIN  "
			+ "    pgowners.zoy_pg_room_details rd ON frs.room_id = rd.room_id "
			+ "LEFT JOIN  "
			+ "    pgowners.zoy_pg_room_beds rb ON rd.room_id = rb.room_id "
			+ "LEFT JOIN  "
			+ "    pgowners.zoy_pg_bed_details bd ON rb.bed_id = bd.bed_id "
			+ "LEFT JOIN  "
			+ "    pgowners.zoy_pg_share_master sm ON rd.share_id = sm.share_id "
			+ "LEFT JOIN  "
			+ "    pgowners.zoy_pg_property_terms_conditions ptc ON pd.property_id = ptc.property_id "
			+ "LEFT JOIN  "
			+ "    pgowners.zoy_pg_terms_master tm ON ptc.term_id = tm.term_id "
			+ "LEFT JOIN "
			+ "    pgowners.zoy_pg_type_master zpm ON pd.pg_type_id = zpm.pg_type_id "
            + "LEFT JOIN "
            + "    pgowners.zoy_pg_propety_rent_cycle prc ON pd.property_id = prc.property_id "
            + "LEFT JOIN "
            + "    pgowners.zoy_pg_rent_cycle_master rcm ON prc.cycle_id = rcm.cycle_id "
			+ "LEFT JOIN  "
			+ "    pgcommon.pg_owner_property_status pops ON pd.property_id = pops.property_id "  
			+ "WHERE  "
			+ "    o.pg_owner_id = :pgownerid "
			+ "GROUP BY  "
			+ "    pd.property_name,pops.status, pd.property_charge_status, pd.property_locality, pd.property_city,  "
			+ "    pd.property_state, pd.property_pincode, pd.property_manager_name, pd.property_contact_number,  "
			+ "    pd.property_pg_email, pd.property_gst_number, pd.property_cin_numer, rd.room_id,  "
			+ "    tm.security_deposit, tm.notice_period, tm.agreement_duration, tm.late_payment_fee,  "
			+ "    tm.grace_period, pd.cancellation_fixed_charges, pd.property_agreement_charges,  "
			+ "    pd.property_ekyc_charges, pd.property_description,pd.property_id,zpm.pg_type_name, pf.floor_id,fr.floor_name,rcm.cycle_name,tm.is_late_payment_fee", nativeQuery = true)
	List<Object[]> getPropertyDetailsByOwnerId(String pgownerid);


	@Query(value =" SELECT  "
			+ "    b.user_account_number AS account_number, "
			+ "    b.user_bank_name AS bank_name, "
			+ "    b.user_bank_branch AS bank_branch, "
			+ "    b.user_ifsc_code AS ifsc_code, "
			+ "    CASE  "
			+ "        WHEN b.user_is_primary = TRUE THEN 'Primary Account' "
			+ "        ELSE 'Secondary Account' "
			+ "    END AS account_type "
			+ "FROM  "
			+ "    pgowners.zoy_pg_owner_details o "
			+ "JOIN  "
			+ "    pgcommon.bank_master bm ON o.pg_owner_id = bm.user_id "
			+ "JOIN  "
			+ " pgcommon.bank_details b ON bm.user_bank_id = b.user_bank_id "
			+ "WHERE  "
			+ "    o.pg_owner_id = :pgownerid", nativeQuery = true)
	List<Object[]>getPgOwnerBusinessInfo(String pgownerid);

	@Query(value ="SELECT  "
			+ "    tm.security_deposit, "
			+ "    tm.notice_period, "
			+ "    tm.agreement_duration AS rent_cycle, "
			+ "    tm.late_payment_fee, "
			+ "    tm.grace_period, "
			+ "    pd.cancellation_fixed_charges AS additional_charges, "
			+ "    pd.property_agreement_charges AS agreement_charges, "
			+ "    pd.property_ekyc_charges AS ekyc_charges, "
			+ "    pd.property_description, "
			+ "    pd.property_name "
			+ "FROM  "
			+ "    pgowners.zoy_pg_owner_details o "
			+ "LEFT JOIN  "
			+ "    pgowners.zoy_pg_property_details pd ON o.pg_owner_id = pd.pg_owner_id "
			+ "LEFT JOIN  "
			+ "    pgowners.zoy_pg_property_terms_conditions ptc ON pd.property_id = ptc.property_id "
			+ "LEFT JOIN  "
			+ "    pgowners.zoy_pg_terms_master tm ON ptc.term_id = tm.term_id "
			+ "WHERE  "
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
	
	
	@Query(value = "SELECT  "
			+ "    o.pg_owner_id AS ownerId, "
			+ "    o.pg_owner_name AS ownerName, "
			+ "    o.pg_owner_email AS ownerEmail "
			+ "FROM pgowners.zoy_pg_owner_details o "
			+ "WHERE o.pg_owner_email = :emailId", nativeQuery = true)
    List<Object[]> getDetailsofEncryptedEMail(String emailId);

    
    
    @Query(value="select zpod.pg_owner_id,zpod.pg_owner_name ,pom.first_name ,pom.last_name,zpod.pg_owner_email ,zpod.pg_owner_mobile,  "
    		+ "ekyc.user_ekyc_masked_aadhaar AS aadhaar_number,  "
    		+ "CONCAT(COALESCE(ekyc.user_ekyc_house, ''), ' ',COALESCE(ekyc.user_ekyc_street, ''), ' ',  "
    		+ "COALESCE(ekyc.user_ekyc_locality, ''), ' ',COALESCE(ekyc.user_ekyc_district, ''), ' ',  "
    		+ "COALESCE(ekyc.user_ekyc_state, ''), ' ',COALESCE(ekyc.user_ekyc_country, '')) AS address,  "
    		+ "CASE WHEN up.enabled IS TRUE AND EXISTS (   "
    		+ "SELECT 1 FROM pgcommon.pg_owner_property_status pops WHERE pops.pg_owner_id = zpod.pg_owner_id   "
    		+ "AND pops.status = TRUE) THEN 'Active' WHEN up.pwd IS NULL then 'Not Registered' ELSE 'Registered'   "
    		+ "END AS owner_status,zpod.pg_owner_profile_image,  "
    		+ "string_agg(distinct bd.user_account_number||'|'||bd.user_bank_name||'|'||bd.user_bank_branch ||'|'||bd.user_ifsc_code   "
    		+ "||'|'||(case WHEN bd.user_is_primary = TRUE THEN 'Primary Account' ELSE 'Secondary Account' end),',')as bank_details,   "
    		+ "zppd.property_id,  "
    		+ "zppd.property_name,CASE WHEN pops.status THEN 'Active' ELSE 'Inactive' END AS status ,  "
    		+ "zpm.pg_type_name AS pgtype,  "
    		+ "CONCAT(zppd.property_locality, ', ', zppd.property_city, ', ', zppd.property_state, ' - ', zppd.property_pincode) AS pgaddress,  "
    		+ "zppd.property_manager_name AS managername,  "
    		+ "zppd.property_contact_number AS managercontact,  "
    		+ "zppd.property_pg_email AS manageremailid,  "
    		+ "zppd.property_gst_number AS gstnumber,  "
    		+ "zppd.property_cin_numer AS CIN,  "
    		+ "NULL AS ownershiproof,tm.security_deposit,tm.notice_period,string_agg(distinct rcm.cycle_name,',') AS rent_cycle,  "
    		+ "CASE WHEN tm.is_late_payment_fee = TRUE THEN tm.late_payment_fee ELSE '0.00' END AS late_payment_fee,  "
    		+ "CASE WHEN tm.is_late_payment_fee = TRUE THEN tm.grace_period ELSE '0' END AS grace_period,  "
    		+ "zppd.cancellation_fixed_charges AS additional_charges,zppd.property_agreement_charges AS agreement_charges,  "
    		+ "zppd.property_ekyc_charges AS ekyc_charges,zppd.property_description,  "
    		+ "zppfd.floor_id ,zppfd.floor_name ,zprd.room_id ,zprd.room_name ,  "
    		+ "STRING_AGG(DISTINCT CASE WHEN zpbd.bed_available = 'available' THEN zpbd.bed_id ||'|'||zpbd.bed_name||'|'||zpbd.bed_available END, ',') AS available_beds,  "
    		+ "STRING_AGG(DISTINCT CASE WHEN zpbd.bed_available = 'occupied' THEN zpbd.bed_id ||'|'||zpbd.bed_name||'|'||zpbd.bed_available END, ',') AS occupied_beds,  "
    		+ "zppd.zoy_share AS zoyShare "
    		+ "from pgowners.zoy_pg_owner_details zpod   "
    		+ "join pgadmin.pg_owner_master pom on pom.zoy_code =zpod.zoy_code   "
    		+ "left join pgcommon.ekyc_details ekyc on zpod.pg_owner_encrypted_aadhar =ekyc.enocded_aadhaar   "
    		+ "LEFT JOIN pgcommon.user_profile up ON zpod.pg_owner_email = up.email_id   "
    		+ "left join pgowners.zoy_pg_property_details zppd  on zppd.pg_owner_id =zpod.pg_owner_id   "
    		+ "left join pgowners.zoy_pg_property_floors zppf on zppf.property_id = zppd.property_id  "
    		+ "left join pgcommon.bank_master bm on bm.user_id = zpod.pg_owner_id   "
    		+ "left join pgcommon.bank_details bd on bd.user_bank_id = bm.user_bank_id   "
    		+ "left join pgowners.zoy_pg_property_floor_details zppfd on zppfd.floor_id = zppf.floor_id and zppfd.floor_status = true   "
    		+ "left join pgowners.zoy_pg_floor_rooms zpfr on zpfr.floor_id = zppfd.floor_id   "
    		+ "left join pgowners.zoy_pg_room_details zprd on zprd.room_id =zpfr.room_id and zprd.room_status = true   "
    		+ "left join pgowners.zoy_pg_room_beds zprb on zprb.room_id =zprd.room_id   "
    		+ "left join pgowners.zoy_pg_bed_details zpbd on zpbd.bed_id =zprb.bed_id and zpbd.bed_status = true   "
    		+ "LEFT JOIN pgcommon.pg_owner_property_status pops ON zppd.property_id = pops.property_id    "
    		+ "LEFT join pgowners.zoy_pg_share_master sm ON zprd.share_id = sm.share_id  "
    		+ "LEFT JOIN pgowners.zoy_pg_property_terms_conditions ptc ON zppd.property_id = ptc.property_id  "
    		+ "LEFT JOIN pgowners.zoy_pg_terms_master tm ON ptc.term_id = tm.term_id  "
    		+ "LEFT join pgowners.zoy_pg_type_master zpm ON zppd.pg_type_id = zpm.pg_type_id   "
    		+ "LEFT join pgowners.zoy_pg_propety_rent_cycle prc ON zppd.property_id = prc.property_id   "
    		+ "LEFT join pgowners.zoy_pg_rent_cycle_master rcm ON prc.cycle_id = rcm.cycle_id   "
    		+ "where zpod.pg_owner_id =:ownerId   "
    		+ "group by pom.zoy_code,zpod.pg_owner_id,ekyc.enocded_aadhaar,zppd.property_id,up.enabled, pops.status,zpm.pg_type_name,  "
    		+ "tm.term_id,zppfd.floor_id,zprd.room_id,up.pwd,zppd.zoy_share ",nativeQuery = true)
	List<String[]> getOwnerPropertyDetails(String ownerId);
	

	
	
	
	@Query(value = "SELECT " +
	        "  effective_date AS futureeffectivedate, " +
	        "  force_check_out_days AS newdays " +
	        "FROM pgowners.zoy_pg_force_check_out " +
	        "WHERE is_approved = true AND ( " +
	        "  effective_date = TO_CHAR(CAST(:currentDate AS DATE) + INTERVAL '7 days', 'YYYY-MM-DD') OR " +
	        "  effective_date = TO_CHAR(CAST(:currentDate AS DATE) + INTERVAL '1 day', 'YYYY-MM-DD') OR " +
	        "  effective_date = TO_CHAR(CAST(:currentDate AS DATE), 'YYYY-MM-DD') " +
	        ") " +
	        "UNION " +
	        "SELECT pasteffectivedate, olddays " +
	        "FROM ( " +
	        "  SELECT effective_date AS pasteffectivedate, force_check_out_days AS olddays " +
	        "  FROM pgowners.zoy_pg_force_check_out " +
	        "  WHERE is_approved = TRUE AND " +
	        "    TO_DATE(effective_date, 'YYYY-MM-DD') < CAST(:currentDate AS DATE) " +
	        "  ORDER BY TO_DATE(effective_date, 'YYYY-MM-DD') DESC " +
	        "  LIMIT 1 " +
	        ") adas",
	        nativeQuery = true)
	List<String[]> findCheckOutDaysByDate(@Param("currentDate") String currentDate);
	
	@Query(value = "SELECT  "
			+ "  effective_date AS effectivedate,  "
			+ "  no_rental_agreement_days AS days "
			+ "FROM pgowners.zoy_pg_no_rental_agreement  "
			+ "WHERE is_approved = true AND (  "
			+ "  effective_date = TO_CHAR(CAST(:currentDate AS DATE) + INTERVAL '7 days', 'YYYY-MM-DD') OR  "
			+ "  effective_date = TO_CHAR(CAST(:currentDate AS DATE) + INTERVAL '1 day', 'YYYY-MM-DD') OR  "
			+ "  effective_date = TO_CHAR(CAST(:currentDate AS DATE), 'YYYY-MM-DD')  "
			+ ")  "
			+ "UNION  "
			+ "SELECT effectivedate, days  "
			+ "FROM (  "
			+ "  SELECT effective_date AS effectivedate, no_rental_agreement_days AS days  "
			+ "  FROM pgowners.zoy_pg_no_rental_agreement  "
			+ "  WHERE is_approved = TRUE AND  "
			+ "    TO_DATE(effective_date, 'YYYY-MM-DD') < CAST(:currentDate AS DATE)  "
			+ "  ORDER BY TO_DATE(effective_date, 'YYYY-MM-DD') DESC  "
			+ "  LIMIT 1  "
			+ ") adas",
	        nativeQuery = true)
	List<String[]> findNoRentalAgreementDaysByDate(@Param("currentDate") String currentDate);
	
	@Query(value = "SELECT  "
			+ "  effective_date AS effectivedate,  "
			+ "  fixed_token AS fixedamount, "
			+ "  variable_token as percentage "
			+ "FROM pgowners.zoy_pg_token_details  "
			+ "WHERE is_approved = true AND (  "
			+ "  effective_date = TO_CHAR(CAST(:currentDate AS DATE) + INTERVAL '7 days', 'YYYY-MM-DD') OR  "
			+ "  effective_date = TO_CHAR(CAST(:currentDate AS DATE) + INTERVAL '1 day', 'YYYY-MM-DD') OR  "
			+ "  effective_date = TO_CHAR(CAST(:currentDate AS DATE), 'YYYY-MM-DD')  "
			+ ")  "
			+ "UNION  "
			+ "SELECT effectivedate, fixedamount,percentage "
			+ "FROM (  "
			+ "  SELECT effective_date AS effectivedate, fixed_token AS fixedamount,variable_token as percentage "
			+ "  FROM pgowners.zoy_pg_token_details "
			+ "  WHERE is_approved = TRUE AND  "
			+ "    TO_DATE(effective_date, 'YYYY-MM-DD') < CAST(:currentDate AS DATE)  "
			+ "  ORDER BY TO_DATE(effective_date, 'YYYY-MM-DD') DESC  "
			+ "  LIMIT 1  "
			+ ") adas",
	        nativeQuery = true)
	List<String[]> findTokenAdvanceDetailsByDate(@Param("currentDate") String currentDate);
	
	@Query(value = "SELECT  "
			+ "  effective_date AS effectivedate,  "
			+ "  security_deposit_min AS minamount, "
			+ "  security_deposit_max as maxamount "
			+ "FROM pgowners.zoy_pg_security_deposit_details  "
			+ "WHERE is_approved = true AND (  "
			+ "  effective_date = TO_CHAR(CAST(:currentDate AS DATE) + INTERVAL '7 days', 'YYYY-MM-DD') OR  "
			+ "  effective_date = TO_CHAR(CAST(:currentDate AS DATE) + INTERVAL '1 day', 'YYYY-MM-DD') OR  "
			+ "  effective_date = TO_CHAR(CAST(:currentDate AS DATE), 'YYYY-MM-DD')  "
			+ ")  "
			+ "UNION  "
			+ "SELECT effectivedate, minamount,maxamount "
			+ "FROM (  "
			+ "  SELECT effective_date AS effectivedate,security_deposit_min AS minamount,security_deposit_max as maxamount "
			+ "  FROM pgowners.zoy_pg_security_deposit_details "
			+ "  WHERE is_approved = TRUE AND  "
			+ "    TO_DATE(effective_date, 'YYYY-MM-DD') < CAST(:currentDate AS DATE)  "
			+ "  ORDER BY TO_DATE(effective_date, 'YYYY-MM-DD') DESC  "
			+ "  LIMIT 1  "
			+ ") adas",
	        nativeQuery = true)
	List<String[]> findSecurityDepositDetailsByDate(@Param("currentDate") String currentDate);
	
	@Query(value = "SELECT  "
			+ "  effective_date AS effectivedate, "
			+ "  monthly_rent, "
			+ "  igst_percentage  "
			+ "FROM pgowners.zoy_pg_rent_gst   "
			+ "WHERE is_approved = true AND (   "
			+ "  effective_date = TO_CHAR(CAST(:currentDate AS DATE) + INTERVAL '7 days', 'YYYY-MM-DD') OR   "
			+ "  effective_date = TO_CHAR(CAST(:currentDate AS DATE) + INTERVAL '1 day', 'YYYY-MM-DD') OR   "
			+ "  effective_date = TO_CHAR(CAST(:currentDate AS DATE), 'YYYY-MM-DD')   "
			+ ")   "
			+ "UNION   "
			+ "SELECT effectivedate, monthly_rent,igst_percentage  "
			+ "FROM (   "
			+ "  SELECT effective_date AS effectivedate, monthly_rent,igst_percentage  "
			+ "  FROM pgowners.zoy_pg_rent_gst   "
			+ "  WHERE is_approved = TRUE AND   "
			+ "    TO_DATE(effective_date, 'YYYY-MM-DD') < CAST(:currentDate AS DATE)   "
			+ "  ORDER BY TO_DATE(effective_date, 'YYYY-MM-DD') DESC   "
			+ "  LIMIT 1   "
			+ ") adas",
	        nativeQuery = true)
	List<String[]> findGSTDetailsByDate(@Param("currentDate") String currentDate);
	
	
	
	@Query(value = " SELECT  "
			+ "  effective_date AS effectivedate, "
			+ "   owner_document_charges, "
			+ "   owner_ekyc_charges, "
			+ "   tenant_document_charges, "
			+ "   tenant_ekyc_charges "
			+ "FROM pgowners.zoy_pg_other_charges   "
			+ "WHERE is_approved = true AND (   "
			+ "  effective_date = TO_CHAR(CAST(:currentDate AS DATE) + INTERVAL '7 days', 'YYYY-MM-DD') OR   "
			+ "  effective_date = TO_CHAR(CAST(:currentDate AS DATE) + INTERVAL '1 day', 'YYYY-MM-DD') OR   "
			+ "  effective_date = TO_CHAR(CAST(:currentDate AS DATE), 'YYYY-MM-DD')   "
			+ ")   "
			+ "UNION   "
			+ "SELECT effectivedate, owner_document_charges,owner_ekyc_charges,tenant_document_charges,tenant_ekyc_charges "
			+ "FROM (   "
			+ "  SELECT effective_date AS effectivedate,owner_document_charges,owner_ekyc_charges,tenant_document_charges,tenant_ekyc_charges  "
			+ "  FROM pgowners.zoy_pg_other_charges   "
			+ "  WHERE is_approved = TRUE AND   "
			+ "    TO_DATE(effective_date, 'YYYY-MM-DD') < CAST(:currentDate AS DATE)   "
			+ "  ORDER BY TO_DATE(effective_date, 'YYYY-MM-DD') DESC   "
			+ "  LIMIT 1   "
			+ ") adas",
	        nativeQuery = true)
	List<String[]> findOtherChargesDetailsByDate(@Param("currentDate") String currentDate);
	
	@Query(value = " SELECT  "
			+ "  effective_date AS effectivedate, "
			+ "  trigger_condition, "
			+ "  auto_cancellation_day  "
			+ "FROM pgowners.zoy_pg_auto_cancellation_after_check_in   "
			+ "WHERE is_approved = true AND (   "
			+ "  effective_date = TO_CHAR(CAST(:currentDate AS DATE) + INTERVAL '7 days', 'YYYY-MM-DD') OR   "
			+ "  effective_date = TO_CHAR(CAST(:currentDate AS DATE) + INTERVAL '1 day', 'YYYY-MM-DD') OR   "
			+ "  effective_date = TO_CHAR(CAST(:currentDate AS DATE), 'YYYY-MM-DD')   "
			+ ")   "
			+ "UNION   "
			+ "SELECT effectivedate,trigger_condition,auto_cancellation_day "
			+ "FROM (   "
			+ "  SELECT effective_date AS effectivedate,trigger_condition,auto_cancellation_day  "
			+ "  FROM pgowners.zoy_pg_auto_cancellation_after_check_in   "
			+ "  WHERE is_approved = TRUE AND   "
			+ "    TO_DATE(effective_date, 'YYYY-MM-DD') < CAST(:currentDate AS DATE)   "
			+ "  ORDER BY TO_DATE(effective_date, 'YYYY-MM-DD') DESC   "
			+ "  LIMIT 1   "
			+ ") adas",
	        nativeQuery = true)
	List<String[]> findAftercheckInDateDetailsByDate(@Param("currentDate") String currentDate);
	
	
	@Query(value = "SELECT  "
			+ "  effective_date AS effectivedate, "
			+ "  trigger_condition, "
			+ "  check_out_day  "
			+ "FROM pgowners.zoy_pg_early_check_out   "
			+ "WHERE is_approved = true AND (   "
			+ "  effective_date = TO_CHAR(CAST(:currentDate AS DATE) + INTERVAL '7 days', 'YYYY-MM-DD') OR   "
			+ "  effective_date = TO_CHAR(CAST(:currentDate AS DATE) + INTERVAL '1 day', 'YYYY-MM-DD') OR   "
			+ "  effective_date = TO_CHAR(CAST(:currentDate AS DATE), 'YYYY-MM-DD')   "
			+ ")   "
			+ "UNION   "
			+ "SELECT effectivedate,trigger_condition,check_out_day "
			+ "FROM (   "
			+ "  SELECT effective_date AS effectivedate,trigger_condition,check_out_day  "
			+ "  FROM pgowners.zoy_pg_early_check_out   "
			+ "  WHERE is_approved = TRUE AND   "
			+ "    TO_DATE(effective_date, 'YYYY-MM-DD') < CAST(:currentDate AS DATE)   "
			+ "  ORDER BY TO_DATE(effective_date, 'YYYY-MM-DD') DESC   "
			+ "  LIMIT 1   "
			+ ") adas",
	        nativeQuery = true)
	List<String[]> findEarlyCheckOutDetailsByDate(@Param("currentDate") String currentDate);
	
	
	@Query(value = " SELECT  "
			+ "  effective_date AS effectivedate, "
			+ "  trigger_condition, "
			+ "  auto_cancellation_day, "
			+ "  deduction_percentage  "
			+ "FROM pgowners.zoy_pg_auto_cancellation_master   "
			+ "WHERE is_approved = true AND (   "
			+ "  effective_date = TO_CHAR(CAST(:currentDate AS DATE) + INTERVAL '7 days', 'YYYY-MM-DD') OR   "
			+ "  effective_date = TO_CHAR(CAST(:currentDate AS DATE) + INTERVAL '1 day', 'YYYY-MM-DD') OR   "
			+ "  effective_date = TO_CHAR(CAST(:currentDate AS DATE), 'YYYY-MM-DD')   "
			+ ")   "
			+ "UNION   "
			+ "SELECT effectivedate,trigger_condition,auto_cancellation_day,deduction_percentage "
			+ "FROM (   "
			+ "  SELECT effective_date AS effectivedate,trigger_condition,auto_cancellation_day,deduction_percentage "
			+ "  FROM pgowners.zoy_pg_auto_cancellation_master  "
			+ "  WHERE is_approved = TRUE AND   "
			+ "    TO_DATE(effective_date, 'YYYY-MM-DD') < CAST(:currentDate AS DATE)   "
			+ "  ORDER BY TO_DATE(effective_date, 'YYYY-MM-DD') DESC   "
			+ "  LIMIT 1   "
			+ ") adas",
	        nativeQuery = true)
	List<String[]> findSecurityDepositDeadlineDetailsByDate(@Param("currentDate") String currentDate);
	
	@Query(value = "SELECT pzm.first_name ,pzm.last_name, up.email_id, up.mobile_no "
			+ "FROM pgadmin.pg_owner_master pzm  "
			+ "join pgcommon.user_profile up ON pzm.email_id = up.email_id  "
			+ "WHERE up.enabled = true and (up.email_id =:emailMobile or up.mobile_no =:emailMobile)",
	        nativeQuery = true)
	List<Object[]> getExistingPgOwnerDetails(String emailMobile);

	@Query(value = "select distinct pom.zoy_code, pom.first_name || ' ' || pom.last_name AS owner_name,up.email_id,   "
			+ "up.mobile_no AS contact,  pom.created_at,'pending' AS status,  pom.zoy_share, pom.register_id,pom.property_name  "
			+ "from pgowners.zoy_pg_owner_details zpod  "
			+ "join  pgadmin.pg_owner_master pom on pom.email_id =zpod.pg_owner_email  "
			+ "JOIN pgcommon.user_profile up ON up.email_id = pom.email_id and up.enabled = true and pom.zoy_code != up.zoy_code  "
			+ "JOIN pgowners.zoy_pg_property_details zppd ON zppd.zoy_code != pom.zoy_code  "
			+ "where zpod.pg_owner_id =:ownerId order by zoy_code asc",
	        nativeQuery = true)
	List<Object[]> getAllExistingPgOwnerDetails(String ownerId);

	@Query(value = "SELECT distinct pom.* "
			+ "FROM pgadmin.pg_owner_master pom  "
			+ "JOIN pgcommon.user_profile up ON up.email_id = pom.email_id AND up.zoy_code != pom.zoy_code  "
			+ "JOIN pgowners.zoy_pg_property_details zppd ON zppd.zoy_code != pom.zoy_code  "
			+ "WHERE up.enabled = true and pom.zoy_code = :zoyCode",
	        nativeQuery = true)
	PgOwnerMaster getExistingOwnerDetails(String zoyCode);


}
