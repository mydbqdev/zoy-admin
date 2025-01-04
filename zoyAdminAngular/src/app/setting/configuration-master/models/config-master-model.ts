export class ConfigMasterModel{
    tokenDetails :TokenDetailsModel = new TokenDetailsModel() ;
    depositDetails : SecurityDepositLimitsModel = new SecurityDepositLimitsModel ();
    cancellationDetails : BeforeCheckInCancellationRefundModel[] =[];
    refundRules : SecurityDepositRefundModel= new SecurityDepositRefundModel() ;
    dataGrouping : DataGroupingModel = new DataGroupingModel() ;
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
    bcicrDisable : boolean = true ;
    triggerOn : string ;
    condName : string ;
    sequenceOrder: number ;
}

export class SecurityDepositRefundModel{
    refundId : string ;
    maximumDays : number ;
    plotformCharges : number ;
}

export class DataGroupingModel{
    id : string ;
    considerDays : number ;
}