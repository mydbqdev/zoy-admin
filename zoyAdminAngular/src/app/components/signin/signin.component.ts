import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from 'src/app/common/service/auth.service';
import { User } from 'src/app/common/shared/user';
import { ResponseStore } from 'src/app/common/models/response.model';
import { DataService } from 'src/app/common/service/data.service';
import { SignupDetails } from 'src/app/common/shared/signup-details';


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
	constructor(private route: ActivatedRoute, private router: Router, private authService: AuthService,private dataService :DataService) { }

	ngOnInit() {
		if(JSON.parse(localStorage.getItem('rememberme'))!==null){
			this.username=localStorage.getItem('username');
			this.password=localStorage.getItem('userpwd');
			this.rememberme=JSON.parse(localStorage.getItem('rememberme'));
		}
	}

	doSignin() {
		
		this.submitted=true;    
		if (this.username !== '' && this.username !== null && this.password !== '' && this.password !== null) {
			const user: User = { userEmail: this.username.toLowerCase(), password: this.password };
            this.submitted=false;
			this.authService.getAuthUser(user).subscribe((result) => {
				const res: ResponseStore = { userEmail: this.username.toLowerCase(), token: result.token };
				this.authService.setSessionStore(res);
				this.loadInitialData();
				this.authService.subscribeFcmNotification('unsubscribe').subscribe();
				this.authService.subscribeFcmNotification('subscribe').subscribe();
			//	this.authService.checkLoginUser();
				this.router.navigate(['/home']);
			}, error => {
				this.error = error.error.error;// 'Invalid credentials or something went wrong';
			});
		}// else {
		//	this.error = 'Username or Password is required';
		//}
	}

	loadInitialData(){
	
		this.authService.getUserSignupDetails(this.username.toLowerCase()).subscribe((data) => {
		
			let user:SignupDetails = new SignupDetails() ;
			user.userId= data.id !=undefined?data.id:"";
			user.userEmail= data.userEmail !=undefined?data.userEmail:"";
			user.userFirstName= data.userFirstName !=undefined?data.userFirstName:"";
			user.userLastName= data.userLastName !=undefined?data.userLastName:"";
			user.zipCode= data.zipCode !=undefined?data.zipCode:"";

			this.dataService.setUserDetails(user);

		},error =>{
		  if(error.status==403){
			this.router.navigate(['/forbidden']);
		  }else if (error.error && error.error.message) {
			this.errorMsg =error.error.message;
			console.log("Error:"+this.errorMsg);
	
		  } else {
		
			if(error.status==500 && error.statusText=="Internal Server Error"){
			  this.errorMsg=error.statusText+"! Please login again or contact your Help Desk.";
			}else{
		
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
			if(error.status !== 401 ){//this.notifyService.showError(this.errorMsg, "");
				}
		  }
		 }
		);
	  }

}
