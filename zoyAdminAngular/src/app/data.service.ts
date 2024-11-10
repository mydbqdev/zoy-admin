import { Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
@Injectable({
    providedIn:'root'
})

export class DataService{
 
    public headerName=new BehaviorSubject<string>("Dashboard");

    getHeaderName=this.headerName.asObservable();
    setHeaderName(headerName:string){
       this.headerName.next(headerName);
    }


}