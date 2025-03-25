package com.integration.zoy.service;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.integration.zoy.exception.ZoyAdminApplicationException;
import com.integration.zoy.model.TenantResportsDTO;
import com.integration.zoy.utils.ConsilidatedFinanceDetails;
import com.integration.zoy.utils.PropertyResportsDTO;
import com.integration.zoy.utils.RatingsAndReviewsReport;
import com.integration.zoy.utils.RegisterTenantsDTO;
import com.integration.zoy.utils.TenentDues;
import com.integration.zoy.utils.TenentRefund;
import com.integration.zoy.utils.UserPaymentDTO;
import com.integration.zoy.utils.VendorPayments;
import com.integration.zoy.utils.VendorPaymentsDues;
import com.integration.zoy.utils.VendorPaymentsGst;
import com.itextpdf.io.exceptions.IOException;

@Service
public class ExcelGenerateService {
	
	@Autowired
	private TimestampFormatterUtilService tuService;
	
	private CellStyle currencyStyle;
	
	private void initializeCurrencyStyle(Workbook workbook) {
        currencyStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();

        currencyStyle.setDataFormat(format.getFormat("#,##0.00"));
    }

	private void setCurrencyCell(Row row, int column, String value) { 
	    Cell cell = row.createCell(column);

	    if (value != null && !value.equals("N/A")) {
	        try {
	            String cleanedValue = value.replaceAll("[^\\d.]", "");
	            
	            if (!cleanedValue.isEmpty()) {
	                if (cleanedValue.indexOf('.') != cleanedValue.lastIndexOf('.')) {
	                    cleanedValue = cleanedValue.substring(0, cleanedValue.indexOf('.')) + cleanedValue.substring(cleanedValue.lastIndexOf('.'));
	                }
	                
	                double amount = Double.parseDouble(cleanedValue);
	                
	                cell.setCellValue(amount);
	                
	                cell.setCellStyle(currencyStyle);
	            } else {
	                cell.setCellValue("N/A");
	            }
	        } catch (NumberFormatException e) {
	            cell.setCellValue(value);
	        }
	    } else {
	        cell.setCellValue("N/A");
	    }
	}

    
    public byte[] generateExcelFile(String reportType, Map<String, Object> data) {
        List<?> reportData = (List<?>) data.get("reportData");

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            initializeCurrencyStyle(workbook);

            Sheet sheet = workbook.createSheet(reportType);

            if (reportData == null || reportData.isEmpty()) {
                return new byte[0];
            }

            Row headerRow = sheet.createRow(0);
            createExcelHeaderRow(headerRow, reportData.get(0), reportType);

            for (int i = 0; i < reportData.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                createExcelDataRow(dataRow, reportData.get(i), reportType);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error generating Excel file", e);
        } catch (Exception e) {
            throw new RuntimeException("Error processing the Excel generation", e);
        }
    }

