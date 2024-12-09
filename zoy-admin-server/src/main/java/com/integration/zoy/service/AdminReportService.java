package com.integration.zoy.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.integration.zoy.controller.PgOwnerMasterController;
import com.integration.zoy.exception.WebServiceException;
import com.integration.zoy.exception.ZoyAdminApplicationException;
import com.integration.zoy.model.FilterData;
import com.integration.zoy.repository.UserPaymentDueRepository;
import com.integration.zoy.repository.UserPaymentRepository;
import com.integration.zoy.repository.ZoyPgPropertyDetailsRepository;
import com.integration.zoy.utils.CommonResponseDTO;
import com.integration.zoy.utils.ConsilidatedFinanceDetails;
import com.integration.zoy.utils.TenentDues;
import com.integration.zoy.utils.UserPaymentDTO;
import com.integration.zoy.utils.UserPaymentFilterRequest;
import com.integration.zoy.utils.VendorPayments;
import com.integration.zoy.utils.VendorPaymentsDues;
import com.integration.zoy.utils.VendorPaymentsGst;
@Service
public class AdminReportService implements AdminReportImpl{
	private static final Logger log=LoggerFactory.getLogger(AdminReportService.class);
	@Autowired
	private UserPaymentRepository userPaymentRepository;

	@Autowired
	private UserPaymentDueRepository userPaymentDueRepository;

	@Autowired
	private  PdfGenerateService pdfGenerateService;

	@Autowired
	private ExcelGenerateService excelGenerateService;

	@Autowired
	private CsvGenerateService csvGenerateService;

	@Autowired
	EntityManager entityManager;

	@Autowired
	private ZoyPgPropertyDetailsRepository propertyDetailsRepository;

	@Value("${app.zoy.logo}")
	private String zoyLogoPath;

	@Override
	public CommonResponseDTO<UserPaymentDTO> getUserPaymentDetails(UserPaymentFilterRequest filterRequest,FilterData filterData) throws WebServiceException {
		try{
			StringBuilder queryBuilder = new StringBuilder(
					"SELECT up.user_id, " +
							"up.user_payment_timestamp, " +
							"up.user_payment_bank_transaction_id, " +
							"up.user_payment_payment_status, " +
							"up.user_payment_payable_amount, " +
							"up.user_payment_gst, " +
							"ud.user_personal_name, " +
							"pgt.property_name AS user_pg_propertyname, " + 
							"pgt.property_id AS user_pg_property_id, " +  
							"bd.bed_name, " +
							"up.user_payment_zoy_payment_type, " +
							"up.user_payment_result_method, " +
							"pgt.property_city " + 
							"FROM pgusers.user_payments up " +
							"JOIN pgusers.user_details ud ON up.user_id = ud.user_id " +
							"JOIN pgowners.zoy_pg_owner_booking_details bkd " +
							"ON up.user_id = bkd.tenant_id " +
							"AND up.user_payment_booking_id = bkd.booking_id " +
							"JOIN pgowners.zoy_pg_property_details pgt ON pgt.property_id = bkd.property_id " + 
							"JOIN pgowners.zoy_pg_bed_details bd ON bkd.selected_bed = bd.bed_id " +
							"WHERE 1=1 "
					);
			Map<String, Object> parameters = new HashMap<>();

			if (filterData.getTenantId() != null && !filterData.getTenantId().isEmpty()) {
				queryBuilder.append(" AND up.user_id = :userId");
				parameters.put("userId", filterData.getTenantId());
			}
			if (filterData.getTransactionStatus() != null && !filterData.getTransactionStatus().isEmpty()) {
				queryBuilder.append(" AND LOWER(up.user_payment_payment_status) LIKE LOWER(:resultStatus)");
				parameters.put("resultStatus", filterData.getTransactionStatus());
			}
			if (filterData.getModeOfPayment() != null && !filterData.getModeOfPayment().isEmpty()) {
				queryBuilder.append(" AND up.user_payment_result_method = :paymentMethod");
				parameters.put("paymentMethod", filterData.getModeOfPayment());
			}
			if (filterData.getPgId() != null && !filterData.getPgId().isEmpty()) {
				queryBuilder.append(" AND pgt.property_id = :pgPropertyId");
				parameters.put("pgPropertyId", filterData.getPgId());
			}

			if (filterData.getPgName() != null && !filterData.getPgName().isEmpty()) {
				queryBuilder.append(" AND LOWER(pgt.property_name) LIKE LOWER('%' || :pgPropertyName || '%')");
				parameters.put("pgPropertyName", filterData.getPgName());
			}
			if (filterData.getTenantName() != null && !filterData.getTenantName().isEmpty()) {
				queryBuilder.append(" AND LOWER(ud.user_personal_name) LIKE LOWER(:userPersonalName)");
				parameters.put("userPersonalName", "%" + filterData.getTenantName() + "%");
			}
			if (filterRequest.getCityLocation() != null && !filterRequest.getCityLocation().isEmpty()) {
				queryBuilder.append(" AND LOWER(pgt.property_city) LIKE LOWER(CONCAT('%', :cityLocation, '%'))");
				parameters.put("cityLocation", filterRequest.getCityLocation());
			}
			if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append(" AND up.user_payment_timestamp BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP)");
				parameters.put("fromDate", filterRequest.getFromDate());
				parameters.put("toDate", filterRequest.getToDate());
			}

			String sort = "up.user_payment_timestamp";  
			if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty() && filterRequest.getSortActive() != null) {
				if ("userPaymentBankTransactionId".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_bank_transaction_id";
				} else if ("userPaymentResultStatus".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_payment_status";
				} else if ("userId".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_id";
				} else if ("userPaymentPayableAmount".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_payable_amount";
				} else if ("userPaymentGst".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_gst";
				} else if ("totalAmount".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "(up.user_payment_payable_amount + up.user_payment_gst)";
				} else if ("userPersonalName".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "ud.user_personal_name";
				} else if ("userPgPropertyId".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "pgt.property_id";
				} else if ("userPgPropertyName".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "pgt.property_name";
				} else if ("bedNumber".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "bd.bed_name";
				} else if ("category".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_zoy_payment_type";
				} else if ("paymentMethod".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_result_method";
				} else {
					sort = "up.user_payment_timestamp";  
				}

				String sortDirection = filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";

				queryBuilder.append(" ORDER BY ").append(sort).append(" ").append(sortDirection);
			} else {
				queryBuilder.append(" ORDER BY up.user_payment_timestamp DESC");  
			}
			Query query = entityManager.createNativeQuery(queryBuilder.toString());
			parameters.forEach(query::setParameter);

