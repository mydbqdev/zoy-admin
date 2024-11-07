package com.integration.zoy.repository;

import com.integration.zoy.entity.UserBookings;
import com.integration.zoy.entity.UserPayment;
import com.integration.zoy.entity.ZoyPgOwnerBookingDetails;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPaymentRepository extends JpaRepository<UserPayment, String> {
	
}
