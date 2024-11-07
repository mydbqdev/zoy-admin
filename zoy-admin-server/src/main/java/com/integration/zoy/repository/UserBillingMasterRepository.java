package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.UserBillingMaster;

@Repository
public interface UserBillingMasterRepository extends JpaRepository<UserBillingMaster,String>{

}