			int filterCount=query.getResultList().size();

			query.setFirstResult(filterRequest.getPageIndex() * filterRequest.getPageSize());
			query.setMaxResults(filterRequest.getPageSize());

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
			return new CommonResponseDTO<>(userPaymentDTOs, filterCount);
		}catch (Exception e) {
			new ZoyAdminApplicationException(e, "");
		}
		return null;
	}

	@Override
	public CommonResponseDTO<ConsilidatedFinanceDetails> getConsolidatedFinanceDetails(UserPaymentFilterRequest filterRequest,FilterData filterData) throws WebServiceException{
		try{
			StringBuilder queryBuilder = new StringBuilder(
					"SELECT DISTINCT up.user_payment_timestamp AS transaction_date, " +
							"up.user_payment_bank_transaction_id AS transaction_number, " +
							"up.user_id, " +
							"ud.user_personal_name, " +
							"up.user_payment_payable_amount, " +
							"up.user_payment_gst, " +
							"pgt.property_city " +  
							"FROM pgusers.user_payments up " +
							"JOIN pgusers.user_details ud ON up.user_id = ud.user_id " +
							"JOIN pgowners.zoy_pg_owner_booking_details bkd ON up.user_id = bkd.tenant_id " +
							"JOIN pgowners.zoy_pg_property_details pgt ON bkd.property_id = pgt.property_id " +  // Join to get city details
					"WHERE 1=1 ");
			Map<String, Object> parameters = new HashMap<>();

			if (filterData.getTenantId() != null && !filterData.getTenantId().isEmpty()) {
				queryBuilder.append(" AND up.user_id = :userId");
				parameters.put("userId", filterData.getTenantId());
			}

			if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append(" AND up.user_payment_timestamp BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP)");
				parameters.put("fromDate", filterRequest.getFromDate());
				parameters.put("toDate", filterRequest.getToDate());
			}


			if (filterData.getTenantName() != null && !filterData.getTenantName().isEmpty()) {
				queryBuilder.append(" AND LOWER(ud.user_personal_name) LIKE LOWER(:userPersonalName)");
				parameters.put("userPersonalName", "%" + filterData.getTenantName() + "%");
			}

			if (filterRequest.getCityLocation() != null && !filterRequest.getCityLocation().isEmpty()) {
				queryBuilder.append(" AND LOWER(pgt.property_city) LIKE LOWER(CONCAT('%', :cityLocation, '%'))");
				parameters.put("cityLocation", filterRequest.getCityLocation() + "%");
			}
			String sort = "up.user_payment_timestamp";  

			if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty() && filterRequest.getSortActive() != null) {
				if ("customerId".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_id";
				} else if ("transactionDate".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_timestamp";
				} else if ("transactionNumber".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_bank_transaction_id";
				} else if ("customerName".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "ud.user_personal_name";
				} else if ("creditAmount".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "(up.user_payment_payable_amount + up.user_payment_gst)";
				} else if ("debitAmount".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_payable_amount";
				} else {
					sort = "up.user_payment_timestamp";
				}

				String sortDirection = filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";

				queryBuilder.append(" ORDER BY ").append(sort).append(" ").append(sortDirection);
			} else {
				queryBuilder.append(" ORDER BY up.user_payment_timestamp DESC");
			}

			Query query = entityManager.createNativeQuery(queryBuilder.toString());
			parameters.forEach(query::setParameter);
			int filterCount=query.getResultList().size();
			query.setFirstResult(filterRequest.getPageIndex() * filterRequest.getPageSize());
			query.setMaxResults(filterRequest.getPageSize());
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

			return new CommonResponseDTO<>(consolidatedFinanceDTOs, filterCount);
		}catch (Exception e) {
			new ZoyAdminApplicationException(e, "");
		}
		return null;
	}

	@Override
	public CommonResponseDTO<TenentDues> getTenentDuesDetails(UserPaymentFilterRequest filterRequest, FilterData filterData) throws WebServiceException {
		try{
			StringBuilder queryBuilder = new StringBuilder(
					"SELECT DISTINCT ON (u.user_money_due_id) " +
							"u.user_id, " +
							"u.user_money_due_amount, " +
							"u.user_money_due_bill_start_date, " +
							"ud.user_personal_name, " +
							"pgt.property_name AS user_pg_propertyname, " + 
							"pgt.property_id AS user_pg_property_id, " +
							"bd.bed_name, " +
							"u.user_money_due_id, " +
							"pgt.property_city " + 
							"FROM pgusers.user_dues u " +
							"JOIN pgusers.user_details ud ON u.user_id = ud.user_id " +
							"JOIN pgowners.zoy_pg_owner_booking_details bkd ON u.user_id = bkd.tenant_id " +
							"JOIN pgowners.zoy_pg_property_details pgt ON bkd.property_id = pgt.property_id " +  // Join with property details to get city
							"JOIN pgowners.zoy_pg_bed_details bd ON bkd.selected_bed = bd.bed_id " +
					"WHERE 1=1 ");

			Map<String, Object> parameters = new HashMap<>();

			if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append(" AND u.user_money_due_timestamp BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP)");
				parameters.put("fromDate", filterRequest.getFromDate());
				parameters.put("toDate", filterRequest.getToDate());
			}

			if (filterData.getTenantId() != null && !filterData.getTenantId().isEmpty()) {
				queryBuilder.append(" AND u.user_id = :tenantId");
				parameters.put("tenantId", filterData.getTenantId());
			}

			if (filterData.getTenantName() != null && !filterData.getTenantName().isEmpty()) {
				queryBuilder.append(" AND LOWER(ud.user_personal_name) LIKE LOWER(:tenantName)");
				parameters.put("tenantName", "%" + filterData.getTenantName() + "%");
			}

			if (filterData.getPgId() != null && !filterData.getPgId().isEmpty()) {
				queryBuilder.append(" AND pgd.user_pg_property_id = :pgPropertyId");
				parameters.put("pgPropertyId", filterData.getPgId());
			}

			if (filterData.getPgName() != null && !filterData.getPgName().isEmpty()) {
				queryBuilder.append(" AND LOWER(pgd.user_pg_propertyname) LIKE LOWER(:pgPropertyName)");
				parameters.put("pgPropertyName", filterData.getPgName());
			}

			if (filterRequest.getCityLocation() != null && !filterRequest.getCityLocation().isEmpty()) {
				queryBuilder.append(" AND LOWER(pgt.property_city) LIKE LOWER(:cityLocation)");
				parameters.put("cityLocation", filterRequest.getCityLocation());
			}

			if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty() && filterRequest.getSortActive() != null) {
				String sort = "";

				if ("customerId".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "u.user_id";
				} else if ("customerName".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "ud.user_personal_name";
				} else if ("PgPropertyName".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "pgt.property_name";
				} else if ("PgPropertyId".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "pgt.property_id";
				} else if ("bedNumber".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "bd.bed_name";
				} else if ("pendingAmount".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "u.user_money_due_amount";
				} else if ("pendingDueDate".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "u.user_money_due_bill_start_date";
				} else {
					sort = "u.user_money_due_bill_start_date"; 
				}

				String sortDirection = filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";
				queryBuilder.append(" ORDER BY u.user_money_due_id, ").append(sort).append(" ").append(sortDirection);
			} else {
				queryBuilder.append(" ORDER BY  u.user_money_due_id,u.user_money_due_bill_start_date DESC");
			}


			Query query = entityManager.createNativeQuery(queryBuilder.toString());
			parameters.forEach(query::setParameter);
			int filterCount=query.getResultList().size();
			query.setFirstResult(filterRequest.getPageIndex() * filterRequest.getPageSize());
			query.setMaxResults(filterRequest.getPageSize());
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

			return new CommonResponseDTO<>(tenentDuesDto, filterCount);
		}catch (Exception e) {
			new ZoyAdminApplicationException(e, "");
		}
		return null;
	}


	@Override
	public CommonResponseDTO<VendorPayments> getVendorPaymentDetails(UserPaymentFilterRequest filterRequest, FilterData filterData) throws WebServiceException{	
		try{
			StringBuilder queryBuilder = new StringBuilder(
					"SELECT DISTINCT ON (up.user_payment_id) " + 
							"o.pg_owner_id AS ownerId, " +
							"o.pg_owner_name AS ownerName, " +
							"pd.property_id AS pgId, " +
							"pd.property_name AS pgName, " +
							"up.user_payment_payable_amount AS totalAmountFromTenants, " +
							"up.user_payment_timestamp AS transactionDate, " +
							"up.user_payment_bank_transaction_id AS transactionNumber, " +
							"up.user_payment_payment_status AS paymentStatus, " +
							"pd.property_city AS city " +
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
				queryBuilder.append(" AND up.user_payment_timestamp BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP)");
				parameters.put("fromDate", filterRequest.getFromDate());
				parameters.put("toDate", filterRequest.getToDate());
			}

			if (filterData.getOwnerName() != null && !filterData.getOwnerName().isEmpty()) {
				queryBuilder.append(" AND LOWER(o.pg_owner_name) LIKE LOWER(:ownerName)");
				parameters.put("ownerName", "%" + filterData.getOwnerName() + "%");
			}

			if (filterData.getPgId() != null && !filterData.getPgId().isEmpty()) {
				queryBuilder.append(" AND pd.property_id = :pgId");
				parameters.put("pgId", filterData.getPgId());
			}

			if (filterData.getPgName() != null && !filterData.getPgName().isEmpty()) {
				queryBuilder.append(" AND LOWER(pd.property_name) LIKE LOWER(:pgName)");
				parameters.put("pgName", "%" + filterData.getPgName() + "%");
			}

			if (filterData.getTransactionStatus() != null && !filterData.getTransactionStatus().isEmpty()) {
				queryBuilder.append(" AND up.user_payment_payment_status = :paymentStatus");
				parameters.put("paymentStatus", filterData.getTransactionStatus());
			}

			if (filterRequest.getCityLocation() != null && !filterRequest.getCityLocation().isEmpty()) {
				queryBuilder.append(" AND LOWER(pd.property_city) LIKE LOWER('%' || :cityLocation || '%')");
				parameters.put("cityLocation", filterRequest.getCityLocation());
			}

			if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty() && filterRequest.getSortActive() != null) {
				String sort = "";

				if ("ownerId".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "o.pg_owner_id";
				} else if ("ownerName".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "o.pg_owner_name";
				} else if ("pgId".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "pd.property_id";
				} else if ("pgName".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "pd.property_name";
				} else if ("totalAmountFromTenants".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "SUM(up.user_payment_payable_amount)";
				} else if ("transactionDate".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_timestamp";
				} else if ("transactionNumber".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_bank_transaction_id";
				} else if ("paymentStatus".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_payment_status";
				} else {
					sort = "up.user_payment_timestamp";  
				}

				String sortDirection = filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";

				queryBuilder.append(" ORDER BY up.user_payment_id, ").append(sort).append(" ").append(sortDirection);
			} else {
				queryBuilder.append(" ORDER BY up.user_payment_id, up.user_payment_timestamp DESC");
			}

			Query query = entityManager.createNativeQuery(queryBuilder.toString());
			parameters.forEach(query::setParameter);
			int filterCount=query.getResultList().size();
			query.setFirstResult(filterRequest.getPageIndex() * filterRequest.getPageSize());
			query.setMaxResults(filterRequest.getPageSize());

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

			return new CommonResponseDTO<>(vendorPaymentsDto, filterCount);
		}catch (Exception e) {
			new ZoyAdminApplicationException(e, "");
		}
		return null;
	}


	@Override
	public CommonResponseDTO<VendorPaymentsDues> getVendorPaymentDuesDetails(String fromDate, String toDate) throws WebServiceException{		
		try{
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
			int resultCount = vendorPaymentsDues.size();
			return new CommonResponseDTO<>(vendorPaymentsDues, resultCount);
		}catch (Exception e) {
			new ZoyAdminApplicationException(e, "");
		}
		return null;
	}

	@Override
	public CommonResponseDTO<VendorPaymentsGst> getVendorPaymentGstDetails(String fromDate, String toDate) throws WebServiceException{
		try {
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
			int resultCount = vendorPaymentsGst.size();
			return new CommonResponseDTO<>(vendorPaymentsGst, resultCount);
		}catch (Exception e) {
			new ZoyAdminApplicationException(e, "");
		}
		return null;
	}

	@Override
	public byte[] generateDynamicReport(UserPaymentFilterRequest filterRequest, FilterData filterData) throws WebServiceException {
		try {
			Map<String, Object> data = new HashMap<>();
			CommonResponseDTO<?> reportData = null; 
			switch (filterRequest.getReportType()) {
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
			List<?> dataList = reportData.getData();
			data.put("reportData", dataList);
			data.put("startDate", Timestamp.valueOf(filterRequest.getFromDate()));
			data.put("endDate", Timestamp.valueOf(filterRequest.getToDate()));
			data.put("printDate", new Timestamp(System.currentTimeMillis()));

			switch (filterRequest.getDownloadType().toLowerCase()) {
			case "pdf":
				String logoPath = Paths.get(zoyLogoPath).toFile().getPath();
				//data.put("appLogo", pdfGenerateService.imageToBase64(Paths.get(zoyLogoPath).toFile().getPath()));
				log.info("Logo path: {}", zoyLogoPath);
				File logoFile = new File(zoyLogoPath);
				if (!logoFile.exists()) {
					log.error("Logo file not found at path: {}", zoyLogoPath);
					throw new FileNotFoundException("Logo file not found at path: " + zoyLogoPath);
				}
				try {
					String base64Logo = pdfGenerateService.imageToBase64(logoPath);
					data.put("appLogo", base64Logo);
					log.info("Logo converted to Base64 successfully.");
				} catch (Exception e) {
					log.error("Error converting logo to Base64: {}", e.getMessage(), e);
					throw new IllegalStateException("Failed to convert logo to Base64", e);
				}
				return pdfGenerateService.generatePdfFile(filterRequest.getReportType(), data);
			case "excel":
				return excelGenerateService.generateExcelFile(filterRequest.getReportType(), data);  
			case "csv":
				return csvGenerateService.generateCsvFile(filterRequest.getReportType(), data);  
			default:
				throw new IllegalArgumentException("Invalid file type provided. Supported types: pdf, excel, csv");
			}
		}catch (Exception e) {
			new ZoyAdminApplicationException(e, "");
		}
		return null;
	}

	public String[] getDistinctCities() {
		return propertyDetailsRepository.findDistinctCities();
	}

}
