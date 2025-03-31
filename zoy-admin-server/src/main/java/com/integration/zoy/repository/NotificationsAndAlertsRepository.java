package com.integration.zoy.repository;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.NotificationsAndAlerts;

@Repository
public interface NotificationsAndAlertsRepository extends JpaRepository<NotificationsAndAlerts, Long> {

  
	
	@Query(value = "   SELECT DISTINCT\r\n"
			+ "    ur.user_email\r\n"
			+ "FROM\r\n"
			+ "    pgadmin.user_role ur\r\n"
			+ "JOIN\r\n"
			+ "    pgadmin.role_screen rs ON ur.role_id = rs.role_id\r\n"
			+ "WHERE\r\n"
			+ "    rs.screen_name =:screenName \r\n"
			+ "    AND rs.read_prv = true \r\n"
			+ "    AND rs.write_prv = true", 
			nativeQuery = true)
	String[] findScreenAccess(String screenName);
	 
	@Query(value = "SELECT * FROM notifications_and_alerts_table WHERE userid = :emailId ORDER BY is_seen,created_at desc ", 
			nativeQuery = true)
	Page<Object[]> findNotification(String emailId, Pageable pageable);
	
	@Query(value="SELECT COUNT(*) FROM notifications_and_alerts_table WHERE userid = :emailId AND is_seen = TRUE",nativeQuery = true)
	String isSeencount(String emailId);
	
	@Transactional
	@Modifying
    @Query(value = "UPDATE notifications_and_alerts_table SET is_seen = TRUE, updated_at = CURRENT_TIMESTAMP WHERE notification_id = :notificationId", nativeQuery = true)
		void toggleNotificationStatus(@Param("notificationId") Long notificationId);
	

}
