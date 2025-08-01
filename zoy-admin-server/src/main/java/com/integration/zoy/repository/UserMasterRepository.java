package com.integration.zoy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.UserMaster;
import com.integration.zoy.entity.ZoyPgOwnerDetails;

@Repository
public interface UserMasterRepository extends JpaRepository<UserMaster, String>{
	@Query(value = "SELECT * FROM pgusers.user_master WHERE user_mobile = :mobile and user_email=:email", nativeQuery = true)
	Optional<UserMaster> findUserMaster(String mobile, String email);

	@Query(value = "select concat(um.first_name,' ',um.last_name )as userName,um.user_email  from user_master um  order by  LOWER( um.first_name) asc, LOWER(um.last_name) ASC ",nativeQuery = true)
	List<Object[]> getUsersNameList();
	@Query(value = "SELECT " +
			"CONCAT(um.user_first_name, ' ', um.user_last_name) AS tenantName, " +
			"um.user_mobile AS contactNumber, " +
			"um.user_email AS userEmail, " +
			"um.user_status AS status, " +
			"CASE " +
			"    WHEN um.user_pin IS NOT NULL THEN 'Yes' " +
			"    ELSE 'No' " +
			"END AS appUser, " +
			"CASE " +
			"    WHEN um.user_ekyc_isekycverified = true AND um.user_ekyc_isvideo_verified = true THEN 'Verified' " +
			"    WHEN um.user_ekyc_isekycverified = true AND (um.user_ekyc_isvideo_verified = false OR um.user_ekyc_isvideo_verified IS NULL) THEN 'Partially Verified' " +
			"    ELSE 'Pending' " +
			"END AS ekycStatus, " +
			"um.user_created_at AS registeredDate, " +
			"um.user_id AS tenantId " +
			"FROM pgusers.user_master um " +
			"WHERE (:searchText IS NULL OR " +
			"LOWER(CAST(CONCAT(um.user_first_name, ' ', um.user_last_name) AS text)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
			"LOWER(CAST(um.user_mobile AS text)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
			"LOWER(CAST(um.user_email AS text)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
			"LOWER(CAST(um.user_status AS text)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
			"LOWER(CAST(CASE WHEN um.user_pin IS NOT NULL THEN 'Yes' ELSE 'No' END AS text)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
			"LOWER(CAST(CASE WHEN um.user_ekyc_isekycverified = true AND um.user_ekyc_isvideo_verified = true THEN 'Verified' " +
			"    WHEN um.user_ekyc_isekycverified = true AND (um.user_ekyc_isvideo_verified = false OR um.user_ekyc_isvideo_verified IS NULL) THEN 'Partially Verified' " +
			"    ELSE 'Pending' END AS text)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
			"LOWER(CAST(um.user_created_at AS text)) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
			"AND ((:startDate = '1970-01-01 00:00:00' or :endDate = '9999-12-31 23:59:59') or ((:startDate != '1970-01-01 00:00:00' AND :endDate != '9999-12-31 23:59:59') and um.user_created_at BETWEEN CAST(:startDate AS timestamp) AND CAST(:endDate AS timestamp)))" +
			"AND um.user_status  in (:status) ",
			countQuery = "SELECT COUNT(*) FROM pgusers.user_master um " +
					"WHERE (:searchText IS NULL OR " +
					"LOWER(CAST(CONCAT(um.user_first_name, ' ', um.user_last_name) AS text)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
					"LOWER(CAST(um.user_mobile AS text)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
					"LOWER(CAST(um.user_email AS text)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
					"LOWER(CAST(um.user_status AS text)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
					"LOWER(CAST(CASE WHEN um.user_pin IS NOT NULL THEN 'Yes' ELSE 'No' END AS text)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
					"LOWER(CAST(CASE WHEN um.user_ekyc_isekycverified = true AND um.user_ekyc_isvideo_verified = true THEN 'Verified' " +
					"    WHEN um.user_ekyc_isekycverified = true AND (um.user_ekyc_isvideo_verified = false OR um.user_ekyc_isvideo_verified IS NULL) THEN 'Partially Verified' " +
					"    ELSE 'Pending' END AS text)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
					"LOWER(CAST(um.user_created_at AS text)) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
					"AND  ((:startDate = '1970-01-01 00:00:00' or :endDate = '9999-12-31 23:59:59') or ((:startDate != '1970-01-01 00:00:00' AND :endDate != '9999-12-31 23:59:59') AND um.user_created_at BETWEEN CAST(:startDate AS timestamp) AND CAST(:endDate AS timestamp)))" +
					"AND um.user_status  in (:status) ",
					nativeQuery = true)
	Page<Object[]> getTenantDetails(Pageable pageable, @Param("searchText") String searchText, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("status") List<String> status);

