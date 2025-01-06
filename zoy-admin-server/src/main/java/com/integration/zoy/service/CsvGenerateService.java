package com.integration.zoy.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.integration.zoy.utils.ConsilidatedFinanceDetails;
import com.integration.zoy.utils.RatingsAndReviewsReport;
import com.integration.zoy.utils.TenentDues;
import com.integration.zoy.utils.TenentRefund;
import com.integration.zoy.utils.UserPaymentDTO;
import com.integration.zoy.utils.VendorPayments;
import com.integration.zoy.utils.VendorPaymentsDues;
import com.integration.zoy.utils.VendorPaymentsGst;

@Service
public class CsvGenerateService {

    public byte[] generateCsvFile(String reportType, Map<String, Object> data) {
        List<?> reportData = (List<?>) data.get("reportData");
        
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
        	outputStream.write(0xEF);
            outputStream.write(0xBB);
            outputStream.write(0xBF);
            if (reportData == null || reportData.isEmpty()) {
                return new byte[0];
            } else {
                createCsvHeaderRow(writer, reportData.get(0), reportType);

                for (Object dto : reportData) {
                    createCsvDataRow(writer, dto, reportType);
                }
            }

            writer.flush();
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating CSV file", e);
        }
    }

    private void createCsvHeaderRow(PrintWriter writer, Object dto, String reportType) {
        switch (reportType) {
            case "userTransactionReport":
                writer.println("Tenant Name,Tenant Mobile Number,PG Name,PG Address,BED Number,Transaction Date,Transaction No,Transaction Status,Due Amount,GST Amount,Total Amount,Category,Mode of Payment");
                break;
            case "userPaymentGstReport":
                writer.println("Transaction Date,Transaction No,Tenant Name,PG Name,PG Address,Total Amount,GST Amount,Due Amount,Mode of Payment");
                break;
            case "consolidatedFinanceReport":
                writer.println("Transaction Date,Transaction Number,Payer/Payee Type,Name of the Payer/Payee,Debit,Credit");
                break;
            case "tenantDuesReport":
                writer.println("Tenant Name,Tenant Mobile Number,PG Name,PG Address,Bed No,Pending Amount,Payment Due Date");
                break;
            case "vendorPaymentsReport":
                writer.println("Owner Name,PG Name,Owner Email ID,PG Address,Total Amount Received from Tenants,Total Amount Paid to Owner,ZOY Share,Transaction Date,Transaction Number,Payment Status,Owner approval Status");
                break;
            case "vendorPaymentsDuesReport":
                writer.println("Owner ID,Owner Name,Pending Amount,Pending Due Date,Property ID,Property Name,Total Amount Paid,Total Amount Payable");
                break;
            case "vendorPaymentsGstReport":
                writer.println("Transaction Date,Transaction No,Property ID,Property Name,Total Amount,GST Amount,Basic Amount,Payment Method");
                break;
            case "tenantRefundReport":
                writer.println("Tenant Name,Tenant Mobile Number,PG Name,PG Address,Booking ID,Refund Title,Refundable Amount,Amount Paid,Payment Date,Transaction Number,Status");
                break;    
            case "reviewsAndRatingReport":
                writer.println("Review Date,Tenant Name,PG Name,Tenant Contact,Cleanliness,Accommodation,Aminities,Maintenance,Value For Money,Overall Rating");
                break;       
            default:
                throw new IllegalArgumentException("Invalid report type provided: " + reportType);
        }
    }

    private void createCsvDataRow(PrintWriter writer, Object dto, String reportType) {
    	switch (reportType) {
        case "userTransactionReport":
            if (dto instanceof UserPaymentDTO) {
                UserPaymentDTO userPayment = (UserPaymentDTO) dto;
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                    safeToString(userPayment.getUserPersonalName()),
                    safeToString(userPayment.getTenantContactNum()),
                    safeToString(userPayment.getUserPgPropertyName()),
                    safeToString(userPayment.getPropertyHouseArea()),
                    safeToString(userPayment.getRoomBedNumber()),
                    safeToString(userPayment.getTransactionDate()),
                    safeToString(userPayment.getTransactionNumber()),
                    safeToString(userPayment.getTransactionStatus()),
                    safeToString(userPayment.getDueAmount()),
                    safeToString(userPayment.getGstAmount()),
                    safeToString(userPayment.getTotalAmount()),
                    safeToString(userPayment.getCategory()),
                    safeToString(userPayment.getPaymentMode()));
            }
            break;

        case "userPaymentGstReport":
            if (dto instanceof UserPaymentDTO) {
                UserPaymentDTO userPayment = (UserPaymentDTO) dto;
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                    safeToString(userPayment.getTransactionDate()),
                    safeToString(userPayment.getTransactionNumber()),
                    safeToString(userPayment.getUserPersonalName()),
                    safeToString(userPayment.getUserPgPropertyName()),
                    safeToString(userPayment.getPropertyHouseArea()),
                    safeToString(userPayment.getTotalAmount()),
                    safeToString(userPayment.getGstAmount()),
                    safeToString(userPayment.getDueAmount()),
                    safeToString(userPayment.getPaymentMode()));
            }
            break;

            case "consolidatedFinanceReport":
                if (dto instanceof ConsilidatedFinanceDetails) {
                    ConsilidatedFinanceDetails finance = (ConsilidatedFinanceDetails) dto;
                    writer.printf("%s,%s,%s,%s,%s,%s%n",
                            finance.getUserPaymentTimestamp(),
                            safeToString(finance.getUserPaymentBankTransactionId()),
                            safeToString(finance.getPayerPayeeType()),
                            safeToString(finance.getPayerPayeeName()),
                            safeToString(finance.getDebitAmount()),
                            safeToString(finance.getCreditAmount()));
                }
                break;

            case "tenantDuesReport":
                if (dto instanceof TenentDues) {
                    TenentDues dues = (TenentDues) dto;
                    writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                    		safeToString(dues.getUserPersonalName()),
                            safeToString(dues.getTenantMobileNum()),
                            safeToString(dues.getUserPgPropertyName()),
                            safeToString(dues.getUserPgPropertyAddress()),
                            safeToString(dues.getBedNumber()),
                            safeToString(dues.getPendingAmount()),
                            safeToString(dues.getPendingDueDate()));
                }
                break;

            case "vendorPaymentsReport":
                if (dto instanceof VendorPayments) {
                    VendorPayments vendor = (VendorPayments) dto;
                    writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                            safeToString(vendor.getOwnerName()),
                            safeToString(vendor.getPgName()),
                            safeToString(vendor.getOwnerEmail()),
                            safeToString(vendor.getPgAddress()),
                            safeToString(vendor.getTotalAmountFromTenants()),
                            safeToString(vendor.getAmountPaidToOwner()),
                            safeToString(vendor.getZoyShare()),
                            safeToString(vendor.getTransactionDate()),
                            safeToString(vendor.getTransactionNumber()),
                            safeToString(vendor.getPaymentStatus()),
                            safeToString(vendor.getOwnerApprovalStatus()));
                }
                break;

            case "vendorPaymentsDuesReport":
                if (dto instanceof VendorPaymentsDues) {
                    VendorPaymentsDues vendorDues = (VendorPaymentsDues) dto;
                    writer.printf("%s,%s,%s,%s,%s,%s,%s,%s%n",
                            vendorDues.getOwnerId(),
                            safeToString(vendorDues.getOwnerName()),
                            safeToString(vendorDues.getPendingAmount()),
                            safeToString(vendorDues.getPendingDueDate()),
                            safeToString(vendorDues.getPgId()),
                            safeToString(vendorDues.getPgName()),
                            safeToString(vendorDues.getTotalAmountPaid()),
                            safeToString(vendorDues.getTotalAmountPayable()));
                }
                break;

            case "vendorPaymentsGstReport":
                if (dto instanceof VendorPaymentsGst) {
                    VendorPaymentsGst vendorGst = (VendorPaymentsGst) dto;
                    writer.printf("%s,%s,%s,%s,%s,%s,%s,%s%n",
                            safeToString(vendorGst.getTransactionDate()),
                            safeToString(vendorGst.getTransactionNo()),
                            safeToString(vendorGst.getPgId()),
                            safeToString(vendorGst.getPgName()),
                            safeToString(vendorGst.getTotalAmount()),
                            safeToString(vendorGst.getGstAmount()),
                            safeToString(vendorGst.getBasicAmount()),
                            safeToString(vendorGst.getPaymentMethod()));
                }
                break;
            
            case "tenantRefundReport":
                if (dto instanceof TenentRefund) {
                    TenentRefund tenentRefund = (TenentRefund) dto;
                    writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                            safeToString(tenentRefund.getCustomerName()),
                            safeToString(tenentRefund.getTenantMobileNum()),
                            safeToString(tenentRefund.getPgPropertyName()),
                            safeToString(tenentRefund.getUserPgPropertyAddress()),
                            safeToString(tenentRefund.getBookingId()),
                            safeToString(tenentRefund.getRefundTitle()),
                            safeToString(tenentRefund.getRefundableAmount()),
                            safeToString(tenentRefund.getAmountPaid()),
                            safeToString(tenentRefund.getPaymentDate()),
                            safeToString(tenentRefund.getTransactionNumber()),
                            safeToString(tenentRefund.getPaymentStatus()));
                }
                break;

            case "reviewsAndRatingReport":
                if (dto instanceof RatingsAndReviewsReport) {
                	RatingsAndReviewsReport ratingsAndReviews = (RatingsAndReviewsReport) dto;
                   writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                            safeToString(ratingsAndReviews.getReviewDate()),
                            safeToString(ratingsAndReviews.getCustomerName()),
                            safeToString(ratingsAndReviews.getPropertyName()),
                            safeToString(ratingsAndReviews.getCustomerMobileNo()),
                            safeToString(ratingsAndReviews.getCleanliness()),
                            safeToString(ratingsAndReviews.getAccommodation()),
                            safeToString(ratingsAndReviews.getAmenities()),
                            safeToString(ratingsAndReviews.getMaintenance()),
                    		safeToString(ratingsAndReviews.getValueForMoney()),
                            safeToString(ratingsAndReviews.getOverallRating()));
                }
                break;    
                

            default:
                throw new IllegalArgumentException("Invalid report type provided: " + reportType);
        }
    }

    private String safeToString(Object obj) {
        return obj == null ? "N/A" : obj.toString();
    }
}
