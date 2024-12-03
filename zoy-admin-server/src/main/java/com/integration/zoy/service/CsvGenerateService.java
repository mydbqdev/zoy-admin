package com.integration.zoy.service;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.integration.zoy.utils.ConsilidatedFinanceDetails;
import com.integration.zoy.utils.TenentDues;
import com.integration.zoy.utils.UserPaymentDTO;
import com.integration.zoy.utils.VendorPayments;
import com.integration.zoy.utils.VendorPaymentsDues;
import com.integration.zoy.utils.VendorPaymentsGst;
@Service
public class CsvGenerateService {

	public byte[] generateCsvFile(String templateName, Map<String, Object> data) {
	    List<?> reportData = (List<?>) data.get("reportData");
	    
	    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	         PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {

	        if (reportData == null || reportData.isEmpty()) {
			    return new byte[0];
	        } else {
	            createCsvHeaderRow(writer, reportData.get(0));

	            for (Object dto : reportData) {
	                createCsvDataRow(writer, dto);
	            }
	        }

	        writer.flush();
	        return outputStream.toByteArray();

	    } catch (Exception e) {
	        throw new RuntimeException("Error generating CSV file", e);
	    }
	}


    private void createCsvHeaderRow(PrintWriter writer, Object dto) {
        if (dto instanceof UserPaymentDTO) {
            writer.println("User ID,Payment Timestamp,Bank Transaction ID,Result Status,Payable Amount,GST,Personal Name,Property Name,Property ID,Bed Number,Total Amount,Category,Payment Method");
        } else if (dto instanceof ConsilidatedFinanceDetails) {
            writer.println("User ID,Payment Timestamp,Bank Transaction ID,Personal Name,Total Amount,Debit Amount");
        } else if (dto instanceof TenentDues) {
            writer.println("User ID,Pending Amount,Pending Due Date,Personal Name,Property Name,Property ID,Bed Number");
        } else if (dto instanceof VendorPayments) {
            writer.println("Owner ID,Owner Name,Property ID,Property Name,Total Amount From Tenants,Transaction Date,Transaction Number,Payment Status,Amount Paid to Owner,Zoy Commission");
        } else if (dto instanceof VendorPaymentsDues) {
            writer.println("Owner ID,Owner Name,Pending Amount,Pending Due Date,Property ID,Property Name,Total Amount Paid,Total Amount Payable");
        } else if (dto instanceof VendorPaymentsGst) {
            writer.println("Transaction Date,Transaction No,Property ID,Property Name,Total Amount,GST Amount,Basic Amount,Payment Method");
        }
    }

    private void createCsvDataRow(PrintWriter writer, Object dto) {
        if (dto instanceof UserPaymentDTO) {
            UserPaymentDTO userPayment = (UserPaymentDTO) dto;
            writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                    userPayment.getUserId(),
                    safeToString(userPayment.getUserPaymentTimestamp()),
                    safeToString(userPayment.getUserPaymentBankTransactionId()),
                    safeToString(userPayment.getUserPaymentResultStatus()),
                    safeToString(userPayment.getUserPaymentPayableAmount()),
                    safeToString(userPayment.getUserPaymentGst()),
                    safeToString(userPayment.getUserPersonalName()),
                    safeToString(userPayment.getUserPgPropertyName()),
                    safeToString(userPayment.getUserPgPropertyId()),
                    safeToString(userPayment.getBedNumber()),
                    safeToString(userPayment.getTotalAmount()),
                    safeToString(userPayment.getCategory()),
                    safeToString(userPayment.getPaymentMethod())
            );
        } else if (dto instanceof ConsilidatedFinanceDetails) {
            ConsilidatedFinanceDetails finance = (ConsilidatedFinanceDetails) dto;
            writer.printf("%s,%s,%s,%s,%s,%s%n",
                    finance.getUserId(),
                    safeToString(finance.getUserPaymentTimestamp()),
                    safeToString(finance.getUserPaymentBankTransactionId()),
                    safeToString(finance.getUserPersonalName()),
                    safeToString(finance.getTotalAmount()),
                    safeToString(finance.getDebitAmount())
            );
        } else if (dto instanceof TenentDues) {
            TenentDues dues = (TenentDues) dto;
            writer.printf("%s,%s,%s,%s,%s,%s,%s%n",
                    dues.getUserId(),
                    safeToString(dues.getPendingAmount()),
                    safeToString(dues.getPendingDueDate()),
                    safeToString(dues.getUserPersonalName()),
                    safeToString(dues.getUserPgPropertyName()),
                    safeToString(dues.getUserPgPropertyId()),
                    safeToString(dues.getBedNumber())
            );
        } else if (dto instanceof VendorPayments) {
            VendorPayments vendor = (VendorPayments) dto;
            writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                    vendor.getOwnerId(),
                    safeToString(vendor.getOwnerName()),
                    safeToString(vendor.getPgId()),
                    safeToString(vendor.getPgName()),
                    safeToString(vendor.getTotalAmountFromTenants()),
                    safeToString(vendor.getTransactionDate()),
                    safeToString(vendor.getTransactionNumber()),
                    safeToString(vendor.getPaymentStatus()),
                    safeToString(vendor.getAmountPaidToOwner()),
                    safeToString(vendor.getZoyCommission())
            );
        } else if (dto instanceof VendorPaymentsDues) {
            VendorPaymentsDues vendorDues = (VendorPaymentsDues) dto;
            writer.printf("%s,%s,%s,%s,%s,%s,%s,%s%n",
                    vendorDues.getOwnerId(),
                    safeToString(vendorDues.getOwnerName()),
                    safeToString(vendorDues.getPendingAmount()),
                    safeToString(vendorDues.getPendingDueDate()),
                    safeToString(vendorDues.getPgId()),
                    safeToString(vendorDues.getPgName()),
                    safeToString(vendorDues.getTotalAmountPaid()),
                    safeToString(vendorDues.getTotalAmountPayable())
            );
        } else if (dto instanceof VendorPaymentsGst) {
            VendorPaymentsGst vendorGst = (VendorPaymentsGst) dto;
            writer.printf("%s,%s,%s,%s,%s,%s,%s,%s%n",
                    safeToString(vendorGst.getTransactionDate()),
                    safeToString(vendorGst.getTransactionNo()),
                    safeToString(vendorGst.getPgId()),
                    safeToString(vendorGst.getPgName()),
                    safeToString(vendorGst.getTotalAmount()),
                    safeToString(vendorGst.getGstAmount()),
                    safeToString(vendorGst.getBasicAmount()),
                    safeToString(vendorGst.getPaymentMethod())
            );
        }
    }

    private String safeToString(Object obj) {
        return obj == null ? "N/A" : obj.toString();
    }
}

