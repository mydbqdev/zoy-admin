package com.integration.zoy.service;

import java.sql.Timestamp;
import java.util.List;

import com.integration.zoy.utils.ConsilidatedFinanceDetails;
import com.integration.zoy.utils.TenentDues;
import com.integration.zoy.utils.UserPaymentDTO;
import com.integration.zoy.utils.VendorPayments;
import com.integration.zoy.utils.VendorPaymentsDues;
import com.integration.zoy.utils.VendorPaymentsGst;

public interface AdminReportImpl {
	List<UserPaymentDTO> getUserPaymentDetails( Timestamp fromDate, Timestamp toDate);
	List<ConsilidatedFinanceDetails> getConsolidatedFinanceDetails( Timestamp fromDate, Timestamp toDate);
	List<TenentDues> getTenentDuesDetails( Timestamp fromDate, Timestamp toDate);
	List<VendorPayments> getVendorPaymentDetails( Timestamp fromDate, Timestamp toDate);
	List<VendorPaymentsDues> getVendorPaymentDuesDetails( Timestamp fromDate, Timestamp toDate);
	List<VendorPaymentsGst> getVendorPaymentGstDetails( Timestamp fromDate, Timestamp toDate);
	String downloadUserPaymentDetails( Timestamp fromDate, Timestamp toDate);
}
