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
        const url1=this.basePath +'zoy_admin/role_list';
        return this.httpclient.get<any>(
            url1,
            {
                headers:ServiceHelper.buildHeaders(),
               observe : 'body',
               withCredentials:true
            }
        );
     }

     
      
     public updateRolesUser(data:any): Observable<any> {
        const url1=this.basePath +'zoy_admin/user_assign';
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
     public getUserList(): Observable<any> {
        const url1=this.basePath +'zoy_admin/user_list';

        return this.httpclient.get<any>(
            url1,
            {
                headers:ServiceHelper.buildHeaders(),
               observe : 'body',
               withCredentials:true
            }
        );
     }

     public createUser(data:any): Observable<any> {
        const url1=this.basePath +'zoy_admin/user_create';
        return this.httpclient.post<any>(
            url1,
            data,
            {
                headers:ServiceHelper.buildHeaders(),
               observe : 'body',
               withCredentials:true
            }
        );
     }

     public updateUser(data:any): Observable<any> {
        const url1=this.basePath +'zoy_admin/user_update/'+data.userEmail;
        return this.httpclient.put<any>(
            url1,
            data,
            {
                headers:ServiceHelper.buildHeaders(),
               observe : 'body',
               withCredentials:true
            }
        );
     }

     public userPrvAssign(data:any): Observable<any> {
        const url1=this.basePath +'zoy_admin/user_assign';
        return this.httpclient.post<any>(
            url1,
            data,
            {
                headers:ServiceHelper.buildHeaders(),
               observe : 'body',
               withCredentials:true
            }
        );
     }

     public sendLoginInfo(userEmail:any): Observable<any> {
        const url1=this.basePath +'zoy_admin/send_login_info?userName='+userEmail;
        return this.httpclient.post<any>(
            url1,
            "",
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