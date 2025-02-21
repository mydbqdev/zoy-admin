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
    gstCharges : GstChargesModel = new GstChargesModel();
    shortTerm :ShortTermModel[] = [];
    shortTermRentingDuration:ShortTermRentingDuration = new ShortTermRentingDuration();
    noRentalAgreement:NoRentalAgreement =new NoRentalAgreement();
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
    deduction_day: number ;
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
    isDelete:boolean ;
    isEdit:boolean ;
    isConfirm:boolean ;
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
    deduction_day : number ;
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

export class GstChargesModel{
    rentId: string ;
    cgstPercentage:number ;
    sgstPercentage: number ;
    igstPercentage:number ;
    monthlyRent: number ;
}

export class ShortTermModel{
    shortTermId: string ;
    days: string ;
    percentage: number ;
    termDisabled :boolean =true;
}

export class ShortTermRentingDuration  {
    rentingDurationId :string;
    rentingDurationDays: number;
}
export class NoRentalAgreement  {
    noRentalAgreementId :string;
    noRentalAgreementDays: number;
}
