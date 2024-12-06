package com.integration.zoy.model;

public class PgOwnerBusinessInfo {
	private String accountNumber;
	private String bankName;
	private String bankBranch;
	private String ifscCode;

	public PgOwnerBusinessInfo(String accountNumber, String bankName, String bankBranch, String ifscCode,String accountType) {
		this.accountNumber=accountNumber;
		this.bankName=bankName;
		this.bankBranch=bankBranch;
		this.ifscCode=ifscCode;
		this.accountType=accountType;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	private String accountType;

}
