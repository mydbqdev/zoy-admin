package com.integration.zoy.repository;

import com.integration.zoy.entity.UserDueMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserDueMasterRepository extends JpaRepository<UserDueMaster, String> {

   
}
