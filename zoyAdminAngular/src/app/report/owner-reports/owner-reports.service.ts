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

  export class OwnerReportService {
    message: string;
  
    public oDataBlockSize: number;
    public baseODataUrl: string;
    httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    constructor(private httpclient: HttpClient, private router: Router, private messageService: MessageService, @Inject(BASE_PATH) private basePath: string) { }
  
    getReportApi={
      'vendorPaymentsReport':'vendor-payment-report_details',
      'vendorPaymentsDuesReport':'vendor-payment-dues-report',
      'UpComingPotentialPropertyReport': 'up_coming_potential_property_details',
      'InactivePropertiesReport': 'inactive_property_details',
      'nonPotentialPropertiesReport': 'payment_transfer_details',
      'PotentialPropertyReport': 'potential_property_details',
      
    };
    
	  reportColumnsList: { 'reportName': string, 'columns': string[] }[] = [
    {
		  'reportName': 'Owner Payments Report', //==Vendor Payments Report
		  'columns': [  'transactionDate','ownerName', 'pgName','ownerEmail','pgAddress','totalAmountFromTenants', 'amountPaidToOwner','zoyShare','transactionNumber', 'paymentStatus','ownerApprovalStatus'],
		},
    {
		  'reportName': 'Owner Payments Dues Report', //==Vendor Payments Dues Report
		  'columns': ['pendingDueDate','ownerName','pgName','ownerEmail','pgAddress', 'totalAmountPayable', 'totalAmountPaid', 'pendingAmount'],
    },
    { 
      'reportName': 'Inactive Properties Report', 
      'columns': ['ownerFullName', 'propertyName', 'propertyContactNumber', 'propertyEmailAddress', 'propertyAddress'] 
    }, 
    { 
      'reportName': 'Upcoming Potential Properties Report',
      'columns': ['ownerFullName', 'propertyName', 'propertyContactNumber', 'propertyEmailAddress', 'propertyAddress', 'numberOfBeds', 'expectedRentPerMonth'] 
    }, 
    { 
      'reportName': 'Non-Potential Properties Report', 
      'columns': ['ownerFullName', 'propertyName', 'propertyContactNumber', 'propertyEmailAddress', 'propertyAddress', 'lastCheckoutDate'] 
    }, 
    { 
      'reportName': 'Potential Properties Report', 
      'columns': ['ownerFullName', 'propertyName', 'propertyContactNumber', 'propertyEmailAddress', 'propertyAddress', 'numberOfBeds', 'expectedRentPerMonth']
    }
	  ];
  
    reportNamesList:{'name':string,'key':string}[] = [
      { name: "Owner Payments Report", key: "vendorPaymentsReport" },
      { name: "Owner Payments Dues Report", key: "vendorPaymentsDuesReport" },
      { name: "Inactive Properties Report", key: "InactivePropertiesReport" }, 
      { name: "Upcoming Potential Properties Report", key: "UpComingPotentialPropertyReport" }, 
      { name: "Non-Potential Properties Report", key: "nonPotentialPropertiesReport" }, 
      { name: "Potential Properties Report", key: "PotentialPropertyReport" },
     
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
  
