package com.integration.zoy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.UserMaster;

@Repository
public interface UserMasterRepository extends JpaRepository<UserMaster, String>{
	@Query(value = "SELECT * FROM pgusers.user_master WHERE user_mobile = :mobile and user_email=:email", nativeQuery = true)
	Optional<UserMaster> findUserMaster(String mobile, String email);
}
