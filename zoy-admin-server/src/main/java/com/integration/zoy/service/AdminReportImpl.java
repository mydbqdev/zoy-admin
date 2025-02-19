package com.integration.zoy.service;

import java.sql.Timestamp;

import com.integration.zoy.exception.WebServiceException;
import com.integration.zoy.model.FilterData;
import com.integration.zoy.model.TenantResportsDTO;
import com.integration.zoy.utils.CommonResponseDTO;
import com.integration.zoy.utils.ConsilidatedFinanceDetails;
import com.integration.zoy.utils.RatingsAndReviewsReport;
import com.integration.zoy.utils.TenentDues;
import com.integration.zoy.utils.TenentRefund;
import com.integration.zoy.utils.UserPaymentDTO;
import com.integration.zoy.utils.UserPaymentFilterRequest;
import com.integration.zoy.utils.VendorPayments;
import com.integration.zoy.utils.VendorPaymentsDues;
import com.integration.zoy.utils.VendorPaymentsGst;

public interface AdminReportImpl {
	CommonResponseDTO<UserPaymentDTO> getUserPaymentDetails(UserPaymentFilterRequest filterRequest,FilterData filterData,Boolean applyPagination) throws WebServiceException;
	CommonResponseDTO<ConsilidatedFinanceDetails> getConsolidatedFinanceDetails( UserPaymentFilterRequest filterRequest,FilterData filterData,Boolean applyPagination) throws WebServiceException;
	CommonResponseDTO<TenentDues> getTenentDuesDetails( UserPaymentFilterRequest filterRequest,FilterData filterData,Boolean applyPagination) throws WebServiceException;
	CommonResponseDTO<VendorPayments> getVendorPaymentDetails(UserPaymentFilterRequest filterRequest,FilterData filterData,Boolean applyPagination) throws WebServiceException;
	CommonResponseDTO<TenentRefund> getTenantRefunds(UserPaymentFilterRequest filterRequest,FilterData filterData,Boolean applyPagination)throws WebServiceException;
	CommonResponseDTO<VendorPaymentsDues> getVendorPaymentDuesDetails( Timestamp fromDate, Timestamp toDate) throws WebServiceException;
	CommonResponseDTO<VendorPaymentsGst> getVendorPaymentGstDetails( Timestamp fromDate, Timestamp toDate) throws WebServiceException;
	CommonResponseDTO<RatingsAndReviewsReport> getRatingsAndReviewsDetails(UserPaymentFilterRequest filterRequest,FilterData filterData, Boolean applyPagination) throws WebServiceException;
	CommonResponseDTO<TenantResportsDTO> getUpcomingTenantsReport(UserPaymentFilterRequest filterRequest,FilterData filterData, Boolean applyPagination) throws WebServiceException;
	CommonResponseDTO<TenantResportsDTO> getActiveTenantsReport(UserPaymentFilterRequest filterRequest,FilterData filterData, Boolean applyPagination) throws WebServiceException;
	byte[] generateDynamicReport(UserPaymentFilterRequest filterRequest, FilterData filterData,Boolean applyPagination)  throws WebServiceException;
	String[] getDistinctCities() throws WebServiceException;
	
}
