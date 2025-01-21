import { HttpClient, HttpErrorResponse, HttpHeaders } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { Observable,of } from 'rxjs';
import { ServiceHelper } from 'src/app/common/shared/service-helper';
import { BASE_PATH } from 'src/app/common/shared/variables';
import { MessageService } from 'src/app/message.service';

@Injectable({
    providedIn: 'root'
  })

  export class TenantService{
    message : string;
    httpOptions ={
        headers :new HttpHeaders({'Content-Type':'application/json'})
    };
    constructor(private httpclient:HttpClient,@Inject(BASE_PATH) private basePath:string, private messageService:MessageService){

    }
    
    public getTenantsList(data:any): Observable<any> {
          const url1=this.basePath +"zoy_admin/manage-tenants";
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
    public getTenantsDetails(tenantId:any): Observable<any> {
        const url1=this.basePath +"zoy_admin/manage-tenants-details?tenantid="+tenantId;
          return  this.httpclient.get<any>(
              url1,
              {
                 headers:ServiceHelper.buildHeaders(),
                 observe : 'body',
                 withCredentials:true
              }
          );
    }  
    public doDeactivateActivate(tenantId:any): Observable<any> {
        const url1=this.basePath +"zoy_admin/manage-tenants-details?tenantid="+tenantId;
          return  this.httpclient.get<any>(
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