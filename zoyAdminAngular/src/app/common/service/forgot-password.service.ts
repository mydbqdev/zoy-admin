import { HttpClient, HttpErrorResponse, HttpHeaders } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { Observable,of } from 'rxjs';
import { ServiceHelper } from 'src/app/common/shared/service-helper';
import { BASE_PATH } from 'src/app/common/shared/variables';
import { MessageService } from 'src/app/message.service';

@Injectable({
    providedIn: 'root'
  })

  export class ForgotPasswordService{
    message : string;
    httpOptions ={
        headers :new HttpHeaders({'Content-Type':'application/json'})
    };
    constructor(private httpclient:HttpClient,@Inject(BASE_PATH) private basePath:string, private messageService:MessageService){

    }
       public rolesDropdown(): Observable<any> {
        const url1=this.basePath +'userrole/user/showRolesDropdown';
        return this.httpclient.post<String[]>(
            url1,
            '',
            {
                headers:ServiceHelper.buildHeaders(),
               observe : 'body',
               withCredentials:true
            }
        );
     }


      
     public sendOTP(userEmail:string): Observable<any> {
        const url1=this.basePath +"zoy_admin/send_otp?userEmail"+userEmail ;
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

     public savePassword(data:any): Observable<any> {
        const url1=this.basePath +"zoy_admin/savePassword" ;
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

     public submitOtp(data:any): Observable<any> {
        const url1=this.basePath +"zoy_admin/submitOtp" ;
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