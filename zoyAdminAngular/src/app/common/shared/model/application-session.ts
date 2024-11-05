import { Observable } from 'rxjs';

export interface ApplicationSession{
    username:string;
    token:string;
    dbquser?:Observable<boolean>;
    userEmail?:string;
    empName?:string;
    emp_code?:string;
    designation?:string;
    firstTimePwd?:string;
}