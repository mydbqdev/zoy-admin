package com.integration.zoy.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.integration.zoy.repository.UserDuesRepository;
import com.integration.zoy.repository.UserPaymentDueRepository;
import com.integration.zoy.repository.UserPaymentRepository;
import com.integration.zoy.utils.ConsilidatedFinanceDetails;
import com.integration.zoy.utils.TenentDues;
import com.integration.zoy.utils.UserPaymentDTO;
import com.integration.zoy.utils.VendorPayments;
import com.integration.zoy.utils.VendorPaymentsDues;
import com.integration.zoy.utils.VendorPaymentsGst;
@Service
public class AdminReportService implements AdminReportImpl{
	@Autowired
	private UserPaymentRepository userPaymentRepository;

	@Autowired
	private UserDuesRepository userDuesRepository;

	@Autowired
	private UserPaymentDueRepository userPaymentDueRepository;

	@Autowired
	private  pdfGenerateService pdfGenerateService;

	@Autowired
	private ExcelGenerateService excelGenerateService;

	@Autowired
	private CsvGenerateService csvGenerateService;


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

	@Override
	public List<TenentDues> getTenentDuesDetails(Timestamp fromDate, Timestamp toDate) {
		List<Object[]> results = userDuesRepository.findUserDuesDetailsByDateRange(fromDate, toDate);
		List<TenentDues> tenentDuesDto = new ArrayList<>();
		for (Object[] row : results) {
			TenentDues dto = new TenentDues();
			dto.setUserId((String) row[0]);
			boolean isPresent = userPaymentDueRepository.existsByUserMoneyDueId((String) row[7]);
			if(isPresent) {
				String userPaymentId = userPaymentDueRepository.findUserPaymentIdByUserMoneyDueId((String) row[7]);
				BigDecimal payableAmount = userPaymentRepository.findUserPaymentPayableAmountByUserPaymentId(userPaymentId);
				if (((BigDecimal) row[1]).subtract(payableAmount).compareTo(BigDecimal.ZERO) > 0) {
					dto.setPendingAmount(payableAmount.subtract((BigDecimal) row[1])); 
				}
			}else {
				dto.setPendingAmount((BigDecimal) row[1]);
			}
			dto.setPendingDueDate((Timestamp) row[2]);
			dto.setUserPersonalName((String) row[3]);
			dto.setUserPgPropertyName((String) row[4]);
			dto.setUserPgPropertyId((String) row[5]);
			dto.setBedNumber((String) row[6]);

			tenentDuesDto.add(dto);
		}
		return tenentDuesDto;
	}

	@Override
	public List<VendorPayments> getVendorPaymentDetails(Timestamp fromDate, Timestamp toDate) {
		List<Object[]> results = userPaymentRepository.findVendorPaymentDetailsDateRange(fromDate, toDate);
		List<VendorPayments> tenentDuesDto = new ArrayList<>();
		for (Object[] row : results) {
			VendorPayments dto = new VendorPayments();
			dto.setOwnerId((String) row[0]);
			dto.setOwnerName((String) row[1]);
			dto.setPgId((String) row[2]);
			dto.setPgName((String) row[3]);
			dto.setTotalAmountFromTenants((BigDecimal) row[4]);
			dto.setTransactionDate((Timestamp) row[5]);
			dto.setTransactionNumber((String) row[6]);
			dto.setPaymentStatus((String) row[7]);
			dto.setAmountPaidToOwner(BigDecimal.valueOf(0));
			dto.setZoyCommission(BigDecimal.valueOf(0));
			tenentDuesDto.add(dto);
		}
		return tenentDuesDto;
	}

	@Override
	public List<VendorPaymentsDues> getVendorPaymentDuesDetails(Timestamp fromDate, Timestamp toDate) {
		List<VendorPaymentsDues> vendorPaymentsDues = new ArrayList<>();
		VendorPaymentsDues vendorPayDues = new VendorPaymentsDues();
		vendorPayDues.setOwnerId(" ");
		vendorPayDues.setOwnerName(" ");
		vendorPayDues.setPendingAmount(BigDecimal.valueOf(0));
		vendorPayDues.setPendingDueDate(null);
		vendorPayDues.setPgId(" ");
		vendorPayDues.setPgName(" ");
		vendorPayDues.setTotalAmountPaid(BigDecimal.valueOf(0));
		vendorPayDues.setTotalAmountPayable(BigDecimal.valueOf(0));
		vendorPaymentsDues.add(vendorPayDues);
		return vendorPaymentsDues;
	}

	@Override
	public List<VendorPaymentsGst> getVendorPaymentGstDetails(Timestamp fromDate, Timestamp toDate) {
		List<VendorPaymentsGst> vendorPaymentsGst = new ArrayList<>();
		VendorPaymentsGst vendorPaysGst = new VendorPaymentsGst();
		vendorPaysGst.setTransactionDate(null);
		vendorPaysGst.setTransactionNo(" ");
		vendorPaysGst.setPgId(" ");
		vendorPaysGst.setPgName(" ");
		vendorPaysGst.setTotalAmount(BigDecimal.valueOf(0));
		vendorPaysGst.setGstAmount(BigDecimal.valueOf(0));
		vendorPaysGst.setBasicAmount(BigDecimal.valueOf(0));
		vendorPaysGst.setPaymentMethod(" ");
		vendorPaymentsGst.add(vendorPaysGst);
		return vendorPaymentsGst;
	}

	@Override
	public byte[] generateDynamicReport(String templateName,String fileType, Timestamp fromDate, Timestamp toDate) {
		Map<String, Object> data = new HashMap<>();
		List<?> reportData = null; 
		switch (templateName) {
		case "userTransactionReport":
			reportData = getUserPaymentDetails(fromDate, toDate);
			break;
		case "consolidatedFinanceReport":
			reportData = getConsolidatedFinanceDetails(fromDate, toDate);

			break;

		case "tenantDuesReport":
			reportData = getTenentDuesDetails(fromDate, toDate);
			break;

		case "vendorPaymentsReport":
			reportData = getVendorPaymentDetails(fromDate, toDate);
			break;

		case "vendorPaymentsDuesReport":
			reportData = getVendorPaymentDuesDetails(fromDate, toDate);
			break;
		case "vendorPaymentsGstReport":
			reportData = getVendorPaymentGstDetails(fromDate, toDate);
			break;

		default:
			throw new IllegalArgumentException("Invalid template name provided.");
		}

		data.put("reportData", reportData);
		data.put("startDate", fromDate);
		data.put("endDate", toDate);
		data.put("printDate", new Timestamp(System.currentTimeMillis()));

		switch (fileType.toLowerCase()) {
		case "pdf":
			return pdfGenerateService.generatePdfFile(templateName, data);
		case "excel":
			return excelGenerateService.generateExcelFile(templateName, data);  
		case "csv":
			return csvGenerateService.generateCsvFile(templateName, data);  
		default:
			throw new IllegalArgumentException("Invalid file type provided. Supported types: pdf, excel, csv");
		}
	}

}
