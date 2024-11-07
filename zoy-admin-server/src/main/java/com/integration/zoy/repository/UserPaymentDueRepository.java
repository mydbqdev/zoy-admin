package com.integration.zoy.repository;

import com.integration.zoy.entity.UserPaymentDue;
import com.integration.zoy.entity.UserPaymentDueId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPaymentDueRepository extends JpaRepository<UserPaymentDue, UserPaymentDueId> {

    
}
