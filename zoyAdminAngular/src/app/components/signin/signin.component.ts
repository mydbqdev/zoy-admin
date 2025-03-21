import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from 'src/app/common/service/auth.service';
import { User } from 'src/app/common/shared/user';
import { ResponseStore } from 'src/app/common/models/response.model';
import { DataService } from 'src/app/common/service/data.service';
import { ChangePasswordModel } from 'src/app/profile/model/change-password-model';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AESEncryptDecryptHelper } from 'src/app/common/shared/AESEncryptDecryptHelper';
import { ProfileService } from 'src/app/profile/service/profile-service';
import { NgxSpinnerService } from 'ngx-spinner';
import { NotificationService } from 'src/app/common/shared/message/notification.service';


@Component({
	selector: 'app-signin',
	templateUrl: './signin.component.html',
	styleUrls: ['./signin.component.css']
})
export class SigninComponent implements OnInit {

	username: string = '';
	password: string = '';
	rememberme:boolean=false;
    showPassword: any;
	isSignedin = false;
	submitted=false;
	error: string = '';
	errorMsg =""
	sending:boolean=false;
	isChangePassword :boolean=false;
	viewPassword:boolean=false;
	form: FormGroup;
	changePasswordDetails : ChangePasswordModel=new ChangePasswordModel();
	resetPassword: { 'email': string,'oldPassWord': string,'newPassword': string}={ 'email': '', 'oldPassWord': '', 'newPassword': '', };	
	isPasswordVisible:boolean=false;

	constructor(private route: ActivatedRoute, private router: Router, private authService: AuthService,private dataService :DataService,
		private encryptDecryptHelper:AESEncryptDecryptHelper,private profileService:ProfileService,
				private spinner: NgxSpinnerService,private formBuilder: FormBuilder, private notifyService: NotificationService
	) { }

	ngOnInit() {
				this.form = this.formBuilder.group({
					oldPassword: ['', [Validators.required]],
					newPassword: ['', [
						Validators.required,
						Validators.minLength(8),
						Validators.maxLength(16),
						Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/)
					  ]],
					  confirmPassword: ['', Validators.required]
					}, 
					{ validator: this.passwordsMatch });

