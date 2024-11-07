package com.integration.zoy.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity 
@Table(name = "bank_details", schema = "pgcommon")
public class BankDetails {

    @Id
    @GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "user_bank_id", updatable = false, nullable = false, unique = true, length = 36)
    private String userBankId;
    
    @Column(name = "user_bank_name")
    private String userBankName;
    
    @Column(name = "user_bank_branch")
    private String userBankBranch;

    @Column(name = "user_account_holder_name")
    private String userAccountHolderName;
    
    @Column(name = "user_account_number")
    private String userAccountNumber;
    
    @Column(name = "user_ifsc_code")
    private String userIfscCode;
    
    @Column(name = "user_amount_deposit")
    private BigDecimal userAmountDeposit;

    @Column(name = "user_utr")
    private String userUtr;
    
    @Column(name = "user_transaction_id")
    private String userTransactionId;
    
    @Column(name = "user_message")
    private String userMessage;
    
    @Column(name = "user_is_primary")
    private Boolean userIsPrimary;
    
    @CreationTimestamp
    @Column(name = "user_bank_created_at")
    private Timestamp userBankCreatedAt;
    
    @Column(name = "user_application_name")
    private String userApplicationName;

	public String getUserBankId() {
		return userBankId;
	}

	public void setUserBankId(String userBankId) {
		this.userBankId = userBankId;
	}

	public String getUserBankName() {
		return userBankName;
	}

	public void setUserBankName(String userBankName) {
		this.userBankName = userBankName;
	}

	public String getUserBankBranch() {
		return userBankBranch;
	}

	public void setUserBankBranch(String userBankBranch) {
		this.userBankBranch = userBankBranch;
	}

	public String getUserAccountHolderName() {
		return userAccountHolderName;
	}

	public void setUserAccountHolderName(String userAccountHolderName) {
		this.userAccountHolderName = userAccountHolderName;
	}

	public String getUserAccountNumber() {
		return userAccountNumber;
	}

	public void setUserAccountNumber(String userAccountNumber) {
		this.userAccountNumber = userAccountNumber;
	}

	public String getUserIfscCode() {
		return userIfscCode;
	}

	public void setUserIfscCode(String userIfscCode) {
		this.userIfscCode = userIfscCode;
	}

	public BigDecimal getUserAmountDeposit() {
		return userAmountDeposit;
	}

	public void setUserAmountDeposit(BigDecimal userAmountDeposit) {
		this.userAmountDeposit = userAmountDeposit;
	}

	public String getUserUtr() {
		return userUtr;
	}

	public void setUserUtr(String userUtr) {
		this.userUtr = userUtr;
	}

	public String getUserTransactionId() {
		return userTransactionId;
	}

	public void setUserTransactionId(String userTransactionId) {
		this.userTransactionId = userTransactionId;
	}

	public String getUserMessage() {
		return userMessage;
	}

	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}

	public Boolean getUserIsPrimary() {
		return userIsPrimary;
	}

	public void setUserIsPrimary(Boolean userIsPrimary) {
		this.userIsPrimary = userIsPrimary;
	}

	public Timestamp getUserBankCreatedAt() {
		return userBankCreatedAt;
	}

	public void setUserBankCreatedAt(Timestamp userBankCreatedAt) {
		this.userBankCreatedAt = userBankCreatedAt;
	}

	public String getUserApplicationName() {
		return userApplicationName;
	}

	public void setUserApplicationName(String userApplicationName) {
		this.userApplicationName = userApplicationName;
	}

	
}
