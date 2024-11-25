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
      // 'vendorPaymentsGstReport':'vendor-payment-gst-report'
    };
    
    columnHeaders = {
      'ownerId': 'OWNER ID',
      'ownerName': 'OWNER NAME',
      'pgId': 'PG ID',
      'pgName': 'PG NAME',
      'totalAmountFromTenants': 'TOTAL AMOUNT FROM TENANTS',
      'amountPaidToOwner': 'AMOUNT PAID TO OWNER',
      'zoyCommission': 'ZOY COMMISSION',
      'transactionDate': 'TRANSACTION DATE',
      'transactionNumber': 'TRANSACTION NUMBER',
      'paymentStatus': 'PAYMENT STATUS',
      'transactionStatus': 'TRANSACTION STATUS',
      'baseAmount': 'BASE AMOUNT',
      'gstAmount': 'GST AMOUNT',
      'totalAmount': 'TOTAL AMOUNT',
      'customerName': 'TENANT NAME',//'CUSTOMER NAME',
      'PgPropertyName': 'PG PROPERTY NAME',
      'PgPropertyId': 'PG PROPERTY ID',
      'bedNumber': 'BED NUMBER',
      'category': 'CATEGORY',
      'paymentMethod': 'PAYMENT METHOD',
      'transactionNo': 'TRANSACTION NO',
      'totalAmountPayable': 'TOTAL AMOUNT PAYABLE',
      'totalAmountPaid': 'TOTAL AMOUNT PAID',
      'pendingAmount': 'PENDING AMOUNT',
      'pendingDueDate': 'PENDING DUE DATE',
      'creditAmount': 'CREDIT AMOUNT',
      'debitAmount': 'DEBIT AMOUNT',
      'customerId': 'TENANT ID' ,//'CUSTOMER ID',
      'basicAmount': 'BASIC AMOUNT'
  };
  
	  reportColumnsList: { 'reportName': string, 'columns': string[] ,'object': any }[] = [
		{
		  'reportName': 'User Transactions Report',
		  'columns': ['customerName', 'PgPropertyId', 'transactionDate', 'transactionNumber', 'transactionStatus', 'actions'],
      'object':'new UserTransactionReportModel()'
    },
		{
		  'reportName': 'User Payments GST Report',
		  'columns': ['transactionDate', 'PgPropertyId', 'transactionNumber', 'totalAmount', 'gstAmount', 'actions'],
      'object':'new UserGSTPaymentModel()'
		},
		{
		  'reportName': 'Consolidated Finance Report',
		  'columns': ['transactionDate', 'customerId', 'transactionNumber', 'customerName', 'creditAmount', 'debitAmount', 'actions'],
      'object':'new ConsilidatedFinanceDetailsModel()'
		},
    {
		  'reportName': 'Tenant Dues Report',
		  'columns': ['customerId', 'PgPropertyId', 'bedNumber', 'pendingAmount', 'pendingDueDate', 'actions'],
      'object':'new TenantDuesDetailsModel()'
		},
    {
		  'reportName': 'Vendor Payments Report',
		  'columns': ['ownerId', 'pgId', 'pgName', 'amountPaidToOwner',  'transactionDate', 'paymentStatus', 'actions'],
     'object':'new VendorPaymentsModel()'
		},
    {
		  'reportName': 'Vendor Payments Dues Report',
		  'columns': ['ownerId', 'pgId', 'totalAmountPayable', 'totalAmountPaid', 'pendingAmount', 'pendingDueDate', 'actions'],
      'object':'new VendorPaymentsDues()'
		},
    {
		  'reportName': 'Vendor Payments Gst Report',
		  'columns': ['transactionDate','transactionNo','pgId','totalAmount','gstAmount','actions'],
      'object':'new VendorPaymentsGst()'
		}
	  ];

    reportNamesList:{'name':string,'key':string}[] = [
      { name: "User Transactions Report", key: "userTransactionReport" },
      { name: "User Payments GST Report", key: "userPaymentGstReport" },
      { name: "Consolidated Finance Report", key: "consolidatedFinanceReport" },
      { name: "Tenant Dues Report", key: "tenantDuesReport" },
      { name: "Vendor Payments Report", key: "vendorPaymentsReport" },
      { name: "Vendor Payments Dues Report", key: "vendorPaymentsDuesReport" },
      { name: "Vendor Payments Gst Report", key: "vendorPaymentsGstReport" }
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

  vendorPaymentsGstMockData = [
    {
      transactionDate: '2024-11-01',
      transactionNo: 'T12345',
      pgId: 'PG001',
      pgName: 'PG Name 1',
      totalAmount: 5000,
      gstAmount: 500,
      basicAmount: 4500,
      paymentMethod: 'Credit Card'
    },
    {
      transactionDate: '2024-11-02',
      transactionNo: 'T12346',
      pgId: 'PG002',
      pgName: 'PG Name 2',
      totalAmount: 6000,
      gstAmount: 600,
      basicAmount: 5400,
      paymentMethod: 'Debit Card'
    },
    {
      transactionDate: '2024-11-03',
      transactionNo: 'T12347',
      pgId: 'PG003',
      pgName: 'PG Name 3',
      totalAmount: 7000,
      gstAmount: 700,
      basicAmount: 6300,
      paymentMethod: 'Bank Transfer'
    },
    {
      transactionDate: '2024-11-04',
      transactionNo: 'T12348',
      pgId: 'PG004',
      pgName: 'PG Name 4',
      totalAmount: 8000,
      gstAmount: 800,
      basicAmount: 7200,
      paymentMethod: 'Cash'
    },
    {
      transactionDate: '2024-11-05',
      transactionNo: 'T12349',
      pgId: 'PG005',
      pgName: 'PG Name 5',
      totalAmount: 5500,
      gstAmount: 550,
      basicAmount: 4950,
      paymentMethod: 'Online Payment'
    },
    {
      transactionDate: '2024-11-06',
      transactionNo: 'T12350',
      pgId: 'PG006',
      pgName: 'PG Name 6',
      totalAmount: 9000,
      gstAmount: 900,
      basicAmount: 8100,
      paymentMethod: 'Cheque'
    },
    {
      transactionDate: '2024-11-07',
      transactionNo: 'T12351',
      pgId: 'PG007',
      pgName: 'PG Name 7',
      totalAmount: 6500,
      gstAmount: 650,
      basicAmount: 5850,
      paymentMethod: 'PayPal'
    }
  ];
 vendorPaymentsDuesReportMockData = [
    {
      ownerId: 'O001',
      ownerName: 'John Doe',
      pgId: 'PG001',
      pgName: 'PG Name 1',
      totalAmountPayable: 15000,
      totalAmountPaid: 10000,
      pendingAmount: 5000,
      pendingDueDate: '2024-12-01'
    },
    {
      ownerId: 'O002',
      ownerName: 'Jane Smith',
      pgId: 'PG002',
      pgName: 'PG Name 2',
      totalAmountPayable: 20000,
      totalAmountPaid: 18000,
      pendingAmount: 2000,
      pendingDueDate: '2024-12-05'
    },
    {
      ownerId: 'O003',
      ownerName: 'Robert Brown',
      pgId: 'PG003',
      pgName: 'PG Name 3',
      totalAmountPayable: 12000,
      totalAmountPaid: 10000,
      pendingAmount: 2000,
      pendingDueDate: '2024-12-10'
    },
    {
      ownerId: 'O004',
      ownerName: 'Alice Johnson',
      pgId: 'PG004',
      pgName: 'PG Name 4',
      totalAmountPayable: 25000,
      totalAmountPaid: 22000,
      pendingAmount: 3000,
      pendingDueDate: '2024-12-15'
    },
    {
      ownerId: 'O005',
      ownerName: 'Charlie Williams',
      pgId: 'PG005',
      pgName: 'PG Name 5',
      totalAmountPayable: 17000,
      totalAmountPaid: 15000,
      pendingAmount: 2000,
      pendingDueDate: '2024-12-20'
    },
    {
      ownerId: 'O006',
      ownerName: 'David Miller',
      pgId: 'PG006',
      pgName: 'PG Name 6',
      totalAmountPayable: 30000,
      totalAmountPaid: 29000,
      pendingAmount: 1000,
      pendingDueDate: '2024-12-25'
    },
    {
      ownerId: 'O007',
      ownerName: 'Emily Davis',
      pgId: 'PG007',
      pgName: 'PG Name 7',
      totalAmountPayable: 22000,
      totalAmountPaid: 20000,
      pendingAmount: 2000,
      pendingDueDate: '2024-12-30'
    }
  ];
  
  }
  
