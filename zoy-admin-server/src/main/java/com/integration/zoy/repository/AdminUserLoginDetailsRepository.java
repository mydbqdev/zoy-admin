package com.integration.zoy.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.AdminUserLoginDetails;

@Repository
public interface AdminUserLoginDetailsRepository extends JpaRepository<AdminUserLoginDetails, String> {
	@Query(value = "select * from pgadmin.user_login_details ul where lower(ul.user_email)=:email",nativeQuery = true)
	Optional<AdminUserLoginDetails> findRegisterEmail(String email);
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE user_login_details SET is_active = :status WHERE user_email = :user_email", nativeQuery = true)
	void doUserActiveteDeactiveteInUserLoginDetails(String user_email , boolean status);
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE user_master SET status = :status WHERE user_email = :user_email", nativeQuery = true)
	void doUserActiveteDeactiveteInuserMaster(String user_email , boolean status);
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE pgadmin.user_login_details SET is_lock = true\r\n"
			+ "WHERE user_email = :email", nativeQuery = true)
	void lockUserByEmail(String email);
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE pgadmin.user_login_details SET is_lock = false WHERE user_email = :email  AND is_active = true", nativeQuery = true)
	void unLockUserByEmail(String email);
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE pgadmin.admin_users_lock SET attempt_sequence = 0 WHERE user_email = :email", nativeQuery = true)
	void resetAttemptSequence(String email);
	
	
}