	private void createExcelHeaderRow(Row row, Object dto, String reportType) {
		switch (reportType) {
		case "userTransactionReport":
			row.createCell(0).setCellValue("Tenant Name");
			row.createCell(1).setCellValue("Tenant Mobile Number");
			row.createCell(2).setCellValue("PG Name");
			row.createCell(3).setCellValue("PG Address");
			row.createCell(4).setCellValue("BED Number");
			row.createCell(5).setCellValue("Transaction Date");
			row.createCell(6).setCellValue("Invoice No");
			row.createCell(7).setCellValue("Transaction Status");
			row.createCell(8).setCellValue("Due Amount(₹)");
			row.createCell(9).setCellValue("GST Amount(₹)");
			row.createCell(10).setCellValue("Total Amount(₹)");
			row.createCell(11).setCellValue("Category");
			row.createCell(12).setCellValue("Mode of Payment");
			break;
		case "userPaymentGstReport":
			row.createCell(0).setCellValue("Transaction Date");
			row.createCell(1).setCellValue("Invoice No");
			row.createCell(2).setCellValue("Tenant Name");
			row.createCell(3).setCellValue("PG Name");
			row.createCell(4).setCellValue("PG Address");
			row.createCell(5).setCellValue("Total Amount(₹)");
			row.createCell(6).setCellValue("GST Amount(₹)");
			row.createCell(7).setCellValue("Due Amount(₹)");
			row.createCell(8).setCellValue("MODE OF PAYMENT");
			break;
		case "consolidatedFinanceReport":
			row.createCell(0).setCellValue("Transaction Date");
			row.createCell(1).setCellValue("Invoice Number");
			row.createCell(2).setCellValue("Payer/Payee Type");
			row.createCell(3).setCellValue("Name of the Payer/Payee");
			row.createCell(4).setCellValue("Debit(₹)");
			row.createCell(5).setCellValue("Credit(₹)");
			row.createCell(6).setCellValue("Pg Name");
			row.createCell(7).setCellValue("Contact Number");
			
			break;
			
		case "tenantDuesReport":
			row.createCell(0).setCellValue("Tenant Name");
			row.createCell(1).setCellValue("Tenant Mobile Number");
			row.createCell(2).setCellValue("PG Name");
			row.createCell(3).setCellValue("PG Address");
			row.createCell(4).setCellValue("Bed No");
			row.createCell(5).setCellValue("Pending Amount(₹)");
			row.createCell(6).setCellValue("Payment Due Date");
			break;
		case "vendorPaymentsReport":
			row.createCell(0).setCellValue("Owner Name");
			row.createCell(1).setCellValue("PG Name");
			row.createCell(2).setCellValue("Owner Email ID");
			row.createCell(3).setCellValue("PG Address");
			row.createCell(4).setCellValue("Total Amount Received from Tenants(₹)");
			row.createCell(5).setCellValue("Total Amount Paid to Owner(₹)");
			row.createCell(6).setCellValue("ZOY Share(₹)");
			row.createCell(7).setCellValue("Transaction Date");
			row.createCell(8).setCellValue("Invoice Number");
			row.createCell(9).setCellValue("Payment Status");
			row.createCell(10).setCellValue("Owner approval Status");
			break;
		case "vendorPaymentsDuesReport":
			row.createCell(0).setCellValue("Owner ID");
			row.createCell(1).setCellValue("Owner Name");
			row.createCell(2).setCellValue("Pending Amount(₹)");
			row.createCell(3).setCellValue("Pending Due Date");
			row.createCell(4).setCellValue("Property ID");
			row.createCell(5).setCellValue("Property Name");
			row.createCell(6).setCellValue("Total Amount Paid(₹)");
			row.createCell(7).setCellValue("Total Amount Payable(₹)");
			break;
		case "vendorPaymentsGstReport":
			row.createCell(0).setCellValue(" Date");
			row.createCell(1).setCellValue("Invoice No");
			row.createCell(2).setCellValue("Property ID");
			row.createCell(3).setCellValue("Property Name");
			row.createCell(4).setCellValue("Total Amount(₹)");
			row.createCell(5).setCellValue("GST Amount(₹)");
			row.createCell(6).setCellValue("Basic Amount(₹)");
			row.createCell(7).setCellValue("Payment Method");
			break;
		case "tenantRefundReport":
			row.createCell(0).setCellValue("Tenant Name");
			row.createCell(1).setCellValue("Tenant Mobile Number");
			row.createCell(2).setCellValue("PG Name");
			row.createCell(3).setCellValue("PG Address");
			row.createCell(4).setCellValue("Booking ID");
			row.createCell(5).setCellValue("Refund Title");
			row.createCell(6).setCellValue("Refundable Amount(₹)");
			row.createCell(7).setCellValue("Amount Paid(₹)");
			row.createCell(8).setCellValue("Payment Date");
			row.createCell(9).setCellValue("Invoice Number");
			row.createCell(10).setCellValue("Status");
			break;	
		case "reviewsAndRatingReport":
			row.createCell(0).setCellValue("Review Date");
			row.createCell(1).setCellValue("Tenant Name");
			row.createCell(2).setCellValue("PG Name");
			row.createCell(3).setCellValue("Tenant Contact");
			row.createCell(4).setCellValue("Cleanliness");
			row.createCell(5).setCellValue("Accommodation");
			row.createCell(6).setCellValue("Aminities");
			row.createCell(7).setCellValue("Maintenance");
			row.createCell(8).setCellValue("Value For Money");
			row.createCell(9).setCellValue("Overall Rating");
			break;	
		case "UpcomingTenantsReport":
			row.createCell(0).setCellValue("Tenant Name");
			row.createCell(1).setCellValue("Tenant Contact Number");
			row.createCell(2).setCellValue("Tenant Email Address");
			row.createCell(3).setCellValue("Booked Property Name");
			row.createCell(4).setCellValue("Property Address");
			row.createCell(5).setCellValue("Bed Number");
			row.createCell(6).setCellValue("Expected Check-in Date");
			row.createCell(7).setCellValue("Expected Checked-out Date");
			break;
		case "ActiveTenantsReport":
			row.createCell(0).setCellValue("Tenant Name");
			row.createCell(1).setCellValue("Tenant Contact Number");
			row.createCell(2).setCellValue("Tenant Email Address");
			row.createCell(3).setCellValue("Property Name");
			row.createCell(4).setCellValue("Property Address");
			row.createCell(5).setCellValue("Bed Number");
			row.createCell(6).setCellValue("Check-in Date");
			row.createCell(7).setCellValue("Expected Check-out Date");
			break;
		case "InactiveTenantsReport":
			row.createCell(0).setCellValue("Tenant Name");
			row.createCell(1).setCellValue("Tenant Contact Number");
			row.createCell(2).setCellValue("Tenant Email Address");
			row.createCell(3).setCellValue("Previous Property Name");
			row.createCell(4).setCellValue("Property Address");
			row.createCell(5).setCellValue("Bed Number");
			row.createCell(6).setCellValue("Check-in Date");
			row.createCell(7).setCellValue("Checked-out Date");
			break;
		case "SuspendedTenantsReport":
			row.createCell(0).setCellValue("Tenant Name");
			row.createCell(1).setCellValue("Tenant Contact Number");
			row.createCell(2).setCellValue("Tenant Email Address");
			row.createCell(3).setCellValue("Previous Property Name");
			row.createCell(4).setCellValue("Property Address");
			row.createCell(5).setCellValue("Bed Number");
			row.createCell(6).setCellValue("Checked-out Date");
			row.createCell(6).setCellValue("Suspended Date");
			row.createCell(6).setCellValue("Reason for suspension");
			break;
		case "InactivePropertiesReport":
			row.createCell(0).setCellValue("Owner Full Name");
			row.createCell(1).setCellValue("Inactive Property Name");
			row.createCell(2).setCellValue("Property Contact Number");
			row.createCell(3).setCellValue("Property Email Address");
			row.createCell(4).setCellValue("Property Address");
			break;
		case "RegesterTenantsReport":
			row.createCell(0).setCellValue("Tenant Name");
			row.createCell(1).setCellValue("Tenant Contact Number");
			row.createCell(2).setCellValue("Tenant Email Address");
			row.createCell(3).setCellValue("Registration Date");
			break;
		case "FailedTransactionReport":
			row.createCell(0).setCellValue("Transaction Date");
			row.createCell(1).setCellValue("Tenant Name");
			row.createCell(2).setCellValue("Contact Number");
			row.createCell(3).setCellValue("eMail Id");
			row.createCell(4).setCellValue("Amount");
			row.createCell(5).setCellValue("Reason");
			break;
		case "PotentialPropertyReport":
			row.createCell(0).setCellValue("Owner Name");
			row.createCell(1).setCellValue("Property Name");
			row.createCell(2).setCellValue("Property Contact Number");
			row.createCell(3).setCellValue("Property Email address");
			row.createCell(4).setCellValue("Property Address");
			row.createCell(5).setCellValue("Number of beds occupied");
			row.createCell(6).setCellValue("Expected rent per Month");
			break;
		case "SuspendedPropertiesReport":
			row.createCell(0).setCellValue("Owner Full Name");
			row.createCell(1).setCellValue("Inactive Property Name");
			row.createCell(2).setCellValue("Property Contact Number");
			row.createCell(3).setCellValue("Property Email Address");
			row.createCell(4).setCellValue("Property Address");
			row.createCell(5).setCellValue("Suspended Date");
			row.createCell(6).setCellValue("Reason for suspension");
			break;	
		default:
			throw new IllegalArgumentException("Invalid report type provided: " + reportType);
		}
	}

