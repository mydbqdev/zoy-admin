package com.integration.zoy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.NotificationModeMaster;
import com.integration.zoy.entity.UserBookings;

@Repository
public interface UserBookingsRepository extends JpaRepository<UserBookings,String>{
	
}
