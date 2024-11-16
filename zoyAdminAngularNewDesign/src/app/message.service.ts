import { Injectable } from "@angular/core";
import { ReplaySubject, Subject, Observable } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

export type MesssageType='info'|'warning'|'error';

export interface Message{
    body:string;
    messageType:MesssageType;
    title?:string;
}

@Injectable({
    providedIn :'root'
})
export class MessageService {
  private _message:Subject<Message> = new ReplaySubject();


   public add(body:string, messageType: MesssageType, title?:string){
       if(this.message){
        this._message.next({body:body,messageType:messageType,title});
       }else{
        this._message.next(null);
       }
    }
    public clear(){
        this._message.next(null);
    }

    public addInfo(message:string,title?:string){
       this.add(message,'info',title); 
    }

    public addWarning(message:string,title?:string){
        this.add(message,'warning',title); 
     }

     public addError(message:string,title?:string){
        this.add(message,'error',title); 
     }

     public log(...args:any[]){
         console.log(...args);
     }

    

    public addHttpError(errorResponse:HttpErrorResponse,serviceName:string,operation:string){
        console.error('httpError',errorResponse);
        let message:string =errorResponse.message;
        if(errorResponse.error instanceof ErrorEvent){
            message=errorResponse.error.message;
        }else if(errorResponse.error){
            if(errorResponse.error.fault && errorResponse.error.fault.faultstring){
                message=errorResponse.error.fault.faultstring;
            }else{
                if(errorResponse.error.ERROR_MESSAGES){
                    const errorMessages:any[]= errorResponse.error.ERROR_MESSAGES;
                    message=errorResponse.error.ERROR_MESSAGES[0].ErrorMessageText;
                }
            }
        }
        this.addError(`${serviceName}.${operation} failed.${message}`)
    }


    public get message(): Observable<Message>{
        return this._message;
    }

}