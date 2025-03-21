package com.integration.zoy.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import org.springframework.stereotype.Service;

import com.integration.zoy.exception.WebServiceException;
import com.integration.zoy.exception.ZoyAdminApplicationException;
import com.integration.zoy.model.FilterData;
import com.integration.zoy.model.TenantResportsDTO;
import com.integration.zoy.repository.UserPaymentDueRepository;
import com.integration.zoy.repository.UserPaymentRepository;
import com.integration.zoy.repository.ZoyPgPropertyDetailsRepository;
import com.integration.zoy.utils.CommonResponseDTO;
import com.integration.zoy.utils.ConsilidatedFinanceDetails;
import com.integration.zoy.utils.PropertyResportsDTO;
import com.integration.zoy.utils.RatingsAndReviewsReport;
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
	private PdfGenerateService pdfGenerateService;

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


	@Override
	public CommonResponseDTO<UserPaymentDTO> getUserPaymentDetails(UserPaymentFilterRequest filterRequest,FilterData filterData,Boolean applyPagination) throws WebServiceException {
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
				            + " ud.user_personal_name, \r\n"
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
				queryBuilder.append(" AND LOWER(ud.user_personal_name) LIKE LOWER(:tenantName)");
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
					sort = "ud.user_personal_name";
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
				queryBuilder.append(" GROUP BY \r\n"
					    + " up.user_payment_created_at, \r\n"
					    + " up.user_payment_result_invoice_id, \r\n"
					    + " CASE \r\n"
					    + " WHEN LOWER(up.user_payment_zoy_payment_mode) = 'offline' THEN 'Paid-Cash' \r\n"
					    + " ELSE up.user_payment_payment_status \r\n"
					    + " END, \r\n"
					    + " up.user_payment_payable_amount, \r\n"
					    + " up.user_payment_gst, \r\n"
					    + " ud.user_personal_name, \r\n"
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
				dto.setTransactionDate((Timestamp) row[0]);
				dto.setTransactionNumber(row[1] != null ? (String) row[1] : "");
				dto.setTransactionStatus(row[2] != null ? (String) row[2] : "");
				double payableAmount = row[3] != null ? ((BigDecimal) row[3]).doubleValue() : 0.0;
				double gst = row[4] != null ? ((BigDecimal) row[4]).doubleValue() : 0.0;
				double dueamount=payableAmount-gst;
				dto.setDueAmount(dueamount != 0.0 ? dueamount : null);
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
			StringBuilder queryBuilder = new StringBuilder(
					"SELECT DISTINCT ON (up.user_payment_id) " +
							"o.pg_owner_name AS ownerName, " +
							"pd.property_name AS pgName, " +
							"up.user_payment_payable_amount AS totalAmountFromTenants, " +
							"up.user_payment_timestamp AS transactionDate, " +
							"up.user_payment_result_invoice_id AS transactionNumber, " +
							"up.user_payment_payment_status AS paymentStatus, " +
							"pd.property_city AS city, " +
							"pd.property_house_area AS propertyAddress, " +
							"o.pg_owner_email AS ownerEmail " +
							"FROM pgusers.user_payments up " +
							"JOIN pgusers.user_pg_details pgd ON up.user_id = pgd.user_id " +
							"JOIN pgusers.user_details ud ON up.user_id = ud.user_id " +
							"JOIN pgowners.zoy_pg_owner_booking_details bkd ON up.user_id = bkd.tenant_id " +
							"AND up.user_payment_booking_id = bkd.booking_id " +
							"AND pgd.user_pg_property_id = bkd.property_id " +
							"JOIN pgowners.zoy_pg_bed_details bd ON bkd.selected_bed = bd.bed_id " +
							"JOIN pgowners.zoy_pg_property_details pd ON bkd.property_id = pd.property_id " +
							"JOIN pgowners.zoy_pg_owner_details o ON pd.pg_owner_id = o.pg_owner_id " +
							"WHERE 1=1 "
					);


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
				queryBuilder.append("AND LOWER(o.pg_owner_email) LIKE LOWER(:ownerName)");
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
				dto.setAmountPaidToOwner(BigDecimal.valueOf(0).doubleValue());
				dto.setZoyShare(BigDecimal.valueOf(0).doubleValue());
				dto.setOwnerApprovalStatus(null);
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
				reportData = getUserPaymentDetails(filterRequest, filterData,applyPagination);
				dataListWrapper=this.generateUserTransactionDataList(reportData,filterRequest);
				templatePath = "templates/userTransactionReport.docx";
				break;
			case "userPaymentGstReport":
				reportData = getUserPaymentDetails(filterRequest, filterData,applyPagination);
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
			case "SuspendedPropertiesReport":
				reportData = getSuspendedPropertyReport(filterRequest, filterData,applyPagination);
				dataListWrapper=this.generateSuspendedPropertiesReport(reportData,filterRequest);
				templatePath ="templates/suspendedPropertiesReport.docx";
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

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

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
			data.put("txnStatus", userPayment.getTransactionStatus());
			data.put("tenantName", userPayment.getUserPersonalName());
			data.put("tenantMobile", userPayment.getTenantContactNum());
			data.put("pgName", userPayment.getUserPgPropertyName());
			data.put("pgAddress", userPayment.getPropertyHouseArea());
			data.put("totalAmount", userPayment.getTotalAmount());
			data.put("gstAmount", userPayment.getGstAmount());
			data.put("dueAmount", userPayment.getDueAmount());
			data.put("modeOfPayment", userPayment.getPaymentMode());

			Timestamp fromDateTimestamp = filterRequest.getFromDate();
			Timestamp toDateTimestamp = filterRequest.getToDate();

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

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

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

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

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

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

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

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
			data.put("refundableAmount", tenantRefund.getRefundableAmount());
			data.put("amountPaid", tenantRefund.getAmountPaid());
			data.put("paymentDate", tuService.formatTimestamp(tenantRefund.getPaymentDate().toInstant()) != null ? tuService.formatTimestamp(tenantRefund.getPaymentDate().toInstant()) : "");
			data.put("invoiceNo", tenantRefund.getTransactionNumber() != null ? tenantRefund.getTransactionNumber() : "");
			data.put("status", tenantRefund.getPaymentStatus() != null ? tenantRefund.getPaymentStatus() : "");

			// Common fields
			Timestamp fromDateTimestamp = filterRequest.getFromDate();
			Timestamp toDateTimestamp = filterRequest.getToDate();

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

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
			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

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

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

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
			data.put("checkedOutDate", tuService.formatTimestamp(tenantReport.getCheckedOutDate().toInstant()) != null ? tuService.formatTimestamp(tenantReport.getCheckedOutDate().toInstant()) : "");

			// Common fields
			Timestamp fromDateTimestamp = filterRequest.getFromDate();
			Timestamp toDateTimestamp = filterRequest.getToDate();

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			data.put("fromDate", fromDate.format(formatter));
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

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

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

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			data.put("fromDate", fromDate.format(formatter));
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

			data.put("transactionDate", tuService.formatTimestamp(failedTransactioData.getTransactionDate().toInstant()) != null ? failedTransactioData.getTransactionDate() : "");
			data.put("tenantName", failedTransactioData.getUserPersonalName() != null ? failedTransactioData.getUserPersonalName() : "");
			data.put("contactNumber", failedTransactioData.getTenantContactNum() != null ? failedTransactioData.getTenantContactNum() : "");
			data.put("email", failedTransactioData.getEmail() != null ? failedTransactioData.getEmail() : "");
			data.put("amount", failedTransactioData.getTotalAmount());
			data.put("reason", failedTransactioData.getFailedReason() != null ? failedTransactioData.getFailedReason() : "");
			// Common fields
			Timestamp fromDateTimestamp = filterRequest.getFromDate();
			Timestamp toDateTimestamp = filterRequest.getToDate();

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

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

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

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

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

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

			// Common fields
			Timestamp fromDateTimestamp = filterRequest.getFromDate();
			Timestamp toDateTimestamp = filterRequest.getToDate();

			LocalDate fromDate = fromDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate toDate = toDateTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

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
			StringBuilder queryBuilder = new StringBuilder("SELECT "
					+ "um.user_first_name || ' ' || um.user_last_name AS username, "
					+ "um.user_mobile AS usermobile_number, "
					+ "zppd.property_name AS PG_name, "
					+ "zppd.property_house_area AS Pg_address, "
					+ "urd.booking_id AS booking_ID, "
					+ "ub.user_cancellation_reason AS refund_title, "
					+ "urd.refund_amount AS refund_amount, "
					+ "CASE \r\n"
					+ " WHEN urd.refund_process_status = TRUE THEN 'Completed' \r\n"
					+ " ELSE 'Processing' \r\n"
					+ " END AS Status, "
					+ "urd.refund_created_timestamp, "
					+ "zppd.property_city \r\n"
					+ "FROM pgcommon.user_refund_details urd "
					+ "JOIN pgusers.user_master um ON urd.user_id = um.user_id "
					+ "JOIN pgowners.zoy_pg_property_details zppd ON urd.property_id = zppd.property_id "
					+ "LEFT JOIN pgusers.user_bookings ub ON urd.booking_id = ub.user_bookings_id "
					+ "WHERE 1 = 1 ");

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
							+ "zppd.property_city \r\n"
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
				default:
					sort = "rr.timestamp";
				}
				queryBuilder.append(" group by rr.rating_id,rr.partner_id,rr.written_review,rr.overall_rating,rr.customer_id,rr.property_id, "+
						"rr.timestamp,zpbd.bed_name,zppd.property_id,um.user_id,ud.user_profile_image,um.user_mobile " );


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
				model.setCustomerImage(row[14] != null ? row[14].toString() : "");
				model.setCustomerMobileNo(row[15] != null ? row[15].toString() : "");
				model.setCleanliness(row[16] != null ? row[16].toString() : "");
				model.setAmenities(row[17] != null ? row[17].toString() : "");
				model.setValueForMoney(row[18] != null ? row[18].toString() : "");
				model.setMaintenance(row[19] != null ? row[19].toString() : "");
				model.setAccommodation(row[20] != null ? row[20].toString() : "");
				String replies=row[0] != null ? row[0].toString() : "";

				List<String[]> reviewReplies = zoyPgPropertyDetailsRepository.findAllReviewsReplies(replies);

				model.setThreads(reviewReplies != null ? reviewReplies.stream()
						.map(parts -> new RatingsAndReviewsReport.ReviewReplies(parts[0], parts[1], parts[2], parts[3],
								parts[4], parts[5], parts[6], parts[7] != null ? Timestamp.valueOf(parts[7]) : null,
										parts[8] != null ? Boolean.valueOf(parts[8]) : false,
												parts[9] != null ? Boolean.valueOf(parts[9]) : false,
														parts[10] != null ? parts[10] : "", parts[11] != null ? parts[11] : ""))
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
				case "bookedProperyName":
					sort = "zpd.property_name";
					break;
				case "propertAddress":
					sort = "zpd.property_house_area";
					break;
				case "roomNumber":
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
			StringBuilder queryBuilder = new StringBuilder("SELECT \r\n"
					+ "                 um.user_first_name || ' ' || um.user_last_name AS username,\r\n"
					+ "                 um.user_mobile AS mobileNumber,\r\n"
					+ "                 um.user_email AS emailId,\r\n"
					+ "                 zpd.property_name AS propertyName,\r\n"
					+ "                 zpd.property_house_area AS propertyAddress,\r\n"
					+ "                 bd.bed_name AS bedName,\r\n"
					+ "                 zpqbd.in_date AS inDate, \r\n"
					+ "                 zpqbd.out_date AS outDate,\r\n"
					+ "                 zpd.property_city\r\n"
					+ "					FROM pgusers.user_master um\r\n"
					+ "					JOIN pgowners.zoy_pg_owner_booking_details zpqbd \r\n"
					+ "					    ON um.user_id = zpqbd.tenant_id \r\n"
					+ "					join pgusers.user_bookings ub\r\n"
					+ "					    on zpqbd.booking_id = ub.user_bookings_id \r\n"
					+ "					JOIN pgowners.zoy_pg_property_details zpd \r\n"
					+ "					    ON zpqbd.property_id = zpd.property_id \r\n"
					+ "					JOIN pgowners.zoy_pg_bed_details bd  \r\n"
					+ "					    ON zpqbd.selected_bed = bd.bed_id \r\n"
					+ "					WHERE 1=1 ");

			Map<String, Object> parameters = new HashMap<>();

			if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append(" AND (zpqbd.in_date between CAST(:fromDate AS TIMESTAMP)  AND CAST(:toDate AS TIMESTAMP) \r\n"
						+ "						or zpqbd.out_date between CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP) \r\n"
						+ "						or CAST(:fromDate AS TIMESTAMP) between zpqbd.in_date and zpqbd.out_date\r\n"
						+ "						or CAST(:toDate AS TIMESTAMP) between  zpqbd.in_date and zpqbd.out_date)\r\n"
						+ "					 AND (ub.user_bookings_web_check_in = true OR ub.user_bookings_web_check_out = true)");
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
				case "roomNumber":
					sort = "bd.bed_name";
					break;
				case "checkInDate":
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
					"SELECT \r\n"
					+ "    um.user_first_name || ' ' || um.user_last_name AS username,\r\n"
					+ "    um.user_mobile AS mobileNumber,\r\n"
					+ "    um.user_email AS emailId,\r\n"
					+ "    zpd.property_name AS propertyName,\r\n"
					+ "    zpd.property_house_area AS propertyAddress,\r\n"
					+ "    bd.bed_name AS bedName,\r\n"
					+ "    zpqbd.in_date AS inDate, \r\n"
					+ "    zpqbd.out_date AS outDate,\r\n"
					+ "    zpd.property_city\r\n"
					+ "FROM pgusers.user_master um\r\n"
					+ "JOIN pgowners.zoy_pg_owner_booking_details zpqbd \r\n"
					+ "    ON um.user_id = zpqbd.tenant_id \r\n"
					+ "JOIN pgusers.user_bookings ub\r\n"
					+ "    ON zpqbd.booking_id = ub.user_bookings_id \r\n"
					+ "JOIN pgowners.zoy_pg_property_details zpd \r\n"
					+ "    ON zpqbd.property_id = zpd.property_id \r\n"
					+ "JOIN pgowners.zoy_pg_bed_details bd  \r\n"
					+ "    ON zpqbd.selected_bed = bd.bed_id\r\n"
					+ "WHERE 1=1 ");

			Map<String, Object> parameters = new HashMap<>();

			if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append("and\r\n"
						+ "    NOT (\r\n"
						+ "        zpqbd.in_date BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP)\r\n"
						+ "        OR zpqbd.out_date BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP)\r\n"
						+ "        OR CAST(:fromDate AS TIMESTAMP) BETWEEN zpqbd.in_date AND zpqbd.out_date\r\n"
						+ "        OR CAST(:toDate AS TIMESTAMP) BETWEEN zpqbd.in_date AND zpqbd.out_date\r\n"
						+ "    )\r\n"
						+ "    AND (ub.user_bookings_web_check_in = true OR ub.user_bookings_web_check_out = true)");
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
				case "checkInDate":
					sort="inDate";
					break;
				case "checkedOutDate":
					sort = "outDate";
					break;
				default:
					sort = "outDate";
				}
				String sortDirection = filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";
				queryBuilder.append(" ORDER BY ").append(sort).append(" ").append(sortDirection);
			} else {
				queryBuilder.append(" ORDER BY outDate DESC ");
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
}