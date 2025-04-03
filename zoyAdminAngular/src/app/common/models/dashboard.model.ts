
export class TopRevenuePG{
    sl_no:string;
    pg_name:string;
    revenue:string;
}

export class TotalBookingDetailsModel{
    checked_in:number=0;
    booked:number=0;
    vacancy:number=0;
}   
export class DashboardFilterModel{
    fromDate:string="";
    toDate:string="";
    selectedDays:string="15 days";
}  


export class TenantsCardModel{
    activeTenantsCount : number = 0 ;
    upcomingTenantsCount : number = 0 ;
    inactiveTenantsCount : number = 0 ;
    registerTenantsCount : number = 0 ;
}

export class OwnersCardModel{
    leadOwnersCount : number = 0 ;
    zoyCodeGeneratedOwnersCount : number = 0 ;
    ownerAppUsersCount : number = 0 ;
    ZeroPropertyOwnersCount : number = 0 ;
}

export class PropertiesCardModel{
    potentialProperties : number = 0 ;
    nonPotentialProperties : number = 0 ;
    upComingpotentialProperties : number = 0 ;
}