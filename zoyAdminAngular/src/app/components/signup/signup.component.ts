import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from 'src/app/common/service/auth.service';
import { SignupDetails } from 'src/app/common/shared/signup-details';
import { NgxSpinnerService } from 'ngx-spinner';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
@Component({
	selector: 'app-signup',
	templateUrl: './signup.component.html',
	styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {

	firstname: string = '';
	email: string = '';
	lastname: string = '';
	zipcode: string = '';
	password: string = '';
	submitted=false;
	error: string = '';
	errorMsg: any;
	user :SignupDetails=new SignupDetails();
	constructor(private route: ActivatedRoute, private router: Router, private authService: AuthService,private spinner:NgxSpinnerService,private notifyService:NotificationService) { }
	ngOnInit() {}

	doSignup() {
		this.error="";
		this.submitted=true;	
		if (this.email !== '' && this.email !== null && this.password !== '' && this.password !== null &&
			this.firstname !== '' && this.firstname !== null && this.lastname !== '' && this.lastname !== null
			&& this.zipcode !== '' && this.zipcode !== null )
			 {  
				this.spinner.show();		     
		this.submitted=false;
		this.user.userFirstName=this.firstname;
		this.user.userLastName=this.lastname;
		this.user.userEmail=this.email.toLowerCase();
		this.user.password=this.password;
		this.user.zipCode=this.zipcode;
		this.authService.signupUser(this.user).subscribe((data) => {
		this.notifyService.showSuccess("your account has been succssfully created.", "Congratulations,");
		this.spinner.hide();
		this.router.navigate(['/signin']);
		},error =>{
			this.spinner.hide();
			console.log("error.error",error)
			this.error = (error.error.error !=undefined?(error.error.error  +"."):"")
			+ (error.error.userEmail!=undefined?(error.error.userEmail+"."):"")
			+(error.error.password!=undefined?(error.error.password  +"."):"");
		  if(error.status==403){
			this.router.navigate(['/forbidden']);
		  }else if (error.error && error.error.message) {
			this.errorMsg =error.error.message;
			console.log("Error:"+this.errorMsg);
			// this.notifyService.showError(this.errorMsg, "");
			// this.spinner.hide();
		  } else {
			//this.spinner.hide();
			if(error.status==500 && error.statusText=="Internal Server Error"){
			  this.errorMsg=error.statusText+"! Please login again or contact your Help Desk.";
			}else{
			//  this.spinner.hide();
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

}
