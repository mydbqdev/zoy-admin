package com.integration.zoy.entity;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_payments", schema = "pgusers")
public class UserPayment {

    @Id
    //@GeneratedValue(generator = "UUID")
   	//@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
   	@Column(name = "user_payment_id", updatable = false, nullable = false, unique = true, length = 36)
    private String userPaymentId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_money_due_id")
    private String userMoneyDueId;

    @Column(name = "user_payment_booking_id")
    private String userPaymentBookingId;

    @Column(name = "user_payment_created_at")
    @CreationTimestamp
    private Timestamp userPaymentCreatedAt;

    @Column(name = "user_payment_razorpay_order_id")
    private String userPaymentRazorpayOrderId;

    @Column(name = "user_payment_razorpay_payment_id")
    private String userPaymentRazorpayPaymentId;

    @Column(name = "user_payment_razorpay_signature")
    private String userPaymentRazorpaySignature;

    @Column(name = "user_payment_bank_transaction_id")
    private String userPaymentBankTransactionId;

    @Column(name = "user_payment_result_amount")
    private BigDecimal userPaymentResultAmount;

    @Column(name = "user_payment_result_amount_refunded")
    private BigDecimal userPaymentResultAmountRefunded;

    @Column(name = "user_payment_result_bank")
    private String userPaymentResultBank;

    @Column(name = "user_payment_result_captured")
    private Boolean userPaymentResultCaptured;

    @Column(name = "user_payment_result_card_id")
    private String userPaymentResultCardId;

    @Column(name = "user_payment_result_contact")
    private String userPaymentResultContact;

    @Column(name = "user_payment_result_created_at")
    private Timestamp userPaymentResultCreatedAt;

    @Column(name = "user_payment_result_currency")
    private String userPaymentResultCurrency;

    @Column(name = "user_payment_result_description")
    private String userPaymentResultDescription;
    
    @Column(name = "user_payment_result_reason")
    private String userPaymentResultReason;

    @Column(name = "user_payment_result_email")
    private String userPaymentResultEmail;

    @Column(name = "user_payment_result_entity")
    private String userPaymentResultEntity;

    @Column(name = "user_payment_result_error_code")
    private String userPaymentResultErrorCode;

    @Column(name = "user_payment_result_error_description")
    private String userPaymentResultErrorDescription;

    @Column(name = "user_payment_result_error_source")
    private String userPaymentResultErrorSource;

    @Column(name = "user_payment_result_error_step")
    private String userPaymentResultErrorStep;

    @Column(name = "user_payment_result_fee")
    private BigDecimal userPaymentResultFee;

    @Column(name = "user_payment_result_id")
    private String userPaymentResultId;

    @Column(name = "user_payment_result_international")
    private Boolean userPaymentResultInternational;

    @Column(name = "user_payment_result_invoice_id")
    private String userPaymentResultInvoiceId;

    @Column(name = "user_payment_result_method")
    private String userPaymentResultMethod;

    @Column(name = "user_payment_result_notes")
    private String userPaymentResultNotes;

    @Column(name = "user_payment_result_order_id")
    private String userPaymentResultOrderId;

    @Column(name = "user_payment_result_refund_status")
    private String userPaymentResultRefundStatus;

    @Column(name = "user_payment_result_status")
    private String userPaymentResultStatus;

    @Column(name = "user_payment_result_tax")
    private BigDecimal userPaymentResultTax;

    @Column(name = "user_payment_result_vpa")
    private String userPaymentResultVpa;

    @Column(name = "user_payment_result_wallet")
    private String userPaymentResultWallet;

    @Column(name = "user_payment_gst")
    private BigDecimal userPaymentGst;

    @Column(name = "user_payment_payable_amount")
    private BigDecimal userPaymentPayableAmount;

    @Column(name = "user_payment_payment_status")
    private String userPaymentPaymentStatus;

    @Column(name = "user_payment_receive_date")
    private Timestamp userPaymentReceiveDate;

    @Column(name = "user_payment_timestamp")
    private Timestamp userPaymentTimestamp;

    @Column(name = "user_payment_zoy_payment_mode")
    private String userPaymentZoyPaymentMode;

    @Column(name = "user_payment_zoy_payment_type")
    private String userPaymentZoyPaymentType;
    
    @Column(name = "user_gst_number")
    private String userGstNumber;

    @Column(name = "user_payment_sgst", nullable = false, precision = 20, scale = 2)
    private BigDecimal userPaymentSgst = BigDecimal.ZERO;

    @Column(name = "user_payment_cgst", nullable = false, precision = 20, scale = 2)
    private BigDecimal userPaymentCgst = BigDecimal.ZERO;

    @Column(name = "user_payment_igst", nullable = false, precision = 20, scale = 2)
    private BigDecimal userPaymentIgst = BigDecimal.ZERO;

