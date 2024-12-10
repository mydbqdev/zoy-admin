package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.UserNotifications;
import com.integration.zoy.entity.UserNotificationsId;

@Repository
public interface UserNotificationsRepository extends JpaRepository<UserNotifications, UserNotificationsId> {

}
