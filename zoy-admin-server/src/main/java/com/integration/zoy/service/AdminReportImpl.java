package com.integration.zoy.service;

import java.sql.Timestamp;

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
	CommonResponseDTO<UserPaymentDTO> getUserPaymentDetails(UserPaymentFilterRequest filterRequest,FilterData filterData);
	CommonResponseDTO<ConsilidatedFinanceDetails> getConsolidatedFinanceDetails( UserPaymentFilterRequest filterRequest,FilterData filterData);
	CommonResponseDTO<TenentDues> getTenentDuesDetails( UserPaymentFilterRequest filterRequest,FilterData filterData);
	CommonResponseDTO<VendorPayments> getVendorPaymentDetails(UserPaymentFilterRequest filterRequest,FilterData filterData);
	CommonResponseDTO<VendorPaymentsDues> getVendorPaymentDuesDetails( Timestamp fromDate, Timestamp toDate);
	CommonResponseDTO<VendorPaymentsGst> getVendorPaymentGstDetails( Timestamp fromDate, Timestamp toDate);
	byte[] generateDynamicReport(UserPaymentFilterRequest filterRequest, FilterData filterData);
	String[] getDistinctCities();
	
}
