import { Injectable,Inject } from '@angular/core';
import { HttpHeaders,HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, Observable, of, tap, throwError } from 'rxjs';
import { ServiceHelper } from 'src/app/common/shared/service-helper';
import { BASE_PATH } from 'src/app/common/shared/variables';
import { MessageService } from 'src/app/message.service';

@Injectable({
    providedIn: 'root'
  })

  export class SupportReportsService {
    message: string;
  
    public oDataBlockSize: number;
    public baseODataUrl: string;
    httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    constructor(private httpclient: HttpClient, private router: Router, private messageService: MessageService, @Inject(BASE_PATH) private basePath: string) { }
  
    getReportApi={
      'reviewsAndRatingReport':'user_reviews_ratings_details',
      'RegesterTenantsReport':'register_tenants_details' 
      
    };
  
	  reportColumnsList: { 'reportName': string, 'columns': string[] }[] = [
    {
		  'reportName': 'Ratings and Reviews Report',
		  'columns': ['pgName', 'customerName','cleanliness', 'accommodation', 'amenities', 'maintenance','valueForMoney','overallRating','reviews']
		},
    { 
      'reportName': 'Register Tenants Report', 
      'columns': ['registrationDate', 'tenantName', 'tenantContactNumber', 'tenantEmailAddress']
    }

	  ];
  
    reportNamesList:{'name':string,'key':string}[] = [
      { name: "Ratings and Reviews Report", key: "reviewsAndRatingReport" },
      { name: "Register Tenants Report", key: "RegesterTenantsReport" },
      ];

    getReportsDetails(data:any): Observable<any> {
        const url1=this.basePath +"zoy_admin/"+this.getReportApi[data.reportType];
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
  
