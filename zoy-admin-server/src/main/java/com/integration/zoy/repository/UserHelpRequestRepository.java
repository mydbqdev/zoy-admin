package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.UserHelpRequest;
@Repository
public interface UserHelpRequestRepository extends JpaRepository<UserHelpRequest,Long>{

}
