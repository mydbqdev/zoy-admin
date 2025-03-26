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

  export class ReportsService {
    message: string;
  
    public oDataBlockSize: number;
    public baseODataUrl: string;
    httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    constructor(private httpclient: HttpClient, private router: Router, private messageService: MessageService, @Inject(BASE_PATH) private basePath: string) { }
  
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
      'basicAmount': 'BASIC AMOUNT',
      'failedReason':'FAILURE REASON',
      'contactNum':"CONTACT NUMBER",
      'tenantContactNum' :'TENANT CONTACT',
      'propertyHouseArea' :'PG ADDRESS',
      'payerPayeeType':'PAYER/PAYEE TYPE',
      'payerPayeeName':'PAYER/PAYEE Name',
      'ownerEmail':'OWNER EMAIL',
      'pgAddress':'PG ADDRESS',
      'zoyShare':'ZOY SHARE',
      'tenantMobileNum':'TENANT CONTACT',
      'userPgPropertyAddress':'PG ADDRESS',
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
      'currentPropertName' : 'PROPERTY NAME',
      'propertAddress' : 'PROPERTY ADDRESS',
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
      'email':'EMAIL ID',
      'registrationDate':'REGISTRATION DATE'
      
      
  };
  
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
  
