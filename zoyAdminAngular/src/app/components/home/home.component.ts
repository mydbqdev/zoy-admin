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
import { DashboardCardModel, TopRevenuePG } from 'src/app/common/models/dashboard.model';
import { MatTableDataSource } from '@angular/material/table';
import { UserInfo } from 'src/app/common/shared/model/userinfo.service';

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

	displayedColumns: string[] = ['sl_no','pg_name','revenue'];
	public ELEMENT_DATA:TopRevenuePG[]=[];
	dataSourceTopRevenuePG:MatTableDataSource<TopRevenuePG>=new MatTableDataSource<TopRevenuePG>();
	userInfo:UserInfo=new UserInfo();
	constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,
		private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService,private appService:AppService) {
		this.userNameSession = userService.getUsername();
		//this.defHomeMenu=defMenuEnable;
		if (userService.getUserinfo() != undefined) {
			this.userInfo=userService.getUserinfo();
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
		this.ELEMENT_DATA = Object.assign([],mockData);
	    this.dataSourceTopRevenuePG =new MatTableDataSource(this.ELEMENT_DATA);
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


const mockData = [
	{
        sl_no: "01",
        pg_name: "John Doe PG",
        revenue: "+15%"
	},
	{
        sl_no: "02",
        pg_name: "KK Toy PG",
        revenue: "+12%"
	},
	{
        sl_no: "03",
        pg_name: "KK Tog PG",
        revenue: "+10%"
    },
    {
        sl_no: "04",
        pg_name: "OYO Toy PG",
        revenue: "+08%"
    },
    {
        sl_no: "05",
        pg_name: "YOMU Toy PG",
        revenue: "-02%"
	},
  ];