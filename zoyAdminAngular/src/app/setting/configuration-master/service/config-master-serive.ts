import { HttpClient, HttpErrorResponse, HttpHeaders } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { Observable,of } from 'rxjs';
import { ServiceHelper } from 'src/app/common/shared/service-helper';
import { BASE_PATH } from 'src/app/common/shared/variables';
import { MessageService } from 'src/app/message.service';

@Injectable({
    providedIn: 'root'
  })

  export class ConfigMasterService{
    message : string;
    httpOptions ={
        headers :new HttpHeaders({'Content-Type':'application/json'})
    };
    constructor(private httpclient:HttpClient,@Inject(BASE_PATH) private basePath:string, private messageService:MessageService){

    }
    
     public getConfigMasterDetails(): Observable<any> {
          const url1=this.basePath +"zoy_admin/config/admin-configuration-details";
            return  this.httpclient.post<any>(
                url1,
                "",
                {
                   headers:ServiceHelper.buildHeaders(),
                   observe : 'body',
                   withCredentials:true
                }
            );
    } 
     public updateTokenAdvanceDetails(data:any): Observable<any> {
            const url1=this.basePath +"zoy_admin/config/token_advance";
            return  this.httpclient.post<any>(
                url1,
                data,
                {
                    headers:ServiceHelper.buildHeaders(),
                    observe : 'body',
                    withCredentials:true
                }
            );
    } 
    public updatesecurityDepositLimitsDetails(data:any): Observable<any> {
        const url1=this.basePath +"zoy_admin/config/security-deposit-limits";
        return  this.httpclient.post<any>(
            url1,
            data,
            {
                headers:ServiceHelper.buildHeaders(),
                observe : 'body',
                withCredentials:true
            }
        );
    }    
    public updateDataGroupingDetails(data:any): Observable<any> {
        const url1=this.basePath +"zoy_admin/config/data-grouping";
        return  this.httpclient.post<any>(
            url1,
            data,
            {
                headers:ServiceHelper.buildHeaders(),
                observe : 'body',
                withCredentials:true
            }
        );
    } 

    public submitBeforeCheckInCRfDetails(data:any): Observable<any> {
        const url1=this.basePath +"zoy_admin/config/before-check-in";
        return  this.httpclient.post<any>(
            url1,
            data,
            {
                headers:ServiceHelper.buildHeaders(),
                observe : 'body',
                withCredentials:true
            }
        );
    }
    public updatesecurityDepositRefundDetails(data:any): Observable<any> {
        const url1=this.basePath +"zoy_admin/config/early-checkout-rules";
        return  this.httpclient.post<any>(
            url1,
            data,
            {
                headers:ServiceHelper.buildHeaders(),
                observe : 'body',
                withCredentials:true
            }
        );
    } 
    public deleteRefundRule(data:any): Observable<any> {
        const url1=this.basePath +"zoy_admin/config/deleteCancellationRefundRule";
        return  this.httpclient.delete<any>(
            url1,
            {
                body: data,
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