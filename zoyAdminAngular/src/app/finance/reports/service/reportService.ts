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

  export class ReportService {
    message: string;
  
    public oDataBlockSize: number;
    public baseODataUrl: string;
    httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    constructor(private httpclient: HttpClient, private router: Router, private messageService: MessageService, @Inject(BASE_PATH) private basePath: string) { }
  
    getReportApi={
      'userTransactionReport':'payment_transfer_details',
      'userPaymentGstReport':'user_gst_report_details',
      'consolidatedFinanceReport':'consolidated_finance_report_details',
      'tenantDuesReport':'tenant-dues-report_details',
      'vendorPaymentsReport':'vendor-payment-report_details',
      // 'vendorPaymentsDuesReport':'vendor-payment-dues-report',
      // 'vendorPaymentsGstReport':'vendor-payment-gst-report',
      'tenantRefundReport':'user_refund_details',
      'reviewsAndRatingReport':'user_reviews_ratings_details',
      'upcomingTenantsReport':'payment_transfer_details',
      'suspendedTenantsReport':'suspended_tenant_details',
      'inactiveTenantsReport':'inactive_tenant_details',
      'activeTenantsReport':'active_tenant_details',
     
      'upcomingPotentialPropertiesReport': 'payment_transfer_details',//'upcoming_potential_properties_details',
      'suspendedPropertiesReport': 'payment_transfer_details',//'suspended_properties_details',
      'inactivePropertiesReport': 'payment_transfer_details',//'inactive_properties_details',
      'nonPotentialPropertiesReport': 'payment_transfer_details',//'non_potential_properties_details',
      'potentialPropertiesReport': 'payment_transfer_details',//'potential_properties_details'
      
      
    };
    
    columnHeaders = {
      'ownerId': 'OWNER ID',
      'ownerName': 'OWNER NAME',
      'pgId': 'PG ID',
      'pgName': 'PG PROPERTY NAME',
      'totalAmountFromTenants': 'TOTAL AMOUNT FROM TENANTS',
      'amountPaidToOwner': 'AMOUNT PAID TO OWNER',
      'zoyCommission': 'ZOY COMMISSION',
      'transactionDate': 'TRANSACTION DATE',
      'transactionNumber': 'INVOICE NUMBER',
      'paymentStatus': 'TRANSACTION STATUS',
      'transactionStatus': 'TRANSACTION STATUS',
      'baseAmount': 'BASE AMOUNT',
      'gstAmount': 'GST AMOUNT',
      'totalAmount': 'TOTAL AMOUNT',
      'customerName': 'TENANT NAME',//'CUSTOMER NAME',
      'PgPropertyName': 'PG PROPERTY NAME',
    //  'PgPropertyId': 'PG PROPERTY ID',
      'bedNumber': 'BED NUMBER',
      'category': 'CATEGORY',
      'paymentMethod': 'PAYMENT METHOD',
      'transactionNo': 'TRANSACTION NO',
      'totalAmountPayable': 'TOTAL AMOUNT PAYABLE',
      'totalAmountPaid': 'TOTAL AMOUNT PAID',
      'pendingAmount': 'PENDING AMOUNT',
      'pendingDueDate': 'PAYMENT DUE DATE',
      'paymentDate': 'PAYMENT DUE DATE',
      'creditAmount': 'CREDIT AMOUNT',
      'debitAmount': 'DEBIT AMOUNT',
     // 'customerId': 'TENANT ID' ,//'CUSTOMER ID',
      'basicAmount': 'BASIC AMOUNT',

      'tenantContactNum' :'TENANT CONTACT',
      'propertyHouseArea' :'PG Address',
      'payerPayeeType':'PAYER/PAYEE TYPE',
      'payerPayeeName':'PAYER/PAYEE Name',
      'ownerEmail':'OWNER EMAIL',
      'pgAddress':'PG ADDRESS',
      'zoyShare':'ZOY SHARE',
      'tenantMobileNum':'TENANT CONTACT',
      'userPgPropertyAddress':'PG Address',
      'ownerApprovalStatus':'OWNER APPROVAL STATUS',
      'bookingId':'BOOKING ID',
      'refundTitle':'REFUND TITLE',
      'refundableAmount':'REFUNDABLE AMOUNT',
      'amountPaid':'AMOUNT PAID',
      'cleanliness':'CLEANLINES',
      'amenities':'AMENITIES',
      'maintenance':'MAINTENANCE',
      'valueForMoney':'VALUE FOR MONEY',
      'overallRating':'OVERALL RATING',
      'accommodation':'ACCOMMODATION',

      'tenantName' : 'TENANT NAME',
      'tenantContactNumber' : 'TENANT CONTACT NUMBER',
      'tenantEmailAddress' : 'TENANT EMAIL ADDRESS',
      'bookedProperyName' : 'BOOKED PROPERTY NAME',
      'previousPropertName' : 'PREVIOUS PROPERTY NAME',
      'currentPropertName' : 'CURRENT PROPERTY NAME',
      'propertAddress' : 'PROPERTY ADDRESS',
      'roomNumber' : 'ROOM NUMBER',
      'expectedCheckIndate' : 'EXPECTED CHECK-IN DATE',
      'expectedCheckOutdate' : 'EXPECTED CHECK-OUT DATE',
      'checkInDate' : 'CHECK-IN DATE',
      'checkedInDate' : 'CHECKED-IN DATE',
      'checkedOutDate' : 'CHECKED-OUT DATE',
      'suspendedDate' : 'SUSPENDED DATE',
      'reasonForSuspension' : 'REASON FOR SUSPENSION',

      
      'ownerFullName' : 'OWNER FULL NAME',
      'propertyName' : 'PROPERTY NAME',
      'propertyContactNumber' : 'PROPERTY CONTACT NUMBER',
      'propertyEmailAddress' : 'PROPERTY EMAIL ADDRESS',
      'propertyAddress' : 'PROPERTY ADDRESS',
      'inactivePropertyName' : 'INACTIVE PROPERTY NAME',
      'bookedPropertyName' : 'BOOKED PROPERTY NAME',
      'numberOfBeds' : 'NUMBER OF BEDS',
      'expectedRentPerMonth' : 'EXPECTED RENT PER MONTH',
      'lastCheckoutDate' : 'LAST CHECK-OUT DATE',
      'numberOfBedsOccupied' : 'NUMBER OF BEDS OCCUPIED'
      
      
  };
  
	  reportColumnsList: { 'reportName': string, 'columns': string[] }[] = [
		{
		  'reportName': 'Tenant Transactions Report', //== User Transactions Report
		  'columns': ['transactionDate','customerName', 'tenantContactNum','PgPropertyName','propertyHouseArea','bedNumber',  
                  'transactionNumber', 'transactionStatus', 'baseAmount','gstAmount','totalAmount','category','paymentMethod'],
    },
		{
		  'reportName': 'Tenant Payments GST Report', //==User Payments GST Report
		  'columns': ['transactionDate','transactionNumber','tenantContactNum','customerName','PgPropertyName','propertyHouseArea', 'totalAmount', 'gstAmount', 'paymentMethod'],
		},
    {
		  'reportName': 'Tenant Dues Report',
		  'columns': ['pendingDueDate','customerName', 'tenantMobileNum', 'PgPropertyName','userPgPropertyAddress','bedNumber', 'pendingAmount', ],
		},
    {
		  'reportName': 'Tenant Refunds Report',
		  'columns': ['paymentDate','customerName', 'tenantMobileNum', 'PgPropertyName','userPgPropertyAddress','bookingId', 'refundTitle','refundableAmount','amountPaid','transactionNumber','paymentStatus' ],
		},
    {
		  'reportName': 'Owner Payments Report', //==Vendor Payments Report
		  'columns': [  'transactionDate','ownerName', 'pgName','ownerEmail','pgAddress','totalAmountFromTenants', 'amountPaidToOwner','zoyShare','transactionNumber', 'paymentStatus','ownerApprovalStatus'],
		},
    {
		  'reportName': 'Owner Payments Dues Report', //==Vendor Payments Dues Report
		  'columns': ['pendingDueDate','ownerName','pgName','ownerEmail','pgAddress', 'totalAmountPayable', 'totalAmountPaid', 'pendingAmount'],
    },
    {
		  'reportName': 'Owner Payments Gst Report',// == Vendor Payments Gst Report
		  'columns': ['transactionDate','transactionNumber','ownerName','ownerEmail','pgName','pgAddress','totalAmount','gstAmount','baseAmount','paymentMethod'],
		},
    {
		  'reportName': 'Consolidated Finance Report',
		  'columns': ['transactionDate', 'transactionNumber','payerPayeeType', 'payerPayeeName', 'creditAmount', 'debitAmount']
		},
    {
		  'reportName': 'Ratings and Reviews Report',
		  'columns': ['pgName', 'customerName','cleanliness', 'accommodation', 'amenities', 'maintenance','valueForMoney','overallRating','reviews']
		}

    ,
    {
		  'reportName': 'Upcoming Tenants Report',
		  'columns': ['tenantName', 'tenantContactNumber','tenantEmailAddress', 'bookedProperyName','propertAddress', 'roomNumber', 'expectedCheckIndate','expectedCheckOutdate']
		},
    {
		  'reportName': 'Suspended Tenants Report',
		  'columns': ['tenantName', 'tenantContactNumber','tenantEmailAddress', 'previousPropertName','propertAddress', 'roomNumber', 'checkedOutDate','suspendedDate','reasonForSuspension']
		},
    {
		  'reportName': 'Inactive Tenants Report',
		  'columns': ['tenantName', 'tenantContactNumber','tenantEmailAddress', 'previousPropertName','propertAddress', 'roomNumber', 'checkedOutDate','expectedCheckOutdate']
		},
    {
		  'reportName': 'Active Tenants Report',
		  'columns': ['tenantName', 'tenantContactNumber','tenantEmailAddress', 'currentPropertName', 'propertAddress','roomNumber', 'checkInDate','expectedCheckOutdate']
		},
    
    { 
      'reportName': 'Suspended Properties Report', 
      'columns': ['ownerFullName', 'propertyName', 'propertyContactNumber', 'propertyEmailAddress', 'propertyAddress', 'suspendedDate', 'reasonForSuspension'] 
    }, 
    { 
      'reportName': 'Inactive Properties Report', 
      'columns': ['ownerFullName', 'inactivePropertyName', 'propertyContactNumber', 'propertyEmailAddress', 'propertyAddress'] 
    }, 
    { 
      'reportName': 'Upcoming Potential Properties Report',
      'columns': ['ownerFullName', 'bookedPropertyName', 'propertyContactNumber', 'propertyEmailAddress', 'propertyAddress', 'numberOfBeds', 'expectedRentPerMonth'] 
    }, 
    { 
      'reportName': 'Non-Potential Properties Report', 
      'columns': ['ownerFullName', 'propertyName', 'propertyContactNumber', 'propertyEmailAddress', 'propertyAddress', 'lastCheckoutDate'] 
    }, 
    { 
      'reportName': 'Potential Properties Report', 
      'columns': ['ownerFullName', 'propertyName', 'propertyContactNumber', 'propertyEmailAddress', 'propertyAddress', 'numberOfBedsOccupied', 'expectedRentPerMonth']
    }

	  ];
  
    reportNamesList:{'name':string,'key':string}[] = [
      { name: "Tenant Transactions Report", key: "userTransactionReport" },
      { name: "Tenant Payments GST Report", key: "userPaymentGstReport" },
      { name: "Tenant Dues Report", key: "tenantDuesReport" },
      { name: "Tenant Refunds Report", key: "tenantRefundReport" },
      { name: "Owner Payments Report", key: "vendorPaymentsReport" },
      { name: "Owner Payments Dues Report", key: "vendorPaymentsDuesReport" },
      { name: "Owner Payments Gst Report", key: "vendorPaymentsGstReport" },
      { name: "Consolidated Finance Report", key: "consolidatedFinanceReport" },
      { name: "Ratings and Reviews Report", key: "reviewsAndRatingReport" },
    
      { name: "Upcoming Tenants Report", key: "upcomingTenantsReport" },
      { name: "Suspended Tenants Report", key: "suspendedTenantsReport" },
      { name: "Inactive Tenants Report", key: "inactiveTenantsReport" },
      { name: "Active Tenants Report", key: "activeTenantsReport" },

      { name: "Suspended Properties Report", key: "suspendedPropertiesReport" }, 
      { name: "Inactive Properties Report", key: "inactivePropertiesReport" }, 
      { name: "Upcoming Potential Properties Report", key: "upcomingPotentialPropertiesReport" }, 
      { name: "Non-Potential Properties Report", key: "nonPotentialPropertiesReport" }, 
      { name: "Potential Properties Report", key: "potentialPropertiesReport" }

      ];

      getCityList(): Observable<any> {
        const url1=this.basePath +"zoy_admin/city_list";
            return  this.httpclient.get<any>(
                url1,
                {
                    headers:ServiceHelper.buildHeaders(),
                   observe : 'body',
                   withCredentials:true
                }
            );
    } 
    
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
  

  downloadReportPdf(data:any):Observable<any>{
    const url1=this.basePath +"zoy_admin/download_report";
                 return this.httpclient.post<any>(
                     url1,
                     data,
                     { responseType: 'blob' as 'json'}
                 );
  }

    

  exportReportsToExcelOrCSV(data:any): Observable<any> {

    const dateTime = new Date().toISOString().replace(/[-T:.Z]/g, '').slice(0, 14);
    const fileName = data.reportType+'_'+dateTime+ data.downloadType;

    const url1 = this.basePath + 'zoy_admin/user/exportReportsToExcelOrCSV';
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Accept: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    });
    return this.httpclient.post(url1, data, {
      headers,
      responseType: 'blob' // Important to receive binary data as response
    }).pipe(
      tap((data: Blob) => {
        this.downloadFile(data, fileName);
      }),
      catchError((error) => {
        
        return throwError(error);
      })
    );
  }

  private downloadFile(data: Blob, filename: string) {
    const downloadLink = document.createElement('a');
    const url = window.URL.createObjectURL(data);
  
    downloadLink.href = url;
    downloadLink.download = filename;
    downloadLink.click();
  
    window.URL.revokeObjectURL(url);
    downloadLink.remove();
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
  
