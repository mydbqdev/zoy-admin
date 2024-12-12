import { HttpClient, HttpErrorResponse, HttpHeaders } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { Observable, of } from 'rxjs';
import { ServiceHelper } from 'src/app/common/shared/service-helper';
import { BASE_PATH } from 'src/app/common/shared/variables';
import { MessageService } from 'src/app/message.service';

@Injectable({
  providedIn: 'root'
})
export class ActivityLogService {
  message: string;
  httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
  };

  constructor(
    private httpclient: HttpClient,
    @Inject(BASE_PATH) private basePath: string,
    private messageService: MessageService
  ) {}

  public getActivityLogdetails(data:any): Observable<any> {
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
  downloadReport(data:any):Observable<any>{
  const url1=this.basePath +"zoy_admin/download_user_audit_report";
              return this.httpclient.post<any>(
                  url1,
                  data,
                  { responseType: 'blob' as 'json'}
              );
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
