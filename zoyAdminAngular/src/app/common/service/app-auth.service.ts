import { Injectable, Inject } from '@angular/core';
import {HttpClient,HttpHeaders,HttpErrorResponse} from '@angular/common/http';
import { MessageService } from 'src/app/message.service';
import { BASE_PATH } from '../shared/variables';
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
import { MockData } from './mockData';

@Injectable()
export class AppAuthService extends AuthService{
    
    public oDataBlockSize:number;
    public baseODataUrl:string;
    public sessionSnapshot:ApplicationSession;
    httpOptions ={
        headers :new HttpHeaders({'Content-Type':'application/json'})
    };
    constructor(private httpclient:HttpClient,private router:Router,private messageService:MessageService,@Inject(BASE_PATH) private basePath:string,
    private userService:UserService,private encryptDecryptHelper:AESEncryptDecryptHelper,private dataService:DataService, private browserService: BrowserDetectorService){
       super();
        this.sessionSnapshot=null;
        this.message='';
    }


    setSessionStore(res: ResponseStore):void{
        sessionStorage.setItem('user', res.userEmail);
        sessionStorage.setItem('token', 'Bearer ' + res.token);
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
      //  let loginUser : User ={userEmail:user.userEmail,password:this.encryptDecryptHelper.encrypt(user.password)};
        let loginUser : User ={email:user.email,password:user.password};

        let encrypt=this.encryptDecryptHelper.encrypt(user.password);
   
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

   /* public checkLoginUserOnServer() : Observable<ApplicationSession>{
        const url1=this.basePath +'checkLoginUser';
        let userEmail =this.userService?.getUsername();
        var res=new MockData().checkLoginUserResponce.filter(d =>d.userEmail == userEmail);
        const mockData: ApplicationSession = Object.assign(res[0]);

        return of(mockData);
        // return this.httpclient.post<ApplicationSession>(
        //     url1,
        //     '',
        //     {
        //         headers:ServiceHelper.buildHeaders(),
        //        observe : 'body',
        //        withCredentials:true
        //     }
        // );
    }

    public checkLoginUser() : void{
        var msg:string;
        this.sessionSnapshot =null;
        this.message ='';
        this.checkLoginUserOnServer().subscribe(
            (result)=>{
 
               this.sessionSnapshot = result;
               this.sessionSnapshot.username = result.empEmail;
               this.sessionSnapshot.token = result.token;
               this.userService.setUsername(result.userEmail);
               this.userService.setUserinfo(result);

               let resp: ResponseStore={userEmail:result.userEmail,token:result.token};
               this.setSessionStore(resp);
               if(!this.sessionSnapshot.username){
                   this.router.navigateByUrl('/signin');            
              }else{
                   this.router.navigateByUrl('/home'); 
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
       
    }*/

    public checkLoginUserOnServer1() : Observable<ApplicationSession>{
        const url1=this.basePath +'checkLoginUser';
        return this.httpclient.post<ApplicationSession>(
            url1,
            '',
            {
                headers:ServiceHelper.buildHeaders(),
               observe : 'body',
               withCredentials:true
            }
        );
    }
    public checkLoginUserOnServer() : Observable<ApplicationSession>{
        const url1=this.basePath +'zoy_admin/user_assign';
        let token ={"token":""};
        if(!sessionStorage.getItem("token")){
            this.router.navigateByUrl('/signin');
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
               this.sessionSnapshot = result;
               this.sessionSnapshot.username = result.empEmail;
               this.sessionSnapshot.token = result.token;
               this.userService.setUsername(result.empEmail);
               this.userService.setUserinfo(result);
               //let resp: ResponseStore={empEmail:result.empEmail,token:result.token};
               //this.setSessionStore(resp);
               if(!this.sessionSnapshot.username){
                   this.router.navigateByUrl('/signin');
               }{
                   this.router.navigateByUrl('/home'); 
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
               this.sessionSnapshot = result;
               this.sessionSnapshot.username = result.userEmail;
               this.sessionSnapshot.token = result.token;
               this.userService.setUsername(result.userEmail);
               this.userService.setUserinfo(result);

               let resp: ResponseStore={userEmail:result.userEmail,token:result.token};
               this.setSessionStore(resp);
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

     public checkLoginUserVlidaate() : void{
        var msg:string;
       // this.sessionSnapshot =null;
        this.message ='';
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
        );
    }

     public checkLogoutUserOnServer() : Observable<ApplicationSession>{
        const url1=this.basePath +'userlogout';
        return this.httpclient.post<ApplicationSession>(
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
        this.sessionSnapshot =null;
        this.message ='';
        sessionStorage.clear();
        this.checkLogoutUserOnServer().subscribe(
            (result)=>{ 
                   this.router.navigate(['/signin']); 
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