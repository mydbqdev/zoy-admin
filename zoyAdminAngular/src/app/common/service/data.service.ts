import { Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import { UserInfo } from '../shared/model/userinfo.service';
import { SignupDetails } from '../shared/signup-details';
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

     public owenerId=new BehaviorSubject<string>("Owner Details");
     getOwenerId=this.owenerId.asObservable();
     setOwenerId(owenerId:string){
        this.owenerId.next(owenerId);
     }
}