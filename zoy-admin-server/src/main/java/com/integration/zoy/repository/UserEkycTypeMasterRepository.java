package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.UserEkycTypeMaster;



@Repository
public interface UserEkycTypeMasterRepository extends JpaRepository<UserEkycTypeMaster, String> {

  
}
