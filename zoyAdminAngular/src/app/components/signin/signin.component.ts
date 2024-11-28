import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from 'src/app/common/service/auth.service';
import { User } from 'src/app/common/shared/user';
import { ResponseStore } from 'src/app/common/models/response.model';
import { DataService } from 'src/app/common/service/data.service';


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
	isPasswordVisible:boolean=false;

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
			this.authService.getAuthUser(user).subscribe((result) => {

				const res: ResponseStore = { userEmail: result.email, token: result.token };
				this.authService.setSessionStore(res);
				this.authService.checkLoginUser();
				this.router.navigate(['/home']);
			}, error => {
				console.log("error>>>",error)
				if(error.status == 0) {
					this.error = "Internal Server Error/Connection not established";
				 }else if(error.status==403){
				   this.router.navigate(['/forbidden']);
				 }else if (error.error && error.error.message) {
				   this.error = error.error.message;
				   console.log("Error:" + this.errorMsg);
				 } else {
					if (error.status == 500 && error.statusText == "Internal Server Error") {
					 this.errorMsg = error.statusText + "! Please login again or contact your Help Desk.";
				   	} else {
					 let str;
					 if (error.status == 400) {
					   str = error.error;
					 } else{
					   str = error.message;
					   str = str.substring(str.indexOf(":") + 1);
					 }
					 this.error = str;
				   }
				 }
			   });
		}
	}

}
