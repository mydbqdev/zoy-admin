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
	nextStep(arg0: number) {
		throw new Error('Method not implemented.');
	}
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

	constructor(
		private route: ActivatedRoute, private router: Router, private authService: AuthService, private dataService: DataService,
		private forgotPasswordService: ForgotPasswordService, private formBuilder: FormBuilder, private http: HttpClient,
		private userService: UserService, private notifyService: NotificationService, private spinner: NgxSpinnerService,
		private renderer: Renderer2, private confirmationDialogService: ConfirmationDialogService
	) {
		this.userNameSession = userService.getUsername();
		if (userService.getUserinfo() != undefined) {
			this.rolesArray = userService.getUserinfo().previlageList;
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

	isPasswordVisible: boolean = false;

	togglePasswordVisibility() {
		this.isPasswordVisible = !this.isPasswordVisible;
	}

	resetForm() {
		this.submitted = false;
		this.form.reset();
		this.otpSent = false; // Reset the OTP state when resetting the form
	}

	sendOrResendOtp() {
		this.otpSent = true;
		this.notifyService.showSuccess("OTP sent successfully", "OTP Notification");
		// Add logic to call the OTP sending service/API here
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

	verifyOtp() {
		// Concatenate the OTP values and check if they match
		const enteredOtp = `${this.userReset.otp1}${this.userReset.otp2}${this.userReset.otp3}${this.userReset.otp4}${this.userReset.otp5}${this.userReset.otp6}`;

		// You can compare this with the OTP you have sent
		if (enteredOtp === this.userReset.otp) {
			this.notifyService.showSuccess("OTP verified successfully", "Success");
			// Proceed with the next steps (e.g., reset the password or navigate)
		} else {
			this.notifyService.showError("Invalid OTP", "Error");
		}
	}
}
