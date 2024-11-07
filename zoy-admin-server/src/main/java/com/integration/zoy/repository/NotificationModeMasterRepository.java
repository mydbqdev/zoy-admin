package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.NotificationModeMaster;

@Repository
public interface NotificationModeMasterRepository extends JpaRepository<NotificationModeMaster, String> {
    
}
