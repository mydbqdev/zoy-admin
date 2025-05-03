import { Injectable,Inject } from '@angular/core';
import { HttpHeaders,HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, Observable, of, tap, throwError } from 'rxjs';
import { BASE_PATH } from 'src/app/common/shared/variables';
import { MessageService } from 'src/app/message.service';
import { ServiceHelper } from 'src/app/common/shared/service-helper';

@Injectable({
    providedIn: 'root'
  })

  export class SupportService {
    message: string;
  
    public oDataBlockSize: number;
    public baseODataUrl: string;
    httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    constructor(private httpclient: HttpClient, private router: Router, private messageService: MessageService, @Inject(BASE_PATH) private basePath: string) { }
  
    public getTicketsList(data:any): Observable<any> {
      const url1=this.basePath +"zoy_admin/open-support-ticket";
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

    public getTicketsCloseList(data:any): Observable<any> {
      const url1=this.basePath +"zoy_admin/close-support-ticket";
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

    public getSupportTeamList(): Observable<any> {
      const url1=this.basePath +"zoy_admin/support_user_details";
        return  this.httpclient.get<any>(
            url1,
            {
               headers:ServiceHelper.buildHeaders(),
               observe : 'body',
               withCredentials:true
            }
        );
    } 


    public assignToTeam(data:any): Observable<any> {
      const url1=this.basePath +"zoy_admin/assign-to-team-ticket";
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
  
