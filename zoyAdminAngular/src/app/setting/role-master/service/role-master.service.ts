import { Injectable,Inject } from '@angular/core';
import { HttpHeaders,HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { ServiceHelper } from 'src/app/common/shared/service-helper';
import { BASE_PATH } from 'src/app/common/shared/variables';
import { MessageService } from 'src/app/message.service';

@Injectable({
  providedIn: 'root'
})
export class RoleMasterService {
  message: string;

  public oDataBlockSize: number;
  public baseODataUrl: string;
  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };
  constructor(private httpclient: HttpClient, private router: Router, private messageService: MessageService, @Inject(BASE_PATH) private basePath: string) { }

  saveMyRole(data : any): Observable<any>{
    let url1=this.basePath +'zoy_admin/role_create';
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

  getRolesList(): Observable<any> {
  const url1=this.basePath +"zoy_admin/role_list";
        return this.httpclient.get<any>(
            url1,
            {
                headers:ServiceHelper.buildHeaders(),
               observe : 'body',
               withCredentials:true
            }
        );
} 


editRole(data : any){
  let url1=this.basePath + "zoy_admin/role_update" ;
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

deleteRole(roleId : number ,roleName :string){
  let url1=this.basePath +'zoy_admin/role_delete?roleId='+ roleId+'&roleName='+roleName ;
        return this.httpclient.post<any>(
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
