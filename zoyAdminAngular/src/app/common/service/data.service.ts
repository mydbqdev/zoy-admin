import { Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import { OwnerRequestParam } from 'src/app/owners/managing-owner/models/owner-details-request-model';
import { ZoyOwner } from 'src/app/owners/managing-owner/models/zoy-owner-model';
import { UserInfo } from '../shared/model/userinfo.service';
import { ManageTenant } from 'src/app/tenants/tenant-profile/model/manage.tenant';
import { TotalBookingDetailsModel } from '../models/dashboard.model';
@Injectable({
    providedIn:'root'
})

export class DataService{
    public userDetails=new BehaviorSubject<UserInfo>(null);
    getUserDetails=this.userDetails.asObservable();
    setUserDetails(userDetails:UserInfo){
       this.userDetails.next(userDetails);  
    }

    public isExpandSideBar=new BehaviorSubject<boolean>(true);
    getIsExpandSideBar=this.isExpandSideBar.asObservable();
    setIsExpandSideBar(isExpandSideBar:boolean){
       this.isExpandSideBar.next(isExpandSideBar);
    }

    public $topSearch=new BehaviorSubject<string>("");
    getTopSearch=this.$topSearch.asObservable();
    setTopSearch(topSearch:string){
        this.$topSearch.next(topSearch);
     }

     public headerName=new BehaviorSubject<string>("Dashboard");
     getHeaderName=this.headerName.asObservable();
     setHeaderName(headerName:string){
        this.headerName.next(headerName);
     }

     public owenerId=new BehaviorSubject<string>("");
     getOwenerId=this.owenerId.asObservable();
     setOwenerId(owenerId:string){
        this.owenerId.next(owenerId);
     }

     public owenerListFilter=new BehaviorSubject<ZoyOwner[]>([]);
     getOwenerListFilter=this.owenerListFilter.asObservable();
     setOwenerListFilter(owenerListFilter:ZoyOwner[]){
        this.owenerListFilter.next(owenerListFilter);
     }

     public owenerListFilterTotal=new BehaviorSubject<number>(0);
     getOwenerListFilterTotal=this.owenerListFilterTotal.asObservable();
     setOwenerListFilterTotal(owenerListFilterTotal:number){
        this.owenerListFilterTotal.next(owenerListFilterTotal);
     }

     public owenerListFilterParam=new BehaviorSubject<OwnerRequestParam>(null);
     getOwenerListFilterParam=this.owenerListFilterParam.asObservable();
     setOwenerListFilterParam(owenerListFilterParam:OwnerRequestParam){
        this.owenerListFilterParam.next(owenerListFilterParam);
     }

     public tenantId=new BehaviorSubject<string>("");
     getTenantId=this.tenantId.asObservable();
     setTenantId(tenantId:string){
        this.tenantId.next(tenantId);
     }

     public tenantListFilter=new BehaviorSubject<ManageTenant[]>([]);
     getTenantListFilter=this.tenantListFilter.asObservable();
     setTenantListFilter(tenantListFilter:ManageTenant[]){
        this.tenantListFilter.next(tenantListFilter);
     }

     public tenantListFilterTotal=new BehaviorSubject<number>(0);
     getTenantListFilterTotal=this.owenerListFilterTotal.asObservable();
     setTenantListFilterTotal(tenantListFilterTotal:number){
        this.owenerListFilterTotal.next(tenantListFilterTotal);
     }

     public tenantListFilterParam=new BehaviorSubject<OwnerRequestParam>(null);
     getTenantListFilterParam=this.tenantListFilterParam.asObservable();
     setTenantListFilterParam(tenantListFilterParam:OwnerRequestParam){
        this.tenantListFilterParam.next(tenantListFilterParam);
     }

     public dashboardBookingDetails=new BehaviorSubject<TotalBookingDetailsModel>(new TotalBookingDetailsModel());
     getDashboardBookingDetails=this.dashboardBookingDetails.asObservable();
     setDashboardBookingDetails(dashboardBookingDetails:TotalBookingDetailsModel){
        this.dashboardBookingDetails.next(dashboardBookingDetails);
     }
}