import { HttpClient, HttpErrorResponse, HttpHeaders } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { Observable,of } from 'rxjs';
import { ServiceHelper } from 'src/app/common/shared/service-helper';
import { BASE_PATH } from 'src/app/common/shared/variables';
import { MessageService } from 'src/app/message.service';

@Injectable({
    providedIn: 'root'
  })

  export class ZoyOwnerService{
    message : string;
    httpOptions ={
        headers :new HttpHeaders({'Content-Type':'application/json'})
    };
    constructor(private httpclient:HttpClient,@Inject(BASE_PATH) private basePath:string, private messageService:MessageService){

    }
    
     public getZoyOwnerList(data:any): Observable<any> {
          const url1=this.basePath +"zoy_admin/manage-owners";
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

    public getZoyOwnerDetails(ownerid:any): Observable<any> {
      const url1=this.basePath +"zoy_admin/ownerdetailsportfolio?ownerid="+ownerid;
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

    public doActiveteDeactiveteOwner(ownerid:any): Observable<any> {
      const url1=this.basePath +"zoy_admin/ownerdetailsportfolio?ownerid="+ownerid;
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
    public doActiveteDeactivetePg(ownerid:any): Observable<any> {
      const url1=this.basePath +"zoy_admin/ownerdetailsportfolio?ownerid="+ownerid;
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
      public updateZoyShare(ownerid:any,newZoyShare:any): Observable<any> {
        const url1=this.basePath +"zoy_admin/updateZoyShare?ownerid="+ownerid+"&newZoyShare="+newZoyShare;
          return  this.httpclient.put<any>(
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