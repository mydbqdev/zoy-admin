package com.integration.zoy.service;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.integration.zoy.utils.ConsilidatedFinanceDetails;
import com.integration.zoy.utils.TenentDues;
import com.integration.zoy.utils.UserPaymentDTO;
import com.integration.zoy.utils.VendorPayments;
import com.integration.zoy.utils.VendorPaymentsDues;
import com.integration.zoy.utils.VendorPaymentsGst;

@Service
public class ExcelGenerateService {

	public byte[] generateExcelFile(String templateName, Map<String, Object> data) {
		List<?> reportData = (List<?>) data.get("reportData");

		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet(templateName);

			if (reportData == null || reportData.isEmpty()) {
		        return new byte[0];
		    }
				Row headerRow = sheet.createRow(0);
				createExcelHeaderRow(headerRow, reportData.get(0));

				for (int i = 0; i < reportData.size(); i++) {
					Row dataRow = sheet.createRow(i + 1);
					createExcelDataRow(dataRow, reportData.get(i));
				}
			

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return outputStream.toByteArray();

		} catch (Exception e) {
			throw new RuntimeException("Error generating Excel file", e);
		}
	}

	private void createExcelHeaderRow(Row row, Object dto) {
		if (dto instanceof UserPaymentDTO) {
			row.createCell(0).setCellValue("User ID");
			row.createCell(1).setCellValue("Payment Timestamp");
			row.createCell(2).setCellValue("Bank Transaction ID");
			row.createCell(3).setCellValue("Result Status");
			row.createCell(4).setCellValue("Payable Amount");
			row.createCell(5).setCellValue("GST");
			row.createCell(6).setCellValue("Personal Name");
			row.createCell(7).setCellValue("Property Name");
			row.createCell(8).setCellValue("Property ID");
			row.createCell(9).setCellValue("Bed Number");
			row.createCell(10).setCellValue("Total Amount");
			row.createCell(11).setCellValue("Category");
			row.createCell(12).setCellValue("Payment Method");
		} else if (dto instanceof ConsilidatedFinanceDetails) {
			row.createCell(0).setCellValue("User ID");
			row.createCell(1).setCellValue("Payment Timestamp");
			row.createCell(2).setCellValue("Bank Transaction ID");
			row.createCell(3).setCellValue("Personal Name");
			row.createCell(4).setCellValue("Total Amount");
			row.createCell(5).setCellValue("Debit Amount");
		} else if (dto instanceof TenentDues) {
			row.createCell(0).setCellValue("User ID");
			row.createCell(1).setCellValue("Pending Amount");
			row.createCell(2).setCellValue("Pending Due Date");
			row.createCell(3).setCellValue("Personal Name");
			row.createCell(4).setCellValue("Property Name");
			row.createCell(5).setCellValue("Property ID");
			row.createCell(6).setCellValue("Bed Number");
		} else if (dto instanceof VendorPayments) {
			row.createCell(0).setCellValue("Owner ID");
			row.createCell(1).setCellValue("Owner Name");
			row.createCell(2).setCellValue("Property ID");
			row.createCell(3).setCellValue("Property Name");
			row.createCell(4).setCellValue("Total Amount From Tenants");
			row.createCell(5).setCellValue("Transaction Date");
			row.createCell(6).setCellValue("Transaction Number");
			row.createCell(7).setCellValue("Payment Status");
			row.createCell(8).setCellValue("Amount Paid to Owner");
			row.createCell(9).setCellValue("Zoy Commission");
		} else if (dto instanceof VendorPaymentsDues) {
			row.createCell(0).setCellValue("Owner ID");
			row.createCell(1).setCellValue("Owner Name");
			row.createCell(2).setCellValue("Pending Amount");
			row.createCell(3).setCellValue("Pending Due Date");
			row.createCell(4).setCellValue("Property ID");
			row.createCell(5).setCellValue("Property Name");
			row.createCell(6).setCellValue("Total Amount Paid");
			row.createCell(7).setCellValue("Total Amount Payable");
		} else if (dto instanceof VendorPaymentsGst) {
			row.createCell(0).setCellValue("Transaction Date");
			row.createCell(1).setCellValue("Transaction No");
			row.createCell(2).setCellValue("Property ID");
			row.createCell(3).setCellValue("Property Name");
			row.createCell(4).setCellValue("Total Amount");
			row.createCell(5).setCellValue("GST Amount");
			row.createCell(6).setCellValue("Basic Amount");
			row.createCell(7).setCellValue("Payment Method");
		}
	}

	private void createExcelDataRow(Row row, Object dto) {
		if (dto instanceof UserPaymentDTO) {
			UserPaymentDTO userPayment = (UserPaymentDTO) dto;
			row.createCell(0).setCellValue(nullSafe(userPayment.getUserId()));
			row.createCell(1).setCellValue(nullSafe(userPayment.getUserPaymentTimestamp()));
			row.createCell(2).setCellValue(nullSafe(userPayment.getUserPaymentBankTransactionId()));
			row.createCell(3).setCellValue(nullSafe(userPayment.getUserPaymentResultStatus()));
			row.createCell(4).setCellValue(nullSafe(userPayment.getUserPaymentPayableAmount()));
			row.createCell(5).setCellValue(nullSafe(userPayment.getUserPaymentGst()));
			row.createCell(6).setCellValue(nullSafe(userPayment.getUserPersonalName()));
			row.createCell(7).setCellValue(nullSafe(userPayment.getUserPgPropertyName()));
			row.createCell(8).setCellValue(nullSafe(userPayment.getUserPgPropertyId()));
			row.createCell(9).setCellValue(nullSafe(userPayment.getBedNumber()));
			row.createCell(10).setCellValue(nullSafe(userPayment.getTotalAmount()));
			row.createCell(11).setCellValue(nullSafe(userPayment.getCategory()));
			row.createCell(12).setCellValue(nullSafe(userPayment.getPaymentMethod()));
		} else if (dto instanceof ConsilidatedFinanceDetails) {
			ConsilidatedFinanceDetails finance = (ConsilidatedFinanceDetails) dto;
			row.createCell(0).setCellValue(nullSafe(finance.getUserId()));
			row.createCell(1).setCellValue(nullSafe(finance.getUserPaymentTimestamp()));
			row.createCell(2).setCellValue(nullSafe(finance.getUserPaymentBankTransactionId()));
			row.createCell(3).setCellValue(nullSafe(finance.getUserPersonalName()));
			row.createCell(4).setCellValue(nullSafe(finance.getTotalAmount()));
			row.createCell(5).setCellValue(nullSafe(finance.getDebitAmount()));
		} else if (dto instanceof TenentDues) {
			TenentDues dues = (TenentDues) dto;
			row.createCell(0).setCellValue(nullSafe(dues.getUserId()));
			row.createCell(1).setCellValue(nullSafe(dues.getPendingAmount()));
			row.createCell(2).setCellValue(nullSafe(dues.getPendingDueDate()));
			row.createCell(3).setCellValue(nullSafe(dues.getUserPersonalName()));
			row.createCell(4).setCellValue(nullSafe(dues.getUserPgPropertyName()));
			row.createCell(5).setCellValue(nullSafe(dues.getUserPgPropertyId()));
			row.createCell(6).setCellValue(nullSafe(dues.getBedNumber()));
		} else if (dto instanceof VendorPayments) {
			VendorPayments vendor = (VendorPayments) dto;
			row.createCell(0).setCellValue(nullSafe(vendor.getOwnerId()));
			row.createCell(1).setCellValue(nullSafe(vendor.getOwnerName()));
			row.createCell(2).setCellValue(nullSafe(vendor.getPgId()));
			row.createCell(3).setCellValue(nullSafe(vendor.getPgName()));
			row.createCell(4).setCellValue(nullSafe(vendor.getTotalAmountFromTenants()));
			row.createCell(5).setCellValue(nullSafe(vendor.getTransactionDate()));
			row.createCell(6).setCellValue(nullSafe(vendor.getTransactionNumber()));
			row.createCell(7).setCellValue(nullSafe(vendor.getPaymentStatus()));
			row.createCell(8).setCellValue(nullSafe(vendor.getAmountPaidToOwner()));
			row.createCell(9).setCellValue(nullSafe(vendor.getZoyCommission()));
		} else if (dto instanceof VendorPaymentsDues) {
			VendorPaymentsDues vendorDues = (VendorPaymentsDues) dto;
			row.createCell(0).setCellValue(nullSafe(vendorDues.getOwnerId()));
			row.createCell(1).setCellValue(nullSafe(vendorDues.getOwnerName()));
			row.createCell(2).setCellValue(nullSafe(vendorDues.getPendingAmount()));
			row.createCell(3).setCellValue(nullSafe(vendorDues.getPendingDueDate()));
			row.createCell(4).setCellValue(nullSafe(vendorDues.getPgId()));
			row.createCell(5).setCellValue(nullSafe(vendorDues.getPgName()));
			row.createCell(6).setCellValue(nullSafe(vendorDues.getTotalAmountPaid()));
			row.createCell(7).setCellValue(nullSafe(vendorDues.getTotalAmountPayable()));
		} else if (dto instanceof VendorPaymentsGst) {
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
	}

	private String nullSafe(Object value) {
		return (value == null) ? "N/A" : value.toString();
	}


}
