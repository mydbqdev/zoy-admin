package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.NotificationModeMaster;

@Repository
public interface NotificationModeMasterRepository extends JpaRepository<NotificationModeMaster, String> {
	 @Query(value = "select * from pgusers.notification_mode_master order by notification_mod_name", nativeQuery = true)
	  List<NotificationModeMaster> findAllNotificationModeData();
}
