export class ConfigMasterModel{
    tokenDetails :TokenDetailsModel[]=[];
    depositDetails : SecurityDepositLimitsModel[]=[];
    dataGrouping : DataGroupingModel[]=[];
    earlyCheckOutRuleDetails:EarlyCheckOutRuleDetails[]=[];
    cancellationAfterCheckInDetails:SecurityDepositDeadLineAndAutoCancellationModel[]=[];
    securityDepositDeadLineDetails :SecurityDepositDeadLineAndAutoCancellationModel[]=[];
    otherCharges : OtherChargesModel[]=[];
    forceCheckOut :ForceCheckoutModel[]=[];
    gstCharges : GstChargesModel[]=[];
    noRentalAgreement:NoRentalAgreement[]=[];
}
export class ConfigMasterObjModel {
    tokenDetails: TokenDetailsModel = new TokenDetailsModel();
    depositDetails: SecurityDepositLimitsModel = new SecurityDepositLimitsModel();
    dataGrouping: DataGroupingModel = new DataGroupingModel();
    earlyCheckOutRuleDetails: EarlyCheckOutRuleDetails = new EarlyCheckOutRuleDetails();
    cancellationAfterCheckInDetails: SecurityDepositDeadLineAndAutoCancellationModel = new SecurityDepositDeadLineAndAutoCancellationModel();
    securityDepositDeadLineDetails: SecurityDepositDeadLineAndAutoCancellationModel = new SecurityDepositDeadLineAndAutoCancellationModel();
    otherCharges: OtherChargesModel = new OtherChargesModel();
    forceCheckOut: ForceCheckoutModel = new ForceCheckoutModel();
    gstCharges: GstChargesModel = new GstChargesModel();
    noRentalAgreement: NoRentalAgreement = new NoRentalAgreement();

    beforeCheckInCancellationRefundMainObjModel :BeforeCheckInCancellationRefundMainObjModel =new BeforeCheckInCancellationRefundMainObjModel();
    shortTermMainModel: ShortTermMainModel=new ShortTermMainModel();
  }
  

export class TokenDetailsModel{
    tokenId : string = '';
    fixedToken : number ;
    variableToken : number ;
    effectiveDate:string='';
    isApproved:boolean=false;
    createdBy:string='';
    approvedBy:string='';
    comments:string='';
}

export class SecurityDepositLimitsModel{
    depositId : string ='';
    minimumDeposit : number ;
    maximumDeposit : number ;
    effectiveDate:string='';
    isApproved:boolean=false;
    createdBy:string='';
    approvedBy:string='';
    comments:string='';
}

export class EarlyCheckOutRuleDetails{
    early_check_out_id : string ='';
    trigger_on : string ="WebCheckOut";
    trigger_condition : string ;
    check_out_day : number ;
    deduction_percentage: number ;
    deduction_day: number ;
    trigger_value : string ='Rent' ;
    effectiveDate:string='';
    isApproved:boolean=false;
    createdBy:string='';
    approvedBy:string='';
    comments:string='';
}
export class BeforeCheckInCancellationRefundMainObjModel{
    effectiveDate:string;
    iscreate:boolean=false;
    isApproved:boolean=true;
    approvedBy:string;
    createdBy:string;
    comments:string='';
    pgType:string;
    ZoyBeforeCheckInCancellationInfo:BeforeCheckInCancellationRefundModel[]=[];
    zoy_before_check_in_cancellation_info:BeforeCheckInCancellationRefundModel[]=[];

}
export class BeforeCheckInCancellationRefundModel{
    cancellation_id : string ='';
    before_checkin_days : number ;
    deduction_percentage : number ;
    trigger_value : string ='TotalPaidAmount';
    trigger_on : string  ="WebCheckIn";
    trigger_condition : string ;
    priority: number ;
    isDelete:boolean =false;
    isEdit:boolean ;
    isConfirm:boolean ;
    effectiveDate:string='';
    isApproved:boolean=false;
    createdBy:string='';
    approvedBy:string='';
    comments:string='';
}

export class DataGroupingModel{
    dataGroupingId : string ;
    considerDays : number ;
    effectiveDate:string='';
    isApproved:boolean=false;
    createdBy:string='';
    approvedBy:string='';
    comments:string='';
}

export class SecurityDepositDeadLineAndAutoCancellationModel{
    auto_cancellation_id : string ='';
    trigger_on : string ;
    trigger_condition : string ;
    auto_cancellation_day : number ;
    deduction_percentage : number ;
    deduction_day : number ;
    cond : string ;
    trigger_value : string ='Rent';
    effectiveDate:string='';
    isApproved:boolean=false;
    createdBy:string='';
    approvedBy:string='';
    comments:string='';
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
    comments:string='';
}


export class ForceCheckoutModel{
    forceCheckOutId : string ='';
    forceCheckOutDays  : number ;
    effectiveDate:string='';
    isApproved:boolean=false;
    createdBy:string='';
    approvedBy:string='';
    comments:string='';
}

export class GstChargesModel{
    rentId: string='' ;
    cgstPercentage:number ;
    sgstPercentage: number ;
    igstPercentage:number ;
    perDayRent: number ;
    effectiveDate:string='';
    isApproved:boolean=false;
    createdBy:string='';
    approvedBy:string='';
    comments:string='';
}

export class NoRentalAgreement  {
    noRentalAgreementId :string;
    noRentalAgreementDays: number;
    effectiveDate:string='';
    isApproved:boolean=false;
    createdBy:string='';
    approvedBy:string='';
    comments:string='';
}

export class ShortTermMainModel{
    effectiveDate:string;
    iscreate:boolean;
    isApproved:boolean;
    approvedBy:string;
    createdBy:string;
    comments:string='';
    zoy_short_term_dto_info:ShortTermSubModel[]=[];
    zoyShortTermDtoInfo:ShortTermSubModel[]=[];
}

export class ShortTermSubModel{
    short_term_id : string ='';
    start_day : number ;
    end_day : number ;
    percentage : number ;
    isDelete:boolean ;
    isEdit:boolean ;
    isConfirm:boolean ;
}
