package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.UserBookings;

@Repository
public interface UserBookingsRepository extends JpaRepository<UserBookings,String>{
	
	@Query(value = 
	        "SELECT COUNT(*) \r\n"
	        + "FROM pgusers.user_bookings ub \r\n"
	        + "JOIN pgowners.zoy_pg_owner_booking_details ob \r\n"
	        + "    ON ub.user_bookings_id = ob.booking_id \r\n"
	        + "WHERE \r\n"
	        + "    ub.user_bookings_web_check_in = true \r\n"
	        + "    AND ub.user_bookings_web_check_out = FALSE \r\n"
	        + "    AND ub.user_bookings_is_cancelled = false \r\n"
	        + "    AND ub.user_bookings_date >= CAST(:currentDate AS timestamp) \r\n"
	        + "    AND ub.user_bookings_date < CAST(:currentDate AS timestamp) + INTERVAL '1 day'",
	        nativeQuery = true)
	    long getCheckInCountByDates(@Param("currentDate") String currentDate);
	
	
	@Query(value = 
		    "    SELECT COUNT(*) \r\n"
		    + "FROM pgusers.user_bookings ub \r\n"
		    + "JOIN pgowners.zoy_pg_owner_booking_details ob \r\n"
		    + "    ON ub.user_bookings_id = ob.booking_id \r\n"
		    + "WHERE \r\n"
		    + "    ub.user_bookings_web_check_in = false \r\n"
		    + "    AND ub.user_bookings_web_check_out = FALSE \r\n"
		    + "    AND ub.user_bookings_is_cancelled = false \r\n"
		    + "    AND ub.user_bookings_date >= CAST(:currentDate AS timestamp) \r\n"
		    + "    AND ub.user_bookings_date < CAST(:currentDate AS timestamp) + INTERVAL '1 day'", 
		    nativeQuery = true)
		long getBookedCountByDates(@Param("currentDate") String currentDate);

	@Query(value = 
		    "SELECT \r\n"
		    + "    COUNT(CASE WHEN bed_available = 'available' THEN 1 END) AS vacancy,\r\n"
		    + "    COUNT(*) AS total_beds\r\n"
		    + "FROM \r\n"
		    + "    pgowners.zoy_pg_bed_details", 
		    nativeQuery = true)
		List<Object[]> getVacancyCount();

	
	
}
