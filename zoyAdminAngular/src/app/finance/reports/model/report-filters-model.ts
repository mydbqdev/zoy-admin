export class FiltersRequestModel{

    public searchText :string='';
    public pageIndex :number =0 ;
    public pageSize :number  ;
    public sortActive:string="";
    public sortDirection:string="asc";
    public userEmail :string ='';

    public cityLocation :string  ;
    public fromDate ?: string  ;
    public toDate ?: string  ;
    public reportName ?: string  ;
    public reportType ?: string  ;
    public downloadType ?: string  ;
    public filterData ?:string  ;


}

export class FilterData {
    tenantId: string ='';
    tenantName: string ='';
    transactionStatus: string ='';
    modeOfPayment: string ='';
    zoyCode: string ='';
    ownerName: string ='';
    pgId: string ='';
    pgName: string ='';
    payeeId: string ='';
    payeeName: string ='';
    payerId: string ='';
    payerName: string ='';
  }