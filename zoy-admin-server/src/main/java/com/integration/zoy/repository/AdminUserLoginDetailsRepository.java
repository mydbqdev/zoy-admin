package com.integration.zoy.repository;

import java.sql.Timestamp;
import java.util.List;
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
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM admin_users_lock \n"
			+ "where username in (select user_email from user_login_details \n"
			+ "where (:curdt >= (last_change_on + INTERVAL '45 days' )\n"
			+ "and is_active = true and is_lock = false)) ",nativeQuery = true)
	void deleteDataInAdminUsersLockDetails(Timestamp curdt);
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE \n"
			+ "user_login_details SET is_lock = true\n"
			+ "WHERE (:curdt >= (last_change_on + INTERVAL '45 days')  ) \n"
			+ "	       AND is_active = true AND is_lock = false ",nativeQuery = true)
	void doLockAfterPWExpired(Timestamp curdt);
	
	
	@Query(value = "SELECT um.user_email, " +
            "COALESCE(CONCAT(COALESCE(um.first_name, ''), ' ', COALESCE(um.last_name, '')), '') AS userName, " +
            "(DATE(:curdt) - DATE(uld.last_change_on)) AS days " + // Calculate the difference in days directly
            "FROM user_login_details uld " +
            "JOIN user_master um ON um.user_email = uld.user_email " +
            "WHERE :curdt > (uld.last_change_on + INTERVAL '38 days') " +
            "AND :curdt <= (uld.last_change_on + INTERVAL '45 days') " +
            "AND uld.last_change_on IS NOT NULL " +
            "AND uld.is_active = true " +
            "AND uld.is_lock = false", nativeQuery = true)
	List<String[]> getPasswordWarnerMailsData(Timestamp curdt);



	
}
