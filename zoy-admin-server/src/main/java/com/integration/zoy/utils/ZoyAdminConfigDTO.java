package com.integration.zoy.utils;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ZoyAdminConfigDTO {
	@SerializedName("tokenDetails")
    private ZoyPgTokenDetailsDTO tokenDetails;
	@SerializedName("depositDetails")
    private ZoyPgSecurityDepositDetailsDTO depositDetails;
	@SerializedName("cancellationBeforeCheckInDetails")
    private List<ZoyBeforeCheckInCancellationDto> cancellationBeforeCheckInDetails;
	@SerializedName("earlyCheckOutRuleDetails")
    private ZoyPgEarlyCheckOutRuleDto earlyCheckOutRuleDetails;
	@SerializedName("cancellationAfterCheckInDetails")
    private ZoyAfterCheckInCancellationDto cancellationAfterCheckInDetails;
	@SerializedName("securityDepositDeadLineDetails")
    private ZoySecurityDepositDeadLineDto securityDepositDeadLineDetails;
	@SerializedName("dataGrouping")
    private ZoyDataGroupingDto dataGrouping;
	@SerializedName("otherCharges")
    private ZoyOtherChargesDto otherCharges;
	@SerializedName("gstCharges")
	private ZoyGstChargesDto gstCharges;
	
    
 
}

