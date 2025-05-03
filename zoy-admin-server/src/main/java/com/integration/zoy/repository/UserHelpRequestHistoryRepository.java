package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.UserHelpRequestHistory;

@Repository
public interface UserHelpRequestHistoryRepository extends JpaRepository<UserHelpRequestHistory, String>{

}
