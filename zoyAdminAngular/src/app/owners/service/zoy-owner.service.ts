import { HttpClient, HttpErrorResponse, HttpHeaders } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { Observable,of } from 'rxjs';
import { ServiceHelper } from 'src/app/common/shared/service-helper';
import { BASE_PATH, BASE_PATH_EXTERNAL_SERVER } from 'src/app/common/shared/variables';
import { MessageService } from 'src/app/message.service';

@Injectable({
    providedIn: 'root'
  })

  export class ZoyOwnerService{
    message : string;
    httpOptions ={
        headers :new HttpHeaders({'Content-Type':'application/json'})
    };
    constructor(private httpclient:HttpClient,@Inject(BASE_PATH) private basePath:string, private messageService:MessageService,@Inject(BASE_PATH_EXTERNAL_SERVER) private basePathForCustomerPartnerApis: string){

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

    public updateOwnerStatus(data:any): Observable<any> {
      const url1=this.basePath +"zoy_admin/updateOwnerStatus";
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
    public updatePropertyStatus(data:any): Observable<any> {
      const url1=this.basePath +"zoy_admin/updatePropertyStatus";
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
       public generateAadhaarSession1(): Observable<any> {
        console.log("this.basePathForCustomerPartnerApis",this.basePathForCustomerPartnerApis)
        const url=this.basePathForCustomerPartnerApis+"/zoy_customer/generateAadhaarSession";
   //     return this.httpclient.get<any>(url);
        return  this.httpclient.get<any>(
            url,
            {
              headers: this.buildHeadersForCustomerPartnerApis(),
               observe : 'body',
               withCredentials:true
            }
        );
      }//sessionStorage.setItem('zoyadminapi', 'yes');
    //  sessionStorage.setItem('exterApiToken', basicAuthValue);
      public generateAadhaarSession(): Observable<any> {
        const url=this.basePathForCustomerPartnerApis+"/zoy_customer/generateAadhaarSession";
        return this.httpclient.get<any>(
          url,
          {
            headers: this.buildHeadersForCustomerPartnerApis(),
            observe: 'body',
            withCredentials: true
          }
        );
      }
      
      public generateAadhaarRecaptcha(data:any): Observable<any> {
        const url=this.basePathForCustomerPartnerApis+"/zoy_customer/generateAadhaarRecaptcha";
          return  this.httpclient.post<any>(
              url,
              data,
              {
                 headers:ServiceHelper.buildHeaders(),
                 observe : 'body',
                 withCredentials:true
              }
          );
      }
      public generateAadhaarOtp(data:any): Observable<any> {
        const url=this.basePathForCustomerPartnerApis+"/zoy_customer/generateAadhaarOtp";
          return  this.httpclient.post<any>(
              url,
              data,
              {
                 headers:ServiceHelper.buildHeaders(),
                 observe : 'body',
                 withCredentials:true
              }
          );
      }
      public verifyAadhaarOtp(data:any): Observable<any> {
        const url=this.basePathForCustomerPartnerApis+"/zoy_customer/verifyAadhaarOtp";
          return  this.httpclient.post<any>(
              url,
              data,
              {
                 headers:ServiceHelper.buildHeaders(),
                 observe : 'body',
                 withCredentials:true
              }
          );
      }
      public updateAadhaar(data:any): Observable<any> {
        const url=this.basePathForCustomerPartnerApis+"/zoy_partner/update-aadhaar";
          return  this.httpclient.post<any>(
              url,
              data,
              {
                 headers:ServiceHelper.buildHeaders(),
                 observe : 'body',
                 withCredentials:true
              }
          );
      }
      public  buildHeadersForCustomerPartnerApis(): HttpHeaders {
        const username = 'zoyapp';  
        const password = 'zoypass';
        const basicAuthValue = `Basic ${btoa(username + ':' + password)}`;  
        sessionStorage.setItem('exterApiToken', basicAuthValue);
      
        let headers: HttpHeaders = new HttpHeaders({
          'Accept': 'application/json',
          'Content-Type': 'application/json',
          'Authorization': basicAuthValue,
         // 'Access-Control-Allow-Origin': '*'  
        });
       
        return headers;
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