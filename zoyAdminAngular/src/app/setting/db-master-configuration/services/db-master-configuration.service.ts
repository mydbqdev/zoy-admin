import { HttpClient, HttpErrorResponse, HttpHeaders } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { Observable, of } from 'rxjs';
import { ServiceHelper } from 'src/app/common/shared/service-helper';
import { BASE_PATH } from 'src/app/common/shared/variables';
import { MessageService } from 'src/app/message.service';
import { settingTypeObjClmApiDetailsModel } from "../models/db-setting-models";

@Injectable({
  providedIn: 'root'
})
export class DbMasterConfigurationService {
  message: string;
  httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
  };

  constructor(
    private httpclient: HttpClient,
    @Inject(BASE_PATH) private basePath: string,
    private messageService: MessageService
  ) {}


  settingTypeObjClmApiDetails: settingTypeObjClmApiDetailsModel[]=[
  //{ 'type': string,'obj':string, 'columns': string[] ,'api': string  }[] = [
		{
		  'type': 'Share Type',
      'obj' : 'new shareTypeModel()',
		  'columns': ['share_type', 'share_occupancy_count', 'actions'],
      'api':'zoy_admin/shareType'
    },
    {
		  'type': 'Room Type',
      'obj' : 'new roomTypeModel()',
		  'columns': ['room_type_name', 'actions'],
      'api':'zoy_admin/roomType'
    },
    {
		  'type': 'Rent Cycle',
      'obj' : 'new rentCycleModel()',
		  'columns': ['cycle_name', 'actions'],
      'api':'zoy_admin/rentCycle'
    },
  ]

  columnHeaders = {
    'share_type': 'SHARE TYPE',                 
    'share_occupancy_count': 'SHARE OCCUPANCY COUNT',
    'room_type_name': 'ROOM TYPE NAME',
    'cycle_name': 'CYCLE NAME'
  }

  getDbSettingDetails(api:string): Observable<any> {
    const url1=this.basePath +api;
        return  this.httpclient.get<any>(
            url1,
            {
                headers:ServiceHelper.buildHeaders(),
               observe : 'body',
               withCredentials:true
            }
        );
  } 


    public getDbMasterConfigurationList(): Observable<any> {
    const url = this.basePath + 'zoy_admin/getDbMasterConfigurationList'; 
    return  this.httpclient.get<any>(
      url,
      {
          headers:ServiceHelper.buildHeaders(),
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
