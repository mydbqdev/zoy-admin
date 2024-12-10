package com.integration.zoy.utils;


import java.math.BigDecimal;

import com.google.gson.annotations.SerializedName;

public class ZoyPgSecurityDepositDetailsDTO {

	@SerializedName("depositId")
	private String depositId;
	
	@SerializedName("minimumDeposit")
	private BigDecimal minimumDeposit;
	
	@SerializedName("maximumDeposit")
	private BigDecimal maximumDeposit;
	
	public String getDepositId() {
		return depositId;
	}
	public void setDepositId(String depositId) {
		this.depositId = depositId;
	}
	public BigDecimal getMinimumDeposit() {
		return minimumDeposit;
	}
	public void setMinimumDeposit(BigDecimal minimumDeposit) {
		this.minimumDeposit = minimumDeposit;
	}
	public BigDecimal getMaximumDeposit() {
		return maximumDeposit;
	}
	public void setMaximumDeposit(BigDecimal maximumDeposit) {
		this.maximumDeposit = maximumDeposit;
	}


}
