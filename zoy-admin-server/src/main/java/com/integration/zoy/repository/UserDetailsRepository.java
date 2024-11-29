package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.UserDetails;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, String> {

}
