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
			firstName: ['', [Validators.required]],
			lastName: ['', [Validators.required]],
			designation: ['', [Validators.required]],
			email: ['', [
				Validators.required,
				Validators.pattern(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/)
			]],
			password: ['', [
				Validators.required,
				Validators.minLength(8),
				Validators.maxLength(16),
				Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/)
			]],
			repeatPassword: ['', Validators.required],
			otp1: ['', Validators.required],
			otp2: ['', Validators.required],
			otp3: ['', Validators.required],
			otp4: ['', Validators.required],
			otp5: ['', Validators.required],
			otp6: ['', Validators.required]// Added OTP field to form
		}, { validator: this.passwordsMatch });
	}

	passwordsMatch(formGroup: FormGroup) {
		const password = formGroup.get('password')?.value;
		const repeatPassword = formGroup.get('repeatPassword')?.value;
		return password === repeatPassword ? null : { passwordsMismatch: true };
	}

	nameValidation(event: any, inputId: string) {
		const clipboardData = event.clipboardData || (window as any).clipboardData;
		const pastedText = clipboardData.getData('text/plain');
		const clString = pastedText.replace(/[^a-zA-Z\s.]/g, '');
		event.preventDefault();
		switch (inputId) {
			case 'firstName':
				this.userReset.firstName = clString;
				break;
			case 'lastName':
				this.userReset.lastName = clString;
				break;
		}
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
		this.notifyService.showSuccess("OTP sent successfully", "OTP Notification");
		this.spinner.show;
		this.stepPassword=2;
		// Add logic to call the OTP sending service/API here
	}

	// showNewPassword: boolean = false;
    passwordResetSuccess: boolean = false;

  // Dummy form for validation (optional, replace with your actual form group)
  

  togglePasswordVisibility(type: string): void {
    if (type === 'new') {
      this.showNewPassword = !this.showNewPassword;
    }
  }

  resetPassword(event: Event): void {
    event.preventDefault();
    
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

		// Proceed with registration logic
		this.notifyService.showSuccess("Account created Successfully", "Congratulations");
		this.resetForm();
		this.registerCloseModal.nativeElement.click();
	}


	resendOtp(): void {
	  }

	verifyOtp() {
		const enteredOtp = `${this.userReset.otp1}${this.userReset.otp2}${this.userReset.otp3}${this.userReset.otp4}${this.userReset.otp5}${this.userReset.otp6}`;
		if (enteredOtp === this.userReset.otp) {
			this.notifyService.showSuccess("OTP verified successfully", "Success");
		} else {
			this.notifyService.showError("Invalid OTP", "Error");
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