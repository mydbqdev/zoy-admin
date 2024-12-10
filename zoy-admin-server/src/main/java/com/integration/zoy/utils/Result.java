package com.integration.zoy.utils;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

   
public class Result {

   @JsonProperty("notes")
   List<String> notes;

   @JsonProperty("fee")
   BigDecimal fee;

   @JsonProperty("description")
   String description;

   @JsonProperty("created_at")
   long createdAt;

   @JsonProperty("amount_refunded")
   BigDecimal amountRefunded;

   @JsonProperty("bank")
   String bank;

   @JsonProperty("error_reason")
   String errorReason;

   @JsonProperty("error_description")
   String errorDescription;

   @JsonProperty("acquirer_data")
   AcquirerData acquirerData;

   @JsonProperty("captured")
   boolean captured;

   @JsonProperty("contact")
   String contact;

   @JsonProperty("invoice_id")
   String invoiceId;

   @JsonProperty("currency")
   String currency;

   @JsonProperty("id")
   String id;

   @JsonProperty("international")
   boolean international;

   @JsonProperty("email")
   String email;

   @JsonProperty("amount")
   BigDecimal amount;

   @JsonProperty("refund_status")
   String refundStatus;

   @JsonProperty("wallet")
   String wallet;

   @JsonProperty("method")
   String method;

   @JsonProperty("vpa")
   String vpa;

   @JsonProperty("error_source")
   String errorSource;

   @JsonProperty("error_step")
   String errorStep;

   @JsonProperty("tax")
   BigDecimal tax;

   @JsonProperty("card_id")
   String cardId;

   @JsonProperty("error_code")
   String errorCode;

   @JsonProperty("order_id")
   String orderId;

   @JsonProperty("entity")
   String entity;

   @JsonProperty("status")
   String status;


    public void setNotes(List<String> notes) {
        this.notes = notes;
    }
    public List<String> getNotes() {
        return notes;
    }
    
    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }
    public BigDecimal getFee() {
        return fee;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setAmountRefunded(BigDecimal amountRefunded) {
        this.amountRefunded = amountRefunded;
    }
    public BigDecimal getAmountRefunded() {
        return amountRefunded;
    }
    
    public void setBank(String bank) {
        this.bank = bank;
    }
    public String getBank() {
        return bank;
    }
    
    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }
    public String getErrorReason() {
        return errorReason;
    }
    
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
    public String getErrorDescription() {
        return errorDescription;
    }
    
    public void setAcquirerData(AcquirerData acquirerData) {
        this.acquirerData = acquirerData;
    }
    public AcquirerData getAcquirerData() {
        return acquirerData;
    }
    
    public void setCaptured(boolean captured) {
        this.captured = captured;
    }
    public boolean getCaptured() {
        return captured;
    }
    
    public void setContact(String contact) {
        this.contact = contact;
    }
    public String getContact() {
        return contact;
    }
    
    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }
    public String getInvoiceId() {
        return invoiceId;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public String getCurrency() {
        return currency;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    
    public void setInternational(boolean international) {
        this.international = international;
    }
    public boolean getInternational() {
        return international;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }
    public String getRefundStatus() {
        return refundStatus;
    }
    
    public void setWallet(String wallet) {
        this.wallet = wallet;
    }
    public String getWallet() {
        return wallet;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    public String getMethod() {
        return method;
    }
    
    public void setVpa(String vpa) {
        this.vpa = vpa;
    }
    public String getVpa() {
        return vpa;
    }
    
    public void setErrorSource(String errorSource) {
        this.errorSource = errorSource;
    }
    public String getErrorSource() {
        return errorSource;
    }
    
    public void setErrorStep(String errorStep) {
        this.errorStep = errorStep;
    }
    public String getErrorStep() {
        return errorStep;
    }
    
    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }
    public BigDecimal getTax() {
        return tax;
    }
    
    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
    public String getCardId() {
        return cardId;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getOrderId() {
        return orderId;
    }
    
    public void setEntity(String entity) {
        this.entity = entity;
    }
    public String getEntity() {
        return entity;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }
	@Override
	public String toString() {
		return "Result [notes=" + notes + ", fee=" + fee + ", description=" + description + ", createdAt=" + createdAt
				+ ", amountRefunded=" + amountRefunded + ", bank=" + bank + ", errorReason=" + errorReason
				+ ", errorDescription=" + errorDescription + ", acquirerData=" + acquirerData + ", captured=" + captured
				+ ", contact=" + contact + ", invoiceId=" + invoiceId + ", currency=" + currency + ", id=" + id
				+ ", international=" + international + ", email=" + email + ", amount=" + amount + ", refundStatus="
				+ refundStatus + ", wallet=" + wallet + ", method=" + method + ", vpa=" + vpa + ", errorSource="
				+ errorSource + ", errorStep=" + errorStep + ", tax=" + tax + ", cardId=" + cardId + ", errorCode="
				+ errorCode + ", orderId=" + orderId + ", entity=" + entity + ", status=" + status + "]";
	}
    
    
    
}