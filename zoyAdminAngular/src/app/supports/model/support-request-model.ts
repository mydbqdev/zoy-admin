export class SupportRequestParam {
    pageIndex: number;
    pageSize: number;
    sortActive: string;
    sortDirection: string;
    filter: Filter;
    isUserActivity:boolean;
  }

  export class Filter{
    status:string;
    searchText:string;
    startDate: string;
    endDate: string;
    email:string;
  }