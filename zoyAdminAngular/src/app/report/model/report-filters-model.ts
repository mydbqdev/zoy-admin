export class FiltersRequestModel{

    public searchText :string='';
    public pageIndex :number =0 ;
    public pageSize :number  ;
    public sortActive:string="";
    public sortDirection:string="asc";

    public userEmail :string ='';
    public activity :string ='';
    public isUserActivity: boolean =false;

    public cityLocation :string  ;
    public fromDate ?: string  ;
    public toDate ?: string  ;
    public reportName ?: string  ;
    public reportType ?: string  ;
    public downloadType ?: string  ;
    public filterData ?:string  ;
    public propertyId: string ='';

    public isAlert :boolean;

}

export class FilterData {
    tenantName: string ='';
    transactionStatus: string ='';
    ownerName: string ='';
    pgName: string ='';
    payeeName: string ='';
    payerName: string ='';
    payerType: string ='';
    tenantContactNum: string ='';
    transactionNumber: string ='';
    ownerEmail: string ='';
    bedNumber: string ='';
    ownerApprovalStatus: string ='';
    bookingId: string ='';
    refundTitle: string ='';
    overallRating: string ='';
    tenantEmail: string ='';
    propertyContactNum : string ='';
    pgAddress:string ='';

    invoiceNo:string='';
    SharingType:string='';
    roomNo:string='';
    modeOfPayment:string='';

  }