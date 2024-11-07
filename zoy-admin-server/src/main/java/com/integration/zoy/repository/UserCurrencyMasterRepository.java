package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.integration.zoy.entity.UserCurrencyMaster;

@Repository
public interface UserCurrencyMasterRepository extends JpaRepository<UserCurrencyMaster, String> {

	@Query(value = "select currency_id from pgusers.user_currency_master ucm where lower(ucm.currency_name)=:currency",nativeQuery = true)
	String getCurrencyId(String currency);

	
   
}
