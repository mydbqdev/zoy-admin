export class ConfigMasterModel{
    tokenDetails :TokenDetailsModel[]=[];// = new TokenDetailsModel() ;
    depositDetails : SecurityDepositLimitsModel[]=[];// = new SecurityDepositLimitsModel ();
    dataGrouping : DataGroupingModel[]=[];// = new DataGroupingModel() ;
   // cancellationBeforeCheckInDetails : BeforeCheckInCancellationRefundModel[]=[];
    earlyCheckOutRuleDetails:EarlyCheckOutRuleDetails[]=[];//=new EarlyCheckOutRuleDetails();
    cancellationAfterCheckInDetails:SecurityDepositDeadLineAndAutoCancellationModel[]=[];//=new SecurityDepositDeadLineAndAutoCancellationModel();
    securityDepositDeadLineDetails :SecurityDepositDeadLineAndAutoCancellationModel[]=[];//=new SecurityDepositDeadLineAndAutoCancellationModel();
    otherCharges : OtherChargesModel[]=[];//=new OtherChargesModel();
    forceCheckOut :ForceCheckoutModel[]=[];// = new ForceCheckoutModel();
    gstCharges : GstChargesModel[]=[];// = new GstChargesModel();
    shortTerm :ShortTermModel[] = [];
    shortTermRentingDuration:ShortTermRentingDuration[]=[];// = new ShortTermRentingDuration();
    noRentalAgreement:NoRentalAgreement[]=[];// =new NoRentalAgreement();
}

export class TokenDetailsModel{
    tokenId : string = '';
    fixedToken : number ;
    variableToken : number ;
    effectiveDate:string='';
    isApproved:boolean=false;
    createdBy:string='';
    approvedBy:string='';
}

export class SecurityDepositLimitsModel{
    depositId : string ='';
    minimumDeposit : number ;
    maximumDeposit : number ;
    effectiveDate:string='';
    isApproved:boolean=false;
    createdBy:string='';
    approvedBy:string='';
}

export class EarlyCheckOutRuleDetails{
    early_check_out_id : string ='';
    trigger_on : string ="WebCheckOut";
    trigger_condition : string ;
    check_out_day : number ;
    deduction_percentage: number ;
    deduction_day: number ;
    trigger_value : string ;
    effectiveDate:string='';
    isApproved:boolean=false;
    createdBy:string='';
    approvedBy:string='';
}
export class BeforeCheckInCancellationRefundMainObjModel{
    effectiveDate:string;
    iscreate:boolean=false;
    isApproved:boolean=false;
    approvedBy:string;
    createdBy:string;
    pgType:string;
    ZoyBeforeCheckInCancellationInfo:BeforeCheckInCancellationRefundModel[]=[];

}
export class BeforeCheckInCancellationRefundModel{
    cancellation_id : string ='';
    before_checkin_days : number ;
    deduction_percentage : number ;
    trigger_value : string ='TotalPaidAmount';
    trigger_on : string  ="WebCheckIn";
    trigger_condition : string ;
    priority: number ;
    isDelete:boolean ;
    isEdit:boolean ;
    isConfirm:boolean ;
    effectiveDate:string='';
    isApproved:boolean=false;
    createdBy:string='';
    approvedBy:string='';
}

export class DataGroupingModel{
    dataGroupingId : string ;
    considerDays : number ;
    effectiveDate:string='';
    isApproved:boolean=false;
    createdBy:string='';
    approvedBy:string='';
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
    effectiveDate:string='';
    isApproved:boolean=false;
    createdBy:string='';
    approvedBy:string='';
}

export class OtherChargesModel{
    otherChargesId : string ='';
    tenantDocumentCharges : number ;  
    ownerEkycCharges : number ;
    ownerDocumentCharges  : number ;
    tenantEkycCharges : number ;
    effectiveDate:string='';
    isApproved:boolean=false; 
    createdBy:string='';
    approvedBy:string='';
}


export class ForceCheckoutModel{
    forceCheckOutId : string ='';
    forceCheckOutDays  : number ;
    effectiveDate:string='';
    isApproved:boolean=false;
    createdBy:string='';
    approvedBy:string='';
}

export class GstChargesModel{
    rentId: string='' ;
    cgstPercentage:number ;
    sgstPercentage: number ;
    igstPercentage:number ;
    monthlyRent: number ;
    effectiveDate:string='';
    isApproved:boolean=false;
    createdBy:string='';
    approvedBy:string='';
}

export class ShortTermModel{
    shortTermId: string ;
    days: string ;
    percentage: number ;
    termDisabled :boolean =true;
    effectiveDate:string='';
    isApproved:boolean=false;
    createdBy:string='';
    approvedBy:string='';
}

export class ShortTermRentingDuration  {
    rentingDurationId :string;
    rentingDurationDays: number;
    effectiveDate:string='';
    isApproved:boolean=false;
    createdBy:string='';
    approvedBy:string='';
}
export class NoRentalAgreement  {
    noRentalAgreementId :string;
    noRentalAgreementDays: number;
    effectiveDate:string='';
    isApproved:boolean=false;
    createdBy:string='';
    approvedBy:string='';
}

export class ShortTermMainModel{
    effectiveDate:string;
    iscreate:boolean;
    isApproved:boolean;
    approvedBy:string;
    createdBy:string;
    ZoyShortTermDtoInfo:ShortTermSubModel[]=[];

}

export class ShortTermSubModel{
    shortTermId : string ='';
    startDay : number ;
    endDay : number ;
    percentage : number ;
    isDelete:boolean ;
    isEdit:boolean ;
    isConfirm:boolean ;
    effectiveDate:string='';
    isApproved:boolean=false;
    createdBy:string='';
    approvedBy:string='';
}