	@Query(value = "SELECT " +
			"CONCAT(um.user_first_name, ' ', um.user_last_name) AS tenantName, " +
			"um.user_mobile AS contactNumber, " +
			"um.user_email AS userEmail, " +
			"um.user_status AS status, " +
			"CASE " +
			"    WHEN um.user_ekyc_isekycverified = true AND um.user_ekyc_isvideo_verified = true THEN 'Verified' " +
			"    WHEN um.user_ekyc_isekycverified = true AND (um.user_ekyc_isvideo_verified = false OR um.user_ekyc_isvideo_verified IS NULL) THEN 'Partially Verified' " +
			"    ELSE 'Pending' " +
			"END AS ekycStatus, " +
			"um.user_created_at AS registeredDate, " +
			"CASE " +
			"    WHEN ub.user_bookings_web_check_in = true " +
			"        AND ub.user_bookings_web_check_out = false " +
			"        AND ub.user_bookings_is_cancelled = false " +
			"    THEN p.property_name " +
			"    ELSE '' " +
			"END AS currentPropertyName, " +
			"ud.emergency_contactnumber AS emergencyContactNumber, " +
			"ud.user_personal_alt_phone AS alternatePhone, " +
			"ud.user_personal_tenant_type AS tenantType, " +
			"ud.user_personal_gender AS gender, " +
			"ud.user_personal_dob AS dateOfBirth, " +
			"ud.user_personal_blood_group AS bloodGroup, " +
			"ud.user_family_father_name AS fatherName, " +
			"ud.user_personal_curr_address AS currentAddress, " +
			"ud.user_personal_permanant_address AS permanentAddress, " +
			"ud.user_personal_nationality AS nationality, " +
			"ud.user_personal_mother_tounge AS motherTongue, " +
			"ud.user_profile_image AS userProfile " +
			"FROM " +
			"pgusers.user_master um " +
			"LEFT JOIN " +
			"pgusers.user_details ud ON um.user_id = ud.user_id " +
			"LEFT JOIN " +
			"pgusers.user_bookings ub ON um.user_id = ub.user_id " +
			"LEFT JOIN " +
			"pgowners.zoy_pg_property_details p ON ub.user_bookings_property_id = p.property_id " +
			"WHERE " +
			"um.user_id = :userId", nativeQuery = true)
	List<String[]> fetchTenantProfileDetails(@Param("userId") String userId);

