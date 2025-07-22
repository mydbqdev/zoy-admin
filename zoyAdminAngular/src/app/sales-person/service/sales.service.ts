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
    
     public registerSubmitSalesPerson(data:any): Observable<any> {
        if(data.userGroupId == "0"){
            data.userGroupId = "";
            data.userGroupName = 'Sales-'+data.userGroupName;
        }
        const url1=this.basePath +"zoy_admin/zoyAdminSalesCreateUser" ;
        let param={"firstName":data.firstName,"middleName":data.middleName,"lastName":data.lastName,"mobileNo":data.contactNumber,"emailId":data.userEmail.toLocaleLowerCase(),"employeeId":data.empId,
          "userDesignation":data.userDesignation,"userGroupId":data.userGroupId,"userGroupName":data.userGroupName
        };
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

     public resendSalesPersonRegistartion(data:string): Observable<any> {
        const url1=this.basePath +"zoy_admin/resendSignInDetails?email="+data ;
   
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
     public getSalesPersonDetails(data:any): Observable<any> {
          const url1=this.basePath +"zoy_admin/getzoyPgSalesUsersDetails";
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
       public getUserDesignation(): Observable<any> {
          const url1=this.basePath +"zoy_admin/userDesignation";
            return  this.httpclient.get<any>(
                url1,
                {
                   headers:ServiceHelper.buildHeaders(),
                   observe : 'body',
                   withCredentials:true
                }
            );
    }
 public getSalesGroup(): Observable<any> {
          const url1=this.basePath +"zoy_admin/salesGroup";
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