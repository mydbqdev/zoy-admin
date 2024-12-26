package com.integration.zoy.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.integration.zoy.entity.AdminUsersLock;


public interface AdminUserLockRepository extends JpaRepository<AdminUsersLock, Long> {
	
	@Query(value = "SELECT * FROM admin_users_lock WHERE username = :username", nativeQuery = true)
	AdminUsersLock findByUsername(String username);

	@Query(value = "SELECT \r\n"
			+ "    a.username AS email,\r\n"
			+ "    umd.first_name, umd.last_name AS username,\r\n"
			+ "    umd.designation AS designation,\r\n"
			+ "    'locked' AS status\r\n"
			+ "FROM \r\n"
			+ "    pgadmin.admin_users_lock a\r\n"
			+ "JOIN \r\n"
			+ "    pgadmin.user_login_details u ON u.user_email = a.username\r\n"
			+ "JOIN \r\n"
			+ "    pgadmin.user_master umd ON umd.user_email = u.user_email\r\n"
			+ "WHERE \r\n"
			+ "    u.is_lock = true", nativeQuery = true)
	List<Object[]> findLockedUsers();

	@Transactional
	@Modifying
	@Query(value = "UPDATE admin_users_lock SET lock_count = 0, lock_time = NULL, attempt_sequence = 0 WHERE username = :username ", nativeQuery = true)
	void unlockUser(String username);

}
