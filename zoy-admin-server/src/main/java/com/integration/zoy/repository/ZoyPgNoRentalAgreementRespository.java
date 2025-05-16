package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgNoRentalAgreement;

@Repository
public interface ZoyPgNoRentalAgreementRespository extends JpaRepository<ZoyPgNoRentalAgreement, String>  {

	@Query(value = "SELECT * FROM pgowners.zoy_pg_no_rental_agreement zpnra where is_approved =true "
			+ "ORDER BY CASE WHEN effective_date\\:\\:date <= CURRENT_DATE THEN 0 ELSE 1 end, effective_date desc LIMIT 1", nativeQuery = true)
	ZoyPgNoRentalAgreement findNoRentAgreementDuration();

}
