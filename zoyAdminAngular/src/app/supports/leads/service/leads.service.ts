import { Injectable,Inject } from '@angular/core';
import { HttpHeaders,HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, Observable, of, tap, throwError } from 'rxjs';
import { BASE_PATH } from 'src/app/common/shared/variables';
import { MessageService } from 'src/app/message.service';

@Injectable({
    providedIn: 'root'
  })

  export class LeadsService {
    message: string;
  
    public oDataBlockSize: number;
    public baseODataUrl: string;
    httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    constructor(private httpclient: HttpClient, private router: Router, private messageService: MessageService, @Inject(BASE_PATH) private basePath: string) { }
  
    getReportApi={
      'LeadsReport':'leads_generate_details',
      'TicketsReport':'tickets_details'      
      };
    
    columnHeaders = {
      'uniqueInquiryNumber': 'Unique Inquiry Number',
      'name': 'Name',
      'inquiredFor': 'Inquired for',
      'date': 'Date',
      'assignTo':'Assign to',
      'status': 'Status' 
  };
  
	  reportColumnsList: { 'reportName': string, 'columns': string[] }[] = [
		{
		  'reportName': 'Leads', //== Leads
		  'columns': ['uniqueInquiryNumber','name', 'inquiredFor','date','assignTo','status','action']
    },
    {
		  'reportName': 'tickets', //== Tickets
		  'columns': ['uniqueInquiryNumber','name', 'inquiredFor','date','assignTo']
    }

	  ];
  
    reportNamesList:{'name':string,'key':string}[] = [
      { name: "Leads", key: "leadsReport" },
      { name: "Tickets", key: "ticketsReport" },
      ];


      leadData:{uniqueInquiryNumber:string,name:string,inquiredFor:string,date:string,assignTo:string,status:string}[]= [
        {
            "uniqueInquiryNumber":"2500001",
            "name":"Abdul T",
            "inquiredFor":"PG Accommodation",
            "date": "2025-02-21 14:39:08",
            "assignTo": "",
            "status": "New"
        },
        {
          "uniqueInquiryNumber":"2500002",
          "name":"Mahendra B",
          "inquiredFor":"Partner inquiry",
          "date": "2025-02-21 14:39:08",
          "assignTo": "",
          "status": "In Progress"
        },
        {
          "uniqueInquiryNumber":"2500003",
          "name":"Keerthi D",
          "inquiredFor":"Business inquiry",
          "date": "2025-02-21 14:39:08",
          "assignTo": "",
          "status": "Follow-up"
        },
        {
          "uniqueInquiryNumber":"2500004",
          "name":"Madhan Ram",
          "inquiredFor":"Support inquiry",
          "date": "2025-02-21 14:39:08",
          "assignTo": "",
          "status": "Converted"
        },
        {
          "uniqueInquiryNumber":"2500005",
          "name":"Aparna",
          "inquiredFor":"Others",
          "date": "2025-02-21 14:39:08",
          "assignTo": "",
          "status": "Lost"
        }
    ];

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
  
