package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.UserBookingPayment;
import com.integration.zoy.entity.UserBookingPaymentId;

@Repository
public interface UserBookingPaymentRepository extends JpaRepository<UserBookingPayment,UserBookingPaymentId>{
	
}
