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

  export class FinanceReportService {
    message: string;
  
    public oDataBlockSize: number;
    public baseODataUrl: string;
    httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    constructor(private httpclient: HttpClient, private router: Router, private messageService: MessageService, @Inject(BASE_PATH) private basePath: string) { }
  
    getReportApi={
      'userPaymentGstReport':'user_gst_report_details',
      'consolidatedFinanceReport':'consolidated_finance_report_details',
      'vendorPaymentsGstReport':'vendor-payment-gst-report',
      'FailedTransactionReport':'failure_transactions_details',
      'zoyShareReport':'user_gst_report_details'//'zoyShareReportDetails'
      
    };
      
	  reportColumnsList: { 'reportName': string, 'columns': string[] }[] = [
		{
		  'reportName': 'Tenant Payments GST Report', //==User Payments GST Report
		  'columns': ['transactionDate','transactionNumber','tenantContactNum','customerName','PgPropertyName','baseAmount', 'gstAmount','totalAmount', 'paymentMethod'],
		},
    {
		  'reportName': 'Owner Payments Gst Report',// == Vendor Payments Gst Report
		  'columns': ['transactionDate','transactionNumber','ownerName','ownerEmail','pgName','pgAddress','baseAmount', 'gstAmount','totalAmount','paymentMethod'],
		},
    {
		  'reportName': 'Consolidated Finance Report',
		  'columns': ['transactionDate', 'transactionNumber','payerPayeeType', 'payerPayeeName', 'creditAmount', 'debitAmount','pgName','contactNum']
		},
    { 
      'reportName': 'Failure Transactions Report', 
      'columns': ['transactionDate', 'customerName', 'tenantContactNum', 'email', 'totalAmount', 'failedReason']
    },
    { 
      'reportName': 'ZOY Share Report', 
      "columns": ["transactionDate", "invoiceNumber", "pgName", "tenantName", "sharingType", "roomNumber", "bedNumber", "paymentMode", "amountPaid", "zoyShare", "zoyShareAmount"]
    }

	  ];

    reportNamesList:{'name':string,'key':string}[] = [
      { name: "Tenant Payments GST Report", key: "userPaymentGstReport" },
      { name: "Owner Payments Gst Report", key: "vendorPaymentsGstReport" },
      { name: "Consolidated Finance Report", key: "consolidatedFinanceReport" },
      { name: "Failure Transactions Report", key: "FailedTransactionReport" },
      { name: "ZOY Share Report", key: "zoyShareReport" }

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
  