	@Query(value = "SELECT " +
			"p.property_name AS currentPropertyName, " +
			"ob.fixed_rent AS fixedRent, " +
			"ob.security_deposit AS securityDeposit, " +
			"ob.in_date AS checkInDate, " +
			"ob.out_date AS checkOutDate, " +
			"r.room_name AS roomName, " +
			"b.bed_name AS bedName, " +
			"rc.cycle_name AS rentCycle, " +
			"tm.notice_period AS noticePeriod, " +
			"(SELECT SUM(due_amount) " +
			" FROM ( " +
			"     SELECT " +
			"         CASE " +
			"             WHEN SUM(COALESCE(up.user_payment_payable_amount, 0)) >= ub.user_money_due_amount THEN 0 " +
			"             ELSE ub.user_money_due_amount - SUM(COALESCE(up.user_payment_payable_amount, 0)) " +
			"         END AS due_amount " +
			"     FROM pgusers.user_dues ub " +
			"     LEFT JOIN pgusers.user_payment_due upd ON upd.user_money_due_id = ub.user_money_due_id " +
			"     LEFT JOIN pgusers.user_payments up ON up.user_payment_id = upd.user_payment_id " +
			"     JOIN pgowners.zoy_pg_due_type_master zpdtm ON zpdtm.due_id = ub.user_money_due_type " +
			"     JOIN pgowners.zoy_pg_due_factor_master zpdfm ON zpdfm.factor_id = ub.user_money_due_billing_type " +
			"     JOIN pgowners.zoy_pg_due_master zpdm ON zpdm.due_type_id = zpdtm.due_type " +
			"     LEFT JOIN pgusers.user_due_image udi ON udi.user_id = ub.user_id AND udi.user_due_id = ub.user_money_due_id " +
			"     WHERE ub.user_id = :userId " +
			"     GROUP BY " +
			"         ub.user_money_due_bill_end_date, " +
			"         ub.user_money_due_bill_start_date, " +
			"         ub.user_money_due_billing_type, " +
			"         ub.user_money_due_description, " +
			"         ub.user_money_due_id, " +
			"         ub.user_money_due_timestamp, " +
			"         ub.user_money_due_type, " +
			"         zpdfm.factor_id, " +
			"         zpdm.due_name " +
			"     HAVING SUM(COALESCE(up.user_payment_payable_amount, 0)) < ub.user_money_due_amount " +
			" ) AS subquery) AS totalDueAmount " +
			"FROM " +
			"pgusers.user_master um " +
			"LEFT JOIN " +
			"pgusers.user_bookings ub ON um.user_id = ub.user_id " +
			"LEFT JOIN " +
			"pgowners.zoy_pg_property_details p ON ub.user_bookings_property_id = p.property_id " +
			"LEFT JOIN " +
			"pgowners.zoy_pg_owner_booking_details ob ON ub.user_bookings_id = ob.booking_id " +
			"LEFT JOIN " +
			"pgowners.zoy_pg_room_details r ON ob.room = r.room_id " +
			"LEFT JOIN " +
			"pgowners.zoy_pg_bed_details b ON ob.selected_bed = b.bed_id AND b.bed_status = true " +
			"LEFT JOIN " +
			"pgowners.zoy_pg_rent_cycle_master rc ON ob.lock_in_period = rc.cycle_id " +
			"LEFT JOIN " +
			"pgowners.zoy_pg_property_terms_conditions ptc ON ob.property_id = ptc.property_id " +
			"LEFT JOIN " +
			"pgowners.zoy_pg_terms_master tm ON ptc.term_id = tm.term_id " +
			"WHERE " +
			"um.user_id = :userId " +
			"AND ub.user_bookings_web_check_in = true " +
			"AND ub.user_bookings_web_check_out = false " +
			"AND ub.user_bookings_is_cancelled = false", nativeQuery = true)
	List<String[]> fetchActiveBookingDetails(@Param("userId") String userId);

