import { Component, ElementRef, OnInit, Renderer2, ViewChild } from '@angular/core';
import { Router, ActivatedRoute, NavigationEnd } from '@angular/router';
import { AuthService } from 'src/app/common/service/auth.service';
import { User } from 'src/app/common/shared/user';
import { ResponseStore } from 'src/app/common/models/response.model';
import { DataService } from 'src/app/common/service/data.service';
import { NgxSpinnerService } from 'ngx-spinner';
import { HttpClient } from '@angular/common/http';
import { UserService } from 'src/app/common/service/user.service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { ForgotPassword } from 'src/app/common/models/forgot-password.model';
import { ForgotPasswordService } from 'src/app/common/service/forgot-password.service';
import { ChangePasswordModel } from 'src/app/profile/model/change-password-model';

@Component({
	selector: 'app-forgot-password',
	templateUrl: './forgot-password.component.html',
	styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit {
	username: string = '';
	password: string = '';
	rememberme: boolean = false;
	showPassword: any;
	isSignedin = false;
	submitted = false;
	error: string = '';
	errorMsg = "";
	empCodeFilter: string = "";
	message: string;
	extraApplications: boolean = false;
	settings = {};
	activeApplicationList: string[] = [];
	selectedItems: any[] = [];
	public rolesArray: string[] = [];
	@ViewChild('editCloseModal') editCloseModal: ElementRef;
	@ViewChild('registerCloseModal') registerCloseModal: ElementRef;
	mySubscription: any;
	public userNameSession: string = "";
	userActiveApplicationList: string[] = [];
	shouldBeChecked = [];
	checkedApplications: { [key: string]: boolean } = {};
	form: FormGroup;
	formOtp: FormGroup;
	formReset: FormGroup;
	userReset: ForgotPassword = new ForgotPassword();
	otpSent: boolean = false;
	otpSubmitted: boolean = false;
	stepPassword: number=1;
	submittedVerifyCode=false;
	submittedResetPwd=false;
	email: string = '';
	isPasswordVisible: boolean = false;
	showNewPassword: boolean = false;
	showConfirmPassword: boolean = false;
	viewPassword:boolean=false;

	constructor(
		private route: ActivatedRoute, private router: Router, private authService: AuthService, private dataService: DataService,
		private forgotPasswordService: ForgotPasswordService, private formBuilder: FormBuilder, private http: HttpClient,
		private userService: UserService, private notifyService: NotificationService, private spinner: NgxSpinnerService,
		private renderer: Renderer2, private confirmationDialogService: ConfirmationDialogService
	) {
		this.userNameSession = userService.getUsername();
		if (userService.getUserinfo() != undefined) {
			this.rolesArray = userService.getUserinfo().privilege;
		}
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
		
		this.formOtp = this.formBuilder.group({
			otp1: ['', Validators.required],
			otp2: ['', Validators.required],
			otp3: ['', Validators.required],
			otp4: ['', Validators.required],
			otp5: ['', Validators.required],
			otp6: ['', Validators.required]// Added OTP field to form
		});
	}
	   passwordsMatch(formGroup: FormGroup) {
		const password = formGroup.get('password')?.value;
		const repeatPassword = formGroup.get('repeatPassword')?.value;
		return password === repeatPassword ? null : { passwordsMismatch: true };
	}	
	changePasswordDetails : ChangePasswordModel=new ChangePasswordModel();
		changePassword() {
			this.submitted=true;	
			this.changePasswordDetails.userEmail=this.userService.getUsername();
			if(this.form.invalid){
				return;
			}
			this.spinner.show();	
			this.forgotPasswordService.savePassword(this.changePasswordDetails).subscribe((res) => {
			  this.notifyService.showSuccess(res.message, "");
			  this.form.reset();
			  this.submitted=false;
			  this.spinner.hide();
			},error =>{
			  this.spinner.hide();
			  console.log("error.error",error)
			  this.errorMsg = (error.error.error !=undefined?(error.error.error  +"."):"")
			  + (error.error.userEmail!=undefined?(error.error.userEmail+"."):"")
			  +(error.error.password!=undefined?(error.error.password  +"."):"");
			  if(error.status==403){
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

	// togglePasswordVisibility(field: 'new' | 'confirm'): void {
	//   if (field === 'new') {
	//     this.showNewPassword = !this.showNewPassword;
	//   } else if (field === 'confirm') {
	//     this.showConfirmPassword = !this.showConfirmPassword;
	//   }
	// }
	
	resetForm() {
		this.submitted = false;
		this.form.reset();
		this.otpSent = false; 
	}

	sendOrResendOtp() {
		this.otpSent = true;
		this.stepPassword=2;
		return;
		this.spinner.show;
		this.forgotPasswordService.sendOTP(this.email).subscribe(data => {
			this.stepPassword=2;
			this.spinner.hide();
			}, error => {
			this.spinner.hide();
			if(error.status==403){
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

	submitOtp() {
		this.submitted=true;	
		this.changePasswordDetails.userEmail=this.userService.getUsername();
		if(this.form.invalid){
			return;
		}
		this.spinner.show();	
		this.forgotPasswordService.submitOtp(this.changePasswordDetails).subscribe((res) => {
		  this.notifyService.showSuccess(res.message, "");
		  this.form.reset();
		  this.submitted=false;
		  this.spinner.hide();
		},error =>{
		  this.spinner.hide();
		  console.log("error.error",error)
		  this.errorMsg = (error.error.error !=undefined?(error.error.error  +"."):"")
		  + (error.error.userEmail!=undefined?(error.error.userEmail+"."):"")
		  +(error.error.password!=undefined?(error.error.password  +"."):"");
		  if(error.status==403){
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



	// showNewPassword: boolean = false;
    passwordResetSuccess: boolean = false;
    togglePasswordVisibility(type: string): void {
 	this.isPasswordVisible=!this.isPasswordVisible;
    if (type === 'new') {
      this.showNewPassword = !this.showNewPassword;
    }
  }

  resetPassword(event: Event): void {
  //  event.preventDefault();
  if(this.formReset.invalid){
	return;
	}
    // Add form validation or API call logic here
    // If the reset is successful:
    this.passwordResetSuccess = true;
  }

	doRegister() {
		this.submitted = true;

		if (this.form.invalid) {
			return;
		}
		if (this.userReset.newPassword !== this.userReset.repeatPassword) {
			return;
		}
		this.resetForm();
	}


	resendOtp(): void {
	  }

	verifyOtp() {
		const enteredOtp = `${this.userReset.otp1}${this.userReset.otp2}${this.userReset.otp3}${this.userReset.otp4}${this.userReset.otp5}${this.userReset.otp6}`;
		if (enteredOtp === this.userReset.otp) {
			this.notifyService.showSuccess("OTP verified successfully", "");
		} else {
			this.notifyService.showError("Invalid OTP", "");
		}
		this.stepPassword=3;
	}
	doSignin() {
		this.submitted=true;    
		if (this.email !== '' && this.email !== null) {		
			const user: User = { email: this.email.toLowerCase()};
            this.submitted=false;
			
		}
	}

	moveFocus(event: KeyboardEvent, nextInputId: string, prevInputId: string): void {
		const input = event.target as HTMLInputElement;
	  
		// Check if the user pressed the 'Backspace' key
		if (event.key === "Backspace" && input.value.length === 0) {
		  // Move focus to the previous input box if available
		  const prevInput = document.getElementById(prevInputId) as HTMLInputElement;
		  if (prevInput) {
			prevInput.focus();
		  }
		} else if (input.value.length === 1) {
		  // If the user types a character, move focus to the next input box
		  const nextInput = document.getElementById(nextInputId) as HTMLInputElement;
		  if (nextInput) {
			nextInput.focus();
		  }
		}
	  }

	  validateNumericInput(event: any): void {
		const inputValue = event.target.value;
		if (!/^[0-9]*$/.test(inputValue)) {
		  event.target.value = inputValue.replace(/[^0-9]/g, '');
		}
	  }
}