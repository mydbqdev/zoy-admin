package com.integration.zoy.service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
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
	NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));
	String rupeeSymbol = "\u20B9";
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

	@Value("${zoy.admin.logo}")
	private String zoyLogoPath;
	
	@Value("${zoy.admin.watermark}")
    private String watermarkImagePath;


	@Override
	public CommonResponseDTO<UserPaymentDTO> getUserPaymentDetails(UserPaymentFilterRequest filterRequest,FilterData filterData,Boolean applyPagination) throws WebServiceException {
		try{
			StringBuilder queryBuilder = new StringBuilder(
				    "SELECT " +
				        "up.user_payment_timestamp, " +
				        "up.user_payment_bank_transaction_id, " +
				        "CASE " +
				        "    WHEN LOWER(up.user_payment_zoy_payment_mode) = 'offline' THEN 'Paid-Cash' " +
				        "    ELSE up.user_payment_payment_status " +
				        "END AS user_payment_payment_status, " +
				        "up.user_payment_payable_amount, " +
				        "up.user_payment_gst, " +
				        "ud.user_personal_name, " +
				        "pgt.property_name AS user_pg_propertyname, " +
				        "bd.bed_name, " +
				        "up.user_payment_zoy_payment_type, " +
				        "CASE " +
				        "    WHEN LOWER(up.user_payment_zoy_payment_mode) = 'offline' THEN 'Cash' " +
				        "    ELSE up.user_payment_result_method " +
				        "END AS user_payment_result_method, " +
				        "pgt.property_city, " +
				        "pgt.property_house_area, " + 
				        "ud.user_personal_phone_num " + 
				        "FROM pgusers.user_payments up " +
				        "LEFT JOIN pgusers.user_details ud ON up.user_id = ud.user_id " +
				        "LEFT JOIN pgowners.zoy_pg_owner_booking_details bkd " +
				        "ON up.user_id = bkd.tenant_id " +
				        "AND up.user_payment_booking_id = bkd.booking_id " +
				        "LEFT JOIN pgowners.zoy_pg_property_details pgt ON pgt.property_id = bkd.property_id " +
				        "LEFT JOIN pgowners.zoy_pg_bed_details bd ON bkd.selected_bed = bd.bed_id " +
				        "WHERE 1=1"
				);
			Map<String, Object> parameters = new HashMap<>();

			if (filterData.getTransactionStatus() != null && !filterData.getTransactionStatus().isEmpty()) {
				queryBuilder.append(" AND LOWER(up.user_payment_payment_status) LIKE LOWER(CONCAT('%', :transactionStatus, '%'))");
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
				queryBuilder.append(" AND up.user_payment_timestamp BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP)");
				parameters.put("fromDate", filterRequest.getFromDate());
				parameters.put("toDate", filterRequest.getToDate());
			}
			
			if (filterData.getTenantContactNum() != null && !filterData.getTenantContactNum().isEmpty()) {
				queryBuilder.append(" AND LOWER(ud.user_personal_phone_num) LIKE LOWER(:tenantContactNum)");
				parameters.put("tenantContactNum", "%" + filterData.getTenantContactNum() + "%");
			}
			
			if (filterData.getTransactionNumber() != null && !filterData.getTransactionNumber().isEmpty()) {
				queryBuilder.append(" AND LOWER(up.user_payment_bank_transaction_id) LIKE LOWER(:transactionNumber)");
				parameters.put("transactionNumber", "%" + filterData.getTransactionNumber() + "%");
			}

			String sort = "up.user_payment_timestamp";  
			if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty() && filterRequest.getSortActive() != null) {
				if ("transactionNumber".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_bank_transaction_id";
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
					sort = "up.user_payment_zoy_payment_type";
				} else if ("paymentMethod".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_result_method";
				} else if("propertyHouseArea".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "pgt.property_house_area";
				} else if("tenantContactNum".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "ud.user_personal_phone_num";
				}
				else {
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
			List<Object[]> result = query.getResultList();
			List<UserPaymentDTO> userPaymentDTOs = result.stream().map(row -> {
			    UserPaymentDTO dto = new UserPaymentDTO();
			    dto.setTransactionDate((Timestamp) row[0]); 
			    dto.setTransactionNumber(row[1] != null ? (String) row[1] : "");
			    dto.setTransactionStatus(row[2] != null ? (String) row[2] : "");
			    BigDecimal payableAmount = (BigDecimal) row[3] != null ? (BigDecimal) row[3] : BigDecimal.ZERO;  
			    BigDecimal gst = (BigDecimal) row[4] != null ? (BigDecimal) row[4] : BigDecimal.ZERO;  
			    dto.setDueAmount(rupeeSymbol+numberFormat.format(filterCount));
			    dto.setGstAmount(rupeeSymbol+numberFormat.format(gst));
			    dto.setUserPersonalName(row[5] != null ? (String) row[5] : ""); 
			    dto.setUserPgPropertyName(row[6] != null ? (String) row[6] : "");  
			    dto.setRoomBedNumber(row[7] != null ? (String) row[7] : ""); 
			    dto.setTotalAmount(rupeeSymbol+numberFormat.format(payableAmount.add(gst)));  
			    dto.setCategory(row[8] != null ? (String) row[8] : "");  
			    dto.setPaymentMode(row[9] != null ? (String) row[9] : ""); 
			    dto.setPropertyHouseArea(row[11] != null ? (String) row[11] : "");  
			    dto.setTenantContactNum(row[12] != null ? (String) row[12] : "");
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
					"SELECT DISTINCT up.user_payment_timestamp AS transaction_date, " +
						    "up.user_payment_bank_transaction_id AS transaction_number, " +
							"'Tenant' AS payer_payee_type, " +
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

			if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append(" AND up.user_payment_timestamp BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP)");
				parameters.put("fromDate", filterRequest.getFromDate());
				parameters.put("toDate", filterRequest.getToDate());
			}


			if (filterData.getPayerName() != null && !filterData.getPayerName().isEmpty()) {
				queryBuilder.append(" AND LOWER(ud.user_personal_name) LIKE LOWER(:userPersonalName)");
				parameters.put("userPersonalName", "%" + filterData.getPayerName() + "%");
			}
			
			 if (filterData.getPayerType() != null && !filterData.getPayerType().isEmpty()) {
		            queryBuilder.append(" AND 'Tenant' = :payerPayeeType");  
		            parameters.put("payerPayeeType", filterData.getPayerType());
		        }

			if (filterRequest.getCityLocation() != null && !filterRequest.getCityLocation().isEmpty()) {
				queryBuilder.append(" AND LOWER(pgt.property_city) LIKE LOWER(CONCAT('%', :cityLocation, '%'))");
				parameters.put("cityLocation", filterRequest.getCityLocation() + "%");
			}
			if (filterData.getTransactionNumber() != null && !filterData.getTransactionNumber().isEmpty()) {
				queryBuilder.append(" AND LOWER(up.user_payment_bank_transaction_id) LIKE LOWER(:transactionNumber)");
				parameters.put("transactionNumber", "%" + filterData.getTransactionNumber() + "%");
			}
			String sort = "up.user_payment_timestamp";  

			if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty() && filterRequest.getSortActive() != null) {
				if ("transactionDate".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_timestamp";
				} else if ("transactionNumber".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_bank_transaction_id";
				} else if ("payerPayeeName".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "ud.user_personal_name";
				} else if ("creditAmount".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "(up.user_payment_payable_amount + up.user_payment_gst)";
				} else if ("debitAmount".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_payable_amount";
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
				BigDecimal gst = (BigDecimal) row[5] != null ? (BigDecimal) row[5] : BigDecimal.ZERO;
				BigDecimal totalAmount = payableAmount.add(gst);
				dto.setCreditAmount(rupeeSymbol+numberFormat.format(totalAmount));
				dto.setDebitAmount(rupeeSymbol+numberFormat.format(BigDecimal.valueOf(0)));
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
				    "SELECT DISTINCT ON (u.user_money_due_id) " +
				            "u.user_money_due_amount, " +  
				            "u.user_money_due_bill_start_date, " +  
				            "ud.user_personal_name, " + 
				            "pgt.property_name AS user_pg_propertyname, " +  
				            "pgt.property_house_area AS pgAddress, " +  
				            "bd.bed_name, " +
				            "u.user_money_due_id, " +
				            "pgt.property_city, " +  
				            "ud.user_personal_phone_num " + 
				    "FROM pgusers.user_dues u " +
				    "JOIN pgusers.user_details ud ON u.user_id = ud.user_id " +
				    "JOIN pgowners.zoy_pg_owner_booking_details bkd ON u.user_id = bkd.tenant_id " +
				    "JOIN pgowners.zoy_pg_property_details pgt ON bkd.property_id = pgt.property_id " +  
				    "JOIN pgowners.zoy_pg_bed_details bd ON bkd.selected_bed = bd.bed_id " +
				    "WHERE 1=1 "
				);


			Map<String, Object> parameters = new HashMap<>();

			if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append(" AND u.user_money_due_timestamp BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP)");
				parameters.put("fromDate", filterRequest.getFromDate());
				parameters.put("toDate", filterRequest.getToDate());
			}


			if (filterData.getTenantName() != null && !filterData.getTenantName().isEmpty()) {
				queryBuilder.append(" AND LOWER(ud.user_personal_name) LIKE LOWER(:tenantName)");
				parameters.put("tenantName", "%" + filterData.getTenantName() + "%");
			}
			
			if (filterData.getBedNumber() != null && !filterData.getBedNumber().isEmpty()) {
				queryBuilder.append(" AND LOWER(bd.bed_name) LIKE LOWER(:bedNumber)");
				parameters.put("bedNumber", "%" + filterData.getBedNumber() + "%");
			}
			
			if (filterData.getTenantContactNum() != null && !filterData.getTenantContactNum().isEmpty()) {
				queryBuilder.append(" AND ud.user_personal_phone_num = :tenantMobileNum");
				parameters.put("tenantMobileNum", "%" + filterData.getTenantContactNum() + "%");
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

			     if ("customerName".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "ud.user_personal_name";
				} else if ("PgPropertyName".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "pgt.property_name";
				}else if ("bedNumber".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "bd.bed_name";
				} else if ("pendingAmount".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "u.user_money_due_amount";
				} else if ("pendingDueDate".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "u.user_money_due_bill_start_date";
				}else if ("tenantMobileNum".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "ud.user_personal_phone_num";
				}else {
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
			            dto.setPendingAmount(rupeeSymbol+numberFormat.format(payableAmount.subtract((BigDecimal) row[0])));
			        } else {
			            dto.setPendingAmount(rupeeSymbol+numberFormat.format((BigDecimal) row[0]));
			        }
			    } else {
			        dto.setPendingAmount(rupeeSymbol+numberFormat.format((BigDecimal) row[0]));
			    }
			    dto.setPendingDueDate((Timestamp) row[1]);
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
				            "up.user_payment_bank_transaction_id AS transactionNumber, " +
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
				queryBuilder.append(" AND up.user_payment_timestamp BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP)");
				parameters.put("fromDate", filterRequest.getFromDate());
				parameters.put("toDate", filterRequest.getToDate());
			}

			if (filterData.getOwnerName() != null && !filterData.getOwnerName().isEmpty()) {
				queryBuilder.append(" AND LOWER(o.pg_owner_name) LIKE LOWER(:ownerName)");
				parameters.put("ownerName", "%" + filterData.getOwnerName() + "%");
			}

			if (filterData.getOwnerEmail() != null && !filterData.getOwnerEmail().isEmpty()) {
				queryBuilder.append(" AND LOWER(o.pg_owner_email) LIKE LOWER(:ownerName)");
				parameters.put("ownerEmail", "%" + filterData.getOwnerEmail() + "%");
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

				if ("ownerName".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "o.pg_owner_name";
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
			    dto.setTotalAmountFromTenants(rupeeSymbol+numberFormat.format((BigDecimal) row[2] != null ? (BigDecimal) row[2] : BigDecimal.ZERO)); 
			    dto.setTransactionDate((Timestamp) row[3]);  
			    dto.setTransactionNumber(row[4] != null ? (String) row[4] : "");  
			    dto.setPaymentStatus(row[5] != null ? (String) row[5] : "");      
			    dto.setPgAddress(row[7] != null ? (String) row[7] : "");  
			    dto.setOwnerEmail(row[8] != null ? (String) row[8] : "");  
			    dto.setAmountPaidToOwner(rupeeSymbol+numberFormat.format(BigDecimal.valueOf(0)));  
			    dto.setZoyShare(rupeeSymbol+numberFormat.format(BigDecimal.valueOf(0)));  
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
	public byte[] generateDynamicReport(UserPaymentFilterRequest filterRequest, FilterData filterData,Boolean applyPagination) throws WebServiceException {
		try {
			Map<String, Object> data = new HashMap<>();
			CommonResponseDTO<?> reportData = null; 
			switch (filterRequest.getReportType()) {
			case "userTransactionReport":
				reportData = getUserPaymentDetails(filterRequest, filterData,applyPagination);
				break;
			case "userPaymentGstReport":
				reportData = getUserPaymentDetails(filterRequest, filterData,applyPagination);
				break;
			case "consolidatedFinanceReport":
				reportData = getConsolidatedFinanceDetails(filterRequest, filterData,applyPagination);
				break;
			case "tenantDuesReport":
				reportData = getTenentDuesDetails(filterRequest, filterData,applyPagination);
				break;
			case "vendorPaymentsReport":
				reportData = getVendorPaymentDetails(filterRequest, filterData,applyPagination);
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
			switch (filterRequest.getDownloadType().toLowerCase()) {
			case "pdf":
				try {
					InputStream inputLogoStreamImg = getClass().getResourceAsStream(zoyLogoPath);
					InputStream inputWaterMarkStreamImg = getClass().getResourceAsStream(watermarkImagePath);

					if (inputLogoStreamImg == null || inputWaterMarkStreamImg == null) {
					    if (inputLogoStreamImg == null) {
					        log.error("Logo image not found at the specified path: {}", zoyLogoPath);
					        throw new FileNotFoundException("Logo image not found at the specified path: " + zoyLogoPath);
					    }
					    if (inputWaterMarkStreamImg == null) {
					        log.error("Watermark image not found at the specified path: {}", watermarkImagePath);
					        throw new FileNotFoundException("Watermark image not found at the specified path: " + watermarkImagePath);
					    }
					}
					data.put("LOGO_PATH", inputLogoStreamImg); 
					data.put("WATERMARK_IMAGE", inputWaterMarkStreamImg);
				}catch (FileNotFoundException e) {
					log.error("Logo image not found, PDF generation failed."+e);
					new ZoyAdminApplicationException(e,"Logo image not found, PDF generation failed.");
				}
				catch (Exception ex) {
					log.error("Exception in logo for pdf report."+ex);
					new ZoyAdminApplicationException(ex,"");
				}
				InputStream reportStream = getClass().getResourceAsStream("/templates/"+filterRequest.getReportType()+".jasper");
				return pdfGenerateService.generatePdfFile(reportStream,data);
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
