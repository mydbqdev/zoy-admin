import { HttpClient, HttpErrorResponse, HttpHeaders } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { catchError, Observable, of, tap, throwError } from 'rxjs';
import { ServiceHelper } from 'src/app/common/shared/service-helper';
import { BASE_PATH } from 'src/app/common/shared/variables';
import { MessageService } from 'src/app/message.service';

@Injectable({
  providedIn: 'root'
})
export class UserAuditService {
  message: string;
  httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
  };

  constructor(
    private httpclient: HttpClient,
    @Inject(BASE_PATH) private basePath: string,
    private messageService: MessageService
  ) {}

  public getUserAuditdetails(data:any): Observable<any> {
    const url1=this.basePath +'zoy_admin/audit-activitieslog';
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
 public getUserNameList(): Observable<any> {
    const url1=this.basePath +'zoy_admin/userName-List';
    return this.httpclient.get<any>(
        url1,
        {
            headers:ServiceHelper.buildHeaders(),
           observe : 'body',
           withCredentials:true
        }
     );
   }
    downloadReport(data:any):Observable<any>{
    const url1=this.basePath +"zoy_admin/download_user_audit_reportt";
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
     responseType: 'blob' 
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
