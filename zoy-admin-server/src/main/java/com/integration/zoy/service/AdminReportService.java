package com.integration.zoy.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.integration.zoy.exception.WebServiceException;
import com.integration.zoy.exception.ZoyAdminApplicationException;
import com.integration.zoy.model.FilterData;
import com.integration.zoy.model.TenantResportsDTO;
import com.integration.zoy.model.UpcomingPotentialPropertyDTO;
import com.integration.zoy.model.ZoyShareReportDTO;
import com.integration.zoy.repository.UserPaymentDueRepository;
import com.integration.zoy.repository.UserPaymentRepository;
import com.integration.zoy.repository.ZoyPgPropertyDetailsRepository;
import com.integration.zoy.utils.CommonResponseDTO;
import com.integration.zoy.utils.ConsilidatedFinanceDetails;
import com.integration.zoy.utils.PropertyResportsDTO;
import com.integration.zoy.utils.RatingsAndReviewsReport;
import com.integration.zoy.utils.RegisterLeadDetails;
import com.integration.zoy.utils.RegisterTenantsDTO;
import com.integration.zoy.utils.TenentDues;
import com.integration.zoy.utils.TenentRefund;
import com.integration.zoy.utils.UserPaymentDTO;
import com.integration.zoy.utils.UserPaymentFilterRequest;
import com.integration.zoy.utils.VendorPayments;
import com.integration.zoy.utils.VendorPaymentsDues;
import com.integration.zoy.utils.VendorPaymentsGst;
@Service
public class AdminReportService implements AdminReportImpl{
	private static final Logger log=LoggerFactory.getLogger(AdminReportService.class);
	NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));
	String rupeeSymbol = "\u20B9";
	@Autowired
	private UserPaymentRepository userPaymentRepository;

	@Autowired
	private UserPaymentDueRepository userPaymentDueRepository;

	@Autowired
	private ExcelGenerateService excelGenerateService;

	@Autowired
	private CsvGenerateService csvGenerateService;

	@Autowired
	private ZoyPgPropertyDetailsRepository zoyPgPropertyDetailsRepository;
	@Autowired
	EntityManager entityManager;

	@Autowired
	private ZoyPgPropertyDetailsRepository propertyDetailsRepository;
	@Autowired
	private WordToPdfConverterService wordToPdfConverterService;
	
	@Autowired
	private TimestampFormatterUtilService tuService;

	@Value("${zoy.admin.logo}")
	private String zoyLogoPath;
	@Value("${zoy.admin.watermark}")
	private String watermarkImagePath;
	
	@Autowired
	ZoyAdminService zoyAdminService;
	@Value("${app.minio.user.photos.bucket.name}")
	private String userPhotoBucketName;
	
	@Value("${app.minio.aadhaar.photos.bucket.name}")
	String aadhaarPhotoBucket;

	@Value("${spring.jackson.time-zone}")
	private String TIME_ZONE;

	@Override
	public CommonResponseDTO<UserPaymentDTO> getUserPaymentDetails(UserPaymentFilterRequest filterRequest,FilterData filterData,Boolean applyPagination,Boolean isGstReport) throws WebServiceException {
		try{
			StringBuilder queryBuilder = new StringBuilder(
					"SELECT \r\n"
				            + " up.user_payment_created_at, \r\n"
				            + " up.user_payment_result_invoice_id, \r\n"
				            + " CASE \r\n"
				            + " WHEN LOWER(up.user_payment_zoy_payment_mode) = 'offline' THEN 'Paid-Cash' \r\n"
				            + " ELSE up.user_payment_payment_status \r\n"
				            + " END AS user_payment_payment_status, \r\n"
				            + " up.user_payment_payable_amount, \r\n"
				            + " up.user_payment_gst, \r\n"
				            + " um.user_first_name || ' ' || um.user_last_name AS user_personal_name, \r\n" 
				            + " pgt.property_name AS user_pg_propertyname, \r\n"
				            + " bd.bed_name, \r\n"
				            + " STRING_AGG(zpdm.due_name, ', ') AS user_money_due_descriptions, \r\n"
				            + " CASE \r\n"
				            + " WHEN LOWER(up.user_payment_zoy_payment_mode) = 'offline' THEN 'Cash' \r\n"
				            + " ELSE up.user_payment_result_method \r\n"
				            + " END AS user_payment_result_method, \r\n"
				            + " pgt.property_city, \r\n"
				            + " pgt.property_house_area, \r\n"
				            + " um.user_mobile, \r\n"
				            + " pgt.property_id \r\n"
				            + "FROM pgusers.user_payments up \r\n"
				            + "LEFT JOIN pgusers.user_details ud \r\n"
				            + " ON up.user_id = ud.user_id\r\n"
				            + "LEFT JOIN pgusers.user_master um \r\n"
				            + " ON up.user_id = um.user_id \r\n"
				            + "LEFT JOIN pgowners.zoy_pg_owner_booking_details bkd \r\n"
				            + " ON up.user_id = bkd.tenant_id \r\n"
				            + " AND up.user_payment_booking_id = bkd.booking_id \r\n"
				            + "LEFT JOIN pgowners.zoy_pg_property_details pgt \r\n"
				            + " ON pgt.property_id = bkd.property_id \r\n"
				            + "LEFT JOIN pgowners.zoy_pg_bed_details bd \r\n"
				            + " ON bkd.selected_bed = bd.bed_id \r\n"
				            + "LEFT JOIN pgusers.user_payment_due due \r\n"
				            + " ON up.user_payment_id = due.user_payment_id \r\n"
				            + "LEFT JOIN pgusers.user_dues dues \r\n"
				            + " ON due.user_money_due_id = dues.user_money_due_id \r\n"
				            + "JOIN pgowners.zoy_pg_due_type_master zpdtm \r\n"
				            + " ON zpdtm.due_id = dues.user_money_due_type \r\n"
				            + "JOIN pgowners.zoy_pg_due_master zpdm \r\n"
				            + " ON zpdm.due_type_id = zpdtm.due_type \r\n"
				            + "WHERE 1=1 \r\n");
			Map<String, Object> parameters = new HashMap<>();

			if (filterData.getTransactionStatus() != null && !filterData.getTransactionStatus().isEmpty()) {
				queryBuilder.append(" AND LOWER(CASE " +
						" WHEN LOWER(up.user_payment_zoy_payment_mode) = 'offline' THEN 'Paid-Cash' " +
						" ELSE up.user_payment_payment_status " +
						"END) LIKE LOWER(CONCAT('%', :transactionStatus, '%'))");
				parameters.put("transactionStatus", filterData.getTransactionStatus());
			}
			if (filterData.getModeOfPayment() != null && !filterData.getModeOfPayment().isEmpty()) {
				queryBuilder.append(" AND LOWER(up.user_payment_result_method) LIKE LOWER(CONCAT('%', :modeOfPayment, '%'))");
				parameters.put("modeOfPayment", filterData.getModeOfPayment());
			}
			if (filterData.getPgName() != null && !filterData.getPgName().isEmpty()) {
				queryBuilder.append(" AND LOWER(pgt.property_name) LIKE LOWER('%' || :pgName || '%')");
				parameters.put("pgName", filterData.getPgName());
			}
			if (filterData.getTenantName() != null && !filterData.getTenantName().isEmpty()) {
				queryBuilder.append(" AND LOWER(um.user_first_name || ' ' || um.user_last_name) LIKE LOWER(:tenantName)");
				parameters.put("tenantName", "%" + filterData.getTenantName() + "%");
			}
			if (filterRequest.getCityLocation() != null && !filterRequest.getCityLocation().isEmpty()) {
				queryBuilder.append(" AND LOWER(pgt.property_city) LIKE LOWER(CONCAT('%', :cityLocation, '%'))");
				parameters.put("cityLocation", filterRequest.getCityLocation());
			}
			if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append(" AND up.user_payment_created_at BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP)");
				parameters.put("fromDate", filterRequest.getFromDate());
				parameters.put("toDate", filterRequest.getToDate());
			}
			if (filterRequest.getPropertyId() != null && !filterRequest.getPropertyId().isEmpty()) {
				queryBuilder.append(" AND LOWER(pgt.property_id) LIKE LOWER(:propertyId)");
				parameters.put("propertyId", "%" + filterRequest.getPropertyId() + "%");
			}
			if (filterData.getTenantContactNum() != null && !filterData.getTenantContactNum().isEmpty()) {
				queryBuilder.append(" AND LOWER(um.user_mobile) LIKE LOWER(:tenantContactNum)");
				parameters.put("tenantContactNum", "%" + filterData.getTenantContactNum() + "%");
			}
			if (filterData.getTransactionNumber() != null && !filterData.getTransactionNumber().isEmpty()) {
				queryBuilder.append(" AND LOWER(up.user_payment_result_invoice_id) LIKE LOWER(:transactionNumber)");
				parameters.put("transactionNumber", "%" + filterData.getTransactionNumber() + "%");
			}

			String sort = "up.user_payment_created_at";
			if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty() && filterRequest.getSortActive() != null) {
				if ("transactionNumber".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_result_invoice_id";
				} else if ("transactionStatus".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_payment_status";
				} else if ("baseAmount".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_payable_amount";
				} else if ("gstAmount".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_gst";
				} else if ("totalAmount".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "(up.user_payment_payable_amount + up.user_payment_gst)";
				} else if ("customerName".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "um.user_first_name || ' ' || um.user_last_name";
				} else if ("PgPropertyName".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "pgt.property_name";
				} else if ("bedNumber".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "bd.bed_name";
				} else if ("category".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "user_money_due_descriptions";
				} else if ("paymentMethod".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_result_method";
				} else if("propertyHouseArea".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "pgt.property_house_area";
				} else if("tenantContactNum".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "um.user_mobile";
				}
				else {
					sort = "up.user_payment_created_at";
				}
				if(isGstReport) {
					 queryBuilder.append(" AND  up.user_payment_gst IS NOT NULL AND up.user_payment_gst <> 0 \r\n");
					
				}
				queryBuilder.append(" GROUP BY \r\n"
					    + " up.user_payment_created_at, \r\n"
					    + " up.user_payment_result_invoice_id, \r\n"
					    + " CASE \r\n"
					    + " WHEN LOWER(up.user_payment_zoy_payment_mode) = 'offline' THEN 'Paid-Cash' \r\n"
					    + " ELSE up.user_payment_payment_status \r\n"
					    + " END, \r\n"
					    + " up.user_payment_payable_amount, \r\n"
					    + " up.user_payment_gst, \r\n"
					    + " um.user_first_name || ' ' || um.user_last_name, \r\n"
					    + " pgt.property_name, \r\n"
					    + " bd.bed_name, \r\n"
					    + " CASE \r\n"
					    + " WHEN LOWER(up.user_payment_zoy_payment_mode) = 'offline' THEN 'Cash' \r\n"
					    + " ELSE up.user_payment_result_method \r\n"
					    + " END, \r\n"
					    + " pgt.property_city, \r\n"
					    + " pgt.property_house_area, \r\n"
					    + " um.user_mobile, \r\n"
					    + " pgt.property_id, \r\n"
					    + " up.user_payment_result_method, \r\n"
					    + " up.user_payment_payment_status, \r\n"
					    + " up.user_payment_result_reason \r\n");
				
				String sortDirection = filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";

				queryBuilder.append(" ORDER BY ").append(sort).append(" ").append(sortDirection);
			} else {
				queryBuilder.append(" ORDER BY up.user_payment_created_at DESC");
			}
			Query query = entityManager.createNativeQuery(queryBuilder.toString());
			parameters.forEach(query::setParameter);

			int filterCount=query.getResultList().size();
			if(applyPagination) {
				query.setFirstResult(filterRequest.getPageIndex() * filterRequest.getPageSize());
				query.setMaxResults(filterRequest.getPageSize());
			}
			List<Object[]> result = query.getResultList();
			List<UserPaymentDTO> userPaymentDTOs = result.stream().map(row -> {
				UserPaymentDTO dto = new UserPaymentDTO();
				try {
				dto.setTransactionDate((Timestamp) row[0]);
				dto.setTransactionNumber(row[1] != null ? (String) row[1] : "");
				dto.setTransactionStatus(row[2] != null ? (String) row[2] : "");
				double payableAmount = row[3] != null ? ((BigDecimal) row[3]).doubleValue() : 0.0;
				double gst = row[4] != null ? ((BigDecimal) row[4]).doubleValue() : 0.0;
				double dueamount=payableAmount-gst;
				dto.setDueAmount(dueamount != 0.0 ? dueamount : 0.0);
				dto.setGstAmount((row[4] != null) ? ((Number) row[4]).doubleValue() : 0.0);
				dto.setUserPersonalName(row[5] != null ? (String) row[5] : "");
				dto.setUserPgPropertyName(row[6] != null ? (String) row[6] : "");
				dto.setRoomBedNumber(row[7] != null ? (String) row[7] : "");
				dto.setTotalAmount((row[3] != null) ? ((Number) row[3]).doubleValue() : 0.0);
				dto.setCategory(row[8] != null ? (String) row[8] : "");
				dto.setPaymentMode(row[9] != null ? (String) row[9] : "");
				dto.setPropertyHouseArea(row[11] != null ? (String) row[11] : "");
				dto.setTenantContactNum(row[12] != null ? (String) row[12] : "");
				dto.setPropertyId(row[13] != null ? (String) row[13] : "");
				} catch (Exception e) {
			        e.printStackTrace();
			    }
				return dto;
			}).collect(Collectors.toList());
			return new CommonResponseDTO<>(userPaymentDTOs, filterCount);
		}catch (Exception e) {
			new ZoyAdminApplicationException(e, "");
		}
		return null;
	}

	@Override
	public CommonResponseDTO<ConsilidatedFinanceDetails> getConsolidatedFinanceDetails(UserPaymentFilterRequest filterRequest,FilterData filterData,Boolean applyPagination) throws WebServiceException{
		try{
			StringBuilder queryBuilder = new StringBuilder(
						    "SELECT  up.user_payment_timestamp AS transaction_date, " +
						            "up.user_payment_result_invoice_id AS transaction_number, " +
						            "'Tenant' AS payer_payee_type, " +
						            "um.user_first_name || ' ' || um.user_last_name AS user_personal_name, " +
						            "up.user_payment_payable_amount, " +
						            "up.user_payment_gst, " +
						            "pgt.property_city, " +
						            "pgt.property_name, " +
						            "um.user_mobile " +
						            "FROM pgowners.zoy_pg_owner_booking_details zpobd " +
						            "JOIN pgusers.user_payments up ON up.user_payment_booking_id = zpobd.booking_id " +
						            "JOIN pgusers.user_master um ON zpobd.tenant_id = um.user_id " +
						            "JOIN pgowners.zoy_pg_property_details pgt ON zpobd.property_id = pgt.property_id " +
						            "WHERE 1=1"
						);
			
			
			Map<String, Object> parameters = new HashMap<>();

			if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append(" AND up.user_payment_timestamp BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP)");
				parameters.put("fromDate", filterRequest.getFromDate());
				parameters.put("toDate", filterRequest.getToDate());
			}


			if (filterData.getPayerName() != null && !filterData.getPayerName().isEmpty()) {
				queryBuilder.append(" AND LOWER(um.user_first_name || ' ' || um.user_last_name) LIKE LOWER(:userPersonalName)");
				parameters.put("userPersonalName", "%" + filterData.getPayerName() + "%");
			}

			if (filterData.getPayerType() != null && !filterData.getPayerType().isEmpty()) {
				queryBuilder.append(" AND 'Tenant' = :payerPayeeType");
				parameters.put("payerPayeeType", filterData.getPayerType());
			}

			if (filterData.getPgName() != null && !filterData.getPgName().isEmpty()) {
				queryBuilder.append(" AND LOWER(pgt.property_name) LIKE LOWER(CONCAT('%', :pgName, '%'))");
				parameters.put("pgName", filterData.getPgName() + "%");
			}

			if (filterRequest.getCityLocation() != null && !filterRequest.getCityLocation().isEmpty()) {
				queryBuilder.append(" AND LOWER(pgt.property_city) LIKE LOWER(CONCAT('%', :cityLocation, '%'))");
				parameters.put("cityLocation", filterRequest.getCityLocation() + "%");
			}
			if (filterData.getTransactionNumber() != null && !filterData.getTransactionNumber().isEmpty()) {
				queryBuilder.append(" AND LOWER(up.user_payment_result_invoice_id) LIKE LOWER(:transactionNumber)");
				parameters.put("transactionNumber", "%" + filterData.getTransactionNumber() + "%");
			}
			if (filterData.getTenantContactNum() != null && !filterData.getTenantContactNum().isEmpty()) {
				queryBuilder.append(" AND LOWER(um.user_mobile) LIKE LOWER(:tenantContactNum)");
				parameters.put("tenantContactNum", "%" + filterData.getTenantContactNum() + "%");
			}
			String sort = "up.user_payment_timestamp";

			if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty() && filterRequest.getSortActive() != null) {
				if ("transactionDate".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_timestamp";
				} else if ("transactionNumber".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_result_invoice_id";
				} else if ("payerPayeeName".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "um.user_first_name || ' ' || um.user_last_name";
				} else if ("creditAmount".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "(up.user_payment_payable_amount)";
				} else if ("debitAmount".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_payable_amount";
				}else if ("pgName".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "pgt.property_name";
				}else if ("tenantContactNum".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "um.user_mobile";
				}else {
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
			if(applyPagination) {
				query.setFirstResult(filterRequest.getPageIndex() * filterRequest.getPageSize());
				query.setMaxResults(filterRequest.getPageSize());
			}
			List<Object[]> result= query.getResultList();
			List<ConsilidatedFinanceDetails> consolidatedFinanceDTOs = result.stream().map(row -> {
				ConsilidatedFinanceDetails dto = new ConsilidatedFinanceDetails();
				dto.setUserPaymentTimestamp((Timestamp) row[0]);
				dto.setUserPaymentBankTransactionId(row[1] != null ? (String) row[1] : "");
				dto.setPayerPayeeType(row[2] != null ? (String) row[2] : "");
				dto.setPayerPayeeName(row[3] != null ? (String) row[3] : "");
				BigDecimal payableAmount = (BigDecimal) row[4] != null ? (BigDecimal) row[4] : BigDecimal.ZERO;
				//BigDecimal gst = (BigDecimal) row[5] != null ? (BigDecimal) row[5] : BigDecimal.ZERO;
				//BigDecimal totalAmount = payableAmount;
				dto.setCreditAmount((payableAmount != null) ? ((Number) payableAmount).doubleValue() : 0.0);
				dto.setDebitAmount(BigDecimal.valueOf(0).doubleValue());
				dto.setPgName(row[7] != null ? (String) row[7] : "");
				dto.setContactNum(row[8] != null ? (String) row[8] : "");
				return dto;
			}).collect(Collectors.toList());

			return new CommonResponseDTO<>(consolidatedFinanceDTOs, filterCount);
		}catch (Exception e) {
			new ZoyAdminApplicationException(e, "");
		}
		return null;
	}

	@Override
	public CommonResponseDTO<TenentDues> getTenentDuesDetails(UserPaymentFilterRequest filterRequest, FilterData filterData,Boolean applyPagination) throws WebServiceException {
		try{
			StringBuilder queryBuilder = new StringBuilder(
					"SELECT\r\n"
							+ "ud.user_money_due_amount,\r\n"
							+ "ud.user_money_due_bill_end_date,\r\n"
							+ "um.user_first_name || ' ' || um.user_last_name AS username,\r\n"
							+ "pgt.property_name,\r\n"
							+ "pgt.property_house_area,\r\n"
							+ "bd.bed_name,\r\n"
							+ "ud.user_money_due_id,\r\n"
							+ "pgt.property_city,\r\n"
							+ "um.user_mobile\r\n"
							+ "FROM pgusers.user_dues ud\r\n"
							+ "JOIN pgusers.user_master um ON ud.user_id = um.user_id\r\n"
							+ "JOIN pgowners.zoy_pg_owner_booking_details zpobd ON ud.user_booking_id = zpobd.booking_id\r\n"
							+ "JOIN pgowners.zoy_pg_property_details pgt ON zpobd.property_id = pgt.property_id\r\n"
							+ "JOIN pgowners.zoy_pg_bed_details bd ON zpobd.selected_bed = bd.bed_id\r\n"
							+ "LEFT JOIN pgusers.user_payment_due upd ON ud.user_money_due_id = upd.user_money_due_id\r\n"
							+ "WHERE 1=1 and upd.user_money_due_id IS NULL");

			Map<String, Object> parameters = new HashMap<>();

			if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append(" AND ud.user_money_due_bill_end_date BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP)");
				parameters.put("fromDate", filterRequest.getFromDate());
				parameters.put("toDate", filterRequest.getToDate());
			}

			if (filterData.getTenantName() != null && !filterData.getTenantName().isEmpty()) {
				queryBuilder.append(" AND LOWER(um.user_first_name || ' ' || um.user_last_name) LIKE LOWER(:tenantName)");
				parameters.put("tenantName", "%" + filterData.getTenantName() + "%");
			}
			if (filterData.getBedNumber() != null && !filterData.getBedNumber().isEmpty()) {
				queryBuilder.append(" AND LOWER(bd.bed_name) LIKE LOWER(:bedNumber)");
				parameters.put("bedNumber", "%" + filterData.getBedNumber() + "%");
			}
			if (filterData.getTenantContactNum() != null && !filterData.getTenantContactNum().isEmpty()) {
				queryBuilder.append(" AND um.user_mobile IS NOT NULL AND LOWER(um.user_mobile) LIKE LOWER(:tenantContactNum)");
				parameters.put("tenantContactNum", "%" + filterData.getTenantContactNum() + "%");
			}

			if (filterData.getPgName() != null && !filterData.getPgName().isEmpty()) {
				queryBuilder.append(" AND LOWER(pgt.property_name) LIKE LOWER(:pgName)");
				parameters.put("pgName","%" +filterData.getPgName() +"%");
			}

			if (filterRequest.getCityLocation() != null && !filterRequest.getCityLocation().isEmpty()) {
				queryBuilder.append(" AND LOWER(pgt.property_city) LIKE LOWER(:cityLocation)");
				parameters.put("cityLocation", "%" +filterRequest.getCityLocation()+ "%");
			}

			if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty() && filterRequest.getSortActive() != null) {
				String sort = "";

				if ("customerName".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "um.user_first_name || ' ' || um.user_last_name";
				} else if ("PgPropertyName".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "pgt.property_name";
				}else if ("bedNumber".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "bd.bed_name";
				} else if ("pendingAmount".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "ud.user_money_due_amount";
				} else if ("pendingDueDate".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "ud.user_money_due_bill_end_date";
				}else if ("tenantMobileNum".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "um.user_mobile";
				}else {
					sort = "ud.user_money_due_bill_end_date";
				}

				String sortDirection = filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";
				queryBuilder.append(" ORDER BY ").append(sort).append(" ").append(sortDirection);
			} else {
				queryBuilder.append(" ORDER BY ud.user_money_due_id,ud.user_money_due_bill_end_date DESC");
			}


			Query query = entityManager.createNativeQuery(queryBuilder.toString());
			parameters.forEach(query::setParameter);
			int filterCount=query.getResultList().size();
			if(applyPagination) {
				query.setFirstResult(filterRequest.getPageIndex() * filterRequest.getPageSize());
				query.setMaxResults(filterRequest.getPageSize());
			}
			List<Object[]> results = query.getResultList();
			List<TenentDues> tenentDuesDto = results.stream().map(row -> {
				TenentDues dto = new TenentDues();
				boolean isPresent = userPaymentDueRepository.existsByUserMoneyDueId(row[6] != null ? (String) row[6] : "");
				if (isPresent) {
					String userPaymentId = userPaymentDueRepository.findUserPaymentIdByUserMoneyDueId(row[6] != null ? (String) row[6] : "");
					BigDecimal payableAmount = userPaymentRepository.findUserPaymentPayableAmountByUserPaymentId(userPaymentId);
					if (((BigDecimal) row[0]).subtract(payableAmount).compareTo(BigDecimal.ZERO) > 0) {
						dto.setPendingAmount((payableAmount != null) ? ((Number) payableAmount).doubleValue() : 0.0);
					} else {
						dto.setPendingAmount((row[0] != null) ? ((Number) row[0]).doubleValue() : 0.0);
					}
				} else {
					dto.setPendingAmount((row[0] != null) ? ((Number) row[0]).doubleValue() : 0.0);
				}
				Timestamp pendingDueDate = (Timestamp) row[1];
				String formattedDueDate =tuService.formatTimestamp(pendingDueDate.toInstant());
				String formatteDDate = tuService.convertToDateOnly(formattedDueDate);
				if (pendingDueDate != null) {
			        dto.setPendingDueDate(formatteDDate);
			    }
				dto.setUserPersonalName(row[2] != null ? (String) row[2] : "");
				dto.setUserPgPropertyName(row[3] != null ? (String) row[3] : "");
				dto.setUserPgPropertyAddress(row[4] != null ? (String) row[4] : "");
				dto.setBedNumber(row[5] != null ? (String) row[5] : "");
				dto.setTenantMobileNum(row[8] != null ? (String) row[8] : "");
				return dto;
			}).collect(Collectors.toList());

			return new CommonResponseDTO<>(tenentDuesDto, filterCount);
		}catch (Exception e) {
			new ZoyAdminApplicationException(e, "");
		}
		return null;
	}


	@Override
	public CommonResponseDTO<VendorPayments> getVendorPaymentDetails(UserPaymentFilterRequest filterRequest, FilterData filterData,Boolean applyPagination) throws WebServiceException{
		try{
			StringBuilder queryBuilder = new StringBuilder("SELECT DISTINCT ON (up.user_payment_id) "
					+ "o.pg_owner_name AS ownerName, "
					+ "pd.property_name AS pgName, "
					+ "ud2.user_money_due_amount AS totalAmountFromTenants,  "
					+ "up.user_payment_timestamp AS transactionDate,  "
					+ "up.user_payment_result_invoice_id AS transactionNumber,  "
					+ "up.user_payment_payment_status AS paymentStatus,  "
					+ "pd.property_city AS city,  "
					+ "pd.property_house_area AS propertyAddress,  "
					+ "o.pg_owner_email AS ownerEmail, "
					+ "case when zpdm.due_name = 'Rent' then (case when pd.zoy_variable_share =0 then pd.zoy_fixed_share "
					+ "else pd.zoy_variable_share end) else 0 end as zoyshare, "
					+ "case when zpdm.due_name = 'Rent' then case when pd.zoy_variable_share =0 then "
					+ "ud2.user_money_due_amount - pd.zoy_fixed_share "
					+ "else  ud2.user_money_due_amount - (ud2.user_money_due_amount * (pd.zoy_variable_share/100)) end  "
					+ "else ud2.user_money_due_amount end as paidToOwner, "
					+ "case when zposs.is_approved =true then 'Approved' else (case when zposs.is_rejected =true  "
					+ "then 'Rejected' else 'Processing' end) end as ownerPaymentStatus "
					+ "FROM pgusers.user_payments up  "
					+ "JOIN pgusers.user_pg_details pgd ON up.user_id = pgd.user_id  "
					+ "JOIN pgusers.user_details ud ON up.user_id = ud.user_id  "
					+ "JOIN pgowners.zoy_pg_owner_booking_details bkd ON up.user_id = bkd.tenant_id  "
					+ "AND up.user_payment_booking_id = bkd.booking_id  "
					+ "AND pgd.user_pg_property_id = bkd.property_id  "
					+ "JOIN pgowners.zoy_pg_bed_details bd ON bkd.selected_bed = bd.bed_id  "
					+ "JOIN pgowners.zoy_pg_property_details pd ON bkd.property_id = pd.property_id  "
					+ "JOIN pgowners.zoy_pg_owner_details o ON pd.pg_owner_id = o.pg_owner_id  "
					+ "join pgowners.zoy_pg_owner_settlement_status zposs on zposs.property_id =pd.property_id  "
					+ "join pgowners.zoy_pg_owner_settlement_split_up zpossu on zpossu.pg_owner_settlement_id =zposs.pg_owner_settlement_id  "
					+ "and zpossu.payment_id =up.user_payment_id "
					+ "join pgusers.user_payment_due upd on upd.user_payment_id =up.user_payment_id  "
					+ "join pgusers.user_dues ud2 on ud2.user_money_due_id =upd.user_money_due_id  "
					+ "join pgowners.zoy_pg_due_type_master zpdtm on zpdtm.due_id=ud2.user_money_due_type   "
					+ "join pgowners.zoy_pg_due_factor_master zpdfm on zpdfm.factor_id =ud2.user_money_due_billing_type "
					+ "join pgowners.zoy_pg_due_master zpdm on zpdm.due_type_id =zpdtm.due_type  "
					+ "WHERE 1=1 ");


			Map<String, Object> parameters = new HashMap<>();

			if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append("AND up.user_payment_timestamp BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP)");
				parameters.put("fromDate", filterRequest.getFromDate());
				parameters.put("toDate", filterRequest.getToDate());
			}

			if (filterData.getOwnerName() != null && !filterData.getOwnerName().isEmpty()) {
				queryBuilder.append("AND LOWER(o.pg_owner_name) LIKE LOWER(:ownerName)");
				parameters.put("ownerName", "%" + filterData.getOwnerName() + "%");
			}

			if (filterData.getOwnerEmail() != null && !filterData.getOwnerEmail().isEmpty()) {
				queryBuilder.append("AND LOWER(o.pg_owner_email) LIKE LOWER(:ownerEmail)");
				parameters.put("ownerEmail", "%" + filterData.getOwnerEmail() + "%");
			}
			if (filterData.getPgName() != null && !filterData.getPgName().isEmpty()) {
				queryBuilder.append("AND LOWER(pd.property_name) LIKE LOWER(:pgName)");
				parameters.put("pgName", "%" + filterData.getPgName() + "%");
			}

			if (filterData.getTransactionStatus() != null && !filterData.getTransactionStatus().isEmpty()) {
				queryBuilder.append("AND up.user_payment_payment_status = :paymentStatus");
				parameters.put("paymentStatus", filterData.getTransactionStatus());
			}

			if (filterRequest.getCityLocation() != null && !filterRequest.getCityLocation().isEmpty()) {
				queryBuilder.append("AND LOWER(pd.property_city) LIKE LOWER('%' || :cityLocation || '%')");
				parameters.put("cityLocation", filterRequest.getCityLocation());
			}

			if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty() && filterRequest.getSortActive() != null) {
				String sort = "";

				if ("ownerName".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "o.pg_owner_name";
				} else if ("pgName".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "pd.property_name";
				} else if ("totalAmountFromTenants".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "SUM(up.user_payment_payable_amount)";
				} else if ("transactionDate".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_timestamp";
				} else if ("transactionNumber".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_result_invoice_id";
				} else if ("paymentStatus".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_payment_status";
				} else if ("ownerEmail".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "o.pg_owner_email";
				}
				else {
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
			if(applyPagination) {
				query.setFirstResult(filterRequest.getPageIndex() * filterRequest.getPageSize());
				query.setMaxResults(filterRequest.getPageSize());
			}
			List<Object[]> results = query.getResultList();
			List<VendorPayments> vendorPaymentsDto = results.stream().map(row -> {
				VendorPayments dto = new VendorPayments();
				dto.setOwnerName(row[0] != null ? (String) row[0] : "");
				dto.setPgName(row[1] != null ? (String) row[1] : "");
				dto.setTotalAmountFromTenants((row[2] != null) ? ((Number) row[2]).doubleValue() : 0.0);
				dto.setTransactionDate((Timestamp) row[3]);
				dto.setTransactionNumber(row[4] != null ? (String) row[4] : "");
				dto.setPaymentStatus(row[5] != null ? (String) row[5] : "");
				dto.setPgAddress(row[7] != null ? (String) row[7] : "");
				dto.setOwnerEmail(row[8] != null ? (String) row[8] : "");
				dto.setAmountPaidToOwner(row[10] != null ? new BigDecimal(row[10].toString()).doubleValue():BigDecimal.valueOf(0).doubleValue());
				dto.setZoyShare(row[9] != null ? new BigDecimal(row[9].toString()).doubleValue():BigDecimal.valueOf(0).doubleValue());
				dto.setOwnerApprovalStatus(row[11] != null ? (String) row[11] : "");
				return dto;
			}).collect(Collectors.toList());


			return new CommonResponseDTO<>(vendorPaymentsDto, filterCount);
		}catch (Exception e) {
			new ZoyAdminApplicationException(e, "");
		}
		return null;
	}


	@Override
	public CommonResponseDTO<VendorPaymentsDues> getVendorPaymentDuesDetails(Timestamp fromDate, Timestamp toDate) throws WebServiceException{
		try{
			List<VendorPaymentsDues> vendorPaymentsDues = new ArrayList<>();
			VendorPaymentsDues vendorPayDues = new VendorPaymentsDues();
			vendorPayDues.setOwnerId(" ");
			vendorPayDues.setOwnerName(" ");
			vendorPayDues.setPendingAmount(BigDecimal.valueOf(0).doubleValue());
			vendorPayDues.setPendingDueDate(null);
			vendorPayDues.setPgId(" ");
			vendorPayDues.setPgName(" ");
			vendorPayDues.setTotalAmountPaid(BigDecimal.valueOf(0).doubleValue());
			vendorPayDues.setTotalAmountPayable(BigDecimal.valueOf(0).doubleValue());
			vendorPaymentsDues.add(vendorPayDues);
			int resultCount = vendorPaymentsDues.size();
			return new CommonResponseDTO<>(vendorPaymentsDues, resultCount);
		}catch (Exception e) {
			new ZoyAdminApplicationException(e, "");
		}
		return null;
	}

	@Override
	public CommonResponseDTO<VendorPaymentsGst> getVendorPaymentGstDetails(Timestamp fromDate, Timestamp toDate) throws WebServiceException{
		try {
			List<VendorPaymentsGst> vendorPaymentsGst = new ArrayList<>();
			VendorPaymentsGst vendorPaysGst = new VendorPaymentsGst();
			vendorPaysGst.setTransactionDate(null);
			vendorPaysGst.setTransactionNo(" ");
			vendorPaysGst.setPgId(" ");
			vendorPaysGst.setPgName(" ");
			vendorPaysGst.setTotalAmount(BigDecimal.valueOf(0).doubleValue());
			vendorPaysGst.setGstAmount(BigDecimal.valueOf(0).doubleValue());
			vendorPaysGst.setBasicAmount(BigDecimal.valueOf(0).doubleValue());
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
	public byte[] generateDynamicReport(UserPaymentFilterRequest filterRequest, FilterData filterData,Boolean applyPagination) throws WebServiceException {
		try {
			Map<String, Object> data = new HashMap<>();
			CommonResponseDTO<?> reportData = null;
			List<Map<String, Object>> dataListWrapper=null;
			String templatePath ="";
			switch (filterRequest.getReportType()) {
			case "userTransactionReport":
				boolean isGstReport=false;
				reportData = getUserPaymentDetails(filterRequest, filterData,applyPagination,isGstReport);
				dataListWrapper=this.generateUserTransactionDataList(reportData,filterRequest);
				templatePath = "templates/userTransactionReport.docx";
				break;
			case "userPaymentGstReport":
				boolean isGst=true;
				reportData = getUserPaymentDetails(filterRequest, filterData,applyPagination,isGst);
				dataListWrapper=this.generateUserPaymentGstReport(reportData,filterRequest);
				templatePath ="templates/userPaymentGstReport.docx";
				break;
			case "consolidatedFinanceReport":
				reportData = getConsolidatedFinanceDetails(filterRequest, filterData,applyPagination);
				dataListWrapper=this.generateConsolidatedFinanceReport(reportData,filterRequest);
				templatePath ="templates/consolidatedFinanceReport.docx";
				break;
			case "tenantDuesReport":
				reportData = getTenentDuesDetails(filterRequest, filterData,applyPagination);
				dataListWrapper=this.generateTenantDuesReport(reportData,filterRequest);
				templatePath ="templates/tenantDuesReport.docx";
				break;
			case "vendorPaymentsReport":
				reportData = getVendorPaymentDetails(filterRequest, filterData,applyPagination);
				dataListWrapper=this.generateVendorPaymentsReport(reportData,filterRequest);
				templatePath = "templates/vendorPaymentsReport.docx";
				break;
			case "vendorPaymentsDuesReport":
				reportData = getVendorPaymentDuesDetails(filterRequest.getFromDate(), filterRequest.getToDate());
				break;
			case "tenantRefundReport":
				reportData = getTenantRefunds(filterRequest, filterData,applyPagination);
				dataListWrapper=this.generateTenantRefundReport(reportData,filterRequest);
				templatePath = "templates/tenantRefundReport.docx";
				break;
			case "UpcomingTenantsReport":
				reportData = getUpcomingTenantsReport(filterRequest, filterData,applyPagination);
				dataListWrapper=this.generateUpcomingTenantsReport(reportData,filterRequest);
				templatePath ="templates/upcomingTenantsReport.docx";
				break;
			case "ActiveTenantsReport":
				reportData = getActiveTenantsReport(filterRequest, filterData,applyPagination);
				dataListWrapper=this.generateActiveTenantsReport(reportData,filterRequest);
				templatePath = "templates/activeTenantsReport.docx";
				break;
			case "InactiveTenantsReport":
				reportData = getInActiveTenantsReport(filterRequest, filterData,applyPagination);
				dataListWrapper=this.generateInactiveTenantsReport(reportData,filterRequest);
				templatePath = "templates/inactiveTenantsReport.docx";
				break;
			case "SuspendedTenantsReport":
				reportData = getSuspendedTenantsReport(filterRequest, filterData,applyPagination);
				dataListWrapper=this.generateSuspendedTenantsReport(reportData,filterRequest);
				templatePath = "templates/suspendedTenantsReport.docx";
				break;
			case "reviewsAndRatingReport":
				reportData = getRatingsAndReviewsDetails(filterRequest, filterData,applyPagination);
				dataListWrapper=this.generaterReviewsAndRatingReport(reportData,filterRequest);
				templatePath ="templates/reviewsAndRatingReport.docx";
				break;
			case "InactivePropertiesReport":
				reportData = getInActivePropertyReport(filterRequest, filterData,applyPagination);
				dataListWrapper=this.generateInactivePropertiesReport(reportData,filterRequest);
				templatePath ="templates/inActivePropertiesReport.docx";
				break;
			case "RegesterTenantsReport":
				reportData = getRegisterTenantsReport(filterRequest,applyPagination);
				dataListWrapper=this.generateRegisterTenantReport(reportData,filterRequest);
				templatePath ="templates/regesterTenantsReport.docx";
				break;
			case "FailedTransactionReport":
				reportData = getfailureTransactionReport(filterRequest,filterData,applyPagination);
				dataListWrapper=this.generateFailureTransactionReport(reportData,filterRequest);
				templatePath ="templates/failureTransactionReport.docx";
				break;
			case "PotentialPropertyReport":
				reportData = getpotentialPropertyReport(filterRequest,filterData,applyPagination);
				dataListWrapper=this.generatePotentialPropertyReport(reportData,filterRequest);
				templatePath ="templates/potentialPropertyReport.docx";
				break;
			case "UpComingPotentialPropertyReport":
				reportData = getUpcomingPotentialPropertyReport(filterRequest,filterData,applyPagination);
				dataListWrapper=this.generateUpComingPotentialPropertyReport(reportData,filterRequest);
				templatePath ="templates/upComingPotentialPropertyReport.docx";
				break;
			case "NonPotentialPropertyReport":
				reportData = getNonPotentialPropertyReport(filterRequest,filterData,applyPagination);
				dataListWrapper=this.generateNonPotentialPropertyReport(reportData,filterRequest);
				templatePath ="templates/nonPotentialPropertyReport.docx";
				break;
			case "SuspendedPropertiesReport":
				reportData = getSuspendedPropertyReport(filterRequest, filterData,applyPagination);
				dataListWrapper=this.generateSuspendedPropertiesReport(reportData,filterRequest);
				templatePath ="templates/suspendedPropertiesReport.docx";
				break;	
			case "ZoyShareReport":
				reportData = getZoyShareReport(filterRequest, filterData,applyPagination);
				dataListWrapper=this.generateZoyShareReport(reportData,filterRequest);
				templatePath ="templates/zoyShareReport.docx";
				break;		
			case "RegisteredLeadDetails":
				boolean isSupportUser = false;
				reportData = getRegisterLeadDetails(filterRequest, filterData,applyPagination,isSupportUser);
				dataListWrapper=this.generateRegisterLeadDetailsReport(reportData,filterRequest);
				templatePath ="templates/registeredLeadDetails.docx";
				break;	
			default:
				throw new IllegalArgumentException("Invalid template name provided.");
			}
			List<?> dataList = reportData.getData();
			data.put("reportData", dataList);
			switch (filterRequest.getDownloadType().toLowerCase()) {
			case "pdf":

				return wordToPdfConverterService.generateWordDocument(templatePath,dataListWrapper);
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


	private List<Map<String, Object>> generateRegisterLeadDetailsReport(CommonResponseDTO<?> reportData,
			UserPaymentFilterRequest filterRequest) {
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<?> dataItems = reportData.getData();
		String currentDate = LocalDate.now().toString();

		for (Object item : dataItems) {
			Map<String, Object> data = new HashMap<>();
			RegisterLeadDetails leads = (RegisterLeadDetails) item;

			data.put("inquryNumber",leads.getInquiryNumber() );
			data.put("name", leads.getName());
			data.put("inquiredFor", leads.getInquiredFor());
			data.put("date", tuService.formatTimestamp(leads.getRegisteredDate().toInstant()));
			data.put("assignedTo", leads.getAsignedTo());
			data.put("status", leads.getStatus());
			data.put("printedOn", currentDate);
			dataList.add(data);
		}
		return dataList;
	}

	public List<Map<String, Object>> generateUserTransactionDataList(CommonResponseDTO<?> reportData, UserPaymentFilterRequest filterRequest) {
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<?> dataItems = reportData.getData();
		String currentDate = LocalDate.now().toString();

		for (Object item : dataItems) {
			Map<String, Object> data = new HashMap<>();
			UserPaymentDTO userPayment = (UserPaymentDTO) item;

			data.put("transactionDate", tuService.formatTimestamp(userPayment.getTransactionDate().toInstant()));
			data.put("transactionNo", userPayment.getTransactionNumber());
			data.put("transactionStatus", userPayment.getTransactionStatus());
			data.put("dueAmount", userPayment.getDueAmount());
			data.put("gstAmount", userPayment.getGstAmount());
			data.put("totalAmount", userPayment.getTotalAmount());
			data.put("userName", userPayment.getUserPersonalName());
			data.put("pgName", userPayment.getUserPgPropertyName());
			data.put("propertyId", userPayment.getPropertyId());
			data.put("bedNum", userPayment.getRoomBedNumber());
			data.put("category", userPayment.getCategory());
			data.put("modeOfPayment", userPayment.getPaymentMode());
			data.put("pgAddress", userPayment.getPropertyHouseArea());
			data.put("tenantMobile", userPayment.getTenantContactNum());

			Timestamp fromDateTimestamp = filterRequest.getFromDate();
			Timestamp toDateTimestamp = filterRequest.getToDate();

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			data.put("fromDate", fromDate.format(formatter));
			data.put("toDate", toDate.format(formatter));
			data.put("printedOn", currentDate);

			dataList.add(data);
		}
		return dataList;
	}
	public List<Map<String, Object>> generateUserPaymentGstReport(CommonResponseDTO<?> reportData, UserPaymentFilterRequest filterRequest) {
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<?> dataItems = reportData.getData();
		String currentDate = LocalDate.now().toString();

		for (Object item : dataItems) {
			Map<String, Object> data = new HashMap<>();
			UserPaymentDTO userPayment = (UserPaymentDTO) item;

			data.put("txnDate", tuService.formatTimestamp(userPayment.getTransactionDate().toInstant()));
			data.put("invoiceNo", userPayment.getTransactionNumber());
			data.put("tenantName", userPayment.getUserPersonalName());
			data.put("tenantMobile", userPayment.getTenantContactNum());
			data.put("pgName", userPayment.getUserPgPropertyName());
			data.put("totalAmount", userPayment.getTotalAmount());
			data.put("gstAmount", userPayment.getGstAmount());
			data.put("dueAmount", userPayment.getDueAmount());
			data.put("modeOfPayment", userPayment.getPaymentMode());

			Timestamp fromDateTimestamp = filterRequest.getFromDate();
			Timestamp toDateTimestamp = filterRequest.getToDate();

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			data.put("fromDate", fromDate.format(formatter));
			data.put("toDate", toDate.format(formatter));
			data.put("printedOn", currentDate);

			dataList.add(data);
		}
		return dataList;
	}
	public List<Map<String, Object>> generateConsolidatedFinanceReport(CommonResponseDTO<?> reportData, UserPaymentFilterRequest filterRequest) {
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<?> dataItems = reportData.getData();
		String currentDate = LocalDate.now().toString();

		for (Object item : dataItems) {
			Map<String, Object> data = new HashMap<>();
			ConsilidatedFinanceDetails financeDetails = (ConsilidatedFinanceDetails) item;

			data.put("txnDate", tuService.formatTimestamp(financeDetails.getUserPaymentTimestamp().toInstant()));
			data.put("invoiceNum", financeDetails.getUserPaymentBankTransactionId());
			data.put("payeePayerType", financeDetails.getPayerPayeeType());
			data.put("payeePayerName", financeDetails.getPayerPayeeName());
			data.put("debit", financeDetails.getDebitAmount());
			data.put("credit", financeDetails.getCreditAmount());
			data.put("pgName", financeDetails.getPgName());
			data.put("contactNum", financeDetails.getContactNum());

			Timestamp fromDateTimestamp = filterRequest.getFromDate();
			Timestamp toDateTimestamp = filterRequest.getToDate();

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			data.put("fromDate", fromDate.format(formatter));
			data.put("toDate", toDate.format(formatter));
			data.put("printedOn", currentDate);

			dataList.add(data);
		}
		return dataList;
	}
	public List<Map<String, Object>> generateTenantDuesReport(CommonResponseDTO<?> reportData, UserPaymentFilterRequest filterRequest) {
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<?> dataItems = reportData.getData();
		String currentDate = LocalDate.now().toString();

		for (Object item : dataItems) {
			Map<String, Object> data = new HashMap<>();
			TenentDues tenantDues = (TenentDues) item;

			data.put("tenantName", tenantDues.getUserPersonalName());
			data.put("tenantMobile", tenantDues.getTenantMobileNum());
			data.put("pgName", tenantDues.getUserPgPropertyName());
			data.put("pgAddress", tenantDues.getUserPgPropertyAddress());
			data.put("bedNumber", tenantDues.getBedNumber());
			data.put("pendingAmount", tenantDues.getPendingAmount());
			data.put("paymentDueDate", tenantDues.getPendingDueDate());

			Timestamp fromDateTimestamp = filterRequest.getFromDate();
			Timestamp toDateTimestamp = filterRequest.getToDate();

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			data.put("fromDate", fromDate.format(formatter));
			data.put("toDate", toDate.format(formatter));
			data.put("printedOn", currentDate);

			dataList.add(data);
		}
		return dataList;
	}
	public List<Map<String, Object>> generateVendorPaymentsReport(CommonResponseDTO<?> reportData, UserPaymentFilterRequest filterRequest) {
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<?> dataItems = reportData.getData();
		String currentDate = LocalDate.now().toString();

		for (Object item : dataItems) {
			Map<String, Object> data = new HashMap<>();
			VendorPayments vendorPayment = (VendorPayments) item;

			data.put("ownerName", vendorPayment.getOwnerName() != null ? vendorPayment.getOwnerName() : "");
			data.put("pgName", vendorPayment.getPgName() != null ? vendorPayment.getPgName() : "");
			data.put("ownerEmail", vendorPayment.getOwnerEmail() != null ? vendorPayment.getOwnerEmail() : "");
			data.put("pgAddress", vendorPayment.getPgAddress() != null ? vendorPayment.getPgAddress() : "");
			data.put("receivedFromTenant", vendorPayment.getTotalAmountFromTenants());
			data.put("paidToOwner", vendorPayment.getAmountPaidToOwner());
			data.put("zoyShare", vendorPayment.getZoyShare());
			data.put("txnDate", tuService.formatTimestamp(vendorPayment.getTransactionDate().toInstant()) != null ? tuService.formatTimestamp(vendorPayment.getTransactionDate().toInstant()) : "");
			data.put("invoiceNum", vendorPayment.getTransactionNumber() != null ? vendorPayment.getTransactionNumber() : "");
			data.put("paymentStatus", vendorPayment.getPaymentStatus() != null ? vendorPayment.getPaymentStatus() : "");
			data.put("approvalStatus", vendorPayment.getOwnerApprovalStatus() != null ? vendorPayment.getOwnerApprovalStatus() : "");

			Timestamp fromDateTimestamp = filterRequest.getFromDate();
			Timestamp toDateTimestamp = filterRequest.getToDate();

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			data.put("fromDate", fromDate.format(formatter));
			data.put("toDate", toDate.format(formatter));
			data.put("printedOn", currentDate);

			dataList.add(data);
		}
		return dataList;
	}
	public List<Map<String, Object>> generateTenantRefundReport(CommonResponseDTO<?> reportData, UserPaymentFilterRequest filterRequest) {
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<?> dataItems = reportData.getData();
		String currentDate = LocalDate.now().toString();

		for (Object item : dataItems) {
			Map<String, Object> data = new HashMap<>();
			TenentRefund tenantRefund = (TenentRefund) item;

			data.put("userName", tenantRefund.getCustomerName() != null ? tenantRefund.getCustomerName() : "");
			data.put("tenantMobile", tenantRefund.getTenantMobileNum() != null ? tenantRefund.getTenantMobileNum() : "");
			data.put("pgName", tenantRefund.getPgPropertyName() != null ? tenantRefund.getPgPropertyName() : "");
			data.put("pgAddress", tenantRefund.getUserPgPropertyAddress() != null ? tenantRefund.getUserPgPropertyAddress() : "");
			data.put("bookingId", tenantRefund.getBookingId() != null ? tenantRefund.getBookingId() : "");
			data.put("refundTitle", tenantRefund.getRefundTitle() != null ? tenantRefund.getRefundTitle() : "");
			data.put("Amount", tenantRefund.getRefundableAmount());
			data.put("amountPaid", tenantRefund.getAmountPaid());
			data.put("paymentDate", tuService.formatTimestamp(tenantRefund.getPaymentDate().toInstant()) != null ? tuService.formatTimestamp(tenantRefund.getPaymentDate().toInstant()) : "");
			data.put("invoiceNo", tenantRefund.getTransactionNumber() != null ? tenantRefund.getTransactionNumber() : "");
			data.put("status", tenantRefund.getPaymentStatus() != null ? tenantRefund.getPaymentStatus() : "");
			data.put("accNum", tenantRefund.getTenantAccountNumber() != null ? tenantRefund.getTenantAccountNumber() : "");
			data.put("ifscCode", tenantRefund.getTenantIfscCode() != null ? tenantRefund.getTenantIfscCode() : "");
			
			// Common fields
			Timestamp fromDateTimestamp = filterRequest.getFromDate();
			Timestamp toDateTimestamp = filterRequest.getToDate();

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			data.put("fromDate", fromDate.format(formatter));
			data.put("toDate", toDate.format(formatter));
			data.put("printedOn", currentDate);

			dataList.add(data);
		}
		return dataList;
	}
	public List<Map<String, Object>> generaterReviewsAndRatingReport(CommonResponseDTO<?> reportData, UserPaymentFilterRequest filterRequest) {
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<?> dataItems = reportData.getData();
		String currentDate = LocalDate.now().toString();

		for (Object item : dataItems) {
			Map<String, Object> data = new HashMap<>();
			RatingsAndReviewsReport review = (RatingsAndReviewsReport) item;

			data.put("reviewDate", tuService.formatTimestamp(review.getReviewDate().toInstant()) != null ? tuService.formatTimestamp(review.getReviewDate().toInstant()) : "");
			data.put("tenantName", review.getCustomerName() != null ? review.getCustomerName() : "");
			data.put("pgName", review.getPropertyName() != null ? review.getPropertyName() : "");
			data.put("tenantContact", review.getCustomerMobileNo() != null ? review.getCustomerMobileNo() : "");
			data.put("cleaniliness", review.getCleanliness() != null ? review.getCleanliness() : "");
			data.put("accomodation", review.getAccommodation() != null ? review.getAccommodation() : "");
			data.put("aminities", review.getAmenities() != null ? review.getAmenities() : "");
			data.put("maintenance", review.getMaintenance() != null ? review.getMaintenance() : "");
			data.put("valueForMoney", review.getValueForMoney() != null ? review.getValueForMoney() : "");
			data.put("overallRating", review.getOverallRating() != null ? review.getOverallRating() : "");
			Timestamp fromDateTimestamp = filterRequest.getFromDate();
			Timestamp toDateTimestamp = filterRequest.getToDate();
			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			data.put("fromDate", fromDate.format(formatter));
			data.put("toDate", toDate.format(formatter));
			data.put("printedOn", currentDate);

			dataList.add(data);
		}
		return dataList;
	}
	public List<Map<String, Object>> generateActiveTenantsReport(CommonResponseDTO<?> reportData, UserPaymentFilterRequest filterRequest) {
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<?> dataItems = reportData.getData();
		String currentDate = LocalDate.now().toString();

		for (Object item : dataItems) {
			Map<String, Object> data = new HashMap<>();
			TenantResportsDTO tenantReport = (TenantResportsDTO) item;

			data.put("tenantFullName", tenantReport.getTenantName() != null ? tenantReport.getTenantName() : "");
			data.put("tenantContact", tenantReport.getTenantContactNumber() != null ? tenantReport.getTenantContactNumber() : "");
			data.put("tenantEmail", tenantReport.getTenantEmailAddress() != null ? tenantReport.getTenantEmailAddress() : "");
			data.put("propertyName", tenantReport.getCurrentPropertName() != null ? tenantReport.getCurrentPropertName() : "");
			data.put("propertyAddress", tenantReport.getPropertAddress() != null ? tenantReport.getPropertAddress() : "");
			data.put("roomNumber", tenantReport.getBedNumber() != null ? tenantReport.getBedNumber() : "");
			data.put("checkInDate", tuService.formatTimestamp(tenantReport.getCheckInDate().toInstant()) != null ? tuService.formatTimestamp(tenantReport.getCheckInDate().toInstant()) : "");
			data.put("checkOutDate", tuService.formatTimestamp(tenantReport.getExpectedCheckOutdate().toInstant()) != null ? tuService.formatTimestamp(tenantReport.getExpectedCheckOutdate().toInstant()) : "");

			// Common fields
			Timestamp fromDateTimestamp = filterRequest.getFromDate();
			Timestamp toDateTimestamp = filterRequest.getToDate();

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			data.put("fromDate", fromDate.format(formatter));
			data.put("toDate", toDate.format(formatter));
			data.put("printedOn", currentDate);

			dataList.add(data);
		}
		return dataList;
	}
	public List<Map<String, Object>> generateInactiveTenantsReport(CommonResponseDTO<?> reportData, UserPaymentFilterRequest filterRequest) {
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<?> dataItems = reportData.getData();
		String currentDate = LocalDate.now().toString();

		for (Object item : dataItems) {
			Map<String, Object> data = new HashMap<>();
			TenantResportsDTO tenantReport = (TenantResportsDTO) item;
			data.put("tenantName", tenantReport.getTenantName() != null ? tenantReport.getTenantName() : "");
			data.put("tenantContact", tenantReport.getTenantContactNumber() != null ? tenantReport.getTenantContactNumber() : "");
			data.put("tenantEmail", tenantReport.getTenantEmailAddress() != null ? tenantReport.getTenantEmailAddress() : "");
			data.put("previousPropert", tenantReport.getPreviousPropertName() != null ? tenantReport.getPreviousPropertName() : "");
			data.put("propertyAddress", tenantReport.getPropertAddress() != null ? tenantReport.getPropertAddress() : "");
			data.put("roomNumber", tenantReport.getBedNumber() != null ? tenantReport.getBedNumber() : "");
			data.put("checkindate", tuService.formatTimestamp(tenantReport.getCheckInDate().toInstant()) != null ? tuService.formatTimestamp(tenantReport.getCheckInDate().toInstant()) : "");
			data.put("checkedOutDate", tuService.formatTimestamp(tenantReport.getCheckedOutDate().toInstant()) != null ? tuService.formatTimestamp(tenantReport.getCheckedOutDate().toInstant()) : "");

			// Common fields
			Timestamp toDateTimestamp = filterRequest.getToDate();

			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			data.put("toDate", toDate.format(formatter));
			data.put("printedOn", currentDate);

			dataList.add(data);
		}
		return dataList;
	}


	public List<Map<String, Object>> generateInactivePropertiesReport(CommonResponseDTO<?> reportData, UserPaymentFilterRequest filterRequest) {
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<?> dataItems = reportData.getData();
		String currentDate = LocalDate.now().toString();

		for (Object item : dataItems) {
			Map<String, Object> data = new HashMap<>();
			PropertyResportsDTO inActivePropertyReport = (PropertyResportsDTO) item;

			data.put("ownerFullName", inActivePropertyReport.getOwnerFullName() != null ? inActivePropertyReport.getOwnerFullName() : "");
			data.put("propertyName", inActivePropertyReport.getPropertyName() != null ? inActivePropertyReport.getPropertyName() : "");
			data.put("propertyContact", inActivePropertyReport.getPropertyContactNumber() != null ? inActivePropertyReport.getPropertyContactNumber() : "");
			data.put("propertyEmail", inActivePropertyReport.getPropertyEmailAddress() != null ? inActivePropertyReport.getPropertyEmailAddress() : "");
			data.put("address", inActivePropertyReport.getPropertyAddress() != null ? inActivePropertyReport.getPropertyAddress() : "");

			// Common fields
			Timestamp fromDateTimestamp = filterRequest.getFromDate();
			Timestamp toDateTimestamp = filterRequest.getToDate();

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			data.put("fromDate", fromDate.format(formatter));
			data.put("toDate", toDate.format(formatter));
			data.put("printedOn", currentDate);

			dataList.add(data);
		}
		return dataList;
	}
	
	public List<Map<String, Object>> generateRegisterTenantReport(CommonResponseDTO<?> reportData, UserPaymentFilterRequest filterRequest) {
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<?> dataItems = reportData.getData();
		String currentDate = LocalDate.now().toString();

		for (Object item : dataItems) {
			Map<String, Object> data = new HashMap<>();
			RegisterTenantsDTO registerTenantsReport = (RegisterTenantsDTO) item;

			data.put("tenantName", registerTenantsReport.getTenantName() != null ? registerTenantsReport.getTenantName() : "");
			data.put("tenantContactNumber", registerTenantsReport.getTenantContactNumber() != null ? registerTenantsReport.getTenantContactNumber() : "");
			data.put("tenantEmailAddress", registerTenantsReport.getTenantEmailAddress() != null ? registerTenantsReport.getTenantEmailAddress() : "");
			data.put("registrationDate", tuService.formatTimestamp(registerTenantsReport.getRegistrationDate().toInstant()) != null ? tuService.formatTimestamp(registerTenantsReport.getRegistrationDate().toInstant()) : "");

			// Common fields
			Timestamp fromDateTimestamp = filterRequest.getFromDate();
			Timestamp toDateTimestamp = filterRequest.getToDate();

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			data.put("fromDate", fromDate.format(formatter));
			data.put("toDate", toDate.format(formatter));
			data.put("printedOn", currentDate);

			dataList.add(data);
		}
		return dataList;
	}
	
	public List<Map<String, Object>> generatePotentialPropertyReport(CommonResponseDTO<?> reportData, UserPaymentFilterRequest filterRequest) {
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<?> dataItems = reportData.getData();
		String currentDate = LocalDate.now().toString();

		for (Object item : dataItems) {
			Map<String, Object> data = new HashMap<>();
			PropertyResportsDTO potentialPropertyData = (PropertyResportsDTO) item;
			
			data.put("ownerName", potentialPropertyData.getOwnerFullName() != null ? potentialPropertyData.getOwnerFullName() : "");
			data.put("propertyName", potentialPropertyData.getPropertyName() != null ? potentialPropertyData.getPropertyName() : "");
			data.put("contactNumber", potentialPropertyData.getPropertyContactNumber() != null ? potentialPropertyData.getPropertyContactNumber() : "");
			data.put("email", potentialPropertyData.getPropertyEmailAddress() != null ? potentialPropertyData.getPropertyEmailAddress() : "");
			data.put("address", potentialPropertyData.getPropertyAddress() != null ? potentialPropertyData.getPropertyAddress() : "");
			data.put("occupiedBeds", potentialPropertyData.getNumberOfBeds());
			data.put("rentPerMonth", potentialPropertyData.getExpectedRentPerMonth());
			data.put("zoyPer", potentialPropertyData.getZoyShare() != null ? potentialPropertyData.getZoyShare() : 0);
			data.put("zoyAmt", potentialPropertyData.getZoyShareAmount() != null ? potentialPropertyData.getZoyShareAmount() : 0.0);

			// Common fields
			Timestamp fromDateTimestamp = filterRequest.getFromDate();
			Timestamp toDateTimestamp = filterRequest.getToDate();

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			data.put("fromDate", fromDate.format(formatter));
			data.put("toDate", toDate.format(formatter));
			data.put("printedOn", currentDate);

			dataList.add(data);
		}
		return dataList;
	}
	
	public List<Map<String, Object>> generateNonPotentialPropertyReport(CommonResponseDTO<?> reportData, UserPaymentFilterRequest filterRequest) {
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<?> dataItems = reportData.getData();
		String currentDate = LocalDate.now().toString();

		for (Object item : dataItems) {
			Map<String, Object> data = new HashMap<>();
			PropertyResportsDTO potentialPropertyData = (PropertyResportsDTO) item;
			
			data.put("ownerName", potentialPropertyData.getOwnerFullName() != null ? potentialPropertyData.getOwnerFullName() : "");
			data.put("propertyName", potentialPropertyData.getPropertyName() != null ? potentialPropertyData.getPropertyName() : "");
			data.put("contactNumber", potentialPropertyData.getPropertyContactNumber() != null ? potentialPropertyData.getPropertyContactNumber() : "");
			data.put("email", potentialPropertyData.getPropertyEmailAddress() != null ? potentialPropertyData.getPropertyEmailAddress() : "");
			data.put("address", potentialPropertyData.getPropertyAddress()!= null ? potentialPropertyData.getPropertyAddress() : "");
			data.put("lastOutDate", potentialPropertyData.getLastCheckOutDate() != null ? tuService.formatTimestamp(potentialPropertyData.getLastCheckOutDate().toInstant()) : "-");
            data.put("startDate", potentialPropertyData.getLastCheckInDate() != null ? tuService.formatTimestamp(potentialPropertyData.getLastCheckInDate().toInstant()) : "-");
			// Common fields
			Timestamp fromDateTimestamp = filterRequest.getFromDate();
			Timestamp toDateTimestamp = filterRequest.getToDate();

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			data.put("fromDate", fromDate.format(formatter));
			data.put("toDate", toDate.format(formatter));
			data.put("printedOn", currentDate);

			dataList.add(data);
		}
		return dataList;
	}
	public List<Map<String, Object>> generateUpComingPotentialPropertyReport(CommonResponseDTO<?> reportData, UserPaymentFilterRequest filterRequest) {
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<?> dataItems = reportData.getData();
		String currentDate = LocalDate.now().toString();
	    LocalDate toDate = LocalDate.now();
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		for (Object item : dataItems) {
			Map<String, Object> data = new HashMap<>();
			UpcomingPotentialPropertyDTO potentialPropertyData = (UpcomingPotentialPropertyDTO) item;
			
			data.put("ownerName", potentialPropertyData.getOwnerFullName() != null ? potentialPropertyData.getOwnerFullName() : "");
			data.put("propertyName", potentialPropertyData.getPropertyName() != null ? potentialPropertyData.getPropertyName() : "");
			data.put("contactNumber", potentialPropertyData.getOwnerContactNumber() != null ? potentialPropertyData.getOwnerContactNumber() : "");
			data.put("email", potentialPropertyData.getOwnerEmailAddress() != null ? potentialPropertyData.getOwnerEmailAddress() : "");
			data.put("address", potentialPropertyData.getPropertyAddress()!= null ? potentialPropertyData.getPropertyAddress() : "");
			data.put("toDate", toDate.format(formatter));
			data.put("printedOn", currentDate);

			dataList.add(data);
		}
		return dataList;
	}
	public List<Map<String, Object>> generateFailureTransactionReport(CommonResponseDTO<?> reportData, UserPaymentFilterRequest filterRequest) {
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<?> dataItems = reportData.getData();
		String currentDate = LocalDate.now().toString();

		for (Object item : dataItems) {
			Map<String, Object> data = new HashMap<>();
			UserPaymentDTO failedTransactioData = (UserPaymentDTO) item;

			data.put("transactionDate", tuService.formatTimestamp(failedTransactioData.getTransactionDate().toInstant()) != null ? tuService.formatTimestamp(failedTransactioData.getTransactionDate().toInstant()) : "");
			data.put("tenantName", failedTransactioData.getUserPersonalName() != null ? failedTransactioData.getUserPersonalName() : "");
			data.put("contactNumber", failedTransactioData.getTenantContactNum() != null ? failedTransactioData.getTenantContactNum() : "");
			data.put("email", failedTransactioData.getEmail() != null ? failedTransactioData.getEmail() : "");
			data.put("amount", failedTransactioData.getTotalAmount());
			data.put("reason", failedTransactioData.getFailedReason() != null ? failedTransactioData.getFailedReason() : "");
			// Common fields
			Timestamp fromDateTimestamp = filterRequest.getFromDate();
			Timestamp toDateTimestamp = filterRequest.getToDate();

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			data.put("fromDate", fromDate.format(formatter));
			data.put("toDate", toDate.format(formatter));
			data.put("printedOn", currentDate);

			dataList.add(data);
		}
		return dataList;
	}


	public List<Map<String, Object>> generateSuspendedPropertiesReport(CommonResponseDTO<?> reportData, UserPaymentFilterRequest filterRequest) {
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<?> dataItems = reportData.getData();
		String currentDate = LocalDate.now().toString();

		for (Object item : dataItems) {
			Map<String, Object> data = new HashMap<>();
			PropertyResportsDTO inActivePropertyReport = (PropertyResportsDTO) item;

			data.put("ownerFullName", inActivePropertyReport.getOwnerFullName() != null ? inActivePropertyReport.getOwnerFullName() : "");
			data.put("propertyName", inActivePropertyReport.getPropertyName() != null ? inActivePropertyReport.getPropertyName() : "");
			data.put("propertyContact", inActivePropertyReport.getPropertyContactNumber() != null ? inActivePropertyReport.getPropertyContactNumber() : "");
			data.put("propertyEmail", inActivePropertyReport.getPropertyEmailAddress() != null ? inActivePropertyReport.getPropertyEmailAddress() : "");
			data.put("address", inActivePropertyReport.getPropertyAddress() != null ? inActivePropertyReport.getPropertyAddress() : "");
			data.put("suspendedDate", tuService.formatTimestamp(inActivePropertyReport.getSuspendedDate().toInstant()) != null ? tuService.formatTimestamp(inActivePropertyReport.getSuspendedDate().toInstant()) : "");
			data.put("reason", inActivePropertyReport.getReasonForSuspension() != null ? inActivePropertyReport.getReasonForSuspension() : "");
			// Common fields
			Timestamp fromDateTimestamp = filterRequest.getFromDate();
			Timestamp toDateTimestamp = filterRequest.getToDate();

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			data.put("fromDate", fromDate.format(formatter));
			data.put("toDate", toDate.format(formatter));
			data.put("printedOn", currentDate);

			dataList.add(data);
		}
		return dataList;
	}
	public List<Map<String, Object>> generateSuspendedTenantsReport(CommonResponseDTO<?> reportData, UserPaymentFilterRequest filterRequest) {
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<?> dataItems = reportData.getData();
		String currentDate = LocalDate.now().toString();

		for (Object item : dataItems) {
			Map<String, Object> data = new HashMap<>();
			TenantResportsDTO tenantReport = (TenantResportsDTO) item;

			data.put("tenantName", tenantReport.getTenantName() != null ? tenantReport.getTenantName() : "");
			data.put("tenantContact", tenantReport.getTenantContactNumber() != null ? tenantReport.getTenantContactNumber() : "");
			data.put("tenantEmail", tenantReport.getTenantEmailAddress() != null ? tenantReport.getTenantEmailAddress() : "");
			data.put("previousPropert", tenantReport.getPreviousPropertName() != null ? tenantReport.getPreviousPropertName() : "");
			data.put("roomNumber", tenantReport.getBedNumber() != null ? tenantReport.getBedNumber() : "");
			data.put("checkedOutDate", tuService.formatTimestamp(tenantReport.getCheckedOutDate().toInstant()) != null ? tuService.formatTimestamp(tenantReport.getCheckedOutDate().toInstant()) : "");
			data.put("suspendedDate", tuService.formatTimestamp(tenantReport.getSuspendedDate().toInstant()) != null ? tuService.formatTimestamp(tenantReport.getSuspendedDate().toInstant()) : "");
			data.put("reason", tenantReport.getReasonForSuspension() != null ? tenantReport.getReasonForSuspension() : "");

			// Common fields
			Timestamp fromDateTimestamp = filterRequest.getFromDate();
			Timestamp toDateTimestamp = filterRequest.getToDate();

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			data.put("fromDate", fromDate.format(formatter));
			data.put("toDate", toDate.format(formatter));
			data.put("printedOn", currentDate);

			dataList.add(data);
		}
		return dataList;
	}
	public String[] getDistinctCities() {
		return propertyDetailsRepository.findDistinctCities();
	}

	public List<Map<String, Object>> generateUpcomingTenantsReport(CommonResponseDTO<?> reportData, UserPaymentFilterRequest filterRequest) {
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<?> dataItems = reportData.getData();
		String currentDate = LocalDate.now().toString();

		for (Object item : dataItems) {
			Map<String, Object> data = new HashMap<>();
			TenantResportsDTO tenantRefund = (TenantResportsDTO) item;

			data.put("tenantName", tenantRefund.getTenantName() != null ? tenantRefund.getTenantName() : "");
			data.put("contactNum", tenantRefund.getTenantContactNumber() != null ? tenantRefund.getTenantContactNumber() : "");
			data.put("tenantEmail", tenantRefund.getTenantEmailAddress() != null ? tenantRefund.getTenantEmailAddress() : "");
			data.put("propertyName", tenantRefund.getBookedProperyName() != null ? tenantRefund.getBookedProperyName() : "");
			data.put("propertyAddress", tenantRefund.getPropertAddress() != null ? tenantRefund.getPropertAddress() : "");
			data.put("bedAllocation", tenantRefund.getBedNumber() != null ? tenantRefund.getBedNumber() : "");
			data.put("expectedCheckin", tuService.formatTimestamp(tenantRefund.getExpectedCheckIndate().toInstant()) != null ? tuService.formatTimestamp(tenantRefund.getExpectedCheckIndate().toInstant()) : "");
			data.put("expectedCheckOut", tuService.formatTimestamp(tenantRefund.getExpectedCheckOutdate().toInstant()) != null ? tuService.formatTimestamp(tenantRefund.getExpectedCheckOutdate().toInstant()) : "");
			data.put("printedOn", currentDate);

			dataList.add(data);
		}
		return dataList;
	}
	public List<Map<String, Object>> generateZoyShareReport(CommonResponseDTO<?> reportData, UserPaymentFilterRequest filterRequest) {
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<?> dataItems = reportData.getData();
		String currentDate = LocalDate.now().toString();

		for (Object item : dataItems) {
			Map<String, Object> data = new HashMap<>();
			ZoyShareReportDTO zoyShareReport = (ZoyShareReportDTO) item;

			data.put("transactionDate", tuService.formatTimestamp(zoyShareReport.getTransactionDate().toInstant()) != null ? tuService.formatTimestamp(zoyShareReport.getTransactionDate().toInstant()) : "");
			data.put("invoiceNumber", zoyShareReport.getInvoiceNumber() != null ? zoyShareReport.getInvoiceNumber() : "");
			data.put("pgName", zoyShareReport.getPgName() != null ? zoyShareReport.getPgName() : "");
			data.put("tenantName", zoyShareReport.getTenantName() != null ? zoyShareReport.getTenantName() : "");
			data.put("sharingType", zoyShareReport.getSharingType() != null ? zoyShareReport.getSharingType() : "");
			data.put("bedNumber", zoyShareReport.getBedNumber() != null ? zoyShareReport.getBedNumber() : "");
			data.put("paymentMode", zoyShareReport.getPaymentMode() != null ? zoyShareReport.getPaymentMode() : "");
			data.put("amountPaid", zoyShareReport.getAmountPaid() != null ? zoyShareReport.getAmountPaid() : "");
			data.put("amountType", zoyShareReport.getAmountType() != null ? zoyShareReport.getAmountType() : "");
			data.put("zoyShare", zoyShareReport.getZoyShare() != null ? zoyShareReport.getZoyShare() : "");
			data.put("zoyShareAmount", zoyShareReport.getZoyShareAmount() != null ? zoyShareReport.getZoyShareAmount() : "");

			// Common fields
			Timestamp fromDateTimestamp = filterRequest.getFromDate();
			Timestamp toDateTimestamp = filterRequest.getToDate();

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.of(TIME_ZONE)).toLocalDate();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			data.put("fromDate", fromDate.format(formatter));
			data.put("toDate", toDate.format(formatter));
			data.put("printedOn", currentDate);

			dataList.add(data);
		}
		return dataList;
	}
	public CommonResponseDTO<TenentRefund> getTenantRefunds(UserPaymentFilterRequest filterRequest,
			FilterData filterData, Boolean applyPagination) throws WebServiceException {
		try {
			StringBuilder queryBuilder = new StringBuilder("SELECT \r\n"
					+ "    um.user_first_name || ' ' || um.user_last_name AS username,\r\n"
					+ "    um.user_mobile AS usermobile_number,\r\n"
					+ "    zppd.property_name AS PG_name,\r\n"
					+ "    zppd.property_house_area AS Pg_address,\r\n"
					+ "    urd.booking_id AS booking_ID,\r\n"
					+ "    ub.user_cancellation_reason AS refund_title,\r\n"
					+ "    urd.refund_amount AS refund_amount,\r\n"
					+ "    CASE\r\n"
					+ "        WHEN urd.refund_process_status = 'P' THEN 'Processing'\r\n"
					+ "        WHEN urd.refund_process_status = 'S' THEN 'Success'\r\n"
					+ "        WHEN urd.refund_process_status = 'F' THEN 'Failed'\r\n"
					+ "    END AS Status,\r\n"
					+ "    urd.refund_created_timestamp,\r\n"
					+ "    zppd.property_city,\r\n"
					+ "    bd.user_account_number,\r\n"
					+ "    bd.user_ifsc_code\r\n"
					+ "FROM pgcommon.user_refund_details urd \r\n"
					+ "JOIN pgusers.user_master um ON urd.user_id = um.user_id \r\n"
					+ "JOIN pgowners.zoy_pg_property_details zppd ON urd.property_id = zppd.property_id \r\n"
					+ "LEFT JOIN pgusers.user_bookings ub ON urd.booking_id = ub.user_bookings_id \r\n"
					+ "LEFT JOIN pgcommon.bank_master bm ON urd.user_id = bm.user_id \r\n "
					+ "LEFT JOIN pgcommon.bank_details bd ON bm.user_bank_id = bd.user_bank_id \r\n"
					+ "WHERE 1 = 1");

			Map<String, Object> parameters = new HashMap<>();
			if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append(" AND urd.refund_created_timestamp BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP)");
				parameters.put("fromDate", filterRequest.getFromDate());
				parameters.put("toDate", filterRequest.getToDate());
			}
			if (filterData.getTenantName() != null && !filterData.getTenantName().isEmpty()) {
				queryBuilder.append(" AND LOWER(um.user_first_name || ' ' || um.user_last_name) LIKE LOWER(:pgName) ");
				parameters.put("pgName", "%" + filterData.getTenantName() + "%");
			}

			if (filterData.getTenantContactNum() != null && !filterData.getTenantContactNum().isEmpty()) {
				queryBuilder.append(" AND um.user_mobile LIKE :tenantContactNum ");
				parameters.put("tenantContactNum", "%" + filterData.getTenantContactNum() + "%");
			}

			if (filterData.getPgName() != null && !filterData.getPgName().isEmpty()) {
				queryBuilder.append(" AND LOWER(zppd.property_name) LIKE LOWER(:PgPropertyName) ");
				parameters.put("PgPropertyName", "%" + filterData.getPgName() + "%");
			}

			if (filterData.getPgAddress() != null && !filterData.getPgAddress().isEmpty()) {
				queryBuilder.append(" AND LOWER(zppd.property_house_area) LIKE LOWER(:pgAddress) ");
				parameters.put("pgAddress", "%" + filterData.getPgAddress() + "%");
			}
			if (filterData.getBookinId() != null && !filterData.getBookinId().isEmpty()) {
				queryBuilder.append(" AND LOWER(urd.booking_id) LIKE LOWER(:bookingId)");
				parameters.put("bookingId", "%" + filterData.getBookinId() + "%");
			}

			if (filterData.getRefundTitle() != null && !filterData.getRefundTitle().isEmpty()) {
				queryBuilder.append(" AND LOWER(ub.user_cancellation_reason) LIKE LOWER(:refundTitle) ");
				parameters.put("refundTitle", "%" + filterData.getRefundTitle() + "%");
			}

			if (filterData.getRefundAmount() != null && !filterData.getRefundAmount().isEmpty()) {
				try {
					queryBuilder.append(" AND urd.refund_amount = :refundableAmount ");
					parameters.put("refundableAmount", new BigDecimal(filterData.getRefundAmount()));
				} catch (NumberFormatException e) {
					throw new WebServiceException("Invalid refund amount: " + filterData.getRefundAmount());
				}
			}

			if (filterData.getTransactionStatus() != null && !filterData.getTransactionStatus().isEmpty()) {
				queryBuilder.append(" AND urd.refund_process_status = :paymentStatus ");
				parameters.put("paymentStatus", Boolean.parseBoolean(filterData.getTransactionStatus()));
			}

			if (filterRequest.getCityLocation() != null && !filterRequest.getCityLocation().isEmpty()) {
				queryBuilder.append(" AND LOWER(zppd.property_city) LIKE LOWER(CONCAT('%', :cityLocation, '%'))");
				parameters.put("cityLocation", filterRequest.getCityLocation());
			}
			if (filterData.getTenantAccountNumber() != null && !filterData.getTenantAccountNumber().isEmpty()) {
				queryBuilder.append(" AND bd.user_account_number LIKE :tenantAccountNum ");
				parameters.put("tenantAccountNum", "%" + filterData.getTenantAccountNumber() + "%");
			}
			
			if (filterData.getTenantIfscCode() != null && !filterData.getTenantIfscCode().isEmpty()) {
				queryBuilder.append(" AND bd.user_ifsc_code LIKE :tenantIfscCode ");
				parameters.put("tenantIfscCode", "%" + filterData.getTenantIfscCode() + "%");
			}
			if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty()
					&& filterRequest.getSortActive() != null) {
				String sort = "";
				switch (filterRequest.getSortActive()) {
				case "customerName":
					sort = "um.user_first_name || ' ' || um.user_last_name";
					break;
				case "tenantMobileNum":
					sort = "um.user_mobile";
					break;
				case "PgPropertyName":
					sort = "zppd.property_name";
					break;
				case "userPgPropertyAddress":
					sort = "zppd.property_house_area";
					break;
				case "bookingId":
					sort = "urd.booking_id";
					break;
				case "refundTitle":
					sort = "ub.user_cancellation_reason";
					break;
				case "refundableAmount":
					sort = "urd.refund_amount";
					break;
				case "tenantAccountNum":
					sort = "bd.user_account_number";
					break;
				case "tenantIfscCode":
					sort = "bd.user_ifsc_code";
					break;	
				default:
					sort = "urd.refund_updated_timestamp";
				}
				String sortDirection = filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";
				queryBuilder.append(" ORDER BY ").append(sort).append(" ").append(sortDirection);
			} else {
				queryBuilder.append(" ORDER BY urd.refund_date DESC");
			}

			Query query = entityManager.createNativeQuery(queryBuilder.toString());
			parameters.forEach(query::setParameter);

			int filterCount = query.getResultList().size();

			if (applyPagination) {
				query.setFirstResult(filterRequest.getPageIndex() * filterRequest.getPageSize());
				query.setMaxResults(filterRequest.getPageSize());
			}

			List<Object[]> results = query.getResultList();
			List<TenentRefund> tenentRefundDto = results.stream().map(row -> {
				TenentRefund dto = new TenentRefund();
				dto.setCustomerName(row[0] != null ? (String) row[0] : "");
				dto.setTenantMobileNum(row[1] != null ? (String) row[1] : "");
				dto.setPgPropertyName(row[2] != null ? (String) row[2] : "");
				dto.setUserPgPropertyAddress(row[3] != null ? (String) row[3] : "");
				dto.setBookingId(row[4] != null ? (String) row[4] : "");
				dto.setRefundTitle(row[5] != null ? (String) row[5] : "");
				dto.setRefundableAmount((row[6] != null) ? ((Number) row[6]).doubleValue() : 0.0);
				dto.setPaymentStatus(row[7] != null ? (String) row[7] : "");
				dto.setTransactionNumber("");
				dto.setAmountPaid(BigDecimal.valueOf(0).doubleValue());
				dto.setPaymentDate(row[8] != null ? (Timestamp)(row[8]) : null);
				dto.setTenantAccountNumber(row[10] != null ? (String) row[10] : "");
				dto.setTenantIfscCode(row[11] != null ? (String) row[11] : "");
				return dto;
			}).collect(Collectors.toList());

			return new CommonResponseDTO<>(tenentRefundDto, filterCount);
		} catch (Exception e) {
			throw new WebServiceException("Error retrieving tenant refunds: " + e.getMessage());
		}
	}
	@Override
	public CommonResponseDTO<RatingsAndReviewsReport> getRatingsAndReviewsDetails(
			UserPaymentFilterRequest filterRequest, FilterData filterData, Boolean applyPagination)
					throws WebServiceException {
		try {
			StringBuilder queryBuilder = new StringBuilder(
					" select rr.rating_id, rr.partner_id, rr.written_review, rr.overall_rating, rr.customer_id, rr.property_id, \r\n"
							+ "STRING_AGG(DISTINCT rating_master.review_type_id || ':' || rating_master.review_type || ':' || rrt.rating, ',') as ratingData, \r\n"
							+ "rr.timestamp, rr.booking_id, zpbd.bed_name, zppd.property_name, STRING_AGG(DISTINCT zpim.image_url, ',') as image_urls, \r\n"
							+ "zppd.property_house_area ,um.user_first_name ||' '||um.user_last_name ,ud.user_profile_image,um.user_mobile,MAX(CASE WHEN rating_master.review_type = 'cleanliness' THEN rrt.rating ELSE NULL END) AS cleanliness,\r\n"
							+ " MAX(CASE WHEN rating_master.review_type = 'amenities' THEN rrt.rating ELSE NULL END) AS amenities_rating,\r\n"
							+ " MAX(CASE WHEN rating_master.review_type = 'price' THEN rrt.rating ELSE NULL END) AS value_for_money_rating,\r\n"
							+ " MAX(CASE WHEN rating_master.review_type = 'maintainance' THEN rrt.rating ELSE NULL END) AS maintainance,\r\n"
							+ " MAX(CASE WHEN rating_master.review_type = 'accomodation' THEN rrt.rating ELSE NULL END) AS accomodation,\r\n"
							+ "zpobd.phone_number, \r\n"
							+ "zppd.property_city, \r\n"
							+ "zpobd.name \r\n"
							+ "from pgcommon.review_ratings rr \r\n"
							+ "left join pgcommon.review_ratings_types rrt on rr.rating_id = rrt.rating_id \r\n"
							+ "left join pgcommon.review_ratings_master rating_master on rrt.review_type_id = rating_master.review_type_id \r\n"
							+ "left join pgowners.zoy_pg_owner_booking_details zpobd on zpobd.booking_id = rr.booking_id \r\n"
							+ "left join pgowners.zoy_pg_bed_details zpbd on zpbd.bed_id = zpobd.selected_bed \r\n"
							+ "left join pgowners.zoy_pg_property_details zppd on zppd.property_id = rr.property_id \r\n"
							+ "left join pgowners.zoy_pg_properties_images zppi on zppi.property_id = zppd.property_id \r\n"
							+ "left join pgowners.zoy_pg_image_master zpim on zpim.image_id = zppi.image_id \r\n"
							+ "left join pgusers.user_master um on um.user_id =rr.customer_id \r\n"
							+ "left join pgusers.user_details ud on ud.user_id =um.user_id"
							+ " WHERE 1 = 1");

			Map<String, Object> parameters = new HashMap<>();

			if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append(" AND rr.timestamp BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP)");
				parameters.put("fromDate", filterRequest.getFromDate());
				parameters.put("toDate", filterRequest.getToDate());
			}
			
			if (filterRequest.getLowRating() != null && !filterRequest.getLowRating().isEmpty()) {
			    queryBuilder.append(" AND rr.overall_rating < :lowRating");
			    parameters.put("lowRating", Double.parseDouble(filterRequest.getLowRating()));
			}

			if (filterData.getPgName() != null && !filterData.getPgName().isEmpty()) {
				queryBuilder.append(" AND zppd.property_name IS NOT NULL AND LOWER(zppd.property_name) LIKE LOWER(:pgName)");
				parameters.put("pgName", "%" + filterData.getPgName() + "%");
			}
			
			if (filterData.getTenantName() != null && !filterData.getTenantName().isEmpty()) {
				queryBuilder.append(" AND um.user_first_name || ' ' || um.user_last_name IS NOT NULL AND LOWER(um.user_first_name || ' ' || um.user_last_name) LIKE LOWER(:tenantName)");
				parameters.put("tenantName", "%" + filterData.getTenantName() + "%");
			}

			if (filterData.getTenantContactNum() != null && !filterData.getTenantContactNum().isEmpty()) {
				queryBuilder.append(" AND um.user_mobile IS NOT NULL AND LOWER(um.user_mobile) LIKE LOWER(:tenantContactNum)");
				parameters.put("tenantContactNum", "%" + filterData.getTenantContactNum() + "%");
			}

			if (filterData.getOverallRating() != null && !filterData.getOverallRating().isEmpty()) {
				queryBuilder.append(" AND rr.overall_rating IS NOT NULL AND CAST(rr.overall_rating AS text) LIKE :overallRating");
				parameters.put("overallRating", "%" +filterData.getOverallRating()+ "%");
			}
			if (filterRequest.getCityLocation() != null && !filterRequest.getCityLocation().isEmpty()) {
				queryBuilder.append(" AND LOWER(zppd.property_city) LIKE LOWER(CONCAT('%', :cityLocation, '%'))");
				parameters.put("cityLocation", filterRequest.getCityLocation());
			}
			if(filterData.getOwnerName() != null && !filterData.getOwnerName().isEmpty()) {
				queryBuilder.append(" AND LOWER(zpobd.name) LIKE LOWER(CONCAT('%', :ownerName, '%'))");
				parameters.put("ownerName", filterData.getOwnerName());
			}
			if(filterData.getOwnerContactNum()!= null && !filterData.getOwnerContactNum().isEmpty()) {
				queryBuilder.append(" AND LOWER(zpobd.phone_number) LIKE LOWER(CONCAT('%', :ownerConcatNumber, '%'))");
				parameters.put("ownerConcatNumber", filterData.getOwnerContactNum());
 
			}
			if (filterRequest.getSearchText() != null && !filterRequest.getSearchText().trim().isEmpty()) {
			    String searchText = "%" + filterRequest.getSearchText().toLowerCase().trim() + "%";
			    queryBuilder.append(" AND (")
			        .append("LOWER(zppd.property_name) LIKE :searchText OR ")
			        .append("LOWER(um.user_first_name || ' ' || um.user_last_name) LIKE :searchText OR ")
			        .append("LOWER(um.user_mobile) LIKE :searchText OR ")
			        .append("LOWER(zpobd.name) LIKE :searchText OR ")
			        .append("LOWER(zpobd.phone_number) LIKE :searchText OR ")
			        .append("LOWER(rr.written_review) LIKE :searchText OR ")
			        .append("LOWER(CAST(rr.overall_rating AS TEXT)) LIKE :searchText OR ")
			        .append("LOWER(zppd.property_city) LIKE :searchText")
			        .append(")");
			    parameters.put("searchText", searchText);
			}
 
			if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty()
					&& filterRequest.getSortActive() != null) {
				String sort = "";
				switch (filterRequest.getSortActive()) {
				case "reviewDate":
					sort = "rr.timestamp";
					break;
				case "customerName":
					sort = "um.user_first_name || ' ' || um.user_last_name";
					break;
				case "pgName":
					sort = "zppd.property_name";
					break;
				case "tenantContactNum":
					sort = "um.user_mobile";
					break;
				case "cleanliness":
					sort = "MAX(CASE WHEN rating_master.review_type = 'cleanliness' THEN rrt.rating ELSE NULL END)";
					break;
				case "accommodation":
					sort = "MAX(CASE WHEN rating_master.review_type = 'accomodation' THEN rrt.rating ELSE NULL END)";
					break;
				case "amenities":
					sort = "MAX(CASE WHEN rating_master.review_type = 'amenities' THEN rrt.rating ELSE NULL END)";
					break;
				case "maintenance":
					sort = "MAX(CASE WHEN rating_master.review_type = 'maintainance' THEN rrt.rating ELSE NULL END)";
					break;
				case "valueForMoney":
					sort = "MAX(CASE WHEN rating_master.review_type = 'price' THEN rrt.rating ELSE NULL END)";
					break;
				case "overallRating":
					sort = "rr.overall_rating";
					break;
				case "ownerContactNumber":
					sort = "zpobd.phone_number";	
				case "ownerName":
					sort="zpobd.name";
				default:
					sort = "rr.timestamp";
				}
				queryBuilder.append(" group by rr.rating_id,rr.partner_id,rr.written_review,rr.overall_rating,rr.customer_id,rr.property_id, "+
						"rr.timestamp,zpbd.bed_name,zppd.property_id,um.user_id,ud.user_profile_image,um.user_mobile,zpobd.phone_number,zpobd.name " );


				String sortDirection = filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";

				queryBuilder.append(" ORDER BY ").append(sort).append(" ").append(sortDirection);
			} else {
				queryBuilder.append(" ORDER BY zppd.property_name, rr.timestamp DESC");
			}
			Query query = entityManager.createNativeQuery(queryBuilder.toString());
			parameters.forEach(query::setParameter);
			int filterCount = query.getResultList().size();
			if (applyPagination) {
				query.setFirstResult(filterRequest.getPageIndex() * filterRequest.getPageSize());
				query.setMaxResults(filterRequest.getPageSize());
			}
			List<Object[]> results = query.getResultList();

			List<RatingsAndReviewsReport> ratingsAndReviewsDto = results.stream().map(row -> {
				RatingsAndReviewsReport model = new RatingsAndReviewsReport();
				model.setReviewDate(row[7] != null ? Timestamp.valueOf(String.valueOf(row[7])) : null);
				model.setWrittenReview(row[2] != null ? row[2].toString() : "");
				model.setOverallRating(row[3] != null ? row[3].toString() : "");
				model.setPropertyName(row[10] != null ? row[10].toString() : "");
				model.setCustomerName(row[13] != null ? row[13].toString() : "");
				String userImagePath = row[14] !=null?row[14].toString():null;
				String userImageUrl="";
				if (userImagePath != null && !userImagePath.isEmpty()) {
					String folderName = userImagePath.split("/")[0];
					if(folderName.equals(row[4]))
						userImageUrl= zoyAdminService.generatePreSignedUrl(userPhotoBucketName, userImagePath);
					else 
						userImageUrl= zoyAdminService.generatePreSignedUrl(aadhaarPhotoBucket, userImagePath);
				}
				model.setCustomerImage(userImageUrl);
				model.setCustomerMobileNo(row[15] != null ? row[15].toString() : "");
				model.setCleanliness(row[16] != null ? row[16].toString() : "");
				model.setAmenities(row[17] != null ? row[17].toString() : "");
				model.setValueForMoney(row[18] != null ? row[18].toString() : "");
				model.setMaintenance(row[19] != null ? row[19].toString() : "");
				model.setAccommodation(row[20] != null ? row[20].toString() : "");
				model.setOwnerName(row[23] != null ? row[23].toString() : "");
				model.setOwnerContactNum(row[22] != null ? row[22].toString() : "");
				String replies=row[0] != null ? row[0].toString() : "";

				List<String[]> reviewReplies = zoyPgPropertyDetailsRepository.findAllReviewsReplies(replies);

				model.setThreads(reviewReplies != null ? reviewReplies.stream()
						.map(parts -> {
							String customerImagePath = parts[10] !=null?parts[10].toString():null;
							String customerImageUrl="";
							if (customerImagePath != null && !customerImagePath.isEmpty()) {
								String folderName = customerImagePath.split("/")[0];
								if(folderName.equals(parts[2]))
									customerImageUrl= zoyAdminService.generatePreSignedUrl(userPhotoBucketName, customerImagePath);
								else 
									customerImageUrl= zoyAdminService.generatePreSignedUrl(aadhaarPhotoBucket, customerImagePath);
							}
							String ownerImagePath = parts[11] !=null?parts[11].toString():null;
							String ownerImageUrl="";
							if (ownerImagePath != null && !ownerImagePath.isEmpty()) {
								String folderName = ownerImagePath.split("/")[0];
								if(folderName.equals(parts[4]))
									ownerImageUrl= zoyAdminService.generatePreSignedUrl(userPhotoBucketName, ownerImagePath);
								else 
									ownerImageUrl= zoyAdminService.generatePreSignedUrl(aadhaarPhotoBucket, ownerImagePath);
							}
							return new RatingsAndReviewsReport.ReviewReplies(parts[0], parts[1], parts[2], parts[3],
									parts[4], parts[5], parts[6], parts[7] != null ? Timestamp.valueOf(parts[7]) : null,
											parts[8] != null ? Boolean.valueOf(parts[8]) : false,
													parts[9] != null ? Boolean.valueOf(parts[9]) : false,
															customerImageUrl, ownerImageUrl);
						})
						.collect(Collectors.toList()) : new ArrayList<>());

				return model;
			}).collect(Collectors.toList());

			return new CommonResponseDTO<>(ratingsAndReviewsDto, filterCount);
		} catch (Exception e) {
			new ZoyAdminApplicationException(e, "");
		}
		return null;
	}



	public CommonResponseDTO<TenantResportsDTO> getUpcomingTenantsReport(UserPaymentFilterRequest filterRequest,
			FilterData filterData, Boolean applyPagination) throws WebServiceException {
		try {
			StringBuilder queryBuilder = new StringBuilder("SELECT \r\n"
					+ "    um.user_first_name || ' ' || um.user_last_name AS username,\r\n"
					+ "    um.user_mobile,\r\n"
					+ "    um.user_email,\r\n"
					+ "    zpd.property_name,\r\n"
					+ "    zpd.property_house_area,\r\n"
					+ "    bd.bed_name,\r\n"
					+ "    zpqbd.in_date, \r\n"
					+ "    zpqbd.out_date\r\n"
					+ "FROM pgusers.user_master um\r\n"
					+ "JOIN pgowners.zoy_pg_owner_booking_details zpqbd \r\n"
					+ "    ON um.user_id = zpqbd.tenant_id \r\n"
					+ "JOIN pgowners.zoy_pg_property_details zpd \r\n"
					+ "    ON zpqbd.property_id = zpd.property_id \r\n"
					+ "JOIN pgusers.user_bookings ub \r\n"
					+ "	   ON zpqbd.booking_id = ub.user_bookings_id "
					+ "JOIN pgowners.zoy_pg_bed_details bd  \r\n"
					+ "    ON zpqbd.selected_bed = bd.bed_id\r\n"
					+ "WHERE 1=1  and ub.user_bookings_is_cancelled = false AND ub.user_bookings_web_check_in = false");

			Map<String, Object> parameters = new HashMap<>();
			
			if (filterData.getTenantName() != null && !filterData.getTenantName().isEmpty()) {
				queryBuilder.append(" AND LOWER(um.user_first_name || ' ' || um.user_last_name) LIKE LOWER(:tenantName) ");
				parameters.put("tenantName", "%" + filterData.getTenantName() + "%");
			}

			if (filterData.getTenantContactNum() != null && !filterData.getTenantContactNum().isEmpty()) {
				queryBuilder.append(" AND um.user_mobile LIKE :tenantContactNum ");
				parameters.put("tenantContactNum", "%" + filterData.getTenantContactNum() + "%");
			}

			if (filterData.getOwnerEmail() != null && !filterData.getOwnerEmail().isEmpty()) {
	            queryBuilder.append(" AND LOWER(pom.email_id) LIKE LOWER(:ownerEmail) ");
	            parameters.put("ownerEmail", "%" + filterData.getOwnerEmail() + "%");
	        }
 
			if (filterData.getPgName() != null && !filterData.getPgName().isEmpty()) {
				queryBuilder.append(" AND LOWER(zpd.property_name) LIKE LOWER(:pgName) ");
				parameters.put("pgName", "%" + filterData.getPgName() + "%");
			}

			if (filterData.getBedNumber() != null && !filterData.getBedNumber().isEmpty()) {
				queryBuilder.append(" AND LOWER(bd.bed_name) LIKE LOWER(:bedNumber) ");
				parameters.put("bedNumber", "%" + filterData.getBedNumber() + "%");
			}

			if (filterRequest.getCityLocation() != null && !filterRequest.getCityLocation().isEmpty()) {
				queryBuilder.append(" AND LOWER(zpd.property_city) LIKE LOWER(CONCAT('%', :cityLocation, '%'))");
				parameters.put("cityLocation", filterRequest.getCityLocation());
			}
			if (filterData.getOwnerEmail() != null && !filterData.getOwnerEmail().isEmpty()) {
				queryBuilder.append("AND LOWER(um.user_email) LIKE LOWER(:ownerName)");
				parameters.put("ownerEmail", "%" + filterData.getOwnerEmail() + "%");
			}

			if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty()
					&& filterRequest.getSortActive() != null) {
				String sort = "";
				switch (filterRequest.getSortActive()) {
				case "tenantName":
					sort = "um.user_first_name || ' ' || um.user_last_name";
					break;
				case "tenantContactNumber":
					sort = "um.user_mobile";
					break;
				case "tenantEmailAddress":
					sort="um.user_email";
					break;
				case "bookedProperyName":
					sort = "zpd.property_name";
					break;
				case "propertAddress":
					sort = "zpd.property_house_area";
					break;
				case "bedNumber":
					sort = "bd.bed_name";
					break;
				case "expectedCheckIndate":
					sort = "zpqbd.in_date";
					break;
				case "expectedCheckOutdate" :
					sort="zpqbd.out_date";
					break;
				default:
					sort = "zpqbd.in_date";
				}
				String sortDirection = filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";
				queryBuilder.append(" ORDER BY ").append(sort).append(" ").append(sortDirection);
			} else {
				queryBuilder.append(" ORDER BY zpqbd.in_date DESC");
			}

			Query query = entityManager.createNativeQuery(queryBuilder.toString());
			parameters.forEach(query::setParameter);

			int filterCount = query.getResultList().size();

			if (applyPagination) {
				query.setFirstResult(filterRequest.getPageIndex() * filterRequest.getPageSize());
				query.setMaxResults(filterRequest.getPageSize());
			}

			List<Object[]> results = query.getResultList();
			List<TenantResportsDTO> upcomingTenantsReportDto = results.stream().map(row -> {
				TenantResportsDTO dto = new TenantResportsDTO();
				dto.setTenantName(row[0] != null ? (String) row[0] : "");
				dto.setTenantContactNumber(row[1] != null ? (String) row[1] : "");
				dto.setTenantEmailAddress(row[2] != null ? (String) row[2] : "");
				dto.setBookedProperyName(row[3] != null ? (String) row[3] : "");
				dto.setPropertAddress(row[4] != null ? (String) row[4] : "");
				dto.setBedNumber(row[5] != null ? (String) row[5] : "");
				dto.setExpectedCheckIndate(row[6] != null ? (Timestamp)(row[6]) : null);
				dto.setExpectedCheckOutdate(row[7] != null ? (Timestamp)(row[7]) : null);
				return dto;
			}).collect(Collectors.toList());

			return new CommonResponseDTO<>(upcomingTenantsReportDto, filterCount);
		} catch (Exception e) {
			throw new WebServiceException("Error retrieving Upcoming Tenants: " + e.getMessage());
		}
	}


	public CommonResponseDTO<TenantResportsDTO> getActiveTenantsReport(UserPaymentFilterRequest filterRequest,
			FilterData filterData, Boolean applyPagination) throws WebServiceException {
		try {
			StringBuilder queryBuilder = new StringBuilder("      SELECT\r\n"
					+ "					                 um.user_first_name || ' ' || um.user_last_name AS username,\r\n"
					+ "					                 um.user_mobile AS mobileNumber,\r\n"
					+ "					                 um.user_email AS emailId,\r\n"
					+ "					                 zpd.property_name AS propertyName,\r\n"
					+ "					                 zpd.property_house_area AS propertyAddress,\r\n"
					+ "					                 bd.bed_name AS bedName,\r\n"
					+ "					                 zpqbd.in_date AS inDate, \r\n"
					+ "					                 case when upd.user_pg_checkout_date is not null then upd.user_pg_checkout_date else zpqbd.out_date end as checkOutData,\r\n"
					+ "					                 zpd.property_city\r\n"
					+ "										FROM pgusers.user_master um\r\n"
					+ "										JOIN pgowners.zoy_pg_owner_booking_details zpqbd \r\n"
					+ "										    ON um.user_id = zpqbd.tenant_id \r\n"
					+ "										join pgusers.user_bookings ub\r\n"
					+ "										    on zpqbd.booking_id = ub.user_bookings_id \r\n"
					+ "										JOIN pgowners.zoy_pg_property_details zpd \r\n"
					+ "										    ON zpqbd.property_id = zpd.property_id  \r\n"
					+ "										join pgusers.user_pg_details upd \r\n"
					+ "										on upd.user_pg_booking_id =zpqbd.booking_id \r\n"
					+ "										JOIN pgowners.zoy_pg_bed_details bd  \r\n"
					+ "										    ON zpqbd.selected_bed = bd.bed_id \r\n"
					+ "										WHERE 1=1 ");

			Map<String, Object> parameters = new HashMap<>();

			if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append(" AND (\r\n"
						+ "        zpqbd.in_date BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP) \r\n"
						+ "        OR \r\n"
						+ "        (CASE \r\n"
						+ "            WHEN upd.user_pg_checkout_date IS NOT NULL THEN upd.user_pg_checkout_date \r\n"
						+ "            ELSE zpqbd.out_date \r\n"
						+ "         END) BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP)\r\n"
						+ "        OR \r\n"
						+ "        CAST(:fromDate AS TIMESTAMP) BETWEEN zpqbd.in_date AND \r\n"
						+ "        (CASE \r\n"
						+ "            WHEN upd.user_pg_checkout_date IS NOT NULL THEN upd.user_pg_checkout_date \r\n"
						+ "            ELSE zpqbd.out_date \r\n"
						+ "         END)\r\n"
						+ "        OR \r\n"
						+ "        CAST(:toDate AS TIMESTAMP) BETWEEN zpqbd.in_date AND \r\n"
						+ "        (CASE \r\n"
						+ "            WHEN upd.user_pg_checkout_date IS NOT NULL THEN upd.user_pg_checkout_date \r\n"
						+ "            ELSE zpqbd.out_date \r\n"
						+ "         END)\r\n"
						+ "    )\r\n"
						+ "    AND (ub.user_bookings_web_check_in = TRUE OR ub.user_bookings_web_check_out = TRUE)");
				parameters.put("fromDate", filterRequest.getFromDate());
				parameters.put("toDate", filterRequest.getToDate());
			}
			if (filterData.getTenantName() != null && !filterData.getTenantName().isEmpty()) {
				queryBuilder.append(" AND LOWER(um.user_first_name || ' ' || um.user_last_name) LIKE LOWER(:tenantName) ");
				parameters.put("tenantName", "%" + filterData.getTenantName() + "%");
			}

			if (filterData.getTenantContactNum() != null && !filterData.getTenantContactNum().isEmpty()) {
				queryBuilder.append(" AND um.user_mobile LIKE :tenantContactNum ");
				parameters.put("tenantContactNum", "%" + filterData.getTenantContactNum() + "%");
			}

			if (filterData.getPgName() != null && !filterData.getPgName().isEmpty()) {
				queryBuilder.append(" AND LOWER(zpd.property_name) LIKE LOWER(:pgName) ");
				parameters.put("pgName", "%" + filterData.getPgName() + "%");
			}

			if (filterData.getBedNumber() != null && !filterData.getBedNumber().isEmpty()) {
				queryBuilder.append(" AND LOWER(bd.bed_name) LIKE LOWER(:bedNumber) ");
				parameters.put("bedNumber", "%" + filterData.getBedNumber() + "%");
			}

			if (filterRequest.getCityLocation() != null && !filterRequest.getCityLocation().isEmpty()) {
				queryBuilder.append(" AND LOWER(zpd.property_city) LIKE LOWER(CONCAT('%', :cityLocation, '%'))");
				parameters.put("cityLocation", filterRequest.getCityLocation());
			}

			if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty()
					&& filterRequest.getSortActive() != null) {
				String sort = "";
				switch (filterRequest.getSortActive()) {
				case "tenantName":
					sort = "um.user_first_name || ' ' || um.user_last_name";
					break;
				case "tenantContactNumber":
					sort = "um.user_mobile";
					break;
				case "tenantEmailAddress":
					sort="um.user_email";
					break;
				case "currentPropertName":
					sort = "zpd.property_name";
					break;
				case "propertAddress":
					sort = "zpd.property_house_area";
					break;
				case "bedNumber":
					sort = "bd.bed_name";
					break;
				case "checkInDate":
					sort = "zpqbd.in_date";
					break;
				case "expectedCheckOutdate" :
					sort="checkOutData";
					break;
				default:
					sort = "zpqbd.in_date";
				}
				String sortDirection = filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";
				queryBuilder.append(" ORDER BY ").append(sort).append(" ").append(sortDirection);
			} else {
				queryBuilder.append(" ORDER BY zpqbd.in_date DESC");
			}

			Query query = entityManager.createNativeQuery(queryBuilder.toString());
			parameters.forEach(query::setParameter);

			int filterCount = query.getResultList().size();

			if (applyPagination) {
				query.setFirstResult(filterRequest.getPageIndex() * filterRequest.getPageSize());
				query.setMaxResults(filterRequest.getPageSize());
			}

			List<Object[]> results = query.getResultList();
			List<TenantResportsDTO> activeTenantsReportDto = results.stream().map(row -> {
				TenantResportsDTO dto = new TenantResportsDTO();
				dto.setTenantName(row[0] != null ? (String) row[0] : "");
				dto.setTenantContactNumber(row[1] != null ? (String) row[1] : "");
				dto.setTenantEmailAddress(row[2] != null ? (String) row[2] : "");
				dto.setCurrentPropertName(row[3] != null ? (String) row[3] : "");
				dto.setPropertAddress(row[4] != null ? (String) row[4] : "");
				dto.setBedNumber(row[5] != null ? (String) row[5] : "");
				dto.setCheckInDate(row[6] != null ? Timestamp.valueOf(String.valueOf(row[6])) : null);
				dto.setExpectedCheckOutdate(row[7] != null ? Timestamp.valueOf(String.valueOf(row[7])) : null);
				return dto;
			}).collect(Collectors.toList());

			return new CommonResponseDTO<>(activeTenantsReportDto, filterCount);
		} catch (Exception e) {
			throw new WebServiceException("Error retrieving Active Tenants: " + e.getMessage());
		}
	}


	public CommonResponseDTO<TenantResportsDTO> getInActiveTenantsReport(UserPaymentFilterRequest filterRequest,
			FilterData filterData, Boolean applyPagination) throws WebServiceException {
		try {
			StringBuilder queryBuilder = new StringBuilder(
					"SELECT DISTINCT\r\n"
					+ "    um.user_first_name || ' ' || um.user_last_name AS username,\r\n"
					+ "    um.user_mobile AS mobileNumber,\r\n"
					+ "    um.user_email AS emailId,\r\n"
					+ "    zppd.property_name AS propertyName,\r\n"
					+ "    zppd.property_house_area AS propertyAddress,\r\n"
					+ "    bd.bed_name AS bedName,\r\n"
					+ "    zpobd.in_date AS inDate,\r\n"
					+ "    CASE \r\n"
					+ "        WHEN upd.user_pg_checkout_date IS NOT NULL \r\n"
					+ "        THEN upd.user_pg_checkout_date \r\n"
					+ "        ELSE zpobd.out_date \r\n"
					+ "    END AS checkOutDate\r\n"
					+ "FROM pgowners.zoy_pg_owner_booking_details zpobd\r\n"
					+ "JOIN pgusers.user_master um \r\n"
					+ "    ON zpobd.tenant_id = um.user_id\r\n"
					+ "JOIN pgusers.user_bookings ub \r\n"
					+ "    ON ub.user_bookings_id = zpobd.booking_id\r\n"
					+ "JOIN pgowners.zoy_pg_property_details zppd \r\n"
					+ "    ON zppd.property_id = zpobd.property_id\r\n"
					+ "JOIN pgowners.zoy_pg_bed_details bd \r\n"
					+ "    ON zpobd.selected_bed = bd.bed_id\r\n"
					+ "LEFT JOIN pgusers.user_pg_details upd \r\n"
					+ "    ON upd.user_pg_booking_id = zpobd.booking_id\r\n"
					+ "JOIN (\r\n"
					+ "    SELECT \r\n"
					+ "        zpobd.tenant_id,\r\n"
					+ "        MAX(\r\n"
					+ "            CASE \r\n"
					+ "                WHEN upd.user_pg_checkout_date IS NOT NULL \r\n"
					+ "                THEN upd.user_pg_checkout_date \r\n"
					+ "                ELSE zpobd.out_date \r\n"
					+ "            END\r\n"
					+ "        ) AS checkOutDate\r\n"
					+ "    FROM pgowners.zoy_pg_owner_booking_details zpobd\r\n"
					+ "    JOIN pgusers.user_bookings ub \r\n"
					+ "        ON ub.user_bookings_id = zpobd.booking_id\r\n"
					+ "    LEFT JOIN pgusers.user_pg_details upd \r\n"
					+ "        ON upd.user_pg_booking_id = zpobd.booking_id\r\n"
					+ "    JOIN pgusers.user_master um ON zpobd.tenant_id = um.user_id\r\n"
					+ "    WHERE ub.user_bookings_web_check_out = TRUE and um.user_status = 'Inactive'\r\n"
					+ "    GROUP BY zpobd.tenant_id\r\n"
					+ ") latest_out \r\n"
					+ "    ON zpobd.tenant_id = latest_out.tenant_id \r\n"
					+ "    AND (\r\n"
					+ "        CASE \r\n"
					+ "            WHEN upd.user_pg_checkout_date IS NOT NULL \r\n"
					+ "            THEN upd.user_pg_checkout_date \r\n"
					+ "            ELSE zpobd.out_date \r\n"
					+ "        END\r\n"
					+ "    ) = latest_out.checkOutDate\r\n"
					+ "WHERE \r\n"
					+ "    ub.user_bookings_web_check_out = true and um.user_status = 'Inactive'");
			Map<String, Object> parameters = new HashMap<>();

			if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append(" AND (\r\n"
						+ "        CASE \r\n"
						+ "            WHEN upd.user_pg_checkout_date IS NOT NULL \r\n"
						+ "            THEN upd.user_pg_checkout_date \r\n"
						+ "            ELSE zpobd.out_date \r\n"
						+ "        END\r\n"
						+ "    ) <= :toDate");
			//	parameters.put("fromDate", filterRequest.getFromDate());
				parameters.put("toDate", filterRequest.getToDate());
			}

			if (filterData.getTenantName() != null && !filterData.getTenantName().isEmpty()) {
				queryBuilder.append(" AND LOWER(um.user_first_name || ' ' || um.user_last_name) LIKE LOWER(:tenantName) ");
				parameters.put("tenantName", "%" + filterData.getTenantName() + "%");
			}

			if (filterData.getTenantContactNum() != null && !filterData.getTenantContactNum().isEmpty()) {
				queryBuilder.append(" AND um.user_mobile LIKE :tenantContactNum ");
				parameters.put("tenantContactNum", "%" + filterData.getTenantContactNum() + "%");
			}

			if (filterData.getPgName() != null && !filterData.getPgName().isEmpty()) {
				queryBuilder.append(" AND LOWER(zppd.property_name) LIKE LOWER(:pgName) ");
				parameters.put("pgName", "%" + filterData.getPgName() + "%");
			}

			if (filterData.getBedNumber() != null && !filterData.getBedNumber().isEmpty()) {
				queryBuilder.append(" AND LOWER(bd.bed_name) LIKE LOWER(:bedNumber) ");
				parameters.put("bedNumber", "%" + filterData.getBedNumber() + "%");
			}

			if (filterRequest.getCityLocation() != null && !filterRequest.getCityLocation().isEmpty()) {
				queryBuilder.append(" AND LOWER(zppd.property_city) LIKE LOWER(CONCAT('%', :cityLocation, '%')) ");
				parameters.put("cityLocation", filterRequest.getCityLocation());
			}


			if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty()
					&& filterRequest.getSortActive() != null) {
				String sort = "";
				switch (filterRequest.getSortActive()) {
				case "tenantName":
					sort = "username";
					break;
				case "tenantContactNumber":
					sort = "mobileNumber";
					break;
				case "tenantEmailAddress":
					sort = "emailId";
					break;
				case "previousPropertName":
					sort = "propertyName";
					break;
				case "propertAddress":
					sort = "propertyAddress";
					break;
				case "bedNumber":
					sort = "bedName";
					break;
				case "checkInDate":
					sort="inDate";
					break;
				case "checkedOutDate":
					sort = "checkOutDate";
					break;
				default:
					sort = "inDate";
				}
				String sortDirection = filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";
				queryBuilder.append(" ORDER BY ").append(sort).append(" ").append(sortDirection);
			} else {
				queryBuilder.append(" ORDER BY inDate DESC ");
			}

			Query query = entityManager.createNativeQuery(queryBuilder.toString());
			parameters.forEach(query::setParameter);

			int filterCount = query.getResultList().size();

			if (applyPagination) {
				query.setFirstResult(filterRequest.getPageIndex() * filterRequest.getPageSize());
				query.setMaxResults(filterRequest.getPageSize());
			}

			List<Object[]> results = query.getResultList();
			List<TenantResportsDTO> inActiveTenantsReportDto = results.stream().map(row -> {
				TenantResportsDTO dto = new TenantResportsDTO();
				dto.setTenantName(row[0] != null ? (String) row[0] : ""); 
				dto.setTenantContactNumber(row[1] != null ? (String) row[1] : "");
				dto.setTenantEmailAddress(row[2] != null ? (String) row[2] : ""); 
				dto.setPreviousPropertName(row[3] != null ? (String) row[3] : ""); 
				dto.setPropertAddress(row[4] != null ? (String) row[4] : ""); 
				dto.setBedNumber(row[5] != null ? (String) row[5] : ""); 
				dto.setCheckInDate(row[6] != null ? Timestamp.valueOf(String.valueOf(row[6])) : null);
				dto.setCheckedOutDate(row[7] != null ? Timestamp.valueOf(String.valueOf(row[7])) : null);
				return dto;
			
			}).collect(Collectors.toList());

			return new CommonResponseDTO<>(inActiveTenantsReportDto, filterCount);
		} catch (Exception e) {
			throw new WebServiceException("Error retrieving inActive Tenants: " + e.getMessage());
		}
	}


	public CommonResponseDTO<TenantResportsDTO> getSuspendedTenantsReport(UserPaymentFilterRequest filterRequest,
			FilterData filterData, Boolean applyPagination) throws WebServiceException {
		try {
			StringBuilder queryBuilder = new StringBuilder(
					"SELECT * FROM ( " +
							"    SELECT DISTINCT ON (zpobd.tenant_id) " +
							"       um.user_first_name, " +
							"       um.user_last_name, " +
							"       um.user_first_name || ' ' || um.user_last_name AS username, " +
							"       um.user_mobile AS mobileNumber, " +
							"       um.user_email AS emailId, " +
							"       zppd.property_name AS propertyName, " +
							"       zppd.property_house_area AS propertyAddress, " +
							"       bd.bed_name AS bedName , " +
							"       zpobd.out_date AS outDate, " +
							"       um.user_modified_at AS suspendedDate, " +
							"       um.reason_message AS reasonForSuspension " +
							"    FROM pgowners.zoy_pg_owner_booking_details zpobd " +
							"    JOIN pgusers.user_bookings ub ON zpobd.booking_id = ub.user_bookings_id " +
							//	                        "    AND ub.user_bookings_web_check_out = TRUE"+
							"    JOIN pgusers.user_master um ON um.user_id = zpobd.tenant_id " +
							"    JOIN pgowners.zoy_pg_property_details zppd ON zppd.property_id = zpobd.property_id " +
							"    JOIN pgowners.zoy_pg_bed_details bd ON zpobd.selected_bed = bd.bed_id " +
							"    WHERE um.user_status = 'Suspended' "
					);

			Map<String, Object> parameters = new HashMap<>();

			if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append(" AND zpobd.out_date BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP) ");
				parameters.put("fromDate", filterRequest.getFromDate());
				parameters.put("toDate", filterRequest.getToDate());
			}

			if (filterData.getTenantName() != null && !filterData.getTenantName().isEmpty()) {
				queryBuilder.append(" AND LOWER(um.user_first_name || ' ' || um.user_last_name) LIKE LOWER(:tenantName) ");
				parameters.put("tenantName", "%" + filterData.getTenantName() + "%");
			}

			if (filterData.getTenantContactNum() != null && !filterData.getTenantContactNum().isEmpty()) {
				queryBuilder.append(" AND um.user_mobile LIKE :tenantContactNum ");
				parameters.put("tenantContactNum", "%" + filterData.getTenantContactNum() + "%");
			}

			if (filterData.getPgName() != null && !filterData.getPgName().isEmpty()) {
				queryBuilder.append(" AND LOWER(zppd.property_name) LIKE LOWER(:pgName) ");
				parameters.put("pgName", "%" + filterData.getPgName() + "%");
			}

			if (filterData.getBedNumber() != null && !filterData.getBedNumber().isEmpty()) {
				queryBuilder.append(" AND LOWER(bd.bed_name) LIKE LOWER(:bedNumber) ");
				parameters.put("bedNumber", "%" + filterData.getBedNumber() + "%");
			}

			if (filterRequest.getCityLocation() != null && !filterRequest.getCityLocation().isEmpty()) {
				queryBuilder.append(" AND LOWER(zpd.property_city) LIKE LOWER(CONCAT('%', :cityLocation, '%')) ");
				parameters.put("cityLocation", filterRequest.getCityLocation());
			}

			queryBuilder.append(" ORDER BY zpobd.tenant_id " +
					") sub ");

			if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty()
					&& filterRequest.getSortActive() != null) {
				String sort = "";
				switch (filterRequest.getSortActive()) {
				case "tenantName":
					sort = "username";
					break;
				case "tenantContactNumber":
					sort = "mobileNumber";
					break;
				case "tenantEmailAddress":
					sort = "emailId";
					break;
				case "previousPropertName":
					sort = "propertyName";
					break;
				case "propertAddress":
					sort = "propertyAddress";
					break;
				case "roomNumber":
					sort = "bedName";
					break;
				case "checkedOutDate":
					sort = "outDate";
					break;
				case "suspendedDate":
					sort = "suspendedDate";
					break;
				case "reasonForSuspension":
					sort = "reasonForSuspension";
					break;
				default:
					sort = "suspendedDate";
				}
				String sortDirection = filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";
				queryBuilder.append(" ORDER BY ").append(sort).append(" ").append(sortDirection);
			} else {
				queryBuilder.append(" ORDER BY suspendedDate DESC ");
			}

			Query query = entityManager.createNativeQuery(queryBuilder.toString());
			parameters.forEach(query::setParameter);

			int filterCount = query.getResultList().size();

			if (applyPagination) {
				query.setFirstResult(filterRequest.getPageIndex() * filterRequest.getPageSize());
				query.setMaxResults(filterRequest.getPageSize());
			}

			List<Object[]> results = query.getResultList();
			List<TenantResportsDTO> suspendedTenantsReportDto = results.stream().map(row -> {
				TenantResportsDTO dto = new TenantResportsDTO();
				dto.setTenantName(row[2] != null ? (String) row[2] : ""); 
				dto.setTenantContactNumber(row[3] != null ? (String) row[3] : "");
				dto.setTenantEmailAddress(row[4] != null ? (String) row[4] : ""); 
				dto.setPreviousPropertName(row[5] != null ? (String) row[5] : ""); 
				dto.setPropertAddress(row[6] != null ? (String) row[6] : ""); 
				dto.setBedNumber(row[7] != null ? (String) row[7] : ""); 
				dto.setCheckedOutDate(row[8] != null ? (Timestamp) row[8] : null);
				dto.setSuspendedDate(row[9] != null ? (Timestamp) row[9] : null); 
				dto.setReasonForSuspension(row[10] != null ? (String) row[10] : "");
				return dto;
			}).collect(Collectors.toList());

			return new CommonResponseDTO<>(suspendedTenantsReportDto, filterCount);
		} catch (Exception e) {
			throw new WebServiceException("Error retrieving Suspended Tenants: " + e.getMessage());
		}
	}

	@Override
	public CommonResponseDTO<PropertyResportsDTO> getInActivePropertyReport(UserPaymentFilterRequest filterRequest,
			FilterData filterData, Boolean applyPagination) throws WebServiceException {
		try {
			StringBuilder queryBuilder = new StringBuilder(
					"SELECT " +
							"   zpod.pg_owner_name AS ownerFullName, " +
							"   zppd.property_name AS propertyName, " +
							"   zppd.property_contact_number AS propertyContactNumber, " +
							"   zppd.property_pg_email AS propertyEmailAddress, " +
							"   zppd.property_house_area AS propertyAddress " +
							"FROM pgowners.zoy_pg_property_details zppd " +
							"JOIN pgcommon.pg_owner_property_status pops ON zppd.property_id = pops.property_id " +
							"JOIN pgowners.zoy_pg_owner_details zpod ON zppd.pg_owner_id = zpod.pg_owner_id " +
							"WHERE pops.status = false "
					);

			Map<String, Object> parameters = new HashMap<>();

			if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append(" AND pops.updated_timestamp BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP) ");
				parameters.put("fromDate", filterRequest.getFromDate());
				parameters.put("toDate", filterRequest.getToDate());
			}

			if (filterData.getOwnerName() != null && !filterData.getOwnerName().isEmpty()) {
				queryBuilder.append(" AND LOWER(zpod.pg_owner_name) LIKE LOWER(:ownerName) ");
				parameters.put("ownerName", "%" + filterData.getOwnerName() + "%");
			}
			if (filterData.getPgName() != null && !filterData.getPgName().isEmpty()) {
				queryBuilder.append(" AND LOWER(zppd.property_name) LIKE LOWER(:propertyName) ");
				parameters.put("propertyName", "%" + filterData.getPgName() + "%");
			}
			if (filterData.getPropertyContactNum() != null && !filterData.getPropertyContactNum().isEmpty()) {
				queryBuilder.append(" AND LOWER(zppd.property_contact_number) LIKE LOWER(:propertyContactNumber) ");
				parameters.put("propertyContactNumber", "%" + filterData.getPropertyContactNum() + "%");
			}
			if (filterData.getOwnerEmail() != null && !filterData.getOwnerEmail().isEmpty()) {
				queryBuilder.append(" AND LOWER(zppd.property_pg_email) LIKE LOWER(:propertyEmailAddress) ");
				parameters.put("propertyEmailAddress", "%" + filterData.getOwnerEmail() + "%");
			}
			if (filterData.getPgAddress() != null && !filterData.getPgAddress().isEmpty()) {
				queryBuilder.append(" AND LOWER(zppd.property_house_area) LIKE LOWER(:propertyAddress) ");
				parameters.put("propertyAddress", "%" + filterData.getPgAddress() + "%");
			}


			if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty()
					&& filterRequest.getSortActive() != null) {
				String sort = "";
				switch (filterRequest.getSortActive()) {
				case "ownerFullName":
					sort = "ownerFullName";
					break;
				case "propertyName":
					sort = "propertyName";
					break;
				case "propertyContactNumber":
					sort = "propertyContactNumber";
					break;
				case "propertyEmailAddress":
					sort = "propertyEmailAddress";
					break;
				case "propertyAddress":
					sort = "propertyAddress";
					break;
				default:
					sort = "propertyName";
				}
				String sortDirection = filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";
				queryBuilder.append(" ORDER BY ").append(sort).append(" ").append(sortDirection);
			} else {
				queryBuilder.append(" ORDER BY propertyName DESC ");
			}

			Query query = entityManager.createNativeQuery(queryBuilder.toString());
			parameters.forEach(query::setParameter);

			int filterCount = query.getResultList().size();

			if (applyPagination) {
				query.setFirstResult(filterRequest.getPageIndex() * filterRequest.getPageSize());
				query.setMaxResults(filterRequest.getPageSize());
			}

			List<Object[]> results = query.getResultList();
			List<PropertyResportsDTO> inactivePropertiesReportDto = results.stream().map(row -> {
				PropertyResportsDTO dto = new PropertyResportsDTO();
				dto.setOwnerFullName(row[0] != null ? (String) row[0] : "");
				dto.setPropertyName(row[1] != null ? (String) row[1] : "");
				dto.setPropertyContactNumber(row[2] != null ? (String) row[2] : "");
				dto.setPropertyEmailAddress(row[3] != null ? (String) row[3] : "");
				dto.setPropertyAddress(row[4] != null ? (String) row[4] : "");
				return dto;
			}).collect(Collectors.toList());

			return new CommonResponseDTO<>(inactivePropertiesReportDto, filterCount);
		} catch (Exception e) {
			throw new WebServiceException("Error retrieving Inactive Properties: " + e.getMessage());
		}
	}

	@Override
	public CommonResponseDTO<PropertyResportsDTO> getSuspendedPropertyReport(UserPaymentFilterRequest filterRequest,
			FilterData filterData, Boolean applyPagination) throws WebServiceException {
		try {
			StringBuilder queryBuilder = new StringBuilder(
					"SELECT \r\n"
							+ "    zpod.pg_owner_name AS ownerFullName,\r\n"
							+ "    zppd.property_name AS propertyName,\r\n"
							+ "    zppd.property_contact_number AS propertyContactNumber,\r\n"
							+ "    zppd.property_pg_email AS propertyEmailAddress,\r\n"
							+ "    zppd.property_house_area AS propertyAddress,\r\n"
							+ "    pops.updated_timestamp as suspendedDate,\r\n"
							+ "    pops.status_reason as statusReason,\r\n"
							+ "     pops.status_type as statusType\r\n"
							+ "FROM pgowners.zoy_pg_property_details zppd\r\n"
							+ "JOIN pgcommon.pg_owner_property_status pops \r\n"
							+ "    ON zppd.property_id = pops.property_id\r\n"
							+ "JOIN pgowners.zoy_pg_owner_details zpod \r\n"
							+ "    ON zppd.pg_owner_id = zpod.pg_owner_id\r\n"
							+ "WHERE pops.status_type = 'suspend'"
					);

			Map<String, Object> parameters = new HashMap<>();

			if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append(" AND pops.updated_timestamp BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP) ");
				parameters.put("fromDate", filterRequest.getFromDate());
				parameters.put("toDate", filterRequest.getToDate());
			}

			if (filterData.getOwnerName() != null && !filterData.getOwnerName().isEmpty()) {
				queryBuilder.append(" AND LOWER(zpod.pg_owner_name) LIKE LOWER(:ownerName) ");
				parameters.put("ownerName", "%" + filterData.getOwnerName() + "%");
			}
			if (filterData.getPgName() != null && !filterData.getPgName().isEmpty()) {
				queryBuilder.append(" AND LOWER(zppd.property_name) LIKE LOWER(:propertyName) ");
				parameters.put("propertyName", "%" + filterData.getPgName() + "%");
			}
			if (filterData.getPropertyContactNum() != null && !filterData.getPropertyContactNum().isEmpty()) {
				queryBuilder.append(" AND LOWER(zppd.property_contact_number) LIKE LOWER(:propertyContactNumber) ");
				parameters.put("propertyContactNumber", "%" + filterData.getPropertyContactNum() + "%");
			}
			if (filterData.getOwnerEmail() != null && !filterData.getOwnerEmail().isEmpty()) {
				queryBuilder.append(" AND LOWER(zppd.property_pg_email) LIKE LOWER(:propertyEmailAddress) ");
				parameters.put("propertyEmailAddress", "%" + filterData.getOwnerEmail() + "%");
			}
			if (filterData.getPgAddress() != null && !filterData.getPgAddress().isEmpty()) {
				queryBuilder.append(" AND LOWER(zppd.property_house_area) LIKE LOWER(:propertyAddress) ");
				parameters.put("propertyAddress", "%" + filterData.getPgAddress() + "%");
			}

			if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty()
					&& filterRequest.getSortActive() != null) {
				String sort = "";
				switch (filterRequest.getSortActive()) {
				case "ownerFullName":
					sort = "ownerFullName";
					break;
				case "propertyName":
					sort = "propertyName";
					break;
				case "propertyContactNumber":
					sort = "propertyContactNumber";
					break;
				case "propertyEmailAddress":
					sort = "propertyEmailAddress";
					break;
				case "propertyAddress":
					sort = "propertyAddress";
					break;
				case "suspendedDate":
					sort = "suspendedDate";
					break;    
				default:
					sort = "propertyName";
				}
				String sortDirection = filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";
				queryBuilder.append(" ORDER BY ").append(sort).append(" ").append(sortDirection);
			} else {
				queryBuilder.append(" ORDER BY propertyName DESC ");
			}

			Query query = entityManager.createNativeQuery(queryBuilder.toString());
			parameters.forEach(query::setParameter);

			int filterCount = query.getResultList().size();

			if (applyPagination) {
				query.setFirstResult(filterRequest.getPageIndex() * filterRequest.getPageSize());
				query.setMaxResults(filterRequest.getPageSize());
			}

			List<Object[]> results = query.getResultList();
			List<PropertyResportsDTO> suspendedPropertiesReportDto = results.stream().map(row -> {
				PropertyResportsDTO dto = new PropertyResportsDTO();
				dto.setOwnerFullName(row[0] != null ? (String) row[0] : "");
				dto.setPropertyName(row[1] != null ? (String) row[1] : "");
				dto.setPropertyContactNumber(row[2] != null ? (String) row[2] : "");
				dto.setPropertyEmailAddress(row[3] != null ? (String) row[3] : "");
				dto.setPropertyAddress(row[4] != null ? (String) row[4] : "");
				dto.setSuspendedDate(row[5] != null ? (Timestamp) row[5] : null);
				dto.setReasonForSuspension(row[6] != null ? (String) row[6] : "");
				return dto;
			}).collect(Collectors.toList());

			return new CommonResponseDTO<>(suspendedPropertiesReportDto, filterCount);
		} catch (Exception e) {
			throw new WebServiceException("Error retrieving Inactive Properties: " + e.getMessage());
		}
	}

	@Override
	public CommonResponseDTO<RegisterTenantsDTO> getRegisterTenantsReport(UserPaymentFilterRequest filterRequest,
			Boolean applyPagination) throws WebServiceException {
		try {
			StringBuilder queryBuilder = new StringBuilder(
				    "SELECT \r\n"
				    + "    um.user_first_name || ' ' || um.user_last_name AS username,\r\n"
				    + "    um.user_mobile,\r\n"
				    + "    um.user_email,\r\n"
				    + "    um.user_created_at\r\n"
				    + "FROM pgusers.user_master um\r\n"
				    + "WHERE um.user_status = 'Register'\r\n"
				    + "AND NOT EXISTS (\r\n"
				    + "    SELECT 1\r\n"
				    + "    FROM pgusers.user_bookings ub\r\n"
				    + "    WHERE ub.user_id = um.user_id\r\n"
				    + ")"
				);

			Map<String, Object> parameters = new HashMap<>();

			if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append(" AND um.user_created_at BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP) ");
				parameters.put("fromDate", filterRequest.getFromDate());
				parameters.put("toDate", filterRequest.getToDate());
			}
			
			if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty()
					&& filterRequest.getSortActive() != null) {
				String sort = "";
				switch (filterRequest.getSortActive()) {
				case "tenantName":
					sort = "username";
					break;
				case "tenantContactNumber":
					sort = "um.user_mobile";
					break;
				case "tenantEmailAddress":
					sort = "um.user_email";
					break;
				case "registrationDate":
					sort = "um.user_created_at";
					break;
				default:
					sort = "um.user_created_at";
				}
				String sortDirection = filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";
				queryBuilder.append(" ORDER BY ").append(sort).append(" ").append(sortDirection);
			} else {
				queryBuilder.append(" ORDER BY um.user_created_at DESC ");
			}

			Query query = entityManager.createNativeQuery(queryBuilder.toString());
			parameters.forEach(query::setParameter);
			
			int filterCount = query.getResultList().size();

			if (applyPagination) {
				query.setFirstResult(filterRequest.getPageIndex() * filterRequest.getPageSize());
				query.setMaxResults(filterRequest.getPageSize());
			}
		    
			List<Object[]> results = query.getResultList();
			List<RegisterTenantsDTO> registerTenantsReportDto = results.stream().map(row -> {
				RegisterTenantsDTO dto = new RegisterTenantsDTO();
				dto.setTenantName(row[0] != null ? (String) row[0] : "");
				dto.setTenantContactNumber(row[1] != null ? (String) row[1] : "");
				dto.setTenantEmailAddress(row[2] != null ? (String) row[2] : "");
				dto.setRegistrationDate(row[3] != null ? (Timestamp) row[3] : null);
				return dto;
			}).collect(Collectors.toList());

			return new CommonResponseDTO<>(registerTenantsReportDto, filterCount);
		} catch (Exception e) {
			throw new WebServiceException("Error retrieving Register Tenants Details: " + e.getMessage());
		}
	}

	@Override
	public CommonResponseDTO<UserPaymentDTO> getfailureTransactionReport(UserPaymentFilterRequest filterRequest,FilterData filterData,
			Boolean applyPagination) throws WebServiceException {
		try {
			StringBuilder queryBuilder = new StringBuilder(
					"SELECT \r\n"
				            + "    up.user_payment_created_at, \r\n"
				            + "    up.user_payment_payable_amount, \r\n"
				            + "    up.user_payment_result_reason, \r\n"
				            + "    um.user_first_name || ' ' || um.user_last_name  AS username, \r\n"
				            + "    um.user_mobile, \r\n"
				            + "    um.user_email \r\n"
				            + "FROM pgusers.user_payments up \r\n"
				            + "JOIN pgusers.user_master um ON up.user_id = um.user_id \r\n"
				            + "WHERE (LOWER(up.user_payment_payment_status) LIKE LOWER('%failed%') \r\n"
				            + "   OR up.user_payment_payment_status IS NULL)"
				);

			Map<String, Object> parameters = new HashMap<>();

			if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append(" AND up.user_payment_created_at BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP) ");
				parameters.put("fromDate", filterRequest.getFromDate());
				parameters.put("toDate", filterRequest.getToDate());
			}
			
			if (filterData.getTenantName() != null && !filterData.getTenantName().isEmpty()) {
				queryBuilder.append(" AND LOWER(um.user_first_name || ' ' || um.user_last_name) LIKE LOWER(:tenantName)");
				parameters.put("tenantName", "%" + filterData.getTenantName() + "%");
			}
			
			if (filterData.getTenantContactNum() != null && !filterData.getTenantContactNum().isEmpty()) {
				queryBuilder.append(" AND LOWER(um.user_mobile) LIKE LOWER(:tenantContactNum)");
				parameters.put("tenantContactNum", "%" + filterData.getTenantContactNum() + "%");
			}
			
			if (filterData.getTenantEmail() != null && !filterData.getTenantEmail().isEmpty()) {
				queryBuilder.append(" AND LOWER(um.user_email) LIKE LOWER(:tenantEmail)");
				parameters.put("tenantEmail", "%" + filterData.getTenantEmail() + "%");
			}
			
			if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty()
					&& filterRequest.getSortActive() != null) {
				String sort = "";
				switch (filterRequest.getSortActive()) {
				case "customerName":
					sort = "username";
					break;
				case "transactionDate":
					sort = "up.user_payment_created_at";
					break;
				case "totalAmount":
					sort = "up.user_payment_payable_amount";
					break;
				case "failedReason":
					sort = "up.user_payment_result_reason";
					break;
				case "tenantContactNum":
					sort = "um.user_mobile";
					break;
				case "email":
					sort = "um.user_email";
					break;
				default:
					sort = "up.user_payment_created_at";
				}
				String sortDirection = filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";
				queryBuilder.append(" ORDER BY ").append(sort).append(" ").append(sortDirection);
			} else {
				queryBuilder.append(" ORDER BY up.user_payment_created_at DESC ");
			}

			Query query = entityManager.createNativeQuery(queryBuilder.toString());
			parameters.forEach(query::setParameter);
			
			int filterCount = query.getResultList().size();

			if (applyPagination) {
				query.setFirstResult(filterRequest.getPageIndex() * filterRequest.getPageSize());
				query.setMaxResults(filterRequest.getPageSize());
			}
		    
			List<Object[]> results = query.getResultList();
			List<UserPaymentDTO> failureTransactionReportDto = results.stream().map(row -> {
				UserPaymentDTO dto = new UserPaymentDTO();
				dto.setTransactionDate(row[0] != null ? (Timestamp) row[0] : null);
				dto.setTotalAmount((row[1] != null) ? ((Number) row[1]).doubleValue() : 0.0);
				dto.setFailedReason(row[2] != null ? (String) row[2] : "");
				dto.setUserPersonalName(row[3] != null ? (String) row[3] : "");
				dto.setTenantContactNum(row[4] != null ? (String) row[4] : "");
				dto.setEmail(row[5] != null ? (String) row[5] : "");
				return dto;
			}).collect(Collectors.toList());

			return new CommonResponseDTO<>(failureTransactionReportDto, filterCount);
		} catch (Exception e) {
			throw new WebServiceException("Error retrieving Failure Transaction Details: " + e.getMessage());
		}
	}

	@Override
	public CommonResponseDTO<PropertyResportsDTO> getpotentialPropertyReport(UserPaymentFilterRequest filterRequest,
	        FilterData filterData, Boolean applyPagination) throws WebServiceException {
	    try {
	        StringBuilder queryBuilder = new StringBuilder(
	            "SELECT " +
	            "    zpod.pg_owner_name, " +
	            "    zpd.property_name AS propertyName, " +
	            "    zpd.property_contact_number, " +
	            "    zpd.property_pg_email, " +
	            "    zpd.property_house_area AS propertyAddress, " +
	            "    COUNT(DISTINCT zpqbd.selected_bed) AS numberOfBedsOccupied, " +
	            "    COALESCE(SUM(zpqbd.fixed_rent), 0) AS expectedRent, " +
	            "    case when zpd.zoy_variable_share =0 then concat(zpd.zoy_fixed_share,' Rs') "+
	            "    else concat(zpd.zoy_variable_share,' %') end as zoyshare, "+
	            "    case when zpd.zoy_variable_share =0 then (COUNT(DISTINCT zpqbd.selected_bed) * zpd.zoy_fixed_share) "+
	            "    else ROUND((zpd.zoy_variable_share / 100) * COALESCE(SUM(zpqbd.fixed_rent), 0),2) end AS zoyShareAmount, " +
	            "    zpd.property_city " +
	            "FROM pgowners.zoy_pg_owner_booking_details zpqbd " +
	            "JOIN pgowners.zoy_pg_property_details zpd ON zpqbd.property_id = zpd.property_id " +
	            "JOIN pgowners.zoy_pg_owner_details zpod ON zpd.pg_owner_id = zpod.pg_owner_id " +
	            "JOIN pgowners.zoy_pg_bed_details bd ON zpqbd.selected_bed = bd.bed_id " +
	            "JOIN pgusers.user_pg_details upd ON upd.user_pg_booking_id = zpqbd.booking_id " +
	            "WHERE 1=1 "
	        );

	        Map<String, Object> parameters = new HashMap<>();

	        if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
	            queryBuilder.append("AND ( " +
	                "zpqbd.in_date BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP) " +
	                "OR " +
	                "(CASE WHEN upd.user_pg_checkout_date IS NOT NULL THEN upd.user_pg_checkout_date ELSE zpqbd.out_date END) " +
	                "BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP) " +
	                "OR CAST(:fromDate AS TIMESTAMP) BETWEEN zpqbd.in_date AND (CASE WHEN upd.user_pg_checkout_date IS NOT NULL THEN upd.user_pg_checkout_date ELSE zpqbd.out_date END) " +
	                "OR CAST(:toDate AS TIMESTAMP) BETWEEN zpqbd.in_date AND (CASE WHEN upd.user_pg_checkout_date IS NOT NULL THEN upd.user_pg_checkout_date ELSE zpqbd.out_date END) " +
	                ") ");
	            parameters.put("fromDate", filterRequest.getFromDate());
	            parameters.put("toDate", filterRequest.getToDate());
	        }

	        if (filterData.getPgName() != null && !filterData.getPgName().isEmpty()) {
	            queryBuilder.append("AND LOWER(zpd.property_name) LIKE LOWER(:pgName) ");
	            parameters.put("pgName", "%" + filterData.getPgName() + "%");
	        }

	        if (filterRequest.getCityLocation() != null && !filterRequest.getCityLocation().isEmpty()) {
	            queryBuilder.append("AND LOWER(zpd.property_city) LIKE LOWER(:cityLocation) ");
	            parameters.put("cityLocation", "%" + filterRequest.getCityLocation() + "%");
	        }

			if (filterData.getOwnerName() != null && !filterData.getOwnerName().isEmpty()) {
				queryBuilder.append(" AND LOWER(zpod.pg_owner_name) LIKE LOWER(:ownerName) ");
				parameters.put("ownerName", "%" + filterData.getOwnerName() + "%");
			}
			if (filterData.getPgName() != null && !filterData.getPgName().isEmpty()) {
				queryBuilder.append(" AND LOWER(zpd.property_name) LIKE LOWER(:propertyName) ");
				parameters.put("propertyName", "%" + filterData.getPgName() + "%");
			}
			if (filterData.getPropertyContactNum() != null && !filterData.getPropertyContactNum().isEmpty()) {
				queryBuilder.append(" AND LOWER(zpd.property_contact_number) LIKE LOWER(:propertyContactNumber) ");
				parameters.put("propertyContactNumber", "%" + filterData.getPropertyContactNum() + "%");
			}
			if (filterData.getOwnerEmail() != null && !filterData.getOwnerEmail().isEmpty()) {
				queryBuilder.append(" AND LOWER(zpd.property_pg_email) LIKE LOWER(:propertyEmailAddress) ");
				parameters.put("propertyEmailAddress", "%" + filterData.getOwnerEmail() + "%");
			}
			
	        queryBuilder.append("GROUP BY " +
	            "zpod.pg_owner_name, " +
	            "zpd.property_name, " +
	            "zpd.property_contact_number, " +
	            "zpd.property_pg_email, " +
	            "zpd.property_house_area, " +
	            "zpd.zoy_variable_share , "+
	            "zpd.zoy_fixed_share, " +
	            "zpd.property_city ");

	        if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty()
	                && filterRequest.getSortActive() != null) {
	            String sort = "zpod.pg_owner_name";
	            switch (filterRequest.getSortActive()) {
	                case "ownerFullName":
	                    sort = "zpod.pg_owner_name";
	                    break;
	                case "propertyName":
	                    sort = "zpd.property_name";
	                    break;
	                case "propertyContactNumber":
	                    sort = "zpd.property_contact_number";
	                    break;
	                case "propertyEmailAddress":
	                    sort = "zpd.property_pg_email";
	                    break;
	                case "propertyAddress":
	                    sort = "zpd.property_house_area";
	                    break;
	                case "numberOfBeds":
	                    sort = "numberOfBedsOccupied";
	                    break;
	                case "expectedRentPerMonth":
	                    sort = "expectedRent";
	                    break;
	                case "zoyShare":
	                    sort = "zoyshare";
	                    break;
	                case "zoyShareAmount":
	                    sort = "zoyShareAmount";
	                    break;
	                default:
	                    sort = "zpod.pg_owner_name";
	            }

	            String sortDirection = filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";
	            queryBuilder.append(" ORDER BY ").append(sort).append(" ").append(sortDirection);
	        } else {
	            queryBuilder.append(" ORDER BY zpod.pg_owner_name DESC ");
	        }

	        Query query = entityManager.createNativeQuery(queryBuilder.toString());
	        parameters.forEach(query::setParameter);

	        int filterCount = query.getResultList().size();

	        if (applyPagination) {
	            query.setFirstResult(filterRequest.getPageIndex() * filterRequest.getPageSize());
	            query.setMaxResults(filterRequest.getPageSize());
	        }

	        List<Object[]> results = query.getResultList();

	        List<PropertyResportsDTO> potentialPropertyReportDto = results.stream().map(row -> {
	            PropertyResportsDTO dto = new PropertyResportsDTO();
	            dto.setOwnerFullName(row[0] != null ? (String) row[0] : "");
	            dto.setPropertyName(row[1] != null ? (String) row[1] : "");
	            dto.setPropertyContactNumber(row[2] != null ? (String) row[2] : "");
	            dto.setPropertyEmailAddress(row[3] != null ? (String) row[3] : "");
	            dto.setPropertyAddress(row[4] != null ? (String) row[4] : "");
	            dto.setNumberOfBeds(row[5] != null ? ((BigInteger) row[5]).intValue() : 0);
	            dto.setExpectedRentPerMonth(row[6] != null ? ((Number) row[6]).doubleValue() : 0.0);
	            dto.setZoyShare(row[7] != null ? row[7].toString() : "0");
	            dto.setZoyShareAmount(row[8] != null ? row[8].toString() : "0.0");
	            return dto;
	        }).collect(Collectors.toList());

	        return new CommonResponseDTO<>(potentialPropertyReportDto, filterCount);
	    } catch (Exception e) {
	        throw new WebServiceException("Error retrieving Potential Properties Details: " + e.getMessage());
	    }
	}


	@Override
	public CommonResponseDTO<UpcomingPotentialPropertyDTO> getUpcomingPotentialPropertyReport(
			UserPaymentFilterRequest filterRequest, FilterData filterData, Boolean applyPagination)
			throws WebServiceException {
		try {
			StringBuilder queryBuilder = new StringBuilder(
				    "SELECT pom.first_name || ' ' || pom.last_name AS ownerName,\r\n"
				    + "    pom.property_name,\r\n"
				    + "    pom.mobile_no,\r\n"
				    + "    pom.email_id,\r\n"
				    + "    pom.property_door_number || ', ' || \r\n"
				    + "    pom.property_street_name || ', ' || \r\n"
				    + "    pom.property_locality || ', ' || \r\n"
				    + "    pom.property_house_area AS propertyaddress,\r\n"
				    + "    pom.property_city\r\n"
				    + "FROM pgadmin.pg_owner_master pom\r\n"
				    + "LEFT JOIN pgowners.zoy_pg_property_details zppd\r\n"
				    + "    ON pom.zoy_code = zppd.zoy_code\r\n"
				    + "WHERE zppd.zoy_code IS null "
				);
			Map<String, Object> parameters = new HashMap<>();
			
			if (filterData.getPgName() != null && !filterData.getPgName().isEmpty()) {
				queryBuilder.append(" AND LOWER(pom.property_name) LIKE LOWER(:pgName) ");
				parameters.put("pgName", "%" + filterData.getPgName() + "%");
			}
			if (filterRequest.getCityLocation() != null && !filterRequest.getCityLocation().isEmpty()) {
				queryBuilder.append("AND LOWER(pom.property_city) LIKE LOWER('%' || :cityLocation || '%')");
				parameters.put("cityLocation", filterRequest.getCityLocation());
			}

			if (filterData.getOwnerName() != null && !filterData.getOwnerName().isEmpty()) {
				queryBuilder.append(" AND LOWER(pom.first_name || ' ' || pom.last_name) LIKE LOWER(:ownerName) ");
				parameters.put("ownerName", "%" + filterData.getOwnerName() + "%");
			}
			if (filterData.getPgName() != null && !filterData.getPgName().isEmpty()) {
				queryBuilder.append(" AND LOWER(pom.property_name) LIKE LOWER(:propertyName) ");
				parameters.put("propertyName", "%" + filterData.getPgName() + "%");
			}
			if (filterData.getOwnerContactNum() != null && !filterData.getOwnerContactNum().isEmpty()) {
				queryBuilder.append(" AND LOWER(pom.mobile_no) LIKE LOWER(:ownerContactNumber) ");
				parameters.put("ownerContactNumber", "%" + filterData.getOwnerContactNum() + "%");
			}
			if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty()
					&& filterRequest.getSortActive() != null) {
				String sort = "";
				switch (filterRequest.getSortActive()) {
				case "ownerFullName":
					sort = "ownerName";
					break;
				case "propertyName":
					sort = "pom.property_name";
					break;
				case "ownerContactNumber":
					sort = "pom.mobile_no";
					break;
				case "propertyEmailAddress":
					sort = "pom.email_id";
					break;
				case "propertyAddress":
					sort = "propertyaddress";
					break;
				default:
					sort = "ownerName";
				}

				String sortDirection = filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";
				queryBuilder.append(" ORDER BY ").append(sort).append(" ").append(sortDirection);
			} else {
				queryBuilder.append(" ORDER BY ownerName DESC ");
			}
			Query query = entityManager.createNativeQuery(queryBuilder.toString());
			parameters.forEach(query::setParameter);

			int filterCount = query.getResultList().size();

			if (applyPagination) {
				query.setFirstResult(filterRequest.getPageIndex() * filterRequest.getPageSize());
				query.setMaxResults(filterRequest.getPageSize());
			}
			List<Object[]> results = query.getResultList();
			List<UpcomingPotentialPropertyDTO> potentialPropertyReportDto = results.stream().map(row -> {
				UpcomingPotentialPropertyDTO dto = new UpcomingPotentialPropertyDTO();
				dto.setOwnerFullName(row[0] != null ? (String) row[0] : "");
				dto.setPropertyName(row[1] != null ? (String) row[1] : "");
				dto.setOwnerEmailAddress(row[3] != null ? (String) row[3] : "");
				dto.setOwnerContactNumber(row[2] != null ? (String) row[2] : "");
				dto.setPropertyAddress(row[4] != null ? (String) row[4] : "");
				return dto;
			}).collect(Collectors.toList());
			return new CommonResponseDTO<>(potentialPropertyReportDto, filterCount);
		} catch (Exception e) {
			throw new WebServiceException("Error retrieving upcoming Potential Properties Details: " + e.getMessage());
		}
	}

	@Override
	public CommonResponseDTO<PropertyResportsDTO> getNonPotentialPropertyReport(UserPaymentFilterRequest filterRequest,
			FilterData filterData, boolean applyPagination) throws WebServiceException {
		try {
		    
			StringBuilder queryBuilder = new StringBuilder(
				    "SELECT \r\n" +
				    "    zpod.pg_owner_name, \r\n" +
				    "    zpd.property_name, \r\n" +
				    "    zpd.property_contact_number, \r\n" +
				    "    zpd.property_pg_email, \r\n" +
				    "    zpd.property_house_area, \r\n" +
				    "    MAX(zpobd.out_date) AS last_out_date, \r\n" +
				    "    MAX(zpobd.in_date) AS last_in_date \r\n" +
				    "FROM pgowners.zoy_pg_property_details AS zpd \r\n" +
				    "JOIN pgowners.zoy_pg_owner_details AS zpod \r\n" +
				    "    ON zpd.pg_owner_id = zpod.pg_owner_id \r\n" +
				    "LEFT JOIN pgowners.zoy_pg_owner_booking_details AS zpobd \r\n" +
				    "    ON zpd.property_id = zpobd.property_id \r\n" +
				    "LEFT JOIN pgusers.user_bookings AS ub \r\n" +
				    "    ON zpobd.booking_id = ub.user_bookings_id \r\n" +
				    "WHERE zpd.property_id NOT IN ( \r\n" +
				    "    SELECT DISTINCT zpobd.property_id \r\n" +
				    "    FROM pgowners.zoy_pg_owner_booking_details AS zpobd \r\n" +
				    "    JOIN pgusers.user_bookings AS ub \r\n" +
				    "        ON zpobd.booking_id = ub.user_bookings_id \r\n" +
				    "    WHERE ( \r\n" +
				    "        zpobd.in_date BETWEEN :fromDate AND :toDate \r\n" +
				    "        OR zpobd.out_date BETWEEN :fromDate AND :toDate \r\n" +
				    "    ) \r\n" +
				    "    AND ( \r\n" +
				    "        ub.user_bookings_is_cancelled = true \r\n" +
				    "        OR ub.user_bookings_web_check_out = true \r\n" +
				    "    ) \r\n" +
				    ") \r\n"
				);

				Map<String, Object> parameters = new HashMap<>();

				if (filterData.getPgName() != null && !filterData.getPgName().isEmpty()) {
				    queryBuilder.append("AND LOWER(zpd.property_name) LIKE LOWER(:pgName) \r\n");
				    parameters.put("pgName", "%" + filterData.getPgName() + "%");
				}
				
				if (filterRequest.getCityLocation() != null && !filterRequest.getCityLocation().isEmpty()) {
					queryBuilder.append("AND LOWER(zpd.property_city) LIKE LOWER('%' || :cityLocation || '%')");
					parameters.put("cityLocation", filterRequest.getCityLocation());
				}

				queryBuilder.append(
				    "GROUP BY \r\n" +
				    "    zpod.pg_owner_name, \r\n" +
				    "    zpd.property_name, \r\n" +
				    "    zpd.property_contact_number, \r\n" +
				    "    zpd.property_pg_email, \r\n" +
				    "    zpd.property_house_area \r\n"
				);

				if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty()
				        && filterRequest.getSortActive() != null) {
				    String sort = "";
				    switch (filterRequest.getSortActive()) {
				        case "ownerFullName":
				            sort = "zpod.pg_owner_name";
				            break;
				        case "propertyName":
				            sort = "zpd.property_name";
				            break;
				        case "propertyContactNumber":
				            sort = "zpd.property_contact_number";
				            break;
				        case "propertyEmailAddress":
				            sort = "zpd.property_pg_email";
				            break;
				        case "propertyAddress":
				            sort = "zpd.property_house_area";
				            break;
				        case "lastCheckOutDate":
				            sort = "last_out_date";
				            break;
				        case "lastCheckInDate":
				            sort = "last_in_date";
				            break;
				        default:
				            sort = "last_out_date";
				    }
				    String sortDirection = filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";
				    queryBuilder.append(" ORDER BY ").append(sort).append(" ").append(sortDirection);
				} else {
				    queryBuilder.append(" ORDER BY zpod.pg_owner_name DESC ");
				}

				if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				    parameters.put("fromDate", filterRequest.getFromDate());
				    parameters.put("toDate", filterRequest.getToDate());
				}

				Query query = entityManager.createNativeQuery(queryBuilder.toString());
				parameters.forEach(query::setParameter);

				int filterCount = query.getResultList().size();

				if (applyPagination) {
				    query.setFirstResult(filterRequest.getPageIndex() * filterRequest.getPageSize());
				    query.setMaxResults(filterRequest.getPageSize());
				}

				List<Object[]> results = query.getResultList();
				List<PropertyResportsDTO> potentialPropertyReportDto = results.stream().map(row -> {
				    PropertyResportsDTO dto = new PropertyResportsDTO();
				    dto.setOwnerFullName(row[0] != null ? (String) row[0] : "");
				    dto.setPropertyName(row[1] != null ? (String) row[1] : "");
				    dto.setPropertyContactNumber(row[2] != null ? (String) row[2] : "");
				    dto.setPropertyEmailAddress(row[3] != null ? (String) row[3] : "");
				    dto.setPropertyAddress(row[4] != null ? (String) row[4] : "");
				    dto.setLastCheckOutDate(row[5] != null ? (Timestamp) row[5] : null);
				    dto.setLastCheckInDate(row[6] != null ? (Timestamp) row[6] : null);
				    return dto;
				}).collect(Collectors.toList());

				return new CommonResponseDTO<>(potentialPropertyReportDto, filterCount);
		} catch (Exception e) {
			throw new WebServiceException("Error retrieving  Non Potential Properties Details: " + e.getMessage());
		}
	}

	@Override
	public CommonResponseDTO<RegisterLeadDetails> getRegisterLeadDetails(UserPaymentFilterRequest filterRequest,
			FilterData filterData, boolean applyPagination,boolean isSupportUser) throws WebServiceException {
		try {

			StringBuilder queryBuilder = new StringBuilder(
					"SELECT \r\n" +
							"    register_id AS \"RegisterNumber\", \r\n" +
							"    CONCAT(firstname, ' ', lastname) AS \"Name\", \r\n" +
							"    inquired_for AS \"InquiredFor\", \r\n" +
							"    ts AS \"Date\", \r\n" +
							"    status AS \"Status\", \r\n" +
							"    mobile AS \"mobile\", \r\n" +
							"    property_name AS \"propertyName\", \r\n" +
							"    state AS \"state\", \r\n" +
							"    city AS \"city\", \r\n" +
							"    email AS \"ownerEmail\", \r\n" +
							"    assign_to_name AS \"assignedTo\", \r\n" +
							"    description AS \"description\" \r\n" +
							"FROM \r\n" +
							"    pgowners.zoy_pg_registered_owner_details \r\n" +
							"WHERE 1=1 \r\n"
					);

			Map<String, Object> parameters = new HashMap<>();

			if (filterRequest.getCityLocation() != null && !filterRequest.getCityLocation().isEmpty()) {
				queryBuilder.append("AND LOWER(city) LIKE LOWER('%' || :cityLocation || '%') \r\n");
				parameters.put("cityLocation", filterRequest.getCityLocation());
			}

			if (filterData.getState() != null && !filterData.getState().isEmpty()) {
				queryBuilder.append("AND LOWER(state) LIKE LOWER('%' || :state || '%') \r\n");
				parameters.put("state", filterData.getState());
			}

			if (filterData.getInquiryNumber()!= null && !filterData.getInquiryNumber().isEmpty()) {
				queryBuilder.append("AND LOWER(register_id) LIKE LOWER('%' || :inquiryNumber || '%') \r\n");
				parameters.put("inquiryNumber", filterData.getInquiryNumber());
			}

			if (filterData.getInquiredBy() != null && !filterData.getInquiredBy().isEmpty()) {
				if (filterData.getInquiredBy().equalsIgnoreCase("Owner")) {
					queryBuilder.append("AND LOWER(inquired_for) = LOWER('Partner Inquiry') \r\n");
				} else if (filterData.getInquiredBy().equalsIgnoreCase("Tenant")) {
					queryBuilder.append("AND LOWER(inquired_for) = LOWER('PG Accommodation') \r\n");
				}
			}

			if (filterData.getInquieredFor() != null && !filterData.getInquieredFor().isEmpty()) {
				if (filterData.getInquieredFor().equalsIgnoreCase("Partner Inquiry")) {
					queryBuilder.append("AND LOWER(inquired_for) = LOWER('Partner Inquiry') \r\n");
				} else if (filterData.getInquieredFor().equalsIgnoreCase("PG Accommodation")) {
					queryBuilder.append("AND LOWER(inquired_for) = LOWER('PG Accommodation') \r\n");
				}
			}

			if (filterData.getStatus() != null && !filterData.getStatus().isEmpty()) {
				queryBuilder.append("AND LOWER(status) = LOWER(:status) \r\n");
				parameters.put("status", filterData.getStatus());
			}
			
			if (isSupportUser) {
	            String assignedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
	            queryBuilder.append("AND LOWER(assign_to_email) = LOWER(:assignedEmail) \r\n");
	            parameters.put("assignedEmail", assignedEmail);
	        }
			
			if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty()
					&& filterRequest.getSortActive() != null) {
				String sort = "";
				switch (filterRequest.getSortActive()) {
				case "inquiryNumber":
					sort = "register_id";
					break;
				case "name":
					sort = "CONCAT(firstname, ' ', lastname)";
					break;
				case "inquiredFor":
					sort = "inquired_for";
					break;
				case "registeredDate":
					sort = "ts";
					break;
				case "asignedTo":
					sort = "assign_to_name";
					break;
				case "status":
					sort = "status";
					break;
				default:
					sort = "register_id";
				}
				String sortDirection = filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";
				queryBuilder.append(" ORDER BY ").append(sort).append(" ").append(sortDirection);
			} else {
				queryBuilder.append(" ORDER BY register_id DESC ");
			}

			Query query = entityManager.createNativeQuery(queryBuilder.toString());
			parameters.forEach(query::setParameter);

			int filterCount = query.getResultList().size();

			if (applyPagination) {
				query.setFirstResult(filterRequest.getPageIndex() * filterRequest.getPageSize());
				query.setMaxResults(filterRequest.getPageSize());
			}

			List<Object[]> results = query.getResultList();
			List<RegisterLeadDetails> registerLeadDetails = results.stream().map(row -> {
			    RegisterLeadDetails dto = new RegisterLeadDetails();
			    dto.setInquiryNumber(row[0] != null ? (String) row[0] : "");
			    dto.setName(row[1] != null ? (String) row[1] : "");
			    dto.setInquiredFor(row[2] != null ? (String) row[2] : "");
			    dto.setRegisteredDate(row[3] != null ? (Timestamp) row[3] : null);
			    dto.setStatus(row[4] != null ? (String) row[4] : "");
			    dto.setPhoneNumber(row[5] != null ? (String) row[5] : "");
			    dto.setPropertyName(row[6] != null ? (String) row[6] : "");
			    dto.setState(row[7] != null ? (String) row[7] : "");
			    dto.setCity(row[8] != null ? (String) row[8] : "");
			    dto.setOwnerEmail(row[9] != null ? (String) row[9] : "");
			    dto.setAsignedTo(row[10] != null ? (String) row[10] : "");
			    dto.setDescription(row[11] != null ? (String) row[11] : "");
			    return dto;
			}).collect(Collectors.toList());
			return new CommonResponseDTO<>(registerLeadDetails, filterCount);
		} catch (Exception e) {
			throw new WebServiceException("Error retrieving Register Lead Details: " + e.getMessage());
		}
	}
	
	@Override
	public CommonResponseDTO<ZoyShareReportDTO> getZoyShareReport(UserPaymentFilterRequest filterRequest,
			FilterData filterData, Boolean applyPagination) throws WebServiceException {
		try {
			StringBuilder queryBuilder = new StringBuilder(
				"  SELECT\r\n"
				+ "    zpossu.pg_owner_settlement_split_up_id,\r\n"
				+ "    zposs.pg_owner_settlement_id,\r\n"
				+ "    up.user_payment_created_at AS TransactionDate,\r\n"
				+ "    up.user_payment_result_invoice_id AS TenantInvoiceNo,\r\n"
				+ "    prop.property_name AS PGName,\r\n"
				+ "    CONCAT(um.user_first_name, '', um.user_last_name) AS TenantName,\r\n"
				+ "    sm.share_type AS SharingType,\r\n"
				+ "    bd.bed_name AS BedNo,\r\n"
				+ "    up.user_payment_zoy_payment_mode AS ModeOfPayment,\r\n"
				+ "    ud.user_due_amount AS AmountPaid,\r\n"
				+ "    CASE \r\n"
				+ "        WHEN due_m.due_name = 'Rent' THEN\r\n"
				+ "            CASE \r\n"
				+ "                WHEN prop.zoy_variable_share = 0 THEN CONCAT(prop.zoy_fixed_share, ' Rs')\r\n"
				+ "                ELSE CONCAT(prop.zoy_variable_share, ' %') \r\n"
				+ "            END\r\n"
				+ "        ELSE NULL\r\n"
				+ "    END AS zoyshare,\r\n"
				+ "    CASE \r\n"
				+ "        WHEN due_m.due_name = 'Rent' AND prop.zoy_variable_share = 0 \r\n"
				+ "            THEN (COUNT(DISTINCT zpqbd.selected_bed) * prop.zoy_fixed_share)\r\n"
				+ "        WHEN due_m.due_name = 'Rent' AND prop.zoy_variable_share > 0 \r\n"
				+ "            THEN ROUND((prop.zoy_variable_share / 100.0) * COALESCE(SUM(ud.user_due_amount), 0), 2)\r\n"
				+ "        WHEN due_m.due_name IN ('Ekyc Charges', 'Document Charges') \r\n"
				+ "            THEN COALESCE(SUM(ud.user_due_amount), 0)\r\n"
				+ "        ELSE 0\r\n"
				+ "    END AS zoyShareAmount,\r\n"
				+ "    due_m.due_name as dueType,\r\n"
				+ "    prop.property_city AS PropertyCity\r\n"
				+ "FROM pgowners.zoy_pg_owner_settlement_split_up zpossu\r\n"
				+ "LEFT JOIN pgowners.zoy_pg_owner_settlement_status zposs \r\n"
				+ "    ON zposs.pg_owner_settlement_id = zpossu.pg_owner_settlement_id\r\n"
				+ "LEFT JOIN pgusers.user_payments up \r\n"
				+ "    ON up.user_payment_id = zpossu.payment_id\r\n"
				+ "LEFT JOIN pgusers.user_payment_due upd \r\n"
				+ "    ON up.user_payment_id = upd.user_payment_id\r\n"
				+ "LEFT JOIN pgusers.user_dues ud \r\n"
				+ "    ON ud.user_money_due_id = upd.user_money_due_id \r\n"
				+ "LEFT JOIN pgowners.zoy_pg_due_type_master ad \r\n"
				+ "    ON ud.user_money_due_type = ad.due_id \r\n"
				+ "LEFT JOIN pgowners.zoy_pg_due_master due_m \r\n"
				+ "    ON due_m.due_type_id = ad.due_type   \r\n"
				+ "LEFT JOIN pgowners.zoy_pg_property_details prop \r\n"
				+ "    ON zposs.property_id = prop.property_id\r\n"
				+ "LEFT JOIN pgowners.zoy_pg_owner_booking_details zpdb \r\n"
				+ "    ON up.user_payment_booking_id = zpdb.booking_id\r\n"
				+ "LEFT JOIN pgowners.zoy_pg_bed_details bd \r\n"
				+ "    ON zpdb.selected_bed = bd.bed_id\r\n"
				+ "LEFT JOIN pgusers.user_master um \r\n"
				+ "    ON up.user_id = um.user_id\r\n"
				+ "LEFT JOIN pgowners.zoy_pg_share_master sm \r\n"
				+ "    ON zpdb.share = sm.share_id\r\n"
				+ "LEFT JOIN pgowners.zoy_pg_owner_booking_details zpqbd \r\n"
				+ "    ON zpdb.booking_id = zpqbd.booking_id\r\n"
				+ "WHERE due_m.due_name IN ('Rent', 'Ekyc Charges', 'Document Charges')  "
			);
 
			Map<String, Object> parameters = new HashMap<>();
 
			if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append(" AND up.user_payment_created_at BETWEEN :fromDate AND :toDate ");
				parameters.put("fromDate", filterRequest.getFromDate());
				parameters.put("toDate", filterRequest.getToDate());
			}
			if (filterData.getTenantName() != null && !filterData.getTenantName().isEmpty()) {
				queryBuilder.append(" AND LOWER(CONCAT(um.user_first_name, um.user_last_name)) LIKE LOWER(:tenantName) ");
				parameters.put("tenantName", "%" + filterData.getTenantName() + "%");
			}
			if (filterData.getInvoiceNo() != null && !filterData.getInvoiceNo().isEmpty()) {
				queryBuilder.append(" AND LOWER(up.user_payment_result_invoice_id) LIKE LOWER(:invoiceNo) ");
				parameters.put("invoiceNo", "%" + filterData.getInvoiceNo() + "%");
			}
			if (filterData.getPgName() != null && !filterData.getPgName().isEmpty()) {
				queryBuilder.append(" AND LOWER(prop.property_name) LIKE LOWER(:pgName) ");
				parameters.put("pgName", "%" + filterData.getPgName() + "%");
			}
			if (filterRequest.getCityLocation() != null && !filterRequest.getCityLocation().isEmpty()) {
				queryBuilder.append(" AND LOWER(prop.property_city) LIKE LOWER(:cityLocation) ");
				parameters.put("cityLocation", "%" + filterRequest.getCityLocation() + "%");
			}
			if (filterData.getBedNumber() != null && !filterData.getBedNumber().isEmpty()) {
				queryBuilder.append(" AND LOWER(bd.bed_name) LIKE LOWER(:bedNumber) ");
				parameters.put("bedNumber", "%" + filterData.getBedNumber() + "%");
			}
			if (filterData.getModeOfPayment() != null && !filterData.getModeOfPayment().isEmpty()) {
				queryBuilder.append(" AND LOWER(up.user_payment_zoy_payment_mode) LIKE LOWER(:modeOfPayment) ");
				parameters.put("modeOfPayment", "%" + filterData.getModeOfPayment() + "%");
			}
 
			queryBuilder.append(
				" GROUP BY \r\n"
				+ "    zpossu.pg_owner_settlement_split_up_id,\r\n"
				+ "    zposs.pg_owner_settlement_id,\r\n"
				+ "    up.user_payment_created_at,\r\n"
				+ "    up.user_payment_result_invoice_id,\r\n"
				+ "    prop.property_name,\r\n"
				+ "    um.user_first_name,\r\n"
				+ "    um.user_last_name,\r\n"
				+ "    sm.share_type,\r\n"
				+ "    bd.bed_name,\r\n"
				+ "    up.user_payment_zoy_payment_mode,\r\n"
				+ "    ud.user_due_amount,\r\n"
				+ "    prop.zoy_variable_share,\r\n"
				+ "    prop.zoy_fixed_share,\r\n"
				+ "    prop.property_city,\r\n"
				+ "    due_m.due_name"
			);
 
			if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty()
              && filterRequest.getSortActive() != null) {
 
				String sort;
				
				switch (filterRequest.getSortActive()) {
					case "tenantName":
						sort = "um.user_first_name || '' || um.user_last_name";
						break;
					case "pgName":
						sort = "prop.property_name";
						break;
					case "transactionDate":
						sort = "up.user_payment_created_at";
						break;
					case "invoiceNumber":
						sort = "up.user_payment_result_invoice_id";
						break;
					case "sharingType":
						sort = "sm.share_type";
						break;
					case "bedNumber":
						sort = "bd.bed_name";
						break;
					case "paymentMode":
						sort = "up.user_payment_zoy_payment_mode";
						break;
					case "amountPaid":
						sort = "AmountPaid";
						break;
					case "zoyShare":
						sort = "zoyshare";
						break;
					case "zoyShareAmount":
						sort = "zoyShareAmount";
						break;
					case "amountType":
						sort= "dueType";
						break;
					default:
						sort = "um.user_first_name || '' || um.user_last_name";
				}
				String sortDirection = filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";
				queryBuilder.append(" ORDER BY ").append(sort).append(" ").append(sortDirection);
			} else {
				queryBuilder.append(" ORDER BY um.user_first_name || '' || um.user_last_name DESC ");
			}
 
			Query query = entityManager.createNativeQuery(queryBuilder.toString());
			parameters.forEach(query::setParameter);
 
			int filterCount = query.getResultList().size();
 
			if (applyPagination) {
				query.setFirstResult(filterRequest.getPageIndex() * filterRequest.getPageSize());
				query.setMaxResults(filterRequest.getPageSize());
			}
 
			List<Object[]> results = query.getResultList();
 
			List<ZoyShareReportDTO> dtoList = results.stream().map(row -> {
				ZoyShareReportDTO dto = new ZoyShareReportDTO();
				dto.setTransactionDate(row[2] != null ? (Timestamp) row[2] : null);
				dto.setInvoiceNumber(row[3] != null ? row[3].toString() : "");
				dto.setPgName(row[4] != null ? row[4].toString() : "");
				dto.setTenantName(row[5] != null ? row[5].toString() : "");
				dto.setSharingType(row[6] != null ? row[6].toString() : "");
				dto.setBedNumber(row[7] != null ? row[7].toString() : "");
				dto.setPaymentMode(row[8] != null ? row[8].toString() : "");
				dto.setAmountPaid(row[9] != null ?(BigDecimal) row[9] : BigDecimal.ZERO);
				dto.setZoyShare(row[10] != null ? row[10].toString() : "0");
				dto.setZoyShareAmount(row[11] != null ?(BigDecimal) row[11] : BigDecimal.ZERO);
				dto.setAmountType(row[12] != null ? row[12].toString() : "");
				return dto;
			}).collect(Collectors.toList());
 
			return new CommonResponseDTO<>(dtoList, filterCount);
		} catch (Exception e) {
			throw new WebServiceException("Error retrieving Zoy Share Report: " + e.getMessage());
		}
	}

}