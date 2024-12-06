import { HttpClient, HttpErrorResponse, HttpHeaders } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { Observable,of } from 'rxjs';
import { ServiceHelper } from 'src/app/common/shared/service-helper';
import { BASE_PATH } from 'src/app/common/shared/variables';
import { MessageService } from 'src/app/message.service';
import { BeforeCheckInCancellationRefundModel, DataGroupingModel, SecurityDepositLimitsModel, SecurityDepositRefundModel, TokenDetailsModel } from "../models/config-master-model";

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
          const url1=this.basePath +"zoy_admin/getConfigMasterDetails";
            return  this.httpclient.get<any>(
                url1,
                {
                   headers:ServiceHelper.buildHeaders(),
                   observe : 'body',
                   withCredentials:true
                }
            );
    } 
     public updateTokenAdvanceDetails(data:TokenDetailsModel): Observable<any> {
            const url1=this.basePath +"zoy_admin/UpdateTokenAdvanceDetails";
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
    public updatesecurityDepositLimitsDetails(data:SecurityDepositLimitsModel): Observable<any> {
        const url1=this.basePath +"zoy_admin/UpdatesecurityDepositLimitsDetails";
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
    public updateDataGroupingDetails(data:DataGroupingModel): Observable<any> {
        const url1=this.basePath +"zoy_admin/UpdateDataGroupingDetails";
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

    public submitBeforeCheckInCRfDetails(data:BeforeCheckInCancellationRefundModel): Observable<any> {
        const url1=this.basePath +"zoy_admin/submitBeforeCheckInCRfDetails";
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
    public updatesecurityDepositRefundDetails(data:SecurityDepositRefundModel): Observable<any> {
        const url1=this.basePath +"zoy_admin/updatesecurityDepositRefundDetails";
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