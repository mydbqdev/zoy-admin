package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.FollowUps;
@Repository
public interface FollowUpRepository extends JpaRepository<FollowUps, String> {

	List<FollowUps> findByUserEmail(String userEmail);
}
