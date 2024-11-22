package com.integration.zoy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.AdminUserLoginDetails;

@Repository
public interface AdminUserLoginDetailsRepository extends JpaRepository<AdminUserLoginDetails, String> {
	@Query(value = "select * from pgadmin.user_login_details ul where lower(ul.user_email)=:email",nativeQuery = true)
	Optional<AdminUserLoginDetails> findRegisterEmail(String email);
}
