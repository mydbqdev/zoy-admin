package com.integration.zoy.utils;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.integration.zoy.entity.RentalAgreementDoc;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ZoyAdminConfigDTO {
	@SerializedName("tokenDetails")
    private List<ZoyPgTokenDetailsDTO> tokenDetails;
	@SerializedName("depositDetails")
    private List<ZoyPgSecurityDepositDetailsDTO> depositDetails;
	@SerializedName("cancellationBeforeCheckInDetails")
    private List<ZoyBeforeCheckInCancellationDto> cancellationBeforeCheckInDetails;
	@SerializedName("earlyCheckOutRuleDetails")
    private List<ZoyPgEarlyCheckOutRuleDto> earlyCheckOutRuleDetails;
	@SerializedName("cancellationAfterCheckInDetails")
    private List<ZoyAfterCheckInCancellationDto> cancellationAfterCheckInDetails;
	@SerializedName("securityDepositDeadLineDetails")
    private List<ZoySecurityDepositDeadLineDto> securityDepositDeadLineDetails;
	@SerializedName("dataGrouping")
    private List<ZoyDataGroupingDto> dataGrouping;
	@SerializedName("otherCharges")
    private List<ZoyOtherChargesDto> otherCharges;
	@SerializedName("gstCharges")
	private List<ZoyGstChargesDto> gstCharges;
	@SerializedName("shortTerm")
	private List<ZoyShortTermDto> zoyShortTermDtos;
	@SerializedName("forceCheckOut")
	private List<ZoyForceCheckOutDto> zoyForceCheckOutDto;
	@SerializedName("shortTermRentingDuration")
	private List<ZoyRentingDuration> shortTermRentingDuration;
	@SerializedName("noRentalAgreement")
	private List<ZoyPgNoRentalAgreementDto> NoRentalAgreement;
	@SerializedName("rentalAgreement")
	private List<RentalAgreementDoc> RentalAgreement;
 
}

