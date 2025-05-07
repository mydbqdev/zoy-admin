package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.integration.zoy.entity.RentalAgreementDoc;

public interface RentalAgreementDocRepository extends JpaRepository<RentalAgreementDoc, String>{
	
	 @Query(value = "select * from pgcommon.zoy_pg_rental_agreement_doc order by created_at desc", nativeQuery = true)
	  List<RentalAgreementDoc> findAllPgShareData();
}