	private void createExcelDataRow(Row row, Object dto, String reportType) {
		switch (reportType) {
		case "userTransactionReport":
		case "userPaymentGstReport":
		case "FailedTransactionReport":
			if (dto instanceof UserPaymentDTO) {
				UserPaymentDTO userPayment = (UserPaymentDTO) dto;

				if ("userPaymentGstReport".equals(reportType)) {
					row.createCell(0).setCellValue(nullSafe(tuService.formatTimestamp(userPayment.getTransactionDate().toInstant())));
					row.createCell(1).setCellValue(nullSafe(userPayment.getTransactionNumber()));
					row.createCell(2).setCellValue(nullSafe(userPayment.getUserPersonalName()));
					row.createCell(3).setCellValue(nullSafe(userPayment.getUserPgPropertyName()));
					row.createCell(4).setCellValue(nullSafe(userPayment.getPropertyHouseArea()));
					setCurrencyCell(row, 5, nullSafe(userPayment.getDueAmount()));
					setCurrencyCell(row, 6, nullSafe(userPayment.getGstAmount()));
					setCurrencyCell(row, 7, nullSafe(userPayment.getTotalAmount()));
					row.createCell(8).setCellValue(nullSafe(userPayment.getPaymentMode()));
				} else if("userTransactionReport".equals(reportType)){
					row.createCell(0).setCellValue(nullSafe(userPayment.getUserPersonalName()));
					row.createCell(1).setCellValue(nullSafe(userPayment.getTenantContactNum()));
					row.createCell(2).setCellValue(nullSafe(userPayment.getUserPgPropertyName()));
					row.createCell(3).setCellValue(nullSafe(userPayment.getPropertyHouseArea()));
					row.createCell(4).setCellValue(nullSafe(userPayment.getRoomBedNumber()));
					row.createCell(5).setCellValue(nullSafe(tuService.formatTimestamp(userPayment.getTransactionDate().toInstant())));
					row.createCell(6).setCellValue(nullSafe(userPayment.getTransactionNumber()));
					row.createCell(7).setCellValue(nullSafe(userPayment.getTransactionStatus()));
					setCurrencyCell(row, 8, nullSafe(userPayment.getDueAmount()));
					setCurrencyCell(row, 9, nullSafe(userPayment.getGstAmount()));
					setCurrencyCell(row, 10, nullSafe(userPayment.getTotalAmount()));
					row.createCell(11).setCellValue(nullSafe(userPayment.getCategory()));
					row.createCell(12).setCellValue(nullSafe(userPayment.getPaymentMode()));
				}else {
					row.createCell(0).setCellValue(nullSafe(tuService.formatTimestamp(userPayment.getTransactionDate().toInstant())));
					row.createCell(1).setCellValue(nullSafe(userPayment.getUserPersonalName()));
					row.createCell(2).setCellValue(nullSafe(userPayment.getTenantContactNum()));
					row.createCell(3).setCellValue(nullSafe(userPayment.getEmail()));
					setCurrencyCell(row, 4, nullSafe(userPayment.getTotalAmount()));
					row.createCell(5).setCellValue(nullSafe(userPayment.getFailedReason()));
				}
			}
			break;

		case "consolidatedFinanceReport":
			if (dto instanceof ConsilidatedFinanceDetails) {
				ConsilidatedFinanceDetails finance = (ConsilidatedFinanceDetails) dto;
				row.createCell(0).setCellValue(nullSafe(tuService.formatTimestamp(finance.getUserPaymentTimestamp().toInstant())));
				row.createCell(1).setCellValue(nullSafe(finance.getUserPaymentBankTransactionId()));
				row.createCell(2).setCellValue(nullSafe(finance.getPayerPayeeType()));
				row.createCell(3).setCellValue(nullSafe(finance.getPayerPayeeName()));
				setCurrencyCell(row,4,nullSafe(finance.getDebitAmount()));
				setCurrencyCell(row,5,nullSafe(finance.getCreditAmount()));
				row.createCell(6).setCellValue(nullSafe(finance.getPgName()));
				row.createCell(7).setCellValue(nullSafe(finance.getContactNum()));
			}
			break;

		case "tenantDuesReport":
			if (dto instanceof TenentDues) {
				TenentDues dues = (TenentDues) dto;
				row.createCell(0).setCellValue(nullSafe(dues.getUserPersonalName())); 
				row.createCell(1).setCellValue(nullSafe(dues.getTenantMobileNum())); 
				row.createCell(2).setCellValue(nullSafe(dues.getUserPgPropertyName()));     
				row.createCell(3).setCellValue(nullSafe(dues.getUserPgPropertyAddress()));  
				row.createCell(4).setCellValue(nullSafe(dues.getBedNumber()));
				setCurrencyCell(row,5,nullSafe(dues.getPendingAmount()));
				row.createCell(6).setCellValue(nullSafe(dues.getPendingDueDate()));          
			}
			break;

		case "vendorPaymentsReport":
			if (dto instanceof VendorPayments) {
				VendorPayments vendor = (VendorPayments) dto;
				row.createCell(0).setCellValue(nullSafe(vendor.getOwnerName()));
				row.createCell(1).setCellValue(nullSafe(vendor.getPgName()));
				row.createCell(2).setCellValue(nullSafe(vendor.getOwnerEmail()));
				row.createCell(3).setCellValue(nullSafe(vendor.getPgAddress()));
				setCurrencyCell(row,4,nullSafe(vendor.getTotalAmountFromTenants()));
				setCurrencyCell(row,5,nullSafe(vendor.getAmountPaidToOwner()));
				setCurrencyCell(row,6,nullSafe(vendor.getZoyShare()));
				row.createCell(7).setCellValue(nullSafe(tuService.formatTimestamp(vendor.getTransactionDate().toInstant())));
				row.createCell(8).setCellValue(nullSafe(vendor.getTransactionNumber()));
				row.createCell(9).setCellValue(nullSafe(vendor.getPaymentStatus()));
				row.createCell(10).setCellValue(nullSafe(vendor.getOwnerApprovalStatus()));
			}
			break;

		case "vendorPaymentsDuesReport":
			if (dto instanceof VendorPaymentsDues) {
				VendorPaymentsDues vendorDues = (VendorPaymentsDues) dto;
				row.createCell(0).setCellValue(nullSafe(vendorDues.getOwnerId()));
				row.createCell(1).setCellValue(nullSafe(vendorDues.getOwnerName()));
				setCurrencyCell(row,2,nullSafe(vendorDues.getPendingAmount()));
				row.createCell(3).setCellValue(nullSafe(tuService.formatTimestamp(vendorDues.getPendingDueDate().toInstant())));
				row.createCell(4).setCellValue(nullSafe(vendorDues.getPgId()));
				row.createCell(5).setCellValue(nullSafe(vendorDues.getPgName()));
				setCurrencyCell(row,6,nullSafe(vendorDues.getTotalAmountPaid()));
				setCurrencyCell(row,7,nullSafe(vendorDues.getTotalAmountPayable()));
			}
			break;

		case "vendorPaymentsGstReport":
			if (dto instanceof VendorPaymentsGst) {
				VendorPaymentsGst vendorGst = (VendorPaymentsGst) dto;
				row.createCell(0).setCellValue(nullSafe(tuService.formatTimestamp(vendorGst.getTransactionDate().toInstant())));
				row.createCell(1).setCellValue(nullSafe(vendorGst.getTransactionNo()));
				row.createCell(2).setCellValue(nullSafe(vendorGst.getPgId()));
				row.createCell(3).setCellValue(nullSafe(vendorGst.getPgName()));
				setCurrencyCell(row,4,nullSafe(vendorGst.getTotalAmount()));
				setCurrencyCell(row,5,nullSafe(vendorGst.getGstAmount()));
				setCurrencyCell(row,6,nullSafe(vendorGst.getBasicAmount()));
				row.createCell(7).setCellValue(nullSafe(vendorGst.getPaymentMethod()));
			}
			break;
			
		case "tenantRefundReport":
			if (dto instanceof TenentRefund) {
				TenentRefund tenentRefund = (TenentRefund) dto;
				row.createCell(0).setCellValue(nullSafe(tenentRefund.getCustomerName()));
				row.createCell(1).setCellValue(nullSafe(tenentRefund.getTenantMobileNum()));
				row.createCell(2).setCellValue(nullSafe(tenentRefund.getPgPropertyName()));
				row.createCell(3).setCellValue(nullSafe(tenentRefund.getUserPgPropertyAddress()));
				row.createCell(4).setCellValue(nullSafe(tenentRefund.getBookingId()));
				row.createCell(5).setCellValue(nullSafe(tenentRefund.getRefundTitle()));
				setCurrencyCell(row,6,nullSafe(tenentRefund.getRefundableAmount()));
				row.createCell(7).setCellValue(nullSafe(tuService.formatTimestamp(tenentRefund.getPaymentDate().toInstant())));
				row.createCell(8).setCellValue(nullSafe(tenentRefund.getTransactionNumber()));
				row.createCell(9).setCellValue(nullSafe(tenentRefund.getPaymentStatus()));
			}
			break;	
			
		case "reviewsAndRatingReport":
			if (dto instanceof RatingsAndReviewsReport) {
				RatingsAndReviewsReport ratingsAndReviews = (RatingsAndReviewsReport) dto;
				row.createCell(0).setCellValue(nullSafe(tuService.formatTimestamp(ratingsAndReviews.getReviewDate().toInstant())));
				row.createCell(1).setCellValue(nullSafe(ratingsAndReviews.getCustomerName()));
				row.createCell(2).setCellValue(nullSafe(ratingsAndReviews.getPropertyName()));
				row.createCell(3).setCellValue(nullSafe(ratingsAndReviews.getCustomerMobileNo()));
				row.createCell(4).setCellValue(nullSafe(ratingsAndReviews.getCleanliness()));
				row.createCell(5).setCellValue(nullSafe(ratingsAndReviews.getAccommodation()));
				row.createCell(6).setCellValue(nullSafe(ratingsAndReviews.getAmenities()));
				row.createCell(7).setCellValue(nullSafe(ratingsAndReviews.getMaintenance()));
				row.createCell(8).setCellValue(nullSafe(ratingsAndReviews.getValueForMoney()));
				row.createCell(9).setCellValue(nullSafe(ratingsAndReviews.getOverallRating()));
			}
			break;
		case "UpcomingTenantsReport":
			if (dto instanceof TenantResportsDTO) {
				TenantResportsDTO upcomingTenant = (TenantResportsDTO) dto;
				row.createCell(0).setCellValue(nullSafe(upcomingTenant.getTenantName()));
				row.createCell(1).setCellValue(nullSafe(upcomingTenant.getTenantContactNumber()));
				row.createCell(2).setCellValue(nullSafe(upcomingTenant.getTenantEmailAddress()));
				row.createCell(3).setCellValue(nullSafe(upcomingTenant.getBookedProperyName()));
				row.createCell(4).setCellValue(nullSafe(upcomingTenant.getPropertAddress()));
				row.createCell(5).setCellValue(nullSafe(upcomingTenant.getBedNumber()));
				row.createCell(6).setCellValue(nullSafe(tuService.formatTimestamp(upcomingTenant.getExpectedCheckIndate().toInstant())));
				row.createCell(7).setCellValue(nullSafe(tuService.formatTimestamp(upcomingTenant.getExpectedCheckOutdate().toInstant())));
				
			}
			break;
			
		case "ActiveTenantsReport":
			if (dto instanceof TenantResportsDTO) {
				TenantResportsDTO activeTenants = (TenantResportsDTO) dto;
				row.createCell(0).setCellValue(nullSafe(activeTenants.getTenantName()));
				row.createCell(1).setCellValue(nullSafe(activeTenants.getTenantContactNumber()));
				row.createCell(2).setCellValue(nullSafe(activeTenants.getTenantEmailAddress()));
				row.createCell(3).setCellValue(nullSafe(activeTenants.getCurrentPropertName()));
				row.createCell(4).setCellValue(nullSafe(activeTenants.getPropertAddress()));
				row.createCell(5).setCellValue(nullSafe(activeTenants.getBedNumber()));
				row.createCell(6).setCellValue(nullSafe(tuService.formatTimestamp(activeTenants.getCheckInDate().toInstant())));
				row.createCell(7).setCellValue(nullSafe(tuService.formatTimestamp(activeTenants.getExpectedCheckOutdate().toInstant())));
				
			}
			break;
		case "InactiveTenantsReport":
			if (dto instanceof TenantResportsDTO) {
				TenantResportsDTO inActiveTenants = (TenantResportsDTO) dto;
				row.createCell(0).setCellValue(nullSafe(inActiveTenants.getTenantName()));
				row.createCell(1).setCellValue(nullSafe(inActiveTenants.getTenantContactNumber()));
				row.createCell(2).setCellValue(nullSafe(inActiveTenants.getTenantEmailAddress()));
				row.createCell(3).setCellValue(nullSafe(inActiveTenants.getPreviousPropertName()));
				row.createCell(4).setCellValue(nullSafe(inActiveTenants.getPropertAddress()));
				row.createCell(5).setCellValue(nullSafe(inActiveTenants.getBedNumber()));
				row.createCell(6).setCellValue(nullSafe(tuService.formatTimestamp(inActiveTenants.getCheckInDate().toInstant())));				
				row.createCell(7).setCellValue(nullSafe(tuService.formatTimestamp(inActiveTenants.getCheckedOutDate().toInstant())));				
			}
			break;	
		case "SuspendedTenantsReport":
			if (dto instanceof TenantResportsDTO) {
				TenantResportsDTO suspendedTenant = (TenantResportsDTO) dto;
				row.createCell(0).setCellValue(nullSafe(suspendedTenant.getTenantName()));
				row.createCell(1).setCellValue(nullSafe(suspendedTenant.getTenantContactNumber()));
				row.createCell(2).setCellValue(nullSafe(suspendedTenant.getTenantEmailAddress()));
				row.createCell(3).setCellValue(nullSafe(suspendedTenant.getPreviousPropertName()));
				row.createCell(4).setCellValue(nullSafe(suspendedTenant.getPropertAddress()));
				row.createCell(5).setCellValue(nullSafe(suspendedTenant.getBedNumber()));
				row.createCell(6).setCellValue(nullSafe(tuService.formatTimestamp(suspendedTenant.getCheckedOutDate().toInstant())));
				row.createCell(7).setCellValue(nullSafe(tuService.formatTimestamp(suspendedTenant.getSuspendedDate().toInstant())));				
				row.createCell(8).setCellValue(nullSafe(suspendedTenant.getReasonForSuspension()));				
			}
			break;	
		case "InactivePropertiesReport":
			if (dto instanceof PropertyResportsDTO) {
				PropertyResportsDTO inActivePropertyDetails = (PropertyResportsDTO) dto;
				row.createCell(0).setCellValue(nullSafe(inActivePropertyDetails.getOwnerFullName()));
				row.createCell(1).setCellValue(nullSafe(inActivePropertyDetails.getPropertyName()));
				row.createCell(2).setCellValue(nullSafe(inActivePropertyDetails.getPropertyContactNumber()));
				row.createCell(3).setCellValue(nullSafe(inActivePropertyDetails.getPropertyEmailAddress()));
				row.createCell(4).setCellValue(nullSafe(inActivePropertyDetails.getPropertyAddress()));
			}
			break;
		case "PotentialPropertyReport":
			if (dto instanceof PropertyResportsDTO) {
				PropertyResportsDTO potentialPropertyReport = (PropertyResportsDTO) dto;
				row.createCell(0).setCellValue(nullSafe(potentialPropertyReport.getOwnerFullName()));
				row.createCell(1).setCellValue(nullSafe(potentialPropertyReport.getPropertyName()));
				row.createCell(2).setCellValue(nullSafe(potentialPropertyReport.getPropertyContactNumber()));
				row.createCell(3).setCellValue(nullSafe(potentialPropertyReport.getPropertyEmailAddress()));
				row.createCell(4).setCellValue(nullSafe(potentialPropertyReport.getPropertyAddress()));
				setCurrencyCell(row,5,nullSafe(potentialPropertyReport.getExpectedRentPerMonth()));
				row.createCell(6).setCellValue(nullSafe(potentialPropertyReport.getNumberOfBeds()));			
			}
			break;
		case "RegesterTenantsReport":
			if (dto instanceof RegisterTenantsDTO) {
				RegisterTenantsDTO registeredTenantsDetails = (RegisterTenantsDTO) dto;
				row.createCell(0).setCellValue(nullSafe(registeredTenantsDetails.getTenantName()));
				row.createCell(1).setCellValue(nullSafe(registeredTenantsDetails.getTenantContactNumber()));
				row.createCell(2).setCellValue(nullSafe(registeredTenantsDetails.getTenantEmailAddress()));
				row.createCell(3).setCellValue(nullSafe(tuService.formatTimestamp(registeredTenantsDetails.getRegistrationDate().toInstant())));
			}
			break;
		case "SuspendedPropertiesReport":
			if (dto instanceof PropertyResportsDTO) {
				PropertyResportsDTO suspendedPropertyDetails = (PropertyResportsDTO) dto;
				row.createCell(0).setCellValue(nullSafe(suspendedPropertyDetails.getOwnerFullName()));
				row.createCell(1).setCellValue(nullSafe(suspendedPropertyDetails.getPropertyName()));
				row.createCell(2).setCellValue(nullSafe(suspendedPropertyDetails.getPropertyContactNumber()));
				row.createCell(3).setCellValue(nullSafe(suspendedPropertyDetails.getPropertyEmailAddress()));
				row.createCell(4).setCellValue(nullSafe(suspendedPropertyDetails.getPropertyAddress()));
				row.createCell(5).setCellValue(nullSafe(tuService.formatTimestamp(suspendedPropertyDetails.getSuspendedDate().toInstant())));
				row.createCell(6).setCellValue(nullSafe(suspendedPropertyDetails.getReasonForSuspension()));			
			}
			break;	
		default:
			//throw new IllegalArgumentException("Invalid report type provided: " + reportType);
			new ZoyAdminApplicationException(new Exception() ,"Invalid report type provided: " + reportType);
		}
	}

	private String nullSafe(Object value) {
		return (value == null) ? "N/A" : value.toString();
	}
}
