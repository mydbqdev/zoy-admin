import { HttpClient, HttpErrorResponse, HttpHeaders } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { catchError, Observable, of, tap, throwError } from 'rxjs';
import { ServiceHelper } from 'src/app/common/shared/service-helper';
import { BASE_PATH } from 'src/app/common/shared/variables';
import {BASE_PATH_EXTERNAL_SERVER} from 'src/app/common/shared/variables';
import { MessageService } from 'src/app/message.service';

@Injectable({
  providedIn: 'root'
})
export class BulkUploadService {
  message: string;
  httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
  };

  constructor(
    private httpclient: HttpClient,
    @Inject(BASE_PATH) private basePath: string,
    @Inject(BASE_PATH_EXTERNAL_SERVER) private basePathExternalServer: string,
    private messageService: MessageService
  ) {}


  public getUploadFileDetails(): Observable<any> {
    const url = this.basePath + 'zoy_admin/getBulkUpload';
    return  this.httpclient.get<any>(
      url,
      {
          headers:ServiceHelper.buildHeaders(),
         observe : 'body',
         withCredentials:true
      });
  }

  public getOwnerPropertyDetailsList(): Observable<any> {
    const url = this.basePath + 'zoy_admin/owner_property_details';
    return  this.httpclient.get<any>(
      url,
      {
          headers:ServiceHelper.buildHeaders(),
         observe : 'body',
         withCredentials:true
      });
  }

  public upload_tenant_file(data:any): Observable<any> {
     const url = this.basePath + 'zoy_admin/upload_tenant_file';
    return  this.httpclient.post<any>(
      url,
      data,
      {
          headers:ServiceHelper.filesHeaders(),
         observe : 'body',
         withCredentials:true
      });
  }

  public upload_property_file(data:any): Observable<any> {
    const url = this.basePath + 'zoy_admin/upload_property_file';
    return  this.httpclient.post<any>(
      url,
      data,
      {
          headers:ServiceHelper.filesHeaders(),
         observe : 'body',
         withCredentials:true
      });
  }

  

  private errorHandler(error: HttpErrorResponse) {
    return of(error.message || "server error");
  }

  private log(message: string) {
    this.messageService.add(`AuthService:${message}`, 'info');
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(error);
      this.log(`${operation} failed: ${error.message}`);
      return of(result as T);
    };
  }
}
