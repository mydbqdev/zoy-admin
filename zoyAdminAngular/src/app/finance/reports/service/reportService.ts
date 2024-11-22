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
  

  getUserTransactionReport(fromDate:string,toDate:string): Observable<any> {
    
    let pa ='fromDate='+fromDate+' 00:00:00&toDate='+toDate+' 00:00:00';
      const url1=this.basePath +"zoy_admin/payment_transfer_details?"+pa;
          return  this.httpclient.get<any>(
              url1,
              {
                  headers:ServiceHelper.buildHeaders(),
                 observe : 'body',
                 withCredentials:true
              }
          );
  } 

  
  getUserGSTPaymentReport(fromDate:string,toDate:string): Observable<any> {
    
    let pa ='fromDate='+fromDate+' 00:00:00&toDate='+toDate+' 00:00:00';
      const url1=this.basePath +"zoy_admin/user_gst_report_details?"+pa;
          return  this.httpclient.get<any>(
              url1,
              {
                  headers:ServiceHelper.buildHeaders(),
                 observe : 'body',
                 withCredentials:true
              }
          );
  } 

  getconsilidatedFinanceReport(fromDate:string,toDate:string): Observable<any> {
    
    let pa ='fromDate='+fromDate+' 00:00:00&toDate='+toDate+' 00:00:00';
      const url1=this.basePath +"zoy_admin/consolidated_finance_report_details?"+pa;
          return  this.httpclient.get<any>(
              url1,
              {
                  headers:ServiceHelper.buildHeaders(),
                 observe : 'body',
                 withCredentials:true
              }
          );
  } 

  getTenantDuesDetailsReport(fromDate:string,toDate:string): Observable<any> {
    
    let pa ='fromDate='+fromDate+' 00:00:00&toDate='+toDate+' 00:00:00';
      const url1=this.basePath +"zoy_admin/tenant-dues-report_details?"+pa;
          return  this.httpclient.get<any>(
              url1,
              {
                  headers:ServiceHelper.buildHeaders(),
                 observe : 'body',
                 withCredentials:true
              }
          );
  } 

  getVendorPaymentDetailsReport(fromDate:string,toDate:string): Observable<any> {
    
    let pa ='?fromDate='+fromDate+' 00:00:00&toDate='+toDate+' 00:00:00';
      const url1=this.basePath +"zoy_admin/vendor-payment-report_details"+pa;
          return  this.httpclient.get<any>(
              url1,
              {
                  headers:ServiceHelper.buildHeaders(),
                 observe : 'body',
                 withCredentials:true
              }
          );
  } 

  getVendorPaymentDuesReport(fromDate:string,toDate:string): Observable<any> {
    
    let pa ='?fromDate='+fromDate+' 00:00:00&toDate='+toDate+' 00:00:00';
      const url1=this.basePath +"zoy_admin/vendor-payment-dues-report"+pa;
          return  this.httpclient.get<any>(
              url1,
              {
                  headers:ServiceHelper.buildHeaders(),
                 observe : 'body',
                 withCredentials:true
              }
          );
  } 

  getVendorPaymentGSTReport(fromDate:string,toDate:string): Observable<any> {
    
    let pa ='?fromDate='+fromDate+' 00:00:00&toDate='+toDate+' 00:00:00';
      const url1=this.basePath +"zoy_admin/vendor-payment-gst-report"+pa;
          return  this.httpclient.get<any>(
              url1,
              {
                  headers:ServiceHelper.buildHeaders(),
                 observe : 'body',
                 withCredentials:true
              }
          );
  } 

  downloadPaymentTransferDetailsPdf(fromDate:string,toDate:string):Observable<any>{
    let pa ='?fromDate='+fromDate+' 00:00:00&toDate='+toDate+' 00:00:00';
    const url1=this.basePath +"zoy_admin/download_payment_transfer_details"+pa;
                 return this.httpclient.get<any>(
                     url1,
                     { responseType: 'blob' as 'json'}
                 );
  }


  downloadReportPdf(data:any):Observable<any>{
    const url1=this.basePath +"zoy_admin/downloadReportPdf";
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

  public getReportsDetails(data:any): Observable<any> {
    const url1=this.basePath +'zoy_admin/getReport';
          return this.httpclient.post<any>(
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
  
