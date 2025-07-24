package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyCompanyProfileMaster;
@Repository
public interface ZoyCompanyProfileMasterRepository extends JpaRepository<ZoyCompanyProfileMaster, String>{

	@Query(value = "select * from pgadmin.zoy_company_profile_master zcpm where zcpm.\"type\" ='Head Office'", nativeQuery = true)
	ZoyCompanyProfileMaster getZoyHeadOfficeDetails();
	
}
