import { HttpClient, HttpErrorResponse, HttpHeaders } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { Observable,of } from 'rxjs';
import { ServiceHelper } from 'src/app/common/shared/service-helper';
import { BASE_PATH } from 'src/app/common/shared/variables';
import { MessageService } from 'src/app/message.service';

@Injectable({
    providedIn: 'root'
  })

  export class GenerateSalesService{
    message : string;
    httpOptions ={
        headers :new HttpHeaders({'Content-Type':'application/json'})
    };
    constructor(private httpclient:HttpClient,@Inject(BASE_PATH) private basePath:string, private messageService:MessageService){

    }
    
     public generateOwnerCode(data:any): Observable<any> {
        const url1=this.basePath +"zoy_admin/savePgOwnerData" ;
        let param={"firstName":data.firstName,"lastName":data.lastName,"mobileNo":data.contactNumber,"emailId":data.userEmail,"zoyShare":data.zoyShare};
          return  this.httpclient.post<any>(
              url1,
              param,
              {
                  headers:ServiceHelper.buildHeaders(),
                 observe : 'body',
                 withCredentials:true
              }
          );
     }

     public resendOwnerCode(data:string): Observable<any> {
        const url1=this.basePath +"zoy_admin/resendPgOwnerData?email="+data ;
   
          return  this.httpclient.post<any>(
              url1,
              '',
              {
                headers:ServiceHelper.buildHeaders(),
                observe : 'body',
                withCredentials:true
             }
          );
     }
     public getGeneratedZoyCodeDetails(): Observable<any> {
          const url1=this.basePath +"zoy_admin/getAllPgOwnerData";
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