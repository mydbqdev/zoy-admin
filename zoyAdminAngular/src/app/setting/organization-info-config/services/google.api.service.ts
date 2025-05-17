import { HttpClient, HttpErrorResponse, HttpHeaders } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { Observable,of } from 'rxjs';
import { MessageService } from 'src/app/message.service';
import { BASE_PATH_EXTERNAL_SERVER } from 'src/app/common/shared/variables';
import { ServiceHelper } from "src/app/common/shared/service-helper";
@Injectable({
    providedIn: 'root'
  })

  export class GoogleAPIService{
    message : string;
    API_KEY = 'AIzaSyD7PEuJ8KF2Wd6D5aQzU6hGZ5UQ3jH15TU';
    httpOptions ={
        headers :new HttpHeaders({'Content-Type':'application/json'})
    };

   headers:HttpHeaders = new HttpHeaders({
            'Accept':'application/json',
            'Content-Type':'application/json',
            'Access-Control-Allow-Origin': '*'
        });
    constructor(private httpclient:HttpClient, private messageService:MessageService,@Inject(BASE_PATH_EXTERNAL_SERVER) private basePathExternalServer: string){

    }
    public getCityAndState(pincode: string): Observable<any> {
        const url =this.basePathExternalServer + 'maps/api/geocode/json?address='+pincode+'&key='+this.API_KEY;
        return this.httpclient.get<any>(url);
      }

      public getArea(pincode: string): Observable<any> {
        const url ='https://maps.googleapis.com/maps/api/geocode/json?address='+pincode+'&key='+this.API_KEY;
        return this.httpclient.get<any>(url,{headers:this.headers});
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
