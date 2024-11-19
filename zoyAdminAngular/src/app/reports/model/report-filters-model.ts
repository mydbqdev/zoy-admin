export class FiltersRequestModel{

    public searchText :string='';
    public pageIndex :number =1;
    public pageSize :number ;
    public sortActive:string="";
    public sortDirection:string="asc";

    public cityLocation :string ;
    public fromDate ?: string ;
    public toDate ?: string ;
    public reportType ?: string ;
    public downloadType ?: string ;


}