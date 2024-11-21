package com.integration.zoy.service;

import java.sql.Timestamp;
import java.util.List;

import com.integration.zoy.model.FilterData;
import com.integration.zoy.utils.ConsilidatedFinanceDetails;
import com.integration.zoy.utils.TenentDues;
import com.integration.zoy.utils.UserPaymentDTO;
import com.integration.zoy.utils.UserPaymentFilterRequest;
import com.integration.zoy.utils.VendorPayments;
import com.integration.zoy.utils.VendorPaymentsDues;
import com.integration.zoy.utils.VendorPaymentsGst;

public interface AdminReportImpl {
	List<UserPaymentDTO> getUserPaymentDetails(UserPaymentFilterRequest filterRequest,FilterData filterData);
	List<ConsilidatedFinanceDetails> getConsolidatedFinanceDetails( UserPaymentFilterRequest filterRequest,FilterData filterData);
	List<TenentDues> getTenentDuesDetails( UserPaymentFilterRequest filterRequest,FilterData filterData);
	List<VendorPayments> getVendorPaymentDetails(UserPaymentFilterRequest filterRequest,FilterData filterData);
	List<VendorPaymentsDues> getVendorPaymentDuesDetails( Timestamp fromDate, Timestamp toDate);
	List<VendorPaymentsGst> getVendorPaymentGstDetails( Timestamp fromDate, Timestamp toDate);
	byte[] generateDynamicReport(UserPaymentFilterRequest filterRequest, FilterData filterData);
}
