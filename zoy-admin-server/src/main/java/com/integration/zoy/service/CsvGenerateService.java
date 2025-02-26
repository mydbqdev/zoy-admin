package com.integration.zoy.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.integration.zoy.model.TenantResportsDTO;
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
	 private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

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
    
    public String formatAmountWithCommas(double amount) {
        // Define a pattern that will display commas in Indian-style formatting
    	if (Double.isNaN(amount)) {
            return "0.00";
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');

        // Indian-style format (1,23,45,678.00) with zero handling
        DecimalFormat formatter = new DecimalFormat("#,##,###.00", symbols);

        // If the amount is exactly zero, format it as "0.00"
        if (amount == 0) {
            return "0.00";
        }
        

        // Format the amount
        return formatter.format(amount);
    }
    private void createCsvHeaderRow(PrintWriter writer, Object dto, String reportType) {
        switch (reportType) {
            case "userTransactionReport":
                writer.println("Tenant Name,Tenant Mobile Number,PG Name,PG Address,BED Number,Transaction Date,Invoice No,Transaction Status,Due Amount(₹),GST Amount(₹),Total Amount(₹),Category,Mode of Payment,Failure Reason");
                break;
            case "userPaymentGstReport":
                writer.println("Transaction Date,Invoice No,Tenant Name,PG Name,PG Address,Total Amount(₹),GST Amount(₹),Due Amount(₹),Mode of Payment");
                break;
            case "consolidatedFinanceReport":
                writer.println("Transaction Date,Invoice Number,Payer/Payee Type,Name of the Payer/Payee,Debit(₹),Credit(₹)");
                break;
            case "tenantDuesReport":
                writer.println("Tenant Name,Tenant Mobile Number,PG Name,PG Address,Bed No,Pending Amount(₹),Payment Due Date");
                break;
            case "vendorPaymentsReport":
                writer.println("Owner Name,PG Name,Owner Email ID,PG Address,Total Amount Received from Tenants(₹),Total Amount Paid to Owner(₹),ZOY Share(₹),Transaction Date,Invoice Number,Payment Status,Owner approval Status");
                break;
            case "vendorPaymentsDuesReport":
                writer.println("Owner ID,Owner Name,Pending Amount(₹),Pending Due Date,Property ID,Property Name,Total Amount Paid(₹),Total Amount Payable(₹)");
                break;
            case "vendorPaymentsGstReport":
                writer.println("Transaction Date,Invoice No,Property ID,Property Name,Total Amount(₹),GST Amount(₹),Basic Amount(₹),Payment Method");
                break;
            case "tenantRefundReport":
                writer.println("Tenant Name,Tenant Mobile Number,PG Name,PG Address,Booking ID,Refund Title,Refundable Amount(₹),Amount Paid(₹),Payment Date,Invoice Number,Status");
                break;    
            case "reviewsAndRatingReport":
                writer.println("Review Date,Tenant Name,PG Name,Tenant Contact,Cleanliness,Accommodation,Aminities,Maintenance,Value For Money,Overall Rating");
                break; 
            case "UpcomingTenantsReport":
                writer.println("Tenant Name,Tenant Contact Number,Tenant Email Address,Booked Property Name,Property Address,Room Number/Bed Allocation,Expected Check-in Date,Expected Checked-out Date");
                break; 
            case "ActiveTenantsReport":
                writer.println("Tenant Name,Tenant Contact Number,Tenant Email Address, Property Name,Property Address,Room Number/Bed Allocation, Check-in Date,Expected Checked-out Date");
                break;
            case "InactiveTenantsReport":
            	writer.println("Tenant Name,Tenant Contact Number,Tenant Email Address, Previous Property Name,Property Address,Room Number/Bed Allocation, Checked-out Date");
                break;
            case "SuspendedTenantsReport":
            	writer.println("Tenant Name,Tenant Contact Number,Tenant Email Address, Previous Property Name,Property Address,Room Number/Bed Allocation, Checked-out Date,Suspended Date,Reason for suspension");
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
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                    safeToString(userPayment.getUserPersonalName()),
                    safeToString(userPayment.getTenantContactNum()),
                    safeToString(userPayment.getUserPgPropertyName()),
                    safeToString(userPayment.getPropertyHouseArea()),
                    safeToString(userPayment.getRoomBedNumber()),
                    formatDate(userPayment.getTransactionDate()),
                    safeToString(userPayment.getTransactionNumber()),
                    safeToString(userPayment.getTransactionStatus()),
                    formatAmountWithCommas(userPayment.getDueAmount()),
                    formatAmountWithCommas(userPayment.getGstAmount()),
                    formatAmountWithCommas(userPayment.getTotalAmount()),
                    safeToString(userPayment.getCategory()),
                    safeToString(userPayment.getPaymentMode()),
                    safeToString(userPayment.getFailedReason())
                    );
            }
            break;

        case "userPaymentGstReport":
            if (dto instanceof UserPaymentDTO) {
                UserPaymentDTO userPayment = (UserPaymentDTO) dto;
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                	formatDate(userPayment.getTransactionDate()),
                    safeToString(userPayment.getTransactionNumber()),
                    safeToString(userPayment.getUserPersonalName()),
                    safeToString(userPayment.getUserPgPropertyName()),
                    safeToString(userPayment.getPropertyHouseArea()),
                    formatAmountWithCommas(userPayment.getTotalAmount()),
                    formatAmountWithCommas(userPayment.getGstAmount()),
                    formatAmountWithCommas(userPayment.getDueAmount()),
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
                            formatAmountWithCommas(finance.getDebitAmount()),
                            formatAmountWithCommas(finance.getCreditAmount()));
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
                            formatAmountWithCommas(dues.getPendingAmount()),
                            formatDate(dues.getPendingDueDate()));
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
                            formatAmountWithCommas(vendor.getTotalAmountFromTenants()),
                            formatAmountWithCommas(vendor.getAmountPaidToOwner()),
                            formatAmountWithCommas(vendor.getZoyShare()),
                            formatDate(vendor.getTransactionDate()),
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
                            formatAmountWithCommas(vendorDues.getPendingAmount()),
                            formatDate(vendorDues.getPendingDueDate()),
                            safeToString(vendorDues.getPgId()),
                            safeToString(vendorDues.getPgName()),
                            formatAmountWithCommas(vendorDues.getTotalAmountPaid()),
                            formatAmountWithCommas(vendorDues.getTotalAmountPayable()));
                }
                break;

            case "vendorPaymentsGstReport":
                if (dto instanceof VendorPaymentsGst) {
                    VendorPaymentsGst vendorGst = (VendorPaymentsGst) dto;
                    writer.printf("%s,%s,%s,%s,%s,%s,%s,%s%n",
                    		formatDate(vendorGst.getTransactionDate()),
                            safeToString(vendorGst.getTransactionNo()),
                            safeToString(vendorGst.getPgId()),
                            safeToString(vendorGst.getPgName()),
                            formatAmountWithCommas(vendorGst.getTotalAmount()),
                            formatAmountWithCommas(vendorGst.getGstAmount()),
                            formatAmountWithCommas(vendorGst.getBasicAmount()),
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
                            formatAmountWithCommas(tenentRefund.getRefundableAmount()),
                            formatAmountWithCommas(tenentRefund.getAmountPaid()),
                            formatDate(tenentRefund.getPaymentDate()),
                            safeToString(tenentRefund.getTransactionNumber()),
                            safeToString(tenentRefund.getPaymentStatus()));
                }
                break;

            case "reviewsAndRatingReport":
                if (dto instanceof RatingsAndReviewsReport) {
                	RatingsAndReviewsReport ratingsAndReviews = (RatingsAndReviewsReport) dto;
                   writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                		    formatDate(ratingsAndReviews.getReviewDate()),
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
            case "UpcomingTenantsReport":
            	if (dto instanceof TenantResportsDTO) {
            		TenantResportsDTO upcomingTenants = (TenantResportsDTO) dto;
            		 writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                            safeToString(upcomingTenants.getTenantName()),
                            safeToString(upcomingTenants.getTenantContactNumber()),
                            safeToString(upcomingTenants.getTenantEmailAddress()),
                            safeToString(upcomingTenants.getBookedProperyName()),
                            safeToString(upcomingTenants.getPropertAddress()),
                            safeToString(upcomingTenants.getRoomNumber()),
                            formatDate(upcomingTenants.getExpectedCheckIndate()),
                            formatDate(upcomingTenants.getExpectedCheckOutdate()));
                }
                break;
            case "ActiveTenantsReport":
            	if (dto instanceof TenantResportsDTO) {
            		TenantResportsDTO activeTenants = (TenantResportsDTO) dto;
            		 writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                            safeToString(activeTenants.getTenantName()),
                            safeToString(activeTenants.getTenantContactNumber()),
                            safeToString(activeTenants.getTenantEmailAddress()),
                            safeToString(activeTenants.getCurrentPropertName()),
                            safeToString(activeTenants.getPropertAddress()),
                            safeToString(activeTenants.getRoomNumber()),
                            formatDate(activeTenants.getCheckInDate()),
                            formatDate(activeTenants.getExpectedCheckOutdate()));
                }
                break; 
            case "InactiveTenantsReport":
            	if (dto instanceof TenantResportsDTO) {
            		TenantResportsDTO inActiveTenants = (TenantResportsDTO) dto;
            		 writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                            safeToString(inActiveTenants.getTenantName()),
                            safeToString(inActiveTenants.getTenantContactNumber()),
                            safeToString(inActiveTenants.getTenantEmailAddress()),
                            safeToString(inActiveTenants.getPreviousPropertName()),
                            safeToString(inActiveTenants.getPropertAddress()),
                            safeToString(inActiveTenants.getRoomNumber()),
                            formatDate(inActiveTenants.getCheckedOutDate()));
                }
                break;
            case "SuspendedTenantsReport":
            	if (dto instanceof TenantResportsDTO) {
            		TenantResportsDTO suspendedTenant = (TenantResportsDTO) dto;
            		 writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                            safeToString(suspendedTenant.getTenantName()),
                            safeToString(suspendedTenant.getTenantContactNumber()),
                            safeToString(suspendedTenant.getTenantEmailAddress()),
                            safeToString(suspendedTenant.getPreviousPropertName()),
                            safeToString(suspendedTenant.getPropertAddress()),
                            safeToString(suspendedTenant.getRoomNumber()),
                            formatDate(suspendedTenant.getCheckedOutDate()),
                            formatDate(suspendedTenant.getCheckedOutDate()),
                            safeToString(suspendedTenant.getReasonForSuspension()));
                }
                break;   
            default:
                throw new IllegalArgumentException("Invalid report type provided: " + reportType);
        }
    }

    private Object safeToString(Object obj) {
        return obj == null ? "N/A" : obj;
    }
    
   

    private String formatDate(Object dateObject) {
        if (dateObject instanceof Date) {
            return dateFormat.format((Date) dateObject); 
        }
        return String.valueOf(dateObject);
    }
    
  
}
