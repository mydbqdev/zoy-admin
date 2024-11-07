package com.integration.zoy.repository;

import com.integration.zoy.entity.UserEkycTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface UserEkycTypeMasterRepository extends JpaRepository<UserEkycTypeMaster, String> {

  
}
