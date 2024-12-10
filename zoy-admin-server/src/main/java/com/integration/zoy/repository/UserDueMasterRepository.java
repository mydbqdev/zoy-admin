package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.UserDueMaster;


@Repository
public interface UserDueMasterRepository extends JpaRepository<UserDueMaster, String> {

   
}
