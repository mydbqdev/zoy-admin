package com.integration.zoy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.UserProfile;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {


	@Query(value = "select * from pgcommon.user_profile up where verify_token=:token",nativeQuery = true)
	Optional<UserProfile> findByVerifyToken(String token);

	@Query(value = "select * from pgcommon.user_profile up where up.email_id=:email",nativeQuery = true)
	Optional<UserProfile> findRegisterEmail(String email);

}
