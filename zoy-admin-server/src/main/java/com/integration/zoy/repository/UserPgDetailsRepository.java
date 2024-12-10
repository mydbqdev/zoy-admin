package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.UserPgDetails;

@Repository
public interface UserPgDetailsRepository extends JpaRepository<UserPgDetails, String> {
}
