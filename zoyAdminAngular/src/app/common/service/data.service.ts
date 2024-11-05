import { Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import { SignupDetails } from '../shared/signup-details';
@Injectable({
    providedIn:'root'
})

export class DataService{
    public userDetails=new BehaviorSubject<SignupDetails>(null);

    getUserDetails=this.userDetails.asObservable();

    setUserDetails(userDetails:SignupDetails){
       this.userDetails.next(userDetails);  
    }

    public isSale=new BehaviorSubject<boolean>(true);

    public $topSearch=new BehaviorSubject<string>("");
    
    getIsSale=this.isSale.asObservable();
    setIsSale(isSale:boolean){
       this.isSale.next(isSale);
    }

    getTopSearch=this.$topSearch.asObservable();
    setTopSearch(topSearch:string){
        this.$topSearch.next(topSearch);
     }
}