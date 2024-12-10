package com.integration.zoy.utils;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.integration.zoy.model.ZoyBeforeCheckInCancellation;

public class ZoyAdminConfigDTO {
	@SerializedName("tokenDetails")
    private ZoyPgTokenDetailsDTO tokenDetails;
	@SerializedName("depositDetails")
    private ZoyPgSecurityDepositDetailsDTO depositDetails;
	@SerializedName("cancellationDetails")
    private List<ZoyBeforeCheckInCancellation> cancellationDetails;
	@SerializedName("refundRules")
    private ZoyPgSecurityDepositRefundRuleDto refundRules;
	@SerializedName("dataGrouping")
    private ZoyDataGroupingDto dataGrouping;
	@SerializedName("otherCharges")
    private ZoyOtherChargesDto otherCharges;
	
	public ZoyPgTokenDetailsDTO getTokenDetails() {
		return tokenDetails;
	}
	public void setTokenDetails(ZoyPgTokenDetailsDTO tokenDetails) {
		this.tokenDetails = tokenDetails;
	}
	
	public ZoyPgSecurityDepositDetailsDTO getDepositDetails() {
		return depositDetails;
	}
	public void setDepositDetails(ZoyPgSecurityDepositDetailsDTO depositDetails) {
		this.depositDetails = depositDetails;
	}
	
	public List<ZoyBeforeCheckInCancellation> getCancellationDetails() {
		return cancellationDetails;
	}
	public void setCancellationDetails(List<ZoyBeforeCheckInCancellation> cancellationDetails) {
		this.cancellationDetails = cancellationDetails;
	}
	public ZoyPgSecurityDepositRefundRuleDto getRefundRules() {
		return refundRules;
	}
	public void setRefundRules(ZoyPgSecurityDepositRefundRuleDto refundRules) {
		this.refundRules = refundRules;
	}
	
	public ZoyDataGroupingDto getDataGrouping() {
		return dataGrouping;
	}
	public void setDataGrouping(ZoyDataGroupingDto dataGrouping) {
		this.dataGrouping = dataGrouping;
	}
	public ZoyOtherChargesDto getOtherCharges() {
		return otherCharges;
	}
	public void setOtherCharges(ZoyOtherChargesDto otherCharges) {
		this.otherCharges = otherCharges;
	}
	
    
 
}

