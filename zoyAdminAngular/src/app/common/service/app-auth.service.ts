import { Injectable, Inject } from '@angular/core';
import {HttpClient,HttpHeaders,HttpErrorResponse} from '@angular/common/http';
import { MessageService } from 'src/app/message.service';
import { BASE_PATH, WEDSOCKET_BASE_PATH } from '../shared/variables';
import { count, Observable, of, throwError} from 'rxjs';
import { ServiceHelper } from '../shared/service-helper';
import { Router } from '@angular/router';
import { ApplicationSession } from '../shared/model/application-session';
import { AuthService } from './auth.service';
import { AESEncryptDecryptHelper } from '../shared/AESEncryptDecryptHelper';
import { User } from '../shared/user';
import { UserService } from './user.service';
import { ResponseStore } from '../models/response.model';
import { SignupDetails } from '../shared/signup-details';
import { DataService } from './data.service';
import { BrowserDetectorService } from './browser-detector.service';
import { WebsocketService } from './websocket.service';

@Injectable()
export class AppAuthService extends AuthService{
    
    public oDataBlockSize:number;
    public baseODataUrl:string;
    public sessionSnapshot:ApplicationSession;
    httpOptions ={
        headers :new HttpHeaders({'Content-Type':'application/json'})
    };
    constructor(private httpclient:HttpClient,private router:Router,private messageService:MessageService,@Inject(BASE_PATH) private basePath:string,
    private userService:UserService,private encryptDecryptHelper:AESEncryptDecryptHelper,private dataService:DataService, private browserService: BrowserDetectorService,
    private websocketService:WebsocketService,@Inject(WEDSOCKET_BASE_PATH) private websocketBasePath:string,){
       super();
        this.sessionSnapshot=null;
        this.message='';
    }


    setSessionStore(res: ResponseStore):void{
        sessionStorage.setItem('user', res.userEmail);
        sessionStorage.setItem('token', 'Bearer ' + res.token);
        sessionStorage.setItem('zoyadminapi', 'yes');
    }

    public isLoggedIn(redirectUrl?:string):Observable<boolean>{
        if(this.sessionSnapshot && !(sessionStorage.getItem('user')==null)){
           return of(true);
        }else{
            if(redirectUrl!=='/startup')
            this.router.navigate(['/startup']);
            return of(false);
       }
    }

    public getAuthUser(user:User) : Observable<any>{
        const url1=this.basePath +'zoy_admin/login';
        let loginUser : User ={email:user.email,password:this.encryptDecryptHelper.encrypt(user.password)};
        return this.httpclient.post<User>(
            url1,
            loginUser,
            {
                headers:ServiceHelper.buildHeadersBasicAuth(),
                observe : 'body',
                withCredentials:true
            }
        );
    }

    public signupUser(user:SignupDetails) : Observable<any>{
        const url1=this.basePath +'auth/register';
        return this.httpclient.post<any>(
            url1,
            user,
            {
                headers:ServiceHelper.buildHeaders(),
               observe : 'body',
               withCredentials:true
            }
        );
    }
    public checkLoginUserOnServer() : Observable<ApplicationSession>{
        const url1=this.basePath +'zoy_admin/user_details';
        let token ={"token":""};
        if(!sessionStorage.getItem("token")){
            this.router.navigateByUrl('/signin');
            return of(null);
        }
         token ={"token":sessionStorage.getItem("token").replace("Bearer ","")}
        return this.httpclient.post<any>(
            url1,
            token,
            {
                headers:ServiceHelper.buildHeaders(),
               observe : 'body',
               withCredentials:true
            }
        );
    }

