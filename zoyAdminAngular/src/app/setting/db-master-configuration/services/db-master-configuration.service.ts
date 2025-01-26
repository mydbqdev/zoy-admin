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
		{
		  'type': 'Share Type',
		  'columns': ['share_type', 'share_occupancy_count', 'actions'],
      'api':'zoy_admin/shareType'
    },
    {
		  'type': 'Room Type',
		  'columns': ['room_type_name', 'actions'],
      'api':'zoy_admin/roomType'
    },
    {
		  'type': 'Rent Cycle',
		  'columns': ['cycle_name', 'actions'],
      'api':'zoy_admin/rentCycle'
    },
    {
		  'type': 'PG Type',
		  'columns': ['pg_type_name', 'actions'],
      'api':'zoy_admin/pgType'
    },
    {
		  'type': 'Notification Mode',
		  'columns': ['notification_mod_name', 'actions'],
      'api':'zoy_admin/notification_mode'
    },
    {
		  'type': 'Factor',
		  'columns': ['factor_name', 'actions'],
      'api':'zoy_admin/factor'
    },
    {
		  'type': 'Due Type',
		  'columns': ['due_type_name', 'actions'],
      'api':'zoy_admin/dueType'
    },
    {
      'type': 'Currency Type',
      'columns': ['currency_name', 'actions'],
      'api': 'zoy_admin/currencyType'
    },
    // {
    //     'type': 'Billing Type',
    //     'columns': ['billing_type_name', 'actions'],
    //     'api': 'zoy_admin/billingType'
    // },
    {
        'type': 'Amenities',
        'columns': ['ameneties_name','ameneties_image', 'actions'],
        'api': 'zoy_admin/ameneties'
    },
    {
      'type': 'Short Term',
      'columns': ['start_day','end_day'],
      'api': 'zoy_admin/shortTerm'
    }
  ]

  columnHeaders = {
    'share_type': 'SHARE TYPE',                 
    'share_occupancy_count': 'SHARE OCCUPANCY COUNT',
    'room_type_name': 'ROOM TYPE NAME',
    'cycle_name': 'Rent CYCLE',
    'pg_type_name': 'PG TYPE NAME',
    'notification_mod_name':'NOTIFICATION MOD NAME',
    'factor_name':'FACTOR NAME',
    'due_type_name':'DUE TYPE',
    'currency_name': 'CURRENCY NAME',
    'billing_type_name': 'BILLING TYPE NAME',
    'ameneties_name': 'AMENITIES NAME',
    'ameneties_image':"AMENITIES PICTURE",
    'start_day' : 'START DAY',
    'end_day' :'END DAY'
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

  submitData(data:any,isCreate:boolean,api:string,formData:any,isPhoto:boolean): Observable<any> {
    
    if(isCreate){
      const url1=this.basePath +api;
      if(isPhoto && api=='zoy_admin/ameneties'){
        data=formData;
      }
        return  this.httpclient.post<any>(
          url1,
          data,
          {
              headers:ServiceHelper.buildHeaders(),
            observe : 'body',
            withCredentials:true
          }
      );
    }else{
      var url1=this.basePath +api;
      if(isPhoto && api=='zoy_admin/ameneties'){
        data=formData;
         url1=this.basePath +'zoy_admin/amenetiesUpdate';
      }else if(!isPhoto && api=='zoy_admin/ameneties'){
         url1=this.basePath +'zoy_admin/ameneties';
      }else{
        
      }
        return  this.httpclient.put<any>(
            url1,
            data,
            {
                headers:ServiceHelper.buildHeaders(),
               observe : 'body',
               withCredentials:true
            }
        );
      }
  } 

  submitShortTermData(data:any): Observable<any> {
    const url1=this.basePath +'zoy_admin/shortTerm';
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
