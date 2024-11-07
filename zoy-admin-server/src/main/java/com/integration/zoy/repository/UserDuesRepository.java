package com.integration.zoy.repository;

import com.integration.zoy.entity.UserDues;

import java.util.List;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDuesRepository extends JpaRepository<UserDues, String> {
	
}
