package com.integration.zoy.utils;

import java.util.List;

import com.integration.zoy.entity.ZoyDataGrouping;
import com.integration.zoy.entity.ZoyPgCancellationDetails;
import com.integration.zoy.entity.ZoyPgOtherCharges;
import com.integration.zoy.entity.ZoyPgSecurityDepositDetails;
import com.integration.zoy.entity.ZoyPgSecurityDepositRefundRule;
import com.integration.zoy.entity.ZoyPgTokenDetails;

public class ZoyAdminConfigDTO {

    private ZoyPgTokenDetails tokenDetails;
    private ZoyPgSecurityDepositDetails depositDetails;
    private List<ZoyPgCancellationDetails> cancellationDetails;
    private ZoyPgSecurityDepositRefundRule refundRules;
    private ZoyDataGrouping dataGrouping;
    private ZoyPgOtherCharges otherCharges;
	public ZoyPgTokenDetails getTokenDetails() {
		return tokenDetails;
	}
	public void setTokenDetails(ZoyPgTokenDetails tokenDetails) {
		this.tokenDetails = tokenDetails;
	}
	public ZoyPgSecurityDepositDetails getDepositDetails() {
		return depositDetails;
	}
	public void setDepositDetails(ZoyPgSecurityDepositDetails depositDetails) {
		this.depositDetails = depositDetails;
	}
	
	public List<ZoyPgCancellationDetails> getCancellationDetails() {
		return cancellationDetails;
	}
	public void setCancellationDetails(List<ZoyPgCancellationDetails> cancellationDetails) {
		this.cancellationDetails = cancellationDetails;
	}
	public ZoyPgSecurityDepositRefundRule getRefundRules() {
		return refundRules;
	}
	public void setRefundRules(ZoyPgSecurityDepositRefundRule refundRules) {
		this.refundRules = refundRules;
	}
	public ZoyDataGrouping getDataGrouping() {
		return dataGrouping;
	}
	public void setDataGrouping(ZoyDataGrouping dataGrouping) {
		this.dataGrouping = dataGrouping;
	}
	public ZoyPgOtherCharges getOtherCharges() {
		return otherCharges;
	}
	public void setOtherCharges(ZoyPgOtherCharges otherCharges) {
		this.otherCharges = otherCharges;
	}
    
 
}

