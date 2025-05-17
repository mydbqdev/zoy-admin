import { HttpClient, HttpErrorResponse, HttpHeaders } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { Observable,of } from 'rxjs';
import { ServiceHelper } from 'src/app/common/shared/service-helper';
import { BASE_PATH } from 'src/app/common/shared/variables';
import { MessageService } from 'src/app/message.service';

@Injectable({
    providedIn: 'root'
  })

  export class GenerateZoyCodeService{
    message : string;
    httpOptions ={
        headers :new HttpHeaders({'Content-Type':'application/json'})
    };
    constructor(private httpclient:HttpClient,@Inject(BASE_PATH) private basePath:string, private messageService:MessageService){

    }
    
     public generateOwnerCode(data:any): Observable<any> {
        const url1=this.basePath +"zoy_admin/savePgOwnerData" ;
        let param={"firstName":data.firstName,"lastName":data.lastName,"mobileNo":data.contactNumber,"emailId":data.userEmail,"zoyShare":data.zoyShare,"property_name":data.property_name,"property_pincode":data.property_pincode,"property_state":data.property_state,"property_city":data.property_city,"property_state_short_name":data.property_state_short_name,"property_locality":data.property_locality,"property_house_area":data.property_house_area,"property_location_latitude":data.property_location_latitude,"property_location_longitude":data.property_location_longitude};
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

      public generateOwnerCodeForMoreProperty(data:any): Observable<any> {
        const url1=this.basePath +"zoy_admin/saveExistingPgOwnerData" ;
        let param={"firstName":data.firstName,"lastName":data.lastName,"mobileNo":data.contactNumber,"emailId":data.userEmail,"zoyShare":data.zoyShare,"property_name":data.property_name,"property_pincode":data.property_pincode,"property_state":data.property_state,"property_city":data.property_city,"property_state_short_name":data.property_state_short_name,"property_locality":data.property_locality,"property_house_area":data.property_house_area,"property_location_latitude":data.property_location_latitude,"property_location_longitude":data.property_location_longitude};
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




    
      extractCity(components: any[]) {
        for (const component of components) {
          if (component.types.includes('locality')) {
            return component.long_name;
          }
        }
        return '';
      }

	  extractArea(components: any[]) {
        for (const component of components) {
          if (component.types.includes('sublocality')) {
            return component.long_name;
          }
        }
        return '';
      }
    
      extractState(components: any[]) {
        for (const component of components) {
          if (component.types.includes('administrative_area_level_1')) {
            return component.long_name;
          }
        }
        return '';
      }

      extractStateShortName(components: any[]) {
        for (const component of components) {
          if (component.types.includes('administrative_area_level_1')) {
            return component.short_name;
          }
        }
        return '';
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