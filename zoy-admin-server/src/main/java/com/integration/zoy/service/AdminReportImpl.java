package com.integration.zoy.service;

import java.sql.Timestamp;
import java.util.List;

import com.integration.zoy.utils.UserPaymentDTO;

public interface AdminReportImpl {
	List<UserPaymentDTO> getUserPaymentDetails( Timestamp fromDate, Timestamp toDate);
}
