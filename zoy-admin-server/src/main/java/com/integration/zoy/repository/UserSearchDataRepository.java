package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.UserSearchData;

@Repository
public interface UserSearchDataRepository extends JpaRepository<UserSearchData, Integer> {
	
}
