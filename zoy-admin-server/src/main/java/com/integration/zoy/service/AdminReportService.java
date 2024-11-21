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

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.integration.zoy.model.FilterData;
import com.integration.zoy.repository.UserDuesRepository;
import com.integration.zoy.repository.UserPaymentDueRepository;
import com.integration.zoy.repository.UserPaymentRepository;
import com.integration.zoy.utils.ConsilidatedFinanceDetails;
import com.integration.zoy.utils.TenentDues;
import com.integration.zoy.utils.UserPaymentDTO;
import com.integration.zoy.utils.UserPaymentFilterRequest;
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
	
	@Autowired
	EntityManager entityManager;


	@Override
	public List<UserPaymentDTO> getUserPaymentDetails(UserPaymentFilterRequest filterRequest,FilterData filterData) {
		StringBuilder queryBuilder = new StringBuilder(
	            "SELECT up.user_id, " +
	            "up.user_payment_timestamp, " +
	            "up.user_payment_bank_transaction_id, " +
	            "up.user_payment_result_status, " +
	            "up.user_payment_payable_amount, " +
	            "up.user_payment_gst, " +
	            "ud.user_personal_name, " +
	            "pgd.user_pg_propertyname, " +
	            "pgd.user_pg_property_id, " +
	            "bd.bed_name, " +
	            "up.user_payment_zoy_payment_type, " +
	            "up.user_payment_result_method " +
	            "FROM pgusers.user_payments up " +
	            "JOIN (SELECT DISTINCT pgd.user_id, " +
	                  "pgd.user_pg_propertyname, " +
	                  "pgd.user_pg_property_id " +
	                  "FROM pgusers.user_pg_details pgd) pgd " +
	            "ON up.user_id = pgd.user_id " +
	            "JOIN pgusers.user_details ud ON up.user_id = ud.user_id " +
	            "JOIN pgowners.zoy_pg_owner_booking_details bkd " +
	            "ON up.user_id = bkd.tenant_id " +
	            "AND up.user_payment_booking_id = bkd.booking_id " +
	            "AND pgd.user_pg_property_id = bkd.property_id " +
	            "JOIN pgowners.zoy_pg_bed_details bd ON bkd.selected_bed = bd.bed_id " +
	            "WHERE 1=1 ");

	    Map<String, Object> parameters = new HashMap<>();

	    if (filterData.getTenantId() != null && !filterData.getTenantId().isEmpty()) {
	        queryBuilder.append(" AND up.user_id = :userId");
	        parameters.put("userId", filterData.getTenantId());
	    }
	    if (filterData.getTransactionStatus() != null && !filterData.getTransactionStatus().isEmpty()) {
	        queryBuilder.append(" AND up.user_payment_result_status = :resultStatus");
	        parameters.put("resultStatus", filterData.getTransactionStatus());
	    }
	    if (filterData.getModeOfPayment() != null && !filterData.getModeOfPayment().isEmpty()) {
	        queryBuilder.append(" AND up.user_payment_result_method = :paymentMethod");
	        parameters.put("paymentMethod", filterData.getModeOfPayment());
	    }
	    if (filterData.getPgId() != null && !filterData.getPgId().isEmpty()) {
	        queryBuilder.append(" AND pgd.user_pg_property_id = :pgPropertyId");
	        parameters.put("pgPropertyId", filterData.getPgId());
	    }
	    
	    if (filterData.getPgName() != null && !filterData.getPgName().isEmpty()) {
	        queryBuilder.append(" AND pgd.user_pg_propertyname = :pgPropertyName");
	        parameters.put("pgPropertyName", filterData.getPgName());
	    }
	    if (filterData.getTenantName() != null && !filterData.getTenantName().isEmpty()) {
	        queryBuilder.append(" AND ud.user_personal_name LIKE :userPersonalName");
	        parameters.put("userPersonalName", "%" + filterData.getTenantName() + "%");
	    }
	    if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
	        queryBuilder.append(" AND up.user_payment_timestamp BETWEEN :fromDate AND :toDate");
	        parameters.put("fromDate", filterRequest.getFromDate());
	        parameters.put("toDate", filterRequest.getToDate());
	    }

	    queryBuilder.append(" ORDER BY up.user_payment_timestamp DESC");

	    Query query = entityManager.createNativeQuery(queryBuilder.toString());
	    parameters.forEach(query::setParameter);

	    query.setFirstResult(filterRequest.getPage() * filterRequest.getSize());
	    query.setMaxResults(filterRequest.getSize());

	    List<Object[]> result= query.getResultList();
	    List<UserPaymentDTO> userPaymentDTOs = result.stream().map(row -> {
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
            dto.setTotalAmount(payableAmount.add(gst));
            dto.setCategory((String) row[10]);
            dto.setPaymentMethod((String) row[11]);
            return dto;
        }).collect(Collectors.toList());
	    
	    return userPaymentDTOs;
	}

	@Override
	public List<ConsilidatedFinanceDetails> getConsolidatedFinanceDetails(UserPaymentFilterRequest filterRequest,FilterData filterData) {
		StringBuilder queryBuilder = new StringBuilder(
		        "SELECT up.user_payment_timestamp AS transaction_date, " +
		        "up.user_payment_bank_transaction_id AS transaction_number, " +
		        "up.user_id, " +
		        "ud.user_personal_name, " +
		        "up.user_payment_payable_amount, " +
		        "up.user_payment_gst " +
		        "FROM pgusers.user_payments up " +
		        "JOIN pgusers.user_details ud ON up.user_id = ud.user_id " + 
		        "WHERE 1=1 ");
		    Map<String, Object> parameters = new HashMap<>();
		    
		    if (filterData.getTenantId() != null && !filterData.getTenantId().isEmpty()) {
		        queryBuilder.append(" AND up.user_id = :userId");
		        parameters.put("userId", filterData.getTenantId());
		    }
		    
		    if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
		        queryBuilder.append(" AND up.user_payment_timestamp BETWEEN :fromDate AND :toDate");
		        parameters.put("fromDate", filterRequest.getFromDate());
		        parameters.put("toDate", filterRequest.getToDate());
		    }
		    
		    if (filterData.getTenantName() != null && !filterData.getTenantName().isEmpty()) {
		        queryBuilder.append(" AND ud.user_personal_name LIKE :userPersonalName");
		        parameters.put("userPersonalName", "%" + filterData.getTenantName() + "%");
		    }
		    queryBuilder.append(" ORDER BY up.user_payment_timestamp DESC");
		    Query query = entityManager.createNativeQuery(queryBuilder.toString());
		    parameters.forEach(query::setParameter);
		    query.setFirstResult(filterRequest.getPage() * filterRequest.getSize());
		    query.setMaxResults(filterRequest.getSize());
		    List<Object[]> result= query.getResultList();
		    List<ConsilidatedFinanceDetails> consolidatedFinanceDTOs = result.stream().map(row -> {
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
			return dto;
	        }).collect(Collectors.toList());
		    
		    return consolidatedFinanceDTOs;
	}

	@Override
	public List<TenentDues> getTenentDuesDetails(UserPaymentFilterRequest filterRequest, FilterData filterData) {
	    StringBuilder queryBuilder = new StringBuilder(
	            "SELECT u.user_id, " +
	            "u.user_money_due_amount, " +
	            "u.user_money_due_bill_start_date, " +
	            "ud.user_personal_name, " +
	            "pgd.user_pg_propertyname, " +
	            "pgd.user_pg_property_id, " +
	            "bd.bed_name, " +
	            "u.user_money_due_id " +
	            "FROM pgusers.user_dues u " +
	            "JOIN pgusers.user_details ud ON u.user_id = ud.user_id " +
	            "JOIN pgusers.user_pg_details pgd ON u.user_id = pgd.user_id " +
	            "JOIN pgowners.zoy_pg_owner_booking_details bkd ON u.user_id = bkd.tenant_id " +
	            "JOIN pgowners.zoy_pg_bed_details bd ON bkd.selected_bed = bd.bed_id " +
	            "WHERE 1=1 ");
	    
	    Map<String, Object> parameters = new HashMap<>();

	    if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
	        queryBuilder.append(" AND u.user_money_due_timestamp BETWEEN :fromDate AND :toDate");
	        parameters.put("fromDate", filterRequest.getFromDate());
	        parameters.put("toDate", filterRequest.getToDate());
	    }

	    if (filterData.getTenantId() != null && !filterData.getTenantId().isEmpty()) {
	        queryBuilder.append(" AND u.user_id = :tenantId");
	        parameters.put("tenantId", filterData.getTenantId());
	    }

	    if (filterData.getTenantName() != null && !filterData.getTenantName().isEmpty()) {
	        queryBuilder.append(" AND ud.user_personal_name LIKE :tenantName");
	        parameters.put("tenantName", "%" + filterData.getTenantName() + "%");
	    }
	    
	    if (filterData.getPgId() != null && !filterData.getPgId().isEmpty()) {
	        queryBuilder.append(" AND pgd.user_pg_property_id = :pgPropertyId");
	        parameters.put("pgPropertyId", filterData.getPgId());
	    }
	    
	    if (filterData.getPgName() != null && !filterData.getPgName().isEmpty()) {
	        queryBuilder.append(" AND pgd.user_pg_propertyname = :pgPropertyName");
	        parameters.put("pgPropertyName", filterData.getPgName());
	    }

	    queryBuilder.append(" ORDER BY u.user_money_due_timestamp DESC");
	    
	    Query query = entityManager.createNativeQuery(queryBuilder.toString());
	    parameters.forEach(query::setParameter);

	    query.setFirstResult(filterRequest.getPage() * filterRequest.getSize());
	    query.setMaxResults(filterRequest.getSize());

	    List<Object[]> results = query.getResultList();

	    List<TenentDues> tenentDuesDto = results.stream().map(row -> {
	        TenentDues dto = new TenentDues();
	        dto.setUserId((String) row[0]);

	        boolean isPresent = userPaymentDueRepository.existsByUserMoneyDueId((String) row[7]);
	        if (isPresent) {
	            String userPaymentId = userPaymentDueRepository.findUserPaymentIdByUserMoneyDueId((String) row[7]);
	            BigDecimal payableAmount = userPaymentRepository.findUserPaymentPayableAmountByUserPaymentId(userPaymentId);
	            if (((BigDecimal) row[1]).subtract(payableAmount).compareTo(BigDecimal.ZERO) > 0) {
	                dto.setPendingAmount(payableAmount.subtract((BigDecimal) row[1]));
	            }
	        } else {
	            dto.setPendingAmount((BigDecimal) row[1]);
	        }

	        dto.setPendingDueDate((Timestamp) row[2]);
	        dto.setUserPersonalName((String) row[3]);
	        dto.setUserPgPropertyName((String) row[4]);
	        dto.setUserPgPropertyId((String) row[5]);
	        dto.setBedNumber((String) row[6]);
	        return dto;
	    }).collect(Collectors.toList());

	    return tenentDuesDto;
	}


	@Override
	public List<VendorPayments> getVendorPaymentDetails(UserPaymentFilterRequest filterRequest, FilterData filterData) {
	    StringBuilder queryBuilder = new StringBuilder(
	            "SELECT o.pg_owner_id AS ownerId, " +
	            "o.pg_owner_name AS ownerName, " +
	            "pd.property_id AS pgId, " +
	            "pd.property_name AS pgName, " +
	            "SUM(up.user_payment_payable_amount) AS totalAmountFromTenants, " +
	            "up.user_payment_timestamp AS transactionDate, " +
	            "up.user_payment_bank_transaction_id AS transactionNumber, " +
	            "up.user_payment_payment_status AS paymentStatus " +
	            "FROM pgusers.user_payments up " +
	            "JOIN pgusers.user_pg_details pgd ON up.user_id = pgd.user_id " +
	            "JOIN pgusers.user_details ud ON up.user_id = ud.user_id " +
	            "JOIN pgowners.zoy_pg_owner_booking_details bkd ON up.user_id = bkd.tenant_id " +
	            "AND up.user_payment_booking_id = bkd.booking_id " +
	            "AND pgd.user_pg_property_id = bkd.property_id " +
	            "JOIN pgowners.zoy_pg_bed_details bd ON bkd.selected_bed = bd.bed_id " +
	            "JOIN pgowners.zoy_pg_property_details pd ON bkd.property_id = pd.property_id " +
	            "JOIN pgowners.zoy_pg_owner_details o ON pd.pg_owner_id = o.pg_owner_id " +
	            "WHERE 1=1 ");

	    Map<String, Object> parameters = new HashMap<>();

	    if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
	        queryBuilder.append(" AND up.user_payment_timestamp BETWEEN :fromDate AND :toDate");
	        parameters.put("fromDate", filterRequest.getFromDate());
	        parameters.put("toDate", filterRequest.getToDate());
	    }

	    if (filterData.getOwnerName() != null && !filterData.getOwnerName().isEmpty()) {
	        queryBuilder.append(" AND o.pg_owner_name LIKE :ownerName");
	        parameters.put("ownerName", "%" + filterData.getOwnerName() + "%");
	    }

	    if (filterData.getPgId() != null && !filterData.getPgId().isEmpty()) {
	        queryBuilder.append(" AND pd.property_id = :pgId");
	        parameters.put("pgId", filterData.getPgId());
	    }

	    if (filterData.getPgName() != null && !filterData.getPgName().isEmpty()) {
	        queryBuilder.append(" AND pd.property_name LIKE :pgName");
	        parameters.put("pgName", "%" + filterData.getPgName() + "%");
	    }

	    if (filterData.getTransactionStatus() != null && !filterData.getTransactionStatus().isEmpty()) {
	        queryBuilder.append(" AND up.user_payment_payment_status = :paymentStatus");
	        parameters.put("paymentStatus", filterData.getTransactionStatus());
	    }

	    queryBuilder.append(
	            " GROUP BY o.pg_owner_id, o.pg_owner_name, pd.property_id, pd.property_name, up.user_payment_timestamp, up.user_payment_bank_transaction_id, up.user_payment_payment_status");
	    queryBuilder.append(" ORDER BY up.user_payment_timestamp DESC");

	    Query query = entityManager.createNativeQuery(queryBuilder.toString());
	    parameters.forEach(query::setParameter);
	    
	    query.setFirstResult(filterRequest.getPage() * filterRequest.getSize());
	    query.setMaxResults(filterRequest.getSize());

	    List<Object[]> results = query.getResultList();
	    List<VendorPayments> vendorPaymentsDto = results.stream().map(row -> {
	        VendorPayments dto = new VendorPayments();
	        dto.setOwnerId((String) row[0]);
	        dto.setOwnerName((String) row[1]);
	        dto.setPgId((String) row[2]);
	        dto.setPgName((String) row[3]);
	        dto.setTotalAmountFromTenants((BigDecimal) row[4] != null ? (BigDecimal) row[4] : BigDecimal.ZERO);
	        dto.setTransactionDate((Timestamp) row[5]);
	        dto.setTransactionNumber((String) row[6]);
	        dto.setPaymentStatus((String) row[7]);
	        dto.setAmountPaidToOwner(BigDecimal.valueOf(0)); 
	        dto.setZoyCommission(BigDecimal.valueOf(0));     
	        return dto;
	    }).collect(Collectors.toList());

	    return vendorPaymentsDto;
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
	public byte[] generateDynamicReport(UserPaymentFilterRequest filterRequest, FilterData filterData) {
		Map<String, Object> data = new HashMap<>();
		List<?> reportData = null; 
		switch (filterRequest.getTemplateName()) {
		case "userTransactionReport":
			reportData = getUserPaymentDetails(filterRequest, filterData);
			break;
		case "userPaymentGstReport":
			reportData = getUserPaymentDetails(filterRequest, filterData);
			break;
		case "consolidatedFinanceReport":
			reportData = getConsolidatedFinanceDetails(filterRequest, filterData);

			break;

		case "tenantDuesReport":
			reportData = getTenentDuesDetails(filterRequest, filterData);
			break;

		case "vendorPaymentsReport":
			reportData = getVendorPaymentDetails(filterRequest, filterData);
			break;

		case "vendorPaymentsDuesReport":
			reportData = getVendorPaymentDuesDetails(filterRequest.getFromDate(), filterRequest.getToDate());
			break;
		case "vendorPaymentsGstReport":
			reportData = getVendorPaymentGstDetails(filterRequest.getFromDate(), filterRequest.getToDate());
			break;

		default:
			throw new IllegalArgumentException("Invalid template name provided.");
		}

		data.put("reportData", reportData);
		data.put("startDate", filterRequest.getFromDate());
		data.put("endDate", filterRequest.getToDate());
		data.put("printDate", new Timestamp(System.currentTimeMillis()));

		switch (filterRequest.getType().toLowerCase()) {
		case "pdf":
			return pdfGenerateService.generatePdfFile(filterRequest.getTemplateName(), data);
		case "excel":
			return excelGenerateService.generateExcelFile(filterRequest.getTemplateName(), data);  
		case "csv":
			return csvGenerateService.generateCsvFile(filterRequest.getTemplateName(), data);  
		default:
			throw new IllegalArgumentException("Invalid file type provided. Supported types: pdf, excel, csv");
		}
	}

}
