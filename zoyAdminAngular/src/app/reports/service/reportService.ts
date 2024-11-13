import { Injectable,Inject } from '@angular/core';
import { HttpHeaders,HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, of } from 'rxjs';
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
  
    
  
  getRolesSaved(): Observable<any> {
    const ELEMENT_DATA = [
        { customerID: 'C001', pgId: 'PG01', transactionDate: '2024-11-01', transactionStatus: 'Completed', totalAmount: 120.50, actions: 'View'},
        { customerID: 'C004', pgId: 'PG02', transactionDate: '2024-11-02', transactionStatus: 'Pending', totalAmount: 75.00, actions: 'View'},
        { customerID: 'C003', pgId: 'PG03', transactionDate: '2024-11-03', transactionStatus: 'Failed', totalAmount: 220.25, actions: 'Retry'},
        { customerID: 'C006', pgId: 'PG04', transactionDate: '2024-11-04', transactionStatus: 'Completed', totalAmount: 350.00, actions: 'View'},
        { customerID: 'C005', pgId: 'PG05', transactionDate: '2024-11-05', transactionStatus: 'Completed', totalAmount: 150.75, actions: 'View'},
        { customerID: 'C006', pgId: 'PG06', transactionDate: '2024-11-06', transactionStatus: 'Pending', totalAmount: 400.00, actions: 'Cancel'},
        { customerID: 'C007', pgId: 'PG07', transactionDate: '2024-11-07', transactionStatus: 'Completed', totalAmount: 180.60, actions: 'View'},
        { customerID: 'C008', pgId: 'PG08', transactionDate: '2024-11-08', transactionStatus: 'Failed', totalAmount: 99.99, actions: 'Retry'},
        { customerID: 'C007', pgId: 'PG07', transactionDate: '2024-11-07', transactionStatus: 'Completed', totalAmount: 180.60, actions: 'View'},
        { customerID: 'C008', pgId: 'PG08', transactionDate: '2024-11-08', transactionStatus: 'Failed', totalAmount: 99.99, actions: 'Retry'},
        { customerID: 'C009', pgId: 'PG09', transactionDate: '2024-11-09', transactionStatus: 'Completed', totalAmount: 500.00, actions: 'View'},
        { customerID: 'C010', pgId: 'PG10', transactionDate: '2024-11-10', transactionStatus: 'Pending', totalAmount: 750.25, actions: 'Cancel'},
        { customerID: 'C001', pgId: 'PG01', transactionDate: '2024-11-01', transactionStatus: 'Completed', totalAmount: 120.50, actions: 'View'},
        { customerID: 'C002', pgId: 'PG02', transactionDate: '2024-11-02', transactionStatus: 'Pending', totalAmount: 75.00, actions: 'View'},
        { customerID: 'C003', pgId: 'PG03', transactionDate: '2024-11-03', transactionStatus: 'Failed', totalAmount: 220.25, actions: 'Retry'},
        { customerID: 'C004', pgId: 'PG04', transactionDate: '2024-11-04', transactionStatus: 'Completed', totalAmount: 350.00, actions: 'View'},
        { customerID: 'C005', pgId: 'PG05', transactionDate: '2024-11-05', transactionStatus: 'Completed', totalAmount: 150.75, actions: 'View'},
        { customerID: 'C009', pgId: 'PG09', transactionDate: '2024-11-09', transactionStatus: 'Completed', totalAmount: 500.00, actions: 'View'},
        { customerID: 'C010', pgId: 'PG10', transactionDate: '2024-11-10', transactionStatus: 'Pending', totalAmount: 750.25, actions: 'Cancel'},
        { customerID: 'C001', pgId: 'PG01', transactionDate: '2024-11-01', transactionStatus: 'Completed', totalAmount: 120.50, actions: 'View'},
        { customerID: 'C042', pgId: 'PG02', transactionDate: '2024-11-02', transactionStatus: 'Pending', totalAmount: 75.00, actions: 'View'},
        { customerID: 'C003', pgId: 'PG03', transactionDate: '2024-11-03', transactionStatus: 'Failed', totalAmount: 220.25, actions: 'Retry'},
        { customerID: 'C004', pgId: 'PG04', transactionDate: '2024-11-04', transactionStatus: 'Completed', totalAmount: 350.00, actions: 'View'},
        { customerID: 'C005', pgId: 'PG05', transactionDate: '2024-11-05', transactionStatus: 'Completed', totalAmount: 150.75, actions: 'View'},
        { customerID: 'C006', pgId: 'PG06', transactionDate: '2024-11-06', transactionStatus: 'Pending', totalAmount: 400.00, actions: 'Cancel'},
        { customerID: 'C006', pgId: 'PG06', transactionDate: '2024-11-06', transactionStatus: 'Pending', totalAmount: 400.00, actions: 'Cancel'},
        { customerID: 'C004', pgId: 'PG04', transactionDate: '2024-11-04', transactionStatus: 'Completed', totalAmount: 350.00, actions: 'View'},
        { customerID: 'C005', pgId: 'PG05', transactionDate: '2024-11-05', transactionStatus: 'Completed', totalAmount: 150.75, actions: 'View'},
        { customerID: 'C006', pgId: 'PG06', transactionDate: '2024-11-06', transactionStatus: 'Pending', totalAmount: 400.00, actions: 'Cancel'},
        { customerID: 'C006', pgId: 'PG06', transactionDate: '2024-11-06', transactionStatus: 'Pending', totalAmount: 400.00, actions: 'Cancel'},
        { customerID: 'C007', pgId: 'PG07', transactionDate: '2024-11-07', transactionStatus: 'Completed', totalAmount: 180.60, actions: 'View'},
        { customerID: 'C008', pgId: 'PG08', transactionDate: '2024-11-08', transactionStatus: 'Failed', totalAmount: 99.99, actions: 'Retry'},
        { customerID: 'C007', pgId: 'PG07', transactionDate: '2024-11-07', transactionStatus: 'Completed', totalAmount: 180.60, actions: 'View'},
        { customerID: 'C008', pgId: 'PG08', transactionDate: '2024-11-08', transactionStatus: 'Failed', totalAmount: 99.99, actions: 'Retry'},
        { customerID: 'C009', pgId: 'PG09', transactionDate: '2024-11-09', transactionStatus: 'Completed', totalAmount: 500.00, actions: 'View'},
        { customerID: 'C010', pgId: 'PG10', transactionDate: '2024-11-10', transactionStatus: 'Pending', totalAmount: 750.25, actions: 'Cancel'}
    ];
    return of(ELEMENT_DATA);


    // const url1=this.basePath +"rolescreenmaster/user/roleScreenMaster";
    //       return this.httpclient.post<any>(
    //           url1,
    //           '',
    //           {
    //               headers:ServiceHelper.buildHeaders(),
    //              observe : 'body',
    //              withCredentials:true
    //           }
    //       );
  } 

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
  





















