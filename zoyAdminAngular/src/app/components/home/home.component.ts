import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { NavigationEnd } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { UserService } from 'src/app/common/service/user.service';
import { AuthService } from 'src/app/common/service/auth.service';
import { DataService } from 'src/app/common/service/data.service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { AppService } from 'src/app/common/service/application.service';
import { DashboardCardModel } from 'src/app/common/models/dashboard.model';

@Component({
	selector: 'app-home',
	templateUrl: './home.component.html',
	styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, AfterViewInit {
	public userNameSession: string = "";
	errorMsg: any = "";
	mySubscription: any;
	isExpandSideBar:boolean=true;
	@ViewChild(SidebarComponent) sidemenuComp;
	public rolesArray: string[] = [];
	dashboardCardModel:DashboardCardModel=new DashboardCardModel();
	constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,
		private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService,private appService:AppService) {
		this.userNameSession = userService.getUsername();
		//this.defHomeMenu=defMenuEnable;
		if (userService.getUserinfo() != undefined) {
			this.rolesArray = userService.getUserinfo().privilege;
		}else{
			this.dataService.getUserDetails.subscribe(name=>{
				if(name?.privilege){
				this.rolesArray =Object.assign([],name.privilege);
				}
			  });
		}
		this.router.routeReuseStrategy.shouldReuseRoute = function () {
			return false;
		};

		this.mySubscription = this.router.events.subscribe((event) => {
			if (event instanceof NavigationEnd) {
				// Trick the Router into believing it's last link wasn't previously loaded
				this.router.navigated = false;
			}
		});
		this.dataService.getIsExpandSideBar.subscribe(name=>{
			this.isExpandSideBar=name;
		});
	}

	ngOnDestroy() {
		if (this.mySubscription) {
			this.mySubscription.unsubscribe();
		}
	}
	ngOnInit() {
		if (this.userNameSession == null || this.userNameSession == undefined || this.userNameSession == '') {
			this.router.navigate(['/']);
		}
		this.getDashboardCard();
	}
	ngAfterViewInit() {
		this.sidemenuComp.expandMenu(1);
		this.sidemenuComp.activeMenu(1, 'home');
		this.dataService.setHeaderName("Dashboard");
	}

	test(){
		this.notifyService.showNotification("Success","");
	}

	getDashboardCard(){
		this.authService.checkLoginUserVlidaate();
		this.appService.getDashboardCard().subscribe((result) => {
			this.dashboardCardModel=result;
		  },error =>{
			if(error.status == 0) {
				this.notifyService.showError("Internal Server Error/Connection not established", "")
			 }else if(error.status==403){
			  this.router.navigate(['/forbidden']);
			}else if (error.error && error.error.message) {
			  this.errorMsg =error.error.message;
			  console.log("Error:"+this.errorMsg);
			  this.notifyService.showError(this.errorMsg, "");
			} else {
			  if(error.status==500 && error.statusText=="Internal Server Error"){
				this.errorMsg=error.statusText+"! Please login again or contact your Help Desk.";
			  }else{
				let str;
				  if(error.status==400){
				  str=error.error.error;
				  }else{
					str=error.message;
					str=str.substring(str.indexOf(":")+1);
				  }
				  console.log("Error:"+str);
				  this.errorMsg=str;
			  }
			  if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
			}
		  });
		  }
	
}
