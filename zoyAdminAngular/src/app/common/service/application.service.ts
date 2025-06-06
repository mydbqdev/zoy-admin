import { Injectable, Inject } from '@angular/core';
import {HttpClient,HttpHeaders,HttpErrorResponse} from '@angular/common/http';
import { MessageService } from 'src/app/message.service';
import { BASE_PATH } from '../shared/variables';
import { Observable, of} from 'rxjs';
import { Router } from '@angular/router';
import { ServiceHelper } from '../shared/service-helper';
@Injectable({
    providedIn:'root'
})

export class AppService{
    
    public oDataBlockSize:number;
    public baseODataUrl:string;
    httpOptions ={
        headers :new HttpHeaders({'Content-Type':'application/json'})
    };
 
    constructor(private httpclient:HttpClient,private router:Router,private messageService:MessageService,@Inject(BASE_PATH) private basePath:string){

    }

    getDashboardCard(): Observable<any> {
        const url1=this.basePath +"zoy_admin/admin_cards_details";
              return this.httpclient.get<any>(
                  url1,
                  {
                      headers:ServiceHelper.buildHeaders(),
                     observe : 'body',
                     withCredentials:true
                  }
              );
      } 
      getTenantCardDetails(): Observable<any> {
        const url1=this.basePath +"zoy_admin/tenant_card_details";
              return this.httpclient.get<any>(
                  url1,
                  {
                      headers:ServiceHelper.buildHeaders(),
                     observe : 'body',
                     withCredentials:true
                  }
              );
      } 
      getOwnersCardDetails(): Observable<any> {
        const url1=this.basePath +"zoy_admin/getOwnerCardDetails";
              return this.httpclient.get<any>(
                  url1,
                  {
                      headers:ServiceHelper.buildHeaders(),
                     observe : 'body',
                     withCredentials:true
                  }
              );
      } 
      getPropertiesCardDetails(): Observable<any> {
        const url1=this.basePath +"zoy_admin/properties_card_details";
              return this.httpclient.get<any>(
                  url1,
                  {
                      headers:ServiceHelper.buildHeaders(),
                     observe : 'body',
                     withCredentials:true
                  }
              );
      } 
      getTotalBookings(): Observable<any> {
        const url1=this.basePath +"zoy_admin/getBookingDetails";
              return this.httpclient.get<any>(
                  url1,
                  {
                      headers:ServiceHelper.buildHeaders(),
                     observe : 'body',
                     withCredentials:true
                  }
              );
      } 
      
      getRevenueCardDetails(year:string): Observable<any> { 
        const url1=this.basePath +"zoy_admin/getQuarterlyRevenue?financialYear="+year;
              return this.httpclient.post<any>(
                  url1,
                  {
                      headers:ServiceHelper.buildHeaders(),
                     observe : 'body',
                     withCredentials:true
                  }
              );
      } 
      getTotalRevenueDetails(): Observable<any> { 
        const url1=this.basePath +"zoy_admin/getLast7DaysRevenue";
              return this.httpclient.get<any>(
                  url1,
                  {
                      headers:ServiceHelper.buildHeaders(),
                     observe : 'body',
                     withCredentials:true
                  }
              );
      } 
    
      getTotalIssuesDetails(): Observable<any> { 
        const url1=this.basePath +"zoy_admin/getTotalIssues";
              return this.httpclient.get<any>(
                  url1,
                  {
                      headers:ServiceHelper.buildHeaders(),
                     observe : 'body',
                     withCredentials:true
                  }
              );
      } 

    private errorHandler(error:HttpErrorResponse){
        return of(error.message || "server error");
        
    }

    private log(message:string){
        this.messageService.add(`AuthService:${message}`,'info');
    }

    private handleError<T>( operation ='operation',result?:T){
        return (error:any):Observable<T> => {
            console.error(error);
            this.log(`${operation} failed: ${error.message}`);

            return of(result as T);
        };
    }
    
}