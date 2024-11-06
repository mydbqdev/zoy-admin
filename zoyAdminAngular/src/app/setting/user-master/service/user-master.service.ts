import { HttpClient, HttpErrorResponse, HttpHeaders } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { Observable,of } from 'rxjs';
import { ServiceHelper } from 'src/app/common/shared/service-helper';
import { BASE_PATH } from 'src/app/common/shared/variables';
import { MessageService } from 'src/app/message.service';

@Injectable({
    providedIn: 'root'
  })

  export class UserMasterService{
    message : string;
    httpOptions ={
        headers :new HttpHeaders({'Content-Type':'application/json'})
    };
    constructor(private httpclient:HttpClient,@Inject(BASE_PATH) private basePath:string, private messageService:MessageService){

    }
    public find(empCode:string,pageIndex:number,pageSize:number): Observable<any> {
        const url1=this.basePath +'userrole/user/showListOfUsers';
        let param={"empCode":empCode,"pageIndex":pageIndex,"pageSize":pageSize};
              return this.httpclient.post<any[]>(
                  url1,
                  param
                  ,
                  {
                      headers:ServiceHelper.buildHeaders(),
                     observe : 'body',
                     withCredentials:true
                  }
              );
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


      
     public saveData(data:any): Observable<any> {
        const url1=this.basePath +'userrole/user/saveUser';
        return this.httpclient.post<any[]>(
            url1,
            data,
            {
                headers:ServiceHelper.buildHeaders(),
               observe : 'body',
               withCredentials:true
            }
        );
     }
     public applicationNamesDropdown(): Observable<any> {
        const url1=this.basePath +'userrole/user/showApplications';
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