		if(JSON.parse(localStorage.getItem('rememberme'))!==null){
			this.username=localStorage.getItem('username');
			this.password=localStorage.getItem('userpwd');
			this.rememberme=JSON.parse(localStorage.getItem('rememberme'));
		}
	}
	ngAfterViewInit() {
		this.doSignout();
	}
	doSignout() {
		this.authService.checkLogout();
  	}

	togglePasswordVisibility(){
		this.isPasswordVisible=!this.isPasswordVisible;
	}
	doSignin() {
		this.submitted=true;    
		if (this.username !== '' && this.username !== null && this.password !== '' && this.password !== null) {
			localStorage.removeItem('username');
			localStorage.removeItem('userpwd');
			localStorage.removeItem('rememberme');

			if(this.rememberme){
				localStorage.setItem('username', this.username);
				localStorage.setItem('userpwd', this.password);
				localStorage.setItem('rememberme', JSON.stringify(this.rememberme));
			}
			const user: User = { email: this.username.toLowerCase(), password: this.password };
            this.submitted=false;
			this.sending=true;
			this.authService.getAuthUser(user).subscribe((result) => {
				this.sending=false;
				const res: ResponseStore = { userEmail: result.email, token: result.token };
				this.authService.setSessionStore(res);
				this.authService.checkLoginUser();
				this.connectWebsocket();
				this.router.navigate(['/home']);
			}, error => {
				this.sending=false;
				if(error.status == 0) {
					this.error = "Internal Server Error/Connection not established";
				 }else if(error.status==403){
				   this.router.navigate(['/forbidden']);
				 }else if (error.error && error.error.message) {
				   this.error = error.error.message;
				   console.log("Error>>>:" + this.error);
				   if(this.error == 'Your password has been expired.'){
					this.authService.setSessionStore(error.error);
					this.isChangePassword = true;
					this.changePasswordDetails.oldPassword = this.password ; 
			  		 }
				 } else {
					if (error.status == 500 && error.statusText == "Internal Server Error") {
					 this.errorMsg = error.statusText + "! Please login again or contact your Help Desk.";
				   	} else {
					 let str;
					 if (error.status == 400) {
					   str = error.error.error;
					 } else{
					   str = error.error.message;
					   str = str?.substring(str.indexOf(":") + 1);
					 }
					 this.error = str;
				   }
				 }
			   });
		}			  
	}
	connectWebsocket(){
		this.authService.connectWebsocket(this.username.toLowerCase(),'AlertNotification');
	  }
	passwordsMatch(formGroup: FormGroup) {
		const password = formGroup.get('newPassword')?.value;
		const confirmPassword = formGroup.get('confirmPassword')?.value;
		return password === confirmPassword ? null : { passwordsMismatch: true };
	}	

	changePassword() {
		this.authService.checkLoginUserVlidaate();
		this.submitted=true;		
		this.changePasswordDetails.userEmail=this.username.toLowerCase();
		if(this.form.invalid){
			return;
			}
		this.resetPassword.email= this.changePasswordDetails.userEmail;
		this.resetPassword.oldPassWord= this.encryptDecryptHelper.encrypt(this.changePasswordDetails.oldPassword);
		this.resetPassword.newPassword= this.encryptDecryptHelper.encrypt(this.changePasswordDetails.newPassword);
		this.spinner.show();	
		this.profileService.changePassword(this.resetPassword).subscribe((res) => {
		  this.afterChangePasswordLogout();
		  this.notifyService.showSuccess(res.message, "");
		  this.isChangePassword = false;
		  this.error = '';
		  this.form.reset();
		  this.submitted=false;
		  this.spinner.hide();
		},error =>{
		  this.spinner.hide();
		  console.log("error.error",error)
		  this.errorMsg = (error.error.error !=undefined?(error.error.error  +"."):"")
		  + (error.error.userEmail!=undefined?(error.error.userEmail+"."):"")
		  +(error.error.password!=undefined?(error.error.password  +"."):"");
		  if(error.status == 0) {
			this.notifyService.showError("Internal Server Error/Connection not established", "")
		 }else if(error.status==401){
			console.error("Unauthorised");
		}else if(error.status==403){
		  this.router.navigate(['/forbidden']);
		  }else if (error.error && error.error.message) {
		  this.errorMsg =error.error.message;
		  console.log("Error:"+this.errorMsg);
		  this.notifyService.showError(this.errorMsg, "");
		  this.spinner.hide();
		  } else {
		  this.spinner.hide();
		  if(error.status==500 && error.statusText=="Internal Server Error"){
			this.errorMsg=error.statusText+"! Please login again or contact your Help Desk.";
		  }else{
		   this.spinner.hide();
			let str;
			if(error.status==400){
			str=error.error;
			}else{
			  str=error.message;
			  str=str?.substring(str.indexOf(":")+1);
			}
			console.log("Error:"+str);
			this.errorMsg=str;
		  }
			if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
		  }
		}
		);  
	  }
	  
	  afterChangePasswordLogout(){
	  	this.profileService.userlogout().subscribe((res) => {
	  		},error =>{
	  		this.spinner.hide();
	  		console.log("error.error",error)
	  		this.errorMsg = (error.error.error !=undefined?(error.error.error  +"."):"")
	  		+ (error.error.userEmail!=undefined?(error.error.userEmail+"."):"")
	  		+(error.error.password!=undefined?(error.error.password  +"."):"");
	  		if(error.status == 0) {
	  			this.notifyService.showError("Internal Server Error/Connection not established", "")
	  		}else if(error.status==401){
	  			console.error("Unauthorised");
	  		}else if(error.status==403){
	  		this.router.navigate(['/forbidden']);
	  		}else if (error.error && error.error.message) {
	  		this.errorMsg =error.error.message;
	  		console.log("Error:"+this.errorMsg);
	  		this.notifyService.showError(this.errorMsg, "");
	  		this.spinner.hide();
	  		} else {
	  		this.spinner.hide();
	  		if(error.status==500 && error.statusText=="Internal Server Error"){
	  			this.errorMsg=error.statusText+"! Please login again or contact your Help Desk.";
	  		}else{
	  		this.spinner.hide();
	  			let str;
	  			if(error.status==400){
	  			str=error.error;
	  			}else{
	  			str=error.message;
	  			str=str?.substring(str.indexOf(":")+1);
	  			}
	  			console.log("Error:"+str);
	  			this.errorMsg=str;
	  		}
	  			if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
	  		}
	  		}
	  		);  
	  	}

}
