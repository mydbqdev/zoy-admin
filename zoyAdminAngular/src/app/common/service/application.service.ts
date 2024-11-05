import { Injectable, Inject } from '@angular/core';
import {HttpClient,HttpHeaders,HttpErrorResponse} from '@angular/common/http';
import { MessageService } from 'src/app/message.service';
import { BASE_PATH } from '../shared/variables';
import { Observable, of} from 'rxjs';
import { Router } from '@angular/router';
@Injectable({
    providedIn:'root'
})

export class AppService{
    
    public oDataBlockSize:number;
    public baseODataUrl:string;
    httpOptions ={
        headers :new HttpHeaders({'Content-Type':'application/json'})
    };
 
    constructor(private httpclient:HttpClient,private router:Router,private messageService:MessageService,@Inject(BASE_PATH) private basePath:string){

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