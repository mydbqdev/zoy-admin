package com.integration.zoy.service;

import com.integration.zoy.exception.WebServiceException;
import com.integration.zoy.model.FilterData;
import com.integration.zoy.utils.CommonResponseDTO;
import com.integration.zoy.utils.ConsilidatedFinanceDetails;
import com.integration.zoy.utils.TenentDues;
import com.integration.zoy.utils.UserPaymentDTO;
import com.integration.zoy.utils.UserPaymentFilterRequest;
import com.integration.zoy.utils.VendorPayments;
import com.integration.zoy.utils.VendorPaymentsDues;
import com.integration.zoy.utils.VendorPaymentsGst;

public interface AdminReportImpl {
	CommonResponseDTO<UserPaymentDTO> getUserPaymentDetails(UserPaymentFilterRequest filterRequest,FilterData filterData) throws WebServiceException;
	CommonResponseDTO<ConsilidatedFinanceDetails> getConsolidatedFinanceDetails( UserPaymentFilterRequest filterRequest,FilterData filterData) throws WebServiceException;
	CommonResponseDTO<TenentDues> getTenentDuesDetails( UserPaymentFilterRequest filterRequest,FilterData filterData) throws WebServiceException;
	CommonResponseDTO<VendorPayments> getVendorPaymentDetails(UserPaymentFilterRequest filterRequest,FilterData filterData) throws WebServiceException;
	CommonResponseDTO<VendorPaymentsDues> getVendorPaymentDuesDetails( String fromDate, String toDate) throws WebServiceException;
	CommonResponseDTO<VendorPaymentsGst> getVendorPaymentGstDetails( String fromDate, String toDate) throws WebServiceException;
	byte[] generateDynamicReport(UserPaymentFilterRequest filterRequest, FilterData filterData)  throws WebServiceException;
	String[] getDistinctCities() throws WebServiceException;
	
}