	@Query(value = "SELECT " +
			"ub.user_bookings_id AS bookingId, " +
			"ub.user_bookings_date AS bookingDate, " +
			"p.property_name AS propertyName, " +
			"p.property_city AS propertyCity, " +
			"p.property_pincode AS propertyPincode, " +
			"p.property_contact_number AS propertyContactNumber, " +
			"b.bed_name AS bedNumber, " +
			"ob.fixed_rent AS monthlyRent, " +
			"ob.security_deposit AS securityDeposit, " +
			"CASE " +
			"    WHEN ub.user_bookings_is_cancelled = true THEN 'Cancelled' " +
			"    WHEN ub.user_bookings_web_check_in = true AND ub.user_bookings_web_check_out = true THEN 'Checked Out' " +
			"END AS bookingStatus, " +
			"ucd.cancellation_timestamp AS cancellationTimestamp " +
			"FROM " +
			"pgusers.user_bookings ub " +
			"LEFT JOIN " +
			"pgowners.zoy_pg_property_details p ON ub.user_bookings_property_id = p.property_id " +
			"LEFT JOIN " +
			"pgowners.zoy_pg_owner_booking_details ob ON ub.user_bookings_id = ob.booking_id " +
			"LEFT JOIN " +
			"pgowners.zoy_pg_bed_details b ON ob.selected_bed = b.bed_id " +
			"LEFT JOIN " +
			"pgusers.user_cancellation_details ucd ON ub.user_bookings_id = ucd.booking_id " +
			"WHERE " +
			"ub.user_bookings_tenant_id = :tenantId " +
			"AND (ub.user_bookings_is_cancelled = true OR (ub.user_bookings_web_check_in = true AND ub.user_bookings_web_check_out = true))", nativeQuery = true)
	List<String[]> fetchCancelBookingDetails(@Param("tenantId") String tenantId);

	@Query(value = "SELECT \r\n"
			+ "ub.user_bookings_id AS bookingId,\r\n"
			+ "ub.user_bookings_date AS bookingDate,\r\n"
			+ "p.property_name AS propertyName,\r\n"
			+ "p.property_city AS propertyCity,\r\n"
			+ "p.property_pincode AS propertyPincode,\r\n"
			+ "p.property_contact_number AS propertyContactNumber,\r\n"
			+ "b.bed_name AS bedNumber,\r\n"
			+ "ob.fixed_rent AS monthlyRent,\r\n"
			+ "ob.security_deposit AS securityDeposit,\r\n"
			+ "    CASE\r\n"
			+ "        WHEN ob.deposit_paid = true THEN 'Paid'\r\n"
			+ "        ELSE 'Pending'\r\n"
			+ "    END AS securityDepositStatus\r\n"
			+ "FROM\r\n"
			+ "pgusers.user_bookings ub\r\n"
			+ "LEFT JOIN\r\n"
			+ "pgowners.zoy_pg_property_details p ON ub.user_bookings_property_id = p.property_id\r\n"
			+ "LEFT JOIN\r\n"
			+ "pgowners.zoy_pg_owner_booking_details ob ON ub.user_bookings_id = ob.booking_id\r\n"
			+ "LEFT JOIN\r\n"
			+ "pgowners.zoy_pg_bed_details b ON ob.selected_bed = b.bed_id\r\n"
			+ "WHERE\r\n"
			+ "ub.user_bookings_tenant_id = :tenantId\r\n"
			+ "AND (ub.user_bookings_is_cancelled = false AND ub.user_bookings_web_check_in = false AND ub.user_bookings_web_check_out = false)", nativeQuery = true)
	List<String[]> fetchUpcomingBookingDetails(@Param("tenantId") String tenantId);

	@Query(value = "SELECT * FROM pgusers.user_master WHERE user_mobile = :phoneNumber", nativeQuery = true)
	Optional<UserMaster> findUserMaster(String phoneNumber);


	@Query(value = "select * from (\r\n"
			+ "SELECT DISTINCT um.user_email as email , CONCAT(um.first_name, ' ', um.last_name) AS full_name, 'SUPPORT_TEAM' as type \n"
			+ "FROM pgadmin.user_master um \n"
			+ "JOIN pgadmin.user_role ur ON um.user_email = ur.user_email \n"
			+ "JOIN pgadmin.role_screen rs ON ur.role_id = rs.role_id \n"
			+ "WHERE rs.screen_name = 'TICKETS' \n"
			+ "union all\r\n"
			+ "select psm.email_id as email,CONCAT(psm.first_name, ' ', psm.last_name) AS full_name,'SALE_TEAM' as type \r\n"
			+ "from pgsales.pg_sales_master psm \r\n"
			+ "where  psm .status = true)ss\r\n"
			+ "ORDER BY ss.full_name asc", nativeQuery = true)
	List<Object[]> findUsersWithTicketScreenAccess();

}
