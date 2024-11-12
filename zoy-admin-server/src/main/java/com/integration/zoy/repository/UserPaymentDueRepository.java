package com.integration.zoy.repository;

import com.integration.zoy.entity.UserPaymentDue;
import com.integration.zoy.entity.UserPaymentDueId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPaymentDueRepository extends JpaRepository<UserPaymentDue, UserPaymentDueId> {

	@Query(value = "SELECT CASE WHEN EXISTS ( " +
	        "SELECT 1 FROM pgusers.user_payment_due " +
	        "WHERE user_money_due_id = :userMoneyDueId) " +
	        "THEN TRUE ELSE FALSE END", nativeQuery = true)
	boolean existsByUserMoneyDueId(@Param("userMoneyDueId") String userMoneyDueId);
	
	@Query(value = "SELECT upd.user_payment_id " +
            "FROM pgusers.user_payment_due upd " +
            "WHERE upd.user_money_due_id = :userMoneyDueId", nativeQuery = true)
String findUserPaymentIdByUserMoneyDueId(@Param("userMoneyDueId") String userMoneyDueId);

}
