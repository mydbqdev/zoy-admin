package com.integration.zoy.service;

import org.springframework.data.domain.Page;

import com.integration.zoy.entity.ZoyPgSalesMaster;
import com.integration.zoy.entity.ZoyPgSalesUserLoginDetails;
import com.integration.zoy.exception.WebServiceException;
import com.integration.zoy.model.ZoyPgSalesMasterModel;
import com.integration.zoy.utils.PaginationRequest;

public interface SalesDBImpl {

	boolean existsByUserEmail(String userEmail) throws WebServiceException;

	ZoyPgSalesMaster saveAdminSalesUser(ZoyPgSalesMaster master) throws WebServiceException;
	ZoyPgSalesUserLoginDetails saveAdminLoginDetails(ZoyPgSalesUserLoginDetails zoyPgSalesUserLoginDetails)throws WebServiceException;

	Page<ZoyPgSalesMasterModel> findAllSalesUsers(PaginationRequest paginationRequest);

}
