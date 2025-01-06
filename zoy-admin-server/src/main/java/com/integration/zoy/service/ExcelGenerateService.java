package com.integration.zoy.service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.integration.zoy.exception.ZoyAdminApplicationException;
import com.integration.zoy.utils.ConsilidatedFinanceDetails;
import com.integration.zoy.utils.RatingsAndReviewsReport;
import com.integration.zoy.utils.TenentDues;
import com.integration.zoy.utils.TenentRefund;
import com.integration.zoy.utils.UserPaymentDTO;
import com.integration.zoy.utils.VendorPayments;
import com.integration.zoy.utils.VendorPaymentsDues;
import com.integration.zoy.utils.VendorPaymentsGst;

@Service
public class ExcelGenerateService {

	public byte[] generateExcelFile(String reportType, Map<String, Object> data) {
		List<?> reportData = (List<?>) data.get("reportData");

		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
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

		} catch (Exception e) {
			//throw new RuntimeException("Error generating Excel file", e);
			new ZoyAdminApplicationException(e, "Error generating Excel file");
		}
		return null;
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
			row.createCell(6).setCellValue("Transaction No");
			row.createCell(7).setCellValue("Transaction Status");
			row.createCell(8).setCellValue("Due Amount");
			row.createCell(9).setCellValue("GST Amount");
			row.createCell(10).setCellValue("Total Amount");
			row.createCell(11).setCellValue("Category");
			row.createCell(12).setCellValue("Mode of Payment");
			break;
		case "userPaymentGstReport":
			row.createCell(0).setCellValue("Transaction Date");
			row.createCell(1).setCellValue("Transaction No");
			row.createCell(2).setCellValue("Tenant Name");
			row.createCell(3).setCellValue("PG Name");
			row.createCell(4).setCellValue("PG Address");
			row.createCell(5).setCellValue("Total Amount");
			row.createCell(6).setCellValue("GST Amount");
			row.createCell(7).setCellValue("Due Amount");
			row.createCell(8).setCellValue("MODE OF PAYMENT");
			break;
		case "consolidatedFinanceReport":
			row.createCell(0).setCellValue("Transaction Date");
			row.createCell(1).setCellValue("Transaction Number");
			row.createCell(2).setCellValue("Payer/Payee Type");
			row.createCell(3).setCellValue("Name of the Payer/Payee");
			row.createCell(4).setCellValue("Debit");
			row.createCell(5).setCellValue("Credit");
			break;
		case "tenantDuesReport":
			row.createCell(0).setCellValue("Tenant Name");
			row.createCell(1).setCellValue("Tenant Mobile Number");
			row.createCell(2).setCellValue("PG Name");
			row.createCell(3).setCellValue("PG Address");
			row.createCell(4).setCellValue("Bed No");
			row.createCell(5).setCellValue("Pending Amount");
			row.createCell(6).setCellValue("Payment Due Date");
			break;
		case "vendorPaymentsReport":
			row.createCell(0).setCellValue("Owner Name");
			row.createCell(1).setCellValue("PG Name");
			row.createCell(2).setCellValue("Owner Email ID");
			row.createCell(3).setCellValue("PG Address");
			row.createCell(4).setCellValue("Total Amount Received from Tenants");
			row.createCell(5).setCellValue("Total Amount Paid to Owner");
			row.createCell(6).setCellValue("ZOY Share");
			row.createCell(7).setCellValue("Transaction Date");
			row.createCell(8).setCellValue("Transaction Number");
			row.createCell(9).setCellValue("Payment Status");
			row.createCell(10).setCellValue("Owner approval Status");
			break;
		case "vendorPaymentsDuesReport":
			row.createCell(0).setCellValue("Owner ID");
			row.createCell(1).setCellValue("Owner Name");
			row.createCell(2).setCellValue("Pending Amount");
			row.createCell(3).setCellValue("Pending Due Date");
			row.createCell(4).setCellValue("Property ID");
			row.createCell(5).setCellValue("Property Name");
			row.createCell(6).setCellValue("Total Amount Paid");
			row.createCell(7).setCellValue("Total Amount Payable");
			break;
		case "vendorPaymentsGstReport":
			row.createCell(0).setCellValue("Transaction Date");
			row.createCell(1).setCellValue("Transaction No");
			row.createCell(2).setCellValue("Property ID");
			row.createCell(3).setCellValue("Property Name");
			row.createCell(4).setCellValue("Total Amount");
			row.createCell(5).setCellValue("GST Amount");
			row.createCell(6).setCellValue("Basic Amount");
			row.createCell(7).setCellValue("Payment Method");
			break;
		case "tenantRefundReport":
			row.createCell(0).setCellValue("Tenant Name");
			row.createCell(1).setCellValue("Tenant Mobile Number");
			row.createCell(2).setCellValue("PG Name");
			row.createCell(3).setCellValue("PG Address");
			row.createCell(4).setCellValue("Booking ID");
			row.createCell(5).setCellValue("Refund Title");
			row.createCell(6).setCellValue("Refundable Amount");
			row.createCell(7).setCellValue("Amount Paid");
			row.createCell(8).setCellValue("Payment Date");
			row.createCell(9).setCellValue("Transaction Number");
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
		default:
			throw new IllegalArgumentException("Invalid report type provided: " + reportType);
		}
	}

	private void createExcelDataRow(Row row, Object dto, String reportType) {
		switch (reportType) {
		case "userTransactionReport":
		case "userPaymentGstReport":
			if (dto instanceof UserPaymentDTO) {
				UserPaymentDTO userPayment = (UserPaymentDTO) dto;

				if ("userPaymentGstReport".equals(reportType)) {
					row.createCell(0).setCellValue(nullSafe(userPayment.getTransactionDate()));
					row.createCell(1).setCellValue(nullSafe(userPayment.getTransactionNumber()));
					row.createCell(2).setCellValue(nullSafe(userPayment.getUserPersonalName()));
					row.createCell(3).setCellValue(nullSafe(userPayment.getUserPgPropertyName()));
					row.createCell(4).setCellValue(nullSafe(userPayment.getPropertyHouseArea()));
					row.createCell(5).setCellValue(nullSafe(userPayment.getTotalAmount()));
					row.createCell(6).setCellValue(nullSafe(userPayment.getGstAmount()));
					row.createCell(7).setCellValue(nullSafe(userPayment.getDueAmount()));
					row.createCell(8).setCellValue(nullSafe(userPayment.getPaymentMode()));
				} else {
					row.createCell(0).setCellValue(nullSafe(userPayment.getUserPersonalName()));
					row.createCell(1).setCellValue(nullSafe(userPayment.getTenantContactNum()));
					row.createCell(2).setCellValue(nullSafe(userPayment.getUserPgPropertyName()));
					row.createCell(3).setCellValue(nullSafe(userPayment.getPropertyHouseArea()));
					row.createCell(4).setCellValue(nullSafe(userPayment.getRoomBedNumber()));
					row.createCell(5).setCellValue(nullSafe(userPayment.getTransactionDate()));
					row.createCell(6).setCellValue(nullSafe(userPayment.getTransactionNumber()));
					row.createCell(7).setCellValue(nullSafe(userPayment.getTransactionStatus()));
					row.createCell(8).setCellValue(nullSafe(userPayment.getDueAmount()));
					row.createCell(9).setCellValue(nullSafe(userPayment.getGstAmount()));
					row.createCell(10).setCellValue(nullSafe(userPayment.getTotalAmount()));
					row.createCell(11).setCellValue(nullSafe(userPayment.getCategory()));
					row.createCell(12).setCellValue(nullSafe(userPayment.getPaymentMode()));
				}
			}
			break;

		case "consolidatedFinanceReport":
			if (dto instanceof ConsilidatedFinanceDetails) {
				ConsilidatedFinanceDetails finance = (ConsilidatedFinanceDetails) dto;
				row.createCell(0).setCellValue(nullSafe(finance.getUserPaymentTimestamp()));
				row.createCell(1).setCellValue(nullSafe(finance.getUserPaymentBankTransactionId()));
				row.createCell(2).setCellValue(nullSafe(finance.getPayerPayeeType()));
				row.createCell(3).setCellValue(nullSafe(finance.getPayerPayeeName()));
				row.createCell(4).setCellValue(nullSafe(finance.getDebitAmount()));
				row.createCell(5).setCellValue(nullSafe(finance.getCreditAmount()));
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
				row.createCell(5).setCellValue(nullSafe(dues.getPendingAmount()));           
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
				row.createCell(4).setCellValue(nullSafe(vendor.getTotalAmountFromTenants())); 
				row.createCell(5).setCellValue(nullSafe(vendor.getAmountPaidToOwner()));
				row.createCell(6).setCellValue(nullSafe(vendor.getZoyShare()));
				row.createCell(7).setCellValue(nullSafe(vendor.getTransactionDate()));
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
				row.createCell(2).setCellValue(nullSafe(vendorDues.getPendingAmount()));
				row.createCell(3).setCellValue(nullSafe(vendorDues.getPendingDueDate()));
				row.createCell(4).setCellValue(nullSafe(vendorDues.getPgId()));
				row.createCell(5).setCellValue(nullSafe(vendorDues.getPgName()));
				row.createCell(6).setCellValue(nullSafe(vendorDues.getTotalAmountPaid()));
				row.createCell(7).setCellValue(nullSafe(vendorDues.getTotalAmountPayable()));

			}
			break;

		case "vendorPaymentsGstReport":
			if (dto instanceof VendorPaymentsGst) {
				VendorPaymentsGst vendorGst = (VendorPaymentsGst) dto;
				row.createCell(0).setCellValue(nullSafe(vendorGst.getTransactionDate()));
				row.createCell(1).setCellValue(nullSafe(vendorGst.getTransactionNo()));
				row.createCell(2).setCellValue(nullSafe(vendorGst.getPgId()));
				row.createCell(3).setCellValue(nullSafe(vendorGst.getPgName()));
				row.createCell(4).setCellValue(nullSafe(vendorGst.getTotalAmount()));
				row.createCell(5).setCellValue(nullSafe(vendorGst.getGstAmount()));
				row.createCell(6).setCellValue(nullSafe(vendorGst.getBasicAmount()));
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
				row.createCell(6).setCellValue(nullSafe(tenentRefund.getRefundableAmount()));
				row.createCell(7).setCellValue(nullSafe(tenentRefund.getPaymentDate()));
				row.createCell(8).setCellValue(nullSafe(tenentRefund.getTransactionNumber()));
				row.createCell(9).setCellValue(nullSafe(tenentRefund.getPaymentStatus()));
			}
			break;	
			
		case "reviewsAndRatingReport":
			if (dto instanceof RatingsAndReviewsReport) {
				RatingsAndReviewsReport ratingsAndReviews = (RatingsAndReviewsReport) dto;
				row.createCell(0).setCellValue(nullSafe(ratingsAndReviews.getReviewDate()));
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

		default:
			//throw new IllegalArgumentException("Invalid report type provided: " + reportType);
			new ZoyAdminApplicationException(new Exception() ,"Invalid report type provided: " + reportType);
		}
	}

	private String nullSafe(Object value) {
		return (value == null) ? "N/A" : value.toString();
	}
}
