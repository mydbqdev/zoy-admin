import { Component,OnInit } from '@angular/core';
import { Router, ActivatedRoute, NavigationEnd } from '@angular/router';
import { AuthService } from 'src/app/common/service/auth.service';
import { NgxSpinnerService } from 'ngx-spinner';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ForgotPassword } from 'src/app/common/models/forgot-password.model';
import { ForgotPasswordService } from 'src/app/common/service/forgot-password.service';
import { ChangePasswordModel } from 'src/app/profile/model/change-password-model';
import { AESEncryptDecryptHelper } from 'src/app/common/shared/AESEncryptDecryptHelper';

@Component({
	selector: 'app-forgot-password',
	templateUrl: './forgot-password.component.html',
	styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit {
	
	forgotPasswordRequest :{'email':string,'otp':string,'password':string}={'email':'','otp':'','password':''};
	submitted = false;
	error: string = '';
	errorMsg = "";
	mySubscription: any;
	form: FormGroup;
	userReset: ForgotPassword = new ForgotPassword();
	stepPassword: number=1;
	email: string = '';
	viewPassword:boolean=false;
	passwordResetSuccess: boolean = false;
	remaining: number = 60;
	timerDisplay: string = ''; 
	timerInterval: any; 

	constructor(
		private route: ActivatedRoute, private router: Router, private authService: AuthService, private notifyService: NotificationService,
		private forgotPasswordService: ForgotPasswordService, private formBuilder: FormBuilder, private spinner: NgxSpinnerService,private encryptDecryptHelper:AESEncryptDecryptHelper
	) {
		// this.userNameSession = userService.getUsername();
		// if (userService.getUserinfo() != undefined) {
		// 	this.rolesArray = userService.getUserinfo().privilege;
		// }
		this.router.routeReuseStrategy.shouldReuseRoute = function () {
			return false;
		};
		this.mySubscription = this.router.events.subscribe((event) => {
			if (event instanceof NavigationEnd) {
				this.router.navigated = false;
			}
		});
	}
	ngOnDestroy() {
		if (this.mySubscription) {
			this.mySubscription.unsubscribe();
		}
	}

	ngOnInit() {
		this.form = this.formBuilder.group({
			newPassword: ['', [
				Validators.required,
				Validators.minLength(8),
				Validators.maxLength(16),
				Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/)
			  ]],
			  confirmPassword: ['', Validators.required]
			}, 
			{ validator: this.passwordsMatch });
		
	}
	passwordsMatch(formGroup: FormGroup) {
		const password = formGroup.get('newPassword')?.value;
		const confirmPassword = formGroup.get('confirmPassword')?.value;
		return password === confirmPassword ? null : { passwordsMismatch: true };
		}	

	sending:boolean=false;

	sendOrResendOtp() {
	this.submitted = true;
		if(this.email=='' || null==this.email){
			return;
		}
		this.resetTimer();
		this.forgotPasswordRequest.email=this.email;
		this.spinner.show;
		this.sending=true;
		this.forgotPasswordService.sendOTP(this.forgotPasswordRequest).subscribe(data => {
		// this.notifyService.showSuccess(data.message, "");
		this.userReset = new ForgotPassword();
		this.sending=false;
		this.resetTimer();
		this.submitted = false;
		this.stepPassword=2;
		this.spinner.hide();
		}, error => {
		this.sending=false;
		this.spinner.hide();
		if(error.status == 0) {
			this.notifyService.showError("Internal Server Error/Connection not established", "")
		 }else if(error.status==403){
			this.router.navigate(['/forbidden']);
			}else if (error.error && error.error.message) {
			this.errorMsg = error.error.message;
			console.log("Error:" + this.errorMsg);
			this.notifyService.showError(this.errorMsg, "");
			} else {
			if (error.status == 500 && error.statusText == "Internal Server Error") {
				this.errorMsg = error.statusText + "! Please login again or contact your Help Desk.";
			} else {
				let str;
				if (error.status == 400) {
				str = error.error;
				} else {
				str = error.message;
				str = str.substring(str.indexOf(":") + 1);
				}
				console.log("Error:" + str);
				this.errorMsg = str;
			}
			if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
			}
		});
	}
	
	resetTimer(){
		this.remaining=60;
		this.timerDisplay = '';
		clearInterval(this.timerInterval);
		this.startTimer();
	}

	startTimer(): void {
	this.updateTimerDisplay();
	this.timerInterval = setInterval(() => {
		if (this.remaining > 0) {
		this.remaining--;
		this.updateTimerDisplay();
		} else {
		clearInterval(this.timerInterval);
		}
	}, 1000);
	}
	
	private updateTimerDisplay(): void {
	const m = Math.floor(this.remaining / 60);
	const s = this.remaining % 60;
	this.timerDisplay = `${m < 10 ? '0' + m : m}:${s < 10 ? '0' + s : s}`;
	}

   

	validateNumericInput(event: any): void {
		const inputValue = event.target.value;
		if (!/^[0-9]*$/.test(inputValue)) {
		  event.target.value = inputValue.replace(/[^0-9]/g, '');
		}
	  }

	moveFocus(event: KeyboardEvent, nextInputId: string, prevInputId: string): void {
		const input = event.target as HTMLInputElement;
	
		if (event.key === "Backspace" && input.value.length === 0) {
	
		  const prevInput = document.getElementById(prevInputId) as HTMLInputElement;
		  if (prevInput) {
			prevInput.focus();
		  }
		} else if (input.value.length === 1) {
		 
		  const nextInput = document.getElementById(nextInputId) as HTMLInputElement;
		  if (nextInput) {
			nextInput.focus();
		  }
		}
		

		  if (this.userReset.otp1 !='' && this.userReset.otp2 !='' && this.userReset.otp3 !='' && this.userReset.otp4 !='' && this.userReset.otp5 !='' && this.userReset.otp6 !='') {
			this.verifyOtp();
		  }
	  }

	  verifyOtp() {
		
		const enteredOtp = `${this.userReset.otp1}${this.userReset.otp2}${this.userReset.otp3}${this.userReset.otp4}${this.userReset.otp5}${this.userReset.otp6}`;
		this.forgotPasswordRequest.otp = enteredOtp;

		this.forgotPasswordService.verifyOtp(this.forgotPasswordRequest).subscribe((res) => {
		//	this.notifyService.showSuccess(res.message, "");
			this.stepPassword=3;
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
				str=str.substring(str.indexOf(":")+1);
			  }
			  console.log("Error:"+str);
			  this.errorMsg=str;
			}
		  //	if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
			}
		  }
		  );  
	}

	
	changePasswordDetails : ChangePasswordModel=new ChangePasswordModel();
		changePassword() {
			this.submitted=true;	
			if(this.form.invalid){
			return;
			}
			this.forgotPasswordRequest.password= this.encryptDecryptHelper.encrypt(this.changePasswordDetails.newPassword);
			this.spinner.show();	
			this.forgotPasswordService.savePassword(this.forgotPasswordRequest).subscribe((res) => {
		//	  this.notifyService.showSuccess(res.message, "");
			  this.passwordResetSuccess = true;
			
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
				  str=str.substring(str.indexOf(":")+1);
				}
				console.log("Error:"+str);
				this.errorMsg=str;
			  }
			//	if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
			  }
			}
			);  
		  }  
	

}