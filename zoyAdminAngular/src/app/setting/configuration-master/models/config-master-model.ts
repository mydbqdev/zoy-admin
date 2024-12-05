export class ConfigMasterModel{
    tokenDetailsModel :TokenDetailsModel = new TokenDetailsModel() ;
    securityDepositLimitsModel : SecurityDepositLimitsModel = new SecurityDepositLimitsModel ();
    beforeCheckInCancellationRefund : BeforeCheckInCancellationRefundModel[] =[];
    securityDepositRefundModel : SecurityDepositRefundModel= new SecurityDepositRefundModel() ;
    dataGroupingModel : DataGroupingModel = new DataGroupingModel() ;
}

export class TokenDetailsModel{
    tokenId : string = '';
    fixedToken : number = null;
    variableToken : number = null;
}

export class SecurityDepositLimitsModel{
    depositId : string ;
    minimumDeposit : number ;
    maximumDeposit : number ;
}

export class BeforeCheckInCancellationRefundModel{
    cancellationId : string ;
    daysBeforeCheckIn : number ;
    deductionPercentages : number ;
}

export class SecurityDepositRefundModel{
    refundId : string ;
    maximumDays : number ;
    deductionPercentages : number ;
}

export class DataGroupingModel{
    id : string ;
    considerDays : number ;
}