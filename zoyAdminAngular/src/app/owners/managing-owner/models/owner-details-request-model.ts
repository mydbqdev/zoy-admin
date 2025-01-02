export class OwnerRequestParam {
    pageIndex: number;
    pageSize: number;
    sortActive: string;
    sortDirection: string;
    filter: Filter;
  }

  export class Filter{
    status:string[];
    searchText:string;
    startDate: string;
    endDate: string;
  }