    public checkLoginUser() : void{
        var msg:string;
        this.sessionSnapshot =null;
        this.message ='';
        this.checkLoginUserOnServer().subscribe(
            (result)=>{
              if (result) { 
               this.sessionSnapshot = result;
               this.sessionSnapshot.username = result.userEmail;
               this.sessionSnapshot.token = result.token;
               this.userService.setUsername(result.userEmail);
               this.userService.setUserinfo(result);
               //let resp: ResponseStore={empEmail:result.empEmail,token:result.token};
               //this.setSessionStore(resp);
               if(!this.sessionSnapshot.username){
                   this.router.navigateByUrl('/signin');
               }{
                this.userService.setLoggedOut(false);
                   this.router.navigateByUrl('/home'); 
              }
             } else {
                this.router.navigateByUrl('/signin');
            }
            },
            (err) =>{
               if(err.error && err.error.message){
                   msg=err.error.message;
               }else{
               msg='An error occured while processing your request.Please contact your Help Desk.';
               }
               this.message=msg;
               this.router.navigateByUrl('/signin');
            },
            () =>{
                if(!this.sessionSnapshot){
                   msg='An error occured while processing your request.Please contact your Help Desk.';
                   this.message=msg;
                   this.router.navigateByUrl('/signin');
                }
            }
        );
       
    }
     public getUserDetails() : void{
        var msg:string;
        this.sessionSnapshot =null;
        this.message ='';
        this.checkLoginUserOnServer().subscribe(
            (result)=>{
             if (result) { 
               this.sessionSnapshot = result;
               this.sessionSnapshot.username = result.userEmail;
               this.sessionSnapshot.token = result.token;
               this.userService.setUsername(result.userEmail);
               this.userService.setUserinfo(result);
               this.userService.setSessionTime(new Date());

               let resp: ResponseStore={userEmail:result.userEmail,token:result.token};
               this.setSessionStore(resp);
               this.dataService.setUserDetails(this.userService.getUserinfo());
            }else{
                this.router.navigateByUrl('/signin');
                this.userService.setSessionTime(null);
                }
            },
            (err) =>{
               if(err.error && err.error.message){
                   msg=err.error.message;
               }else{
               msg='An error occured while processing your request.Please contact your Help Desk.';
               }
               this.message=msg;
               this.router.navigateByUrl('/signin');
               this.userService.setSessionTime(null);
            },
            () =>{
                if(!this.sessionSnapshot){
                   msg='An error occured while processing your request.Please contact your Help Desk.';
                   this.message=msg;
                   this.router.navigateByUrl('/signin');
                   this.userService.setSessionTime(null);
                }
            }
        );
       
    }

     public checkLoginUserVlidaate() : void{
    //    if(!this.userService.isLoggedOut()){
        this.getUserDetails();
      //  }
        //var msg:string;
       // this.sessionSnapshot =null;
        /*this.message ='';
        this.getUserSignupDetails(sessionStorage.getItem('user')).subscribe((data) => {
            let user:SignupDetails = new SignupDetails() ;
            user.userId= data.id !=undefined?data.id:"";
            user.userEmail= data.userEmail !=undefined?data.userEmail:"";
            user.userFirstName= data.userFirstName !=undefined?data.userFirstName:"";
            user.userLastName= data.userLastName !=undefined?data.userLastName:"";
            user.zipCode= data.zipCode !=undefined?data.zipCode:"";
            this.dataService.setUserDetails(user);
         //   this.router.navigateByUrl('/home'); 
        },error =>{
            this.checkLogout();
         }
        );*/
    }

     public checkLogoutUserOnServer() : Observable<any>{
        const url1=this.basePath +'zoy_admin/userlogout';
        return this.httpclient.post<any>(
            url1,
            '',
            {
                headers:ServiceHelper.buildHeaders(),
               observe : 'body',
               withCredentials:true
            }
        );
    }

     public checkLogout() : void{
        var msg:string;
        this.message ='';
        if (!this.userService.getUserinfo() || !this.sessionSnapshot) {
            return;
        }
        
        this.checkLogoutUserOnServer().subscribe(
            (result)=>{ 
                this.userService.setLoggedOut(true);
                this.userService.setUserinfo(null);
                this.sessionSnapshot =null;
                this.userService.setSessionTime(null);
                sessionStorage.clear();
                this.router.navigate(['/signin']); 
                this.websocketService.closeConnection('AlertNotification');
            },
            (err) =>{
               if(err.error && err.error.message){
                   msg=err.error.message;
               }else{
               msg='An error occured while processing your request.Please contact your Help Desk.';
               }
               this.message=msg;
               this.router.navigateByUrl('/signin');
               this.userService.setSessionTime(null);
            },
            () =>{
                if(!this.sessionSnapshot){
                   msg='An error occured while processing your request.Please contact your Help Desk.';
                   this.message=msg;
                   this.router.navigateByUrl('/signin');
                   this.userService.setSessionTime(null);
                }
            }
        );
       
    }

    public getUserSignupDetails(data:any) : Observable<any>{
        const url1=this.basePath +'user/getUserByUserEmail?userEmail='+data;
        return this.httpclient.post<any>(
            url1,
            "",
            {
                headers:ServiceHelper.buildHeaders(),
               observe : 'body',
               withCredentials:true
            }
        );
    }

    public connectWebsocket(userId:string,socket:string) {
        const url=this.websocketBasePath +'notificationPageHandler?userId='+userId;
        this.websocketService.connect(url, socket );
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