package com.integration.zoy.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.integration.zoy.repository.UserPaymentRepository;
import com.integration.zoy.utils.ConsilidatedFinanceDetails;
import com.integration.zoy.utils.UserPaymentDTO;
@Service
public class AdminReportService implements AdminReportImpl{
	@Autowired
    private UserPaymentRepository userPaymentRepository;
	
	@Override
	public List<UserPaymentDTO> getUserPaymentDetails(Timestamp fromDate, Timestamp toDate) {
		List<Object[]> results = userPaymentRepository.findUserPaymentDetailsByUserIdAndDateRange(fromDate, toDate);

	    List<UserPaymentDTO> userPaymentDTOs = new ArrayList<>();
	    for (Object[] row : results) {
	        UserPaymentDTO dto = new UserPaymentDTO();
	        dto.setUserId((String) row[0]);
	        dto.setUserPaymentTimestamp((Timestamp) row[1]);
	        dto.setUserPaymentBankTransactionId((String) row[2]);
	        dto.setUserPaymentResultStatus((String) row[3]);
	        BigDecimal payableAmount = (BigDecimal) row[4] != null ? (BigDecimal) row[4] : BigDecimal.ZERO;
	        BigDecimal gst = (BigDecimal) row[5] != null ? (BigDecimal) row[5] : BigDecimal.ZERO;
	        dto.setUserPaymentPayableAmount(payableAmount);
	        dto.setUserPaymentGst(gst);
	        dto.setUserPersonalName((String) row[6]);
	        dto.setUserPgPropertyName((String) row[7]);
	        dto.setUserPgPropertyId((String) row[8]);
	        dto.setBedNumber((String) row[9]);
	        BigDecimal totalAmount = payableAmount.add(gst);
	        dto.setTotalAmount(totalAmount);
	        dto.setCategory((String) row[10]);
	        dto.setPaymentMethod((String) row[11]);
	        userPaymentDTOs.add(dto);
	    }
	    return userPaymentDTOs;
	}

	@Override
	public List<ConsilidatedFinanceDetails> getConsolidatedFinanceDetails(Timestamp fromDate, Timestamp toDate) {
		List<Object[]> results = userPaymentRepository.findConsolidatedFinanceReportByDateRange(fromDate, toDate);
		List<ConsilidatedFinanceDetails> consolidatedFinanceDto = new ArrayList<>();
		  for (Object[] row : results) {
			  ConsilidatedFinanceDetails dto = new ConsilidatedFinanceDetails();
		        dto.setUserId((String) row[2]);
		        dto.setUserPaymentTimestamp((Timestamp) row[0]);
		        dto.setUserPaymentBankTransactionId((String) row[1]);
		        dto.setUserPersonalName((String) row[3]);
		        BigDecimal payableAmount = (BigDecimal) row[4] != null ? (BigDecimal) row[4] : BigDecimal.ZERO;
		        BigDecimal gst = (BigDecimal) row[5] != null ? (BigDecimal) row[5] : BigDecimal.ZERO;
		        BigDecimal totalAmount = payableAmount.add(gst);
		        dto.setTotalAmount(totalAmount);
		        dto.setDebitAmount(BigDecimal.valueOf(0));
		        consolidatedFinanceDto.add(dto);
		  }
		return consolidatedFinanceDto;
	}

}
