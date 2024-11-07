package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.PgOwnerUserStatus;

@Repository
public interface PgOwnerUserStatusRepository extends JpaRepository<PgOwnerUserStatus,String>{

	
	@Query(value = "SELECT * FROM pgcommon.pg_owner_user_status where user_bookings_id=:bookingId",nativeQuery = true)
	PgOwnerUserStatus findByOwnerPropertyBooking(String bookingId);

}
