package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.UserBookingPayment;
import com.integration.zoy.entity.UserBookingPaymentId;
import com.integration.zoy.utils.UserBookingPaymentDTO;

@Repository
public interface UserBookingPaymentRepository extends JpaRepository<UserBookingPayment,UserBookingPaymentId>{
	
}
