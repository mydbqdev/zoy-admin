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
import com.integration.zoy.model.TenantResportsDTO;
import com.integration.zoy.repository.UserPaymentDueRepository;
import com.integration.zoy.repository.UserPaymentRepository;
import com.integration.zoy.repository.ZoyPgPropertyDetailsRepository;
import com.integration.zoy.utils.CommonResponseDTO;
import com.integration.zoy.utils.ConsilidatedFinanceDetails;
import com.integration.zoy.utils.RatingsAndReviewsReport;
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
	private  PdfGenerateService pdfGenerateService;

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

	@Value("${zoy.admin.logo}")
	private String zoyLogoPath;
	
	@Value("${zoy.admin.watermark}")
    private String watermarkImagePath;


	@Override
	public CommonResponseDTO<UserPaymentDTO> getUserPaymentDetails(UserPaymentFilterRequest filterRequest,FilterData filterData,Boolean applyPagination) throws WebServiceException {
		try{
			StringBuilder queryBuilder = new StringBuilder(
				    "SELECT \r\n"
				    + "    up.user_payment_timestamp, \r\n"
				    + "    up.user_payment_result_invoice_id, \r\n"
				    + "    CASE \r\n"
				    + "        WHEN LOWER(up.user_payment_zoy_payment_mode) = 'offline' THEN 'Paid-Cash' \r\n"
				    + "        ELSE up.user_payment_payment_status \r\n"
				    + "    END AS user_payment_payment_status, \r\n"
				    + "    up.user_payment_payable_amount, \r\n"
				    + "    up.user_payment_gst, \r\n"
				    + "    ud.user_personal_name, \r\n"
				    + "    pgt.property_name AS user_pg_propertyname, \r\n"
				    + "    bd.bed_name, \r\n"
				    + "    up.user_payment_zoy_payment_type, \r\n"
				    + "    CASE \r\n"
				    + "        WHEN LOWER(up.user_payment_zoy_payment_mode) = 'offline' THEN 'Cash' \r\n"
				    + "        ELSE up.user_payment_result_method \r\n"
				    + "    END AS user_payment_result_method, \r\n"
				    + "    pgt.property_city, \r\n"
				    + "    pgt.property_house_area, \r\n"
				    + "    um.user_mobile, \r\n"
				    + "    pgt.property_id\r\n"
				    + "FROM pgusers.user_payments up \r\n"
				    + "LEFT JOIN pgusers.user_details ud \r\n"
				    + "    ON up.user_id = ud.user_id\r\n"
				    + "LEFT JOIN pgusers.user_master um ON up.user_id = um.user_id  \r\n"
				    + "JOIN pgowners.zoy_pg_owner_booking_details bkd \r\n"
				    + "    ON up.user_id = bkd.tenant_id \r\n"
				    + "    AND up.user_payment_booking_id = bkd.booking_id \r\n"
				    + "JOIN pgowners.zoy_pg_property_details pgt \r\n"
				    + "    ON pgt.property_id = bkd.property_id \r\n"
				    + "JOIN pgowners.zoy_pg_bed_details bd \r\n"
				    + "    ON bkd.selected_bed = bd.bed_id WHERE 1=1"
				);
			Map<String, Object> parameters = new HashMap<>();

			if (filterData.getTransactionStatus() != null && !filterData.getTransactionStatus().isEmpty()) {
			    queryBuilder.append(" AND LOWER(CASE " +
			                        "    WHEN LOWER(up.user_payment_zoy_payment_mode) = 'offline' THEN 'Paid-Cash' " +
			                        "    ELSE up.user_payment_payment_status " +
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
				queryBuilder.append(" AND up.user_payment_timestamp BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP)");
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

			String sort = "up.user_payment_timestamp";  
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
			    double payableAmount = row[3] != null ? ((BigDecimal) row[3]).doubleValue() : 0.0;  
			    double gst = row[4] != null ? ((BigDecimal) row[4]).doubleValue() : 0.0;  
			    double dueamount=payableAmount-gst;
			    dto.setDueAmount(dueamount != 0.0 ? dueamount : null);
			    dto.setGstAmount((row[4] != null) ?  ((Number) row[4]).doubleValue() : 0.0);
			    dto.setUserPersonalName(row[5] != null ? (String) row[5] : ""); 
			    dto.setUserPgPropertyName(row[6] != null ? (String) row[6] : "");  
			    dto.setRoomBedNumber(row[7] != null ? (String) row[7] : ""); 
			    dto.setTotalAmount((row[3] != null) ?  ((Number) row[3]).doubleValue() : 0.0);
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
					"SELECT DISTINCT up.user_payment_timestamp AS transaction_date, " +
						    "up.user_payment_result_invoice_id AS transaction_number, " +
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
				queryBuilder.append(" AND LOWER(up.user_payment_result_invoice_id) LIKE LOWER(:transactionNumber)");
				parameters.put("transactionNumber", "%" + filterData.getTransactionNumber() + "%");
			}
			String sort = "up.user_payment_timestamp";  

			if (filterRequest.getSortDirection() != null && !filterRequest.getSortDirection().isEmpty() && filterRequest.getSortActive() != null) {
				if ("transactionDate".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_timestamp";
				} else if ("transactionNumber".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "up.user_payment_result_invoice_id";
				} else if ("payerPayeeName".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "ud.user_personal_name";
				} else if ("creditAmount".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "(up.user_payment_payable_amount)";
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
				//BigDecimal gst = (BigDecimal) row[5] != null ? (BigDecimal) row[5] : BigDecimal.ZERO;
				//BigDecimal totalAmount = payableAmount;
			    dto.setCreditAmount((payableAmount != null) ?  ((Number) payableAmount).doubleValue() : 0.0);
			    dto.setDebitAmount(BigDecimal.valueOf(0).doubleValue());
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
					 + "ud.user_money_due_bill_start_date,\r\n"
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
				queryBuilder.append(" AND ud.user_money_due_bill_start_date BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP)");
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
					sort = "ud.user_money_due_bill_start_date";
				}else if ("tenantMobileNum".equalsIgnoreCase(filterRequest.getSortActive())) {
					sort = "um.user_mobile";
				}else {
					sort = "ud.user_money_due_bill_start_date"; 
				}

				String sortDirection = filterRequest.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";
				queryBuilder.append(" ORDER BY ").append(sort).append(" ").append(sortDirection);
			} else {
				queryBuilder.append(" ORDER BY  ud.user_money_due_id,ud.user_money_due_bill_start_date DESC");
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
			            dto.setPendingAmount((payableAmount != null) ?  ((Number) payableAmount).doubleValue() : 0.0);
			        } else {
			            dto.setPendingAmount((row[0] != null) ?  ((Number) row[0]).doubleValue() : 0.0);
			        }
			    } else {
			        dto.setPendingAmount((row[0] != null) ?  ((Number) row[0]).doubleValue() : 0.0);
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
			    dto.setTotalAmountFromTenants((row[2] != null) ?  ((Number) row[2]).doubleValue() : 0.0);
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
			case "tenantRefundReport":
				reportData = getTenantRefunds(filterRequest, filterData,applyPagination);
				break;
			case "UpcomingTenantsReport":
				reportData = getUpcomingTenantsReport(filterRequest, filterData,applyPagination);
				break;
			case "ActiveTenantsReport":
				reportData = getActiveTenantsReport(filterRequest, filterData,applyPagination);
				break;	
			case "InactiveTenantsReport":
				reportData = getInActiveTenantsReport(filterRequest, filterData,applyPagination);
				break;	
			case "SuspendedTenantsReport":
				reportData = getSuspendedTenantsReport(filterRequest, filterData,applyPagination);
				break;	
			case "reviewsAndRatingReport":
				reportData = getRatingsAndReviewsDetails(filterRequest, filterData,applyPagination);
				break;	
				
	
			default:
				throw new IllegalArgumentException("Invalid template name provided.");
			}
			List<?> dataList = reportData.getData();
			data.put("reportData", dataList);
			data.put("startDate", filterRequest.getFromDate());  
			data.put("endDate", filterRequest.getToDate()); 
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
	                + "        WHEN urd.refund_process_status = TRUE THEN 'Completed' \r\n"
	                + "        ELSE 'Processing' \r\n"
	                + "    END AS Status, "
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
	            dto.setRefundableAmount((row[6] != null) ?  ((Number) row[6]).doubleValue() : 0.0);
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
					"   select rr.rating_id, rr.partner_id, rr.written_review, rr.overall_rating, rr.customer_id, rr.property_id,   \r\n"
					+ "STRING_AGG(DISTINCT rating_master.review_type_id || ':' || rating_master.review_type || ':' || rrt.rating, ',') as ratingData,  \r\n"
					+ "rr.timestamp, rr.booking_id, zpbd.bed_name, zppd.property_name, STRING_AGG(DISTINCT zpim.image_url, ',') as image_urls, \r\n"
					+ "zppd.property_house_area ,um.user_first_name ||' '||um.user_last_name ,ud.user_profile_image,um.user_mobile,MAX(CASE WHEN rating_master.review_type = 'cleanliness' THEN rrt.rating ELSE NULL END) AS cleanliness,\r\n"
					+ "    MAX(CASE WHEN rating_master.review_type = 'amenities' THEN rrt.rating ELSE NULL END) AS amenities_rating,\r\n"
					+ "    MAX(CASE WHEN rating_master.review_type = 'price' THEN rrt.rating ELSE NULL END) AS value_for_money_rating,\r\n"
					+ "    MAX(CASE WHEN rating_master.review_type = 'maintainance' THEN rrt.rating ELSE NULL END) AS maintainance,\r\n"
					+ "    MAX(CASE WHEN rating_master.review_type = 'accomodation' THEN rrt.rating ELSE NULL END) AS accomodation,\r\n"
					+ "zppd.property_city \r\n" 
					+ "from pgcommon.review_ratings rr   \r\n"
					+ "left join pgcommon.review_ratings_types rrt on rr.rating_id = rrt.rating_id   \r\n"
					+ "left join pgcommon.review_ratings_master rating_master on rrt.review_type_id = rating_master.review_type_id   \r\n"
					+ "left join pgowners.zoy_pg_owner_booking_details zpobd on zpobd.booking_id = rr.booking_id   \r\n"
					+ "left join pgowners.zoy_pg_bed_details zpbd on zpbd.bed_id = zpobd.selected_bed   \r\n"
					+ "left join pgowners.zoy_pg_property_details zppd on zppd.property_id = rr.property_id   \r\n"
					+ "left join pgowners.zoy_pg_properties_images zppi on zppi.property_id = zppd.property_id   \r\n"
					+ "left join pgowners.zoy_pg_image_master zpim on zpim.image_id = zppi.image_id  \r\n"
					+ "left join pgusers.user_master um on um.user_id =rr.customer_id  \r\n"
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
	        		+ "JOIN pgowners.zoy_pg_bed_details bd  \r\n"
	        		+ "    ON zpqbd.selected_bed = bd.bed_id\r\n"
	        		+ "WHERE 1=1 and zpqbd.in_date > CURRENT_DATE");

	        Map<String, Object> parameters = new HashMap<>();
	        
	        if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append(" AND zpqbd.in_date BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP)");
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
	            queryBuilder.append(" AND LOWER(zpd.property_name) LIKE LOWER(:PgPropertyName) ");
	            parameters.put("PgPropertyName", "%" + filterData.getPgName() + "%");
	        }

	        if (filterData.getPgAddress() != null && !filterData.getPgAddress().isEmpty()) {
	            queryBuilder.append(" AND LOWER(zpd.property_house_area) LIKE LOWER(:pgAddress) ");
	            parameters.put("pgAddress", "%" + filterData.getPgAddress() + "%");
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
	                case "tenantUser":
	                	sort="um.user_email";
	                	break;
	                case "PgPropertyName":
	                    sort = "zpd.property_name";
	                    break;
	                case "userPgPropertyAddress":
	                    sort = "zpd.property_house_area";
	                    break;
	                case "bedNumber":
	                    sort = "bd.bed_name";
	                    break;
	                case "checkInDate":
	                    sort = "zpqbd.in_date";
	                    break;
	                case "checkOutDate" :
	                	sort="zpqbd.out_date";   	
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
	        List<TenantResportsDTO> upcomingTenantsReportDto = results.stream().map(row -> {
	        	TenantResportsDTO dto = new TenantResportsDTO();
	            dto.setTenantName(row[0] != null ? (String) row[0] : "");
	            dto.setTenantContactNumber(row[1] != null ? (String) row[1] : "");
	            dto.setTenantEmailAddress(row[2] != null ? (String) row[2] : "");
	            dto.setBookedProperyName(row[3] != null ? (String) row[3] : "");
	            dto.setPropertAddress(row[4] != null ? (String) row[4] : "");
	            dto.setRoomNumber(row[5] != null ? (String) row[5] : "");
	            dto.setExpectedCheckIndate(row[6] != null ? (Timestamp)(row[6]) : null);
	            dto.setExpectedCheckOutdate(row[7] != null ? (Timestamp)(row[7]) : null);
	            return dto;
	        }).collect(Collectors.toList());

	        return new CommonResponseDTO<>(upcomingTenantsReportDto, filterCount);
	    } catch (Exception e) {
	        throw new WebServiceException("Error retrieving tenant refunds: " + e.getMessage());
	    }
	}
	
	
	public CommonResponseDTO<TenantResportsDTO> getActiveTenantsReport(UserPaymentFilterRequest filterRequest,
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
	        		+ "join pgusers.user_bookings ub\r\n"
	        		+ "    on zpqbd.booking_id = ub.user_bookings_id \r\n"
	        		+ "JOIN pgowners.zoy_pg_property_details zpd \r\n"
	        		+ "    ON zpqbd.property_id = zpd.property_id \r\n"
	        		+ "JOIN pgowners.zoy_pg_bed_details bd  \r\n"
	        		+ "    ON zpqbd.selected_bed = bd.bed_id \r\n"
	        		+ "WHERE 1=1 and \r\n"
	        		+ "    ub.user_bookings_web_check_in = True\r\n"
	        		+ "	AND ub.user_bookings_web_check_out = FALSE\r\n"
	        		+ "	AND ub.user_bookings_is_cancelled = False");

	        Map<String, Object> parameters = new HashMap<>();
	        
	        if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append(" AND zpqbd.in_date BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP)");
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
	            queryBuilder.append(" AND LOWER(zpd.property_name) LIKE LOWER(:PgPropertyName) ");
	            parameters.put("PgPropertyName", "%" + filterData.getPgName() + "%");
	        }

	        if (filterData.getPgAddress() != null && !filterData.getPgAddress().isEmpty()) {
	            queryBuilder.append(" AND LOWER(zpd.property_house_area) LIKE LOWER(:pgAddress) ");
	            parameters.put("pgAddress", "%" + filterData.getPgAddress() + "%");
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
	                case "tenantUser":
	                	sort="um.user_email";
	                	break;
	                case "PgPropertyName":
	                    sort = "zpd.property_name";
	                    break;
	                case "userPgPropertyAddress":
	                    sort = "zpd.property_house_area";
	                    break;
	                case "bedNumber":
	                    sort = "bd.bed_name";
	                    break;
	                case "checkInDate":
	                    sort = "zpqbd.in_date";
	                    break;
	                case "checkOutDate" :
	                	sort="zpqbd.out_date";   	
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
	        List<TenantResportsDTO> upcomingTenantsReportDto = results.stream().map(row -> {
	        	TenantResportsDTO dto = new TenantResportsDTO();
	            dto.setTenantName(row[0] != null ? (String) row[0] : "");
	            dto.setTenantContactNumber(row[1] != null ? (String) row[1] : "");
	            dto.setTenantEmailAddress(row[2] != null ? (String) row[2] : "");
	            dto.setBookedProperyName(row[3] != null ? (String) row[3] : "");
	            dto.setPropertAddress(row[4] != null ? (String) row[4] : "");
	            dto.setRoomNumber(row[5] != null ? (String) row[5] : "");
	            dto.setExpectedCheckIndate(row[6] != null ? (Timestamp)(row[6]) : null);
	            dto.setExpectedCheckOutdate(row[7] != null ? (Timestamp)(row[7]) : null);
	            return dto;
	        }).collect(Collectors.toList());

	        return new CommonResponseDTO<>(upcomingTenantsReportDto, filterCount);
	    } catch (Exception e) {
	        throw new WebServiceException("Error retrieving tenant refunds: " + e.getMessage());
	    }
	}
	
	
	public CommonResponseDTO<TenantResportsDTO> getInActiveTenantsReport(UserPaymentFilterRequest filterRequest,
	        FilterData filterData, Boolean applyPagination) throws WebServiceException {
	    try {
	        StringBuilder queryBuilder = new StringBuilder("SELECT DISTINCT ON (zpobd.tenant_id) \r\n"
	        		+ "   um.user_first_name || ' ' || um.user_last_name AS username,\r\n"
	        		+ "    um.user_mobile,\r\n"
	        		+ "    um.user_email,\r\n"
	        		+ "    zpPd.property_name,\r\n"
	        		+ "    zpPd.property_house_area,\r\n"
	        		+ "    bd.bed_name,\r\n"
	        		+ "    zpobd.in_date, \r\n"
	        		+ "    zpobd.out_date\r\n"
	        		+ "   FROM pgowners.zoy_pg_owner_booking_details zpobd\r\n"
	        		+ "JOIN pgusers.user_bookings ub\r\n"
	        		+ "    ON zpobd.booking_id = ub.user_bookings_id \r\n"
	        		+ "    AND ub.user_bookings_web_check_out = TRUE \r\n"
	        		+ "join pgusers.user_master um on um.user_id =zpobd.tenant_id \r\n"
	        		+ "join pgowners.zoy_pg_property_details zppd on zppd.property_id=zpobd.property_id \r\n"
	        		+ "JOIN pgowners.zoy_pg_bed_details bd ON zpobd.selected_bed = bd.bed_id where 1=1");

	        Map<String, Object> parameters = new HashMap<>();
	        
	        if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append(" AND zpqbd.in_date BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP)");
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
	            queryBuilder.append(" AND LOWER(zpPd.property_name) LIKE LOWER(:PgPropertyName) ");
	            parameters.put("PgPropertyName", "%" + filterData.getPgName() + "%");
	        }

	        if (filterData.getPgAddress() != null && !filterData.getPgAddress().isEmpty()) {
	            queryBuilder.append(" AND LOWER(zpPd.property_house_area) LIKE LOWER(:pgAddress) ");
	            parameters.put("pgAddress", "%" + filterData.getPgAddress() + "%");
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
	                case "tenantUser":
	                	sort="um.user_email";
	                	break;
	                case "PgPropertyName":
	                    sort = "zpPd.property_name";
	                    break;
	                case "userPgPropertyAddress":
	                    sort = "zpPd.property_house_area";
	                    break;
	                case "bedNumber":
	                    sort = "bd.bed_name";
	                    break;
	                case "checkInDate":
	                    sort = "zpobd.in_date";
	                    break;
	                case "checkOutDate" :
	                	sort="zpobd.out_date";   	
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
	        List<TenantResportsDTO> upcomingTenantsReportDto = results.stream().map(row -> {
	        	TenantResportsDTO dto = new TenantResportsDTO();
	            dto.setTenantName(row[0] != null ? (String) row[0] : "");
	            dto.setTenantContactNumber(row[1] != null ? (String) row[1] : "");
	            dto.setTenantEmailAddress(row[2] != null ? (String) row[2] : "");
	            dto.setBookedProperyName(row[3] != null ? (String) row[3] : "");
	            dto.setPropertAddress(row[4] != null ? (String) row[4] : "");
	            dto.setRoomNumber(row[5] != null ? (String) row[5] : "");
	            dto.setExpectedCheckIndate(row[6] != null ? (Timestamp)(row[6]) : null);
	            dto.setExpectedCheckOutdate(row[7] != null ? (Timestamp)(row[7]) : null);
	            return dto;
	        }).collect(Collectors.toList());

	        return new CommonResponseDTO<>(upcomingTenantsReportDto, filterCount);
	    } catch (Exception e) {
	        throw new WebServiceException("Error retrieving tenant refunds: " + e.getMessage());
	    }
	}
	
	
	public CommonResponseDTO<TenantResportsDTO> getSuspendedTenantsReport(UserPaymentFilterRequest filterRequest,
	        FilterData filterData, Boolean applyPagination) throws WebServiceException {
	    try {
	        StringBuilder queryBuilder = new StringBuilder("SELECT DISTINCT ON (zpobd.tenant_id) \r\n"
	        		+ "   um.user_first_name || ' ' || um.user_last_name AS username,\r\n"
	        		+ "    um.user_mobile,\r\n"
	        		+ "    um.user_email,\r\n"
	        		+ "    zpPd.property_name,\r\n"
	        		+ "    zpPd.property_house_area,\r\n"
	        		+ "    bd.bed_name,\r\n"
	        		+ "    zpobd.out_date,\r\n"
	        		+ "    um.user_modified_at ,\r\n"
	        		+ "    um.reason_message\r\n"
	        		+ "   FROM pgowners.zoy_pg_owner_booking_details zpobd\r\n"
	        		+ "JOIN pgusers.user_bookings ub\r\n"
	        		+ "    ON zpobd.booking_id = ub.user_bookings_id \r\n"
	        		+ "    AND ub.user_bookings_web_check_out = TRUE \r\n"
	        		+ "join pgusers.user_master um on um.user_id =zpobd.tenant_id \r\n"
	        		+ "join pgowners.zoy_pg_property_details zppd on zppd.property_id=zpobd.property_id \r\n"
	        		+ "JOIN pgowners.zoy_pg_bed_details bd ON zpobd.selected_bed = bd.bed_id\r\n"
	        		+ "where 1=1 And um.user_status='Suspended' ");

	        Map<String, Object> parameters = new HashMap<>();
	        
	        if (filterRequest.getFromDate() != null && filterRequest.getToDate() != null) {
				queryBuilder.append(" AND zpqbd.in_date BETWEEN CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP)");
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
	            queryBuilder.append(" AND LOWER(zpPd.property_name) LIKE LOWER(:PgPropertyName) ");
	            parameters.put("PgPropertyName", "%" + filterData.getPgName() + "%");
	        }

	        if (filterData.getPgAddress() != null && !filterData.getPgAddress().isEmpty()) {
	            queryBuilder.append(" AND LOWER(zpPd.property_house_area) LIKE LOWER(:pgAddress) ");
	            parameters.put("pgAddress", "%" + filterData.getPgAddress() + "%");
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
	                case "tenantUser":
	                	sort="um.user_email";
	                	break;
	                case "PgPropertyName":
	                    sort = "zpPd.property_name";
	                    break;
	                case "userPgPropertyAddress":
	                    sort = "zpPd.property_house_area";
	                    break;
	                case "bedNumber":
	                    sort = "bd.bed_name";
	                    break;
	                case "checkInDate":
	                    sort = "zpobd.in_date";
	                    break;
	                case "checkOutDate" :
	                	sort="zpobd.out_date";   	
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
	        List<TenantResportsDTO> upcomingTenantsReportDto = results.stream().map(row -> {
	        	TenantResportsDTO dto = new TenantResportsDTO();
	            dto.setTenantName(row[0] != null ? (String) row[0] : "");
	            dto.setTenantContactNumber(row[1] != null ? (String) row[1] : "");
	            dto.setTenantEmailAddress(row[2] != null ? (String) row[2] : "");
	            dto.setBookedProperyName(row[3] != null ? (String) row[3] : "");
	            dto.setPropertAddress(row[4] != null ? (String) row[4] : "");
	            dto.setRoomNumber(row[5] != null ? (String) row[5] : "");
	            dto.setExpectedCheckOutdate(row[6] != null ? (Timestamp)(row[6]) : null);
	            dto.setSuspendedDate(row[7] != null ? (Timestamp)(row[7]) : null);
	            dto.setReasonForSuspension(row[8] != null ? (String) row[8] : "");
	            return dto;
	        }).collect(Collectors.toList());

	        return new CommonResponseDTO<>(upcomingTenantsReportDto, filterCount);
	    } catch (Exception e) {
	        throw new WebServiceException("Error retrieving tenant refunds: " + e.getMessage());
	    }
	}
	
}