    @Column(name = "user_payment_sgst_percentage", nullable = false, precision = 10, scale = 2)
    private BigDecimal userPaymentSgstPercentage = BigDecimal.ZERO;

    @Column(name = "user_payment_cgst_percentage", nullable = false, precision = 10, scale = 2)
    private BigDecimal userPaymentCgstPercentage = BigDecimal.ZERO;

    @Column(name = "user_payment_igst_percentage", nullable = false, precision = 10, scale = 2)
    private BigDecimal userPaymentIgstPercentage = BigDecimal.ZERO;


	public String getUserPaymentId() {
		return userPaymentId;
	}

	public void setUserPaymentId(String userPaymentId) {
		this.userPaymentId = userPaymentId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserMoneyDueId() {
		return userMoneyDueId;
	}

	public void setUserMoneyDueId(String userMoneyDueId) {
		this.userMoneyDueId = userMoneyDueId;
	}

	public String getUserPaymentBookingId() {
		return userPaymentBookingId;
	}

	public void setUserPaymentBookingId(String userPaymentBookingId) {
		this.userPaymentBookingId = userPaymentBookingId;
	}

	public Timestamp getUserPaymentCreatedAt() {
		return userPaymentCreatedAt;
	}

	public void setUserPaymentCreatedAt(Timestamp userPaymentCreatedAt) {
		this.userPaymentCreatedAt = userPaymentCreatedAt;
	}

	public String getUserPaymentRazorpayOrderId() {
		return userPaymentRazorpayOrderId;
	}

	public void setUserPaymentRazorpayOrderId(String userPaymentRazorpayOrderId) {
		this.userPaymentRazorpayOrderId = userPaymentRazorpayOrderId;
	}

	public String getUserPaymentRazorpayPaymentId() {
		return userPaymentRazorpayPaymentId;
	}

	public void setUserPaymentRazorpayPaymentId(String userPaymentRazorpayPaymentId) {
		this.userPaymentRazorpayPaymentId = userPaymentRazorpayPaymentId;
	}

	public String getUserPaymentRazorpaySignature() {
		return userPaymentRazorpaySignature;
	}

	public void setUserPaymentRazorpaySignature(String userPaymentRazorpaySignature) {
		this.userPaymentRazorpaySignature = userPaymentRazorpaySignature;
	}

	public String getUserPaymentBankTransactionId() {
		return userPaymentBankTransactionId;
	}

	public void setUserPaymentBankTransactionId(String userPaymentBankTransactionId) {
		this.userPaymentBankTransactionId = userPaymentBankTransactionId;
	}

	public BigDecimal getUserPaymentResultAmount() {
		return userPaymentResultAmount;
	}

	public void setUserPaymentResultAmount(BigDecimal userPaymentResultAmount) {
		this.userPaymentResultAmount = userPaymentResultAmount;
	}

	public BigDecimal getUserPaymentResultAmountRefunded() {
		return userPaymentResultAmountRefunded;
	}

	public void setUserPaymentResultAmountRefunded(BigDecimal userPaymentResultAmountRefunded) {
		this.userPaymentResultAmountRefunded = userPaymentResultAmountRefunded;
	}

	public String getUserPaymentResultBank() {
		return userPaymentResultBank;
	}

	public void setUserPaymentResultBank(String userPaymentResultBank) {
		this.userPaymentResultBank = userPaymentResultBank;
	}

	public Boolean getUserPaymentResultCaptured() {
		return userPaymentResultCaptured;
	}

	public void setUserPaymentResultCaptured(Boolean userPaymentResultCaptured) {
		this.userPaymentResultCaptured = userPaymentResultCaptured;
	}

	public String getUserPaymentResultCardId() {
		return userPaymentResultCardId;
	}

	public void setUserPaymentResultCardId(String userPaymentResultCardId) {
		this.userPaymentResultCardId = userPaymentResultCardId;
	}

	public String getUserPaymentResultContact() {
		return userPaymentResultContact;
	}

	public void setUserPaymentResultContact(String userPaymentResultContact) {
		this.userPaymentResultContact = userPaymentResultContact;
	}

	public Timestamp getUserPaymentResultCreatedAt() {
		return userPaymentResultCreatedAt;
	}

	public void setUserPaymentResultCreatedAt(Timestamp userPaymentResultCreatedAt) {
		this.userPaymentResultCreatedAt = userPaymentResultCreatedAt;
	}

	public String getUserPaymentResultCurrency() {
		return userPaymentResultCurrency;
	}

	public void setUserPaymentResultCurrency(String userPaymentResultCurrency) {
		this.userPaymentResultCurrency = userPaymentResultCurrency;
	}

	public String getUserPaymentResultDescription() {
		return userPaymentResultDescription;
	}

	public void setUserPaymentResultDescription(String userPaymentResultDescription) {
		this.userPaymentResultDescription = userPaymentResultDescription;
	}

	public String getUserPaymentResultEmail() {
		return userPaymentResultEmail;
	}

	public void setUserPaymentResultEmail(String userPaymentResultEmail) {
		this.userPaymentResultEmail = userPaymentResultEmail;
	}

	public String getUserPaymentResultEntity() {
		return userPaymentResultEntity;
	}

	public void setUserPaymentResultEntity(String userPaymentResultEntity) {
		this.userPaymentResultEntity = userPaymentResultEntity;
	}

	public String getUserPaymentResultErrorCode() {
		return userPaymentResultErrorCode;
	}

	public void setUserPaymentResultErrorCode(String userPaymentResultErrorCode) {
		this.userPaymentResultErrorCode = userPaymentResultErrorCode;
	}

	public String getUserPaymentResultErrorDescription() {
		return userPaymentResultErrorDescription;
	}

	public void setUserPaymentResultErrorDescription(String userPaymentResultErrorDescription) {
		this.userPaymentResultErrorDescription = userPaymentResultErrorDescription;
	}

	public String getUserPaymentResultErrorSource() {
		return userPaymentResultErrorSource;
	}

	public void setUserPaymentResultErrorSource(String userPaymentResultErrorSource) {
		this.userPaymentResultErrorSource = userPaymentResultErrorSource;
	}

	public String getUserPaymentResultErrorStep() {
		return userPaymentResultErrorStep;
	}

	public void setUserPaymentResultErrorStep(String userPaymentResultErrorStep) {
		this.userPaymentResultErrorStep = userPaymentResultErrorStep;
	}

	public BigDecimal getUserPaymentResultFee() {
		return userPaymentResultFee;
	}

	public void setUserPaymentResultFee(BigDecimal userPaymentResultFee) {
		this.userPaymentResultFee = userPaymentResultFee;
	}

	public String getUserPaymentResultId() {
		return userPaymentResultId;
	}

	public void setUserPaymentResultId(String userPaymentResultId) {
		this.userPaymentResultId = userPaymentResultId;
	}

	public Boolean getUserPaymentResultInternational() {
		return userPaymentResultInternational;
	}

	public void setUserPaymentResultInternational(Boolean userPaymentResultInternational) {
		this.userPaymentResultInternational = userPaymentResultInternational;
	}

	public String getUserPaymentResultInvoiceId() {
		return userPaymentResultInvoiceId;
	}

	public void setUserPaymentResultInvoiceId(String userPaymentResultInvoiceId) {
		this.userPaymentResultInvoiceId = userPaymentResultInvoiceId;
	}

	public String getUserPaymentResultMethod() {
		return userPaymentResultMethod;
	}

	public void setUserPaymentResultMethod(String userPaymentResultMethod) {
		this.userPaymentResultMethod = userPaymentResultMethod;
	}

	public String getUserPaymentResultNotes() {
		return userPaymentResultNotes;
	}

	public void setUserPaymentResultNotes(String userPaymentResultNotes) {
		this.userPaymentResultNotes = userPaymentResultNotes;
	}

	public String getUserPaymentResultOrderId() {
		return userPaymentResultOrderId;
	}

	public void setUserPaymentResultOrderId(String userPaymentResultOrderId) {
		this.userPaymentResultOrderId = userPaymentResultOrderId;
	}

	public String getUserPaymentResultRefundStatus() {
		return userPaymentResultRefundStatus;
	}

	public void setUserPaymentResultRefundStatus(String userPaymentResultRefundStatus) {
		this.userPaymentResultRefundStatus = userPaymentResultRefundStatus;
	}

	public String getUserPaymentResultStatus() {
		return userPaymentResultStatus;
	}

	public void setUserPaymentResultStatus(String userPaymentResultStatus) {
		this.userPaymentResultStatus = userPaymentResultStatus;
	}

	public BigDecimal getUserPaymentResultTax() {
		return userPaymentResultTax;
	}

	public void setUserPaymentResultTax(BigDecimal userPaymentResultTax) {
		this.userPaymentResultTax = userPaymentResultTax;
	}

	public String getUserPaymentResultVpa() {
		return userPaymentResultVpa;
	}

	public void setUserPaymentResultVpa(String userPaymentResultVpa) {
		this.userPaymentResultVpa = userPaymentResultVpa;
	}

	public String getUserPaymentResultWallet() {
		return userPaymentResultWallet;
	}

	public void setUserPaymentResultWallet(String userPaymentResultWallet) {
		this.userPaymentResultWallet = userPaymentResultWallet;
	}

	public BigDecimal getUserPaymentGst() {
		return userPaymentGst;
	}

	public void setUserPaymentGst(BigDecimal userPaymentGst) {
		this.userPaymentGst = userPaymentGst;
	}

	public BigDecimal getUserPaymentPayableAmount() {
		return userPaymentPayableAmount;
	}

	public void setUserPaymentPayableAmount(BigDecimal userPaymentPayableAmount) {
		this.userPaymentPayableAmount = userPaymentPayableAmount;
	}

	public String getUserPaymentPaymentStatus() {
		return userPaymentPaymentStatus;
	}

	public void setUserPaymentPaymentStatus(String userPaymentPaymentStatus) {
		this.userPaymentPaymentStatus = userPaymentPaymentStatus;
	}

	public Timestamp getUserPaymentReceiveDate() {
		return userPaymentReceiveDate;
	}

	public void setUserPaymentReceiveDate(Timestamp userPaymentReceiveDate) {
		this.userPaymentReceiveDate = userPaymentReceiveDate;
	}

	public Timestamp getUserPaymentTimestamp() {
		return userPaymentTimestamp;
	}

	public void setUserPaymentTimestamp(Timestamp userPaymentTimestamp) {
		this.userPaymentTimestamp = userPaymentTimestamp;
	}

	public String getUserPaymentZoyPaymentMode() {
		return userPaymentZoyPaymentMode;
	}

	public void setUserPaymentZoyPaymentMode(String userPaymentZoyPaymentMode) {
		this.userPaymentZoyPaymentMode = userPaymentZoyPaymentMode;
	}

	public String getUserPaymentZoyPaymentType() {
		return userPaymentZoyPaymentType;
	}

	public void setUserPaymentZoyPaymentType(String userPaymentZoyPaymentType) {
		this.userPaymentZoyPaymentType = userPaymentZoyPaymentType;
	}

	public String getUserGstNumber() {
		return userGstNumber;
	}

	public void setUserGstNumber(String userGstNumber) {
		this.userGstNumber = userGstNumber;
	}

	public String getUserPaymentResultReason() {
		return userPaymentResultReason;
	}

	public void setUserPaymentResultReason(String userPaymentResultReason) {
		this.userPaymentResultReason = userPaymentResultReason;
	}

	public BigDecimal getUserPaymentSgst() {
		return userPaymentSgst;
	}

	public void setUserPaymentSgst(BigDecimal userPaymentSgst) {
		this.userPaymentSgst = userPaymentSgst;
	}

	public BigDecimal getUserPaymentCgst() {
		return userPaymentCgst;
	}

	public void setUserPaymentCgst(BigDecimal userPaymentCgst) {
		this.userPaymentCgst = userPaymentCgst;
	}

	public BigDecimal getUserPaymentIgst() {
		return userPaymentIgst;
	}

	public void setUserPaymentIgst(BigDecimal userPaymentIgst) {
		this.userPaymentIgst = userPaymentIgst;
	}

	public BigDecimal getUserPaymentSgstPercentage() {
		return userPaymentSgstPercentage;
	}

	public void setUserPaymentSgstPercentage(BigDecimal userPaymentSgstPercentage) {
		this.userPaymentSgstPercentage = userPaymentSgstPercentage;
	}

	public BigDecimal getUserPaymentCgstPercentage() {
		return userPaymentCgstPercentage;
	}

	public void setUserPaymentCgstPercentage(BigDecimal userPaymentCgstPercentage) {
		this.userPaymentCgstPercentage = userPaymentCgstPercentage;
	}

	public BigDecimal getUserPaymentIgstPercentage() {
		return userPaymentIgstPercentage;
	}

	public void setUserPaymentIgstPercentage(BigDecimal userPaymentIgstPercentage) {
		this.userPaymentIgstPercentage = userPaymentIgstPercentage;
	}

	@PrePersist
    private void generateBookingId() {
        if (this.userPaymentId == null || this.userPaymentId.isEmpty()) {
            String prefix = "PAY_ZOY";
            String uniquePart = generateUniquePartSafe();
            String formattedDate = String.format("%1$tY%1$tm%1$td", new Timestamp(System.currentTimeMillis())); 
            this.userPaymentId = prefix + formattedDate + uniquePart.toUpperCase();
        }
    }
	private String generateUniquePartSafe() {
        try {
            String nanoTimestamp = String.valueOf(Instant.now().toEpochMilli()) + System.nanoTime();
            String uuidPart = UUID.randomUUID().toString();
            String rawId = nanoTimestamp + uuidPart;
            return hashWithSHA256Safe(rawId).substring(0, 8);
        } catch (Exception e) {
            return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        }
    }

    private String hashWithSHA256Safe(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return input.substring(0, Math.min(8, input.length()));
        }
    }

}
