package com.integration.zoy.repository;

import java.sql.Timestamp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.UserBookings;

@Repository
public interface UserBookingsRepository extends JpaRepository<UserBookings,String>{
	
	@Query(value = "SELECT COUNT(*) \r\n"
	        + "FROM pgusers.user_bookings ub \r\n"
	        + "JOIN pgowners.zoy_pg_owner_booking_details ob \r\n"
	        + "    ON ub.user_bookings_id = ob.booking_id \r\n"
	        + "WHERE \r\n"
	        + "    ub.user_bookings_web_check_in = FALSE \r\n"
	        + "    AND ub.user_bookings_web_check_out = FALSE \r\n"
	        + "    AND ub.user_bookings_is_cancelled = false \r\n"
	        + "    AND ub.user_bookings_date >= CAST(:fromDate AS timestamp) \r\n"
	        + "    AND ub.user_bookings_date <= CAST(:toDate AS timestamp)", nativeQuery = true)
	long getCheckInCountByDates(Timestamp fromDate,Timestamp toDate);
	
	
	@Query(value = "SELECT COUNT(*) \r\n"
	        + "FROM pgusers.user_bookings ub \r\n"
	        + "JOIN pgowners.zoy_pg_owner_booking_details ob \r\n"
	        + "    ON ub.user_bookings_id = ob.booking_id \r\n"
	        + "WHERE \r\n"
	        + "    ub.user_bookings_web_check_in = TRUE \r\n"
	        + "    AND ub.user_bookings_web_check_out = FALSE \r\n"
	        + "    AND ub.user_bookings_is_cancelled = false \r\n"
	        + "    AND ub.user_bookings_date >= CAST(:fromDate AS timestamp) \r\n"
	        + "    AND ub.user_bookings_date <= CAST(:toDate AS timestamp)", nativeQuery = true)
	long getBookedCountByDates(Timestamp fromDate, Timestamp toDate);

	
	@Query(value = "SELECT COUNT(bd.bed_id)\r\n"
			+ "FROM pgowners.zoy_pg_bed_details bd\r\n"
			+ "WHERE bd.bed_id NOT IN (\r\n"
			+ "    SELECT bd_inner.bed_id\r\n"
			+ "    FROM pgowners.zoy_pg_bed_details bd_inner\r\n"
			+ "    JOIN pgowners.zoy_pg_owner_booking_details ob \r\n"
			+ "        ON bd_inner.bed_id = ob.selected_bed\r\n"
			+ "    JOIN pgusers.user_bookings ub \r\n"
			+ "        ON ob.booking_id = ub.user_bookings_id\r\n"
			+ "    WHERE ub.user_bookings_web_check_in = TRUE\r\n"
			+ "      AND ub.user_bookings_web_check_out = FALSE\r\n"
			+ "      AND ub.user_bookings_is_cancelled = FALSE\r\n"
			+ "      AND ub.user_bookings_date >= CAST(:fromDate AS TIMESTAMP)\r\n"
			+ "      AND ub.user_bookings_date <= CAST(:toDate AS TIMESTAMP))", nativeQuery = true)
	long getVacancyCount(Timestamp fromDate, Timestamp toDate);
	
	
}
