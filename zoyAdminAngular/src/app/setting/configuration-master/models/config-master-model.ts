export class ConfigMasterModel{
    tokenDetails :TokenDetailsModel = new TokenDetailsModel() ;
    depositDetails : SecurityDepositLimitsModel = new SecurityDepositLimitsModel ();
    dataGrouping : DataGroupingModel = new DataGroupingModel() ;
    cancellationBeforeCheckInDetails : BeforeCheckInCancellationRefundModel[]=[];
    earlyCheckOutRuleDetails:EarlyCheckOutRuleDetails=new EarlyCheckOutRuleDetails();
    cancellationAfterCheckInDetails:SecurityDepositDeadLineAndAutoCancellationModel=new SecurityDepositDeadLineAndAutoCancellationModel();
    securityDepositDeadLineDetails :SecurityDepositDeadLineAndAutoCancellationModel=new SecurityDepositDeadLineAndAutoCancellationModel();
    otherCharges : OtherChargesModel=new OtherChargesModel();
    forceCheckOut :ForceCheckoutModel = new ForceCheckoutModel();
    checkoutDeductionDetails: CheckoutDeductionDetailsModel = new CheckoutDeductionDetailsModel();
    rentSlabModel :RentSlabModel = new RentSlabModel();
    gstSlabModel :GstSlabModel[] = [];
    gstCharges : GstChargesModel = new GstChargesModel();
    shortTerm :ShortTermModel[] = [];
    
}

export class TokenDetailsModel{
    tokenId : string = '';
    fixedToken : number = null;
    variableToken : number = null;
}

export class SecurityDepositLimitsModel{
    depositId : string ='';
    minimumDeposit : number ;
    maximumDeposit : number ;
}

export class EarlyCheckOutRuleDetails{
    early_check_out_id : string ='';
    trigger_on : string ="WebCheckOut";
    trigger_condition : string ;
    check_out_day : number ;
    deduction_percentage: number ;
    trigger_value : string ;
}

export class BeforeCheckInCancellationRefundModel{
    cancellation_id : string ='';
    before_checkin_days : number ;
    deduction_percentage : number ;
    trigger_value : string ;
    trigger_on : string  ="WebCheckIn";
    trigger_condition : string ;
    priority: number ;
    isDelete:boolean = false;
    isEdit:boolean = false;
}

export class DataGroupingModel{
    id : string ;
    considerDays : number ;
}

export class SecurityDepositDeadLineAndAutoCancellationModel{
    auto_cancellation_id : string ='';
    trigger_on : string ;
    trigger_condition : string ;
    auto_cancellation_day : number ;
    deduction_percentage : number ;
    cond : string ;
    trigger_value : string ;
}

export class OtherChargesModel{
    otherChargesId : string ='';
    ownerEkycCharges : number ;
    ownerDocumentCharges  : number ;
    tenantEkycCharges : number ;
    tenantDocumentCharges : number ;   
}


export class ForceCheckoutModel{
    id : string ='';
    forceCheckOutDays  : number ;
}

export class CheckoutDeductionDetailsModel {
    checkout_deduction_Id : string ='';
    trigger_condition: string ;
    notice_period_days: number ;
    deduction_percentage: number ;
    trigger_value : string ;
  }

  export class RentSlabModel  {
    rentSlabId: string ;
    slabFrom: string ;
    slabTo: string ;
    rentPercentage: number ;
    isEdit:boolean = false;
    isDelete:boolean = false;
  };

  export class GstSlabModel  {
    rentSlabId: string ;
    slabFrom: string ;
    slabTo: string ;
    gstPercentage: number ;
    isEdit:boolean = false;
    isDelete:boolean = false;
    showInputBox:boolean = false;
  };

 export class GstChargesModel{
    rentId: string ;
    gstPercentage: number ;
    monthlyRent: number ;
}

export class ShortTermModel{
    shortTermId: string ;
    days: string ;
    percentage: number ;
    termDisabled :boolean =true;
}
