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

  export class TenantReportsService {
    message: string;
  
    public oDataBlockSize: number;
    public baseODataUrl: string;
    httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    constructor(private httpclient: HttpClient, private router: Router, private messageService: MessageService, @Inject(BASE_PATH) private basePath: string) { }
  
    getReportApi={
      'userTransactionReport':'payment_transfer_details',
      'tenantDuesReport':'tenant-dues-report_details',
      'tenantRefundReport':'user_refund_details',
      'reviewsAndRatingReport':'user_reviews_ratings_details',
      'UpcomingTenantsReport':'upcoming_tenant_details',
      'InactiveTenantsReport':'inactive_tenant_details',
      'ActiveTenantsReport':'active_tenant_details',
     
    };
 
	  reportColumnsList: { 'reportName': string, 'columns': string[] }[] = [
		{
		  'reportName': 'Tenant Transactions Report', //== User Transactions Report
		  'columns': ['transactionDate','customerName', 'tenantContactNum','PgPropertyName','propertyHouseArea','bedNumber',  
                  'transactionNumber', 'transactionStatus', 'baseAmount','gstAmount','totalAmount','category','paymentMethod'],
    },
    {
		  'reportName': 'Tenant Dues Report',
		  'columns': ['pendingDueDate','customerName', 'tenantMobileNum', 'PgPropertyName','userPgPropertyAddress','bedNumber', 'pendingAmount', ],
		},
    {
		  'reportName': 'Tenant Refunds Report',
		  'columns': ['paymentDate','customerName', 'tenantMobileNum','tenantAccountNumber','tenantIfscCode', 'PgPropertyName','userPgPropertyAddress','bookingId', 'refundTitle','refundableAmount','amountPaid','transactionNumber','paymentStatus' ],
		},
    {
		  'reportName': 'Upcoming Tenants Report',
		  'columns': ['tenantName', 'tenantContactNumber','tenantEmailAddress', 'bookedProperyName','propertAddress', 'bedNumber', 'expectedCheckIndate','expectedCheckOutdate']
		},
    {
		  'reportName': 'Inactive Tenants Report',
		  'columns': ['tenantName', 'tenantContactNumber','tenantEmailAddress', 'previousPropertName','propertAddress', 'bedNumber','checkInDate', 'checkedOutDate']
		},
    {
		  'reportName': 'Active Tenants Report',
		  'columns': ['tenantName', 'tenantContactNumber','tenantEmailAddress', 'currentPropertName', 'propertAddress','bedNumber', 'checkInDate','expectedCheckOutdate']
		},

	  ];
    
    reportNamesList:{'name':string,'key':string}[] = [
      { name: "Active Tenants Report", key: "ActiveTenantsReport" },
      { name: "Inactive Tenants Report", key: "InactiveTenantsReport" },
      { name: "Upcoming Tenants Report", key: "UpcomingTenantsReport" },
      { name: "Tenant Transactions Report", key: "userTransactionReport" },
      { name: "Tenant Dues Report", key: "tenantDuesReport" },
      { name: "Tenant Refunds Report", key: "tenantRefundReport" },
     
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
  
