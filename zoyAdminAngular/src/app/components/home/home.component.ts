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
import { DashboardFilterModel, IssuesDetails, OwnersCardModel, PropertiesCardModel, TenantsCardModel, TopRevenuePG, TotalBookingDetailsModel, ZoyQuarterRevenue } from 'src/app/common/models/dashboard.model';
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
	tenantDetails:TenantsCardModel=new TenantsCardModel();
	ownerDetails:OwnersCardModel = new OwnersCardModel();
	propertiesDetails:PropertiesCardModel = new PropertiesCardModel();

	displayedColumns: string[] = ['sl_no','pg_name','revenue'];
	public ELEMENT_DATA:TopRevenuePG[]=[];
	dataSourceTopRevenuePG:MatTableDataSource<TopRevenuePG>=new MatTableDataSource<TopRevenuePG>();
	userInfo:UserInfo=new UserInfo();
	revenueFilterYears :string[]=[];
	zoyQuarterRevenue :ZoyQuarterRevenue=new ZoyQuarterRevenue();
	revenuefy:string="";
	totalRevenueLast7days:number=0;
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
		this.getTenantCardDetails();
		this.getTotalBookings();
		this.getOwnersCardDetails();
		this.getPropertiesCardDetails();
		this.getLastThreeFinancialYears();
		this.getTotalRevenueDetails();
		this.getTotalIssuesDetails();
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

	getTenantCardDetails(){
		this.authService.checkLoginUserVlidaate();
		this.appService.getTenantCardDetails().subscribe((result) => {
			this.tenantDetails=result;
		  },error =>{
			if(error.status == 0) {
				this.notifyService.showError("Internal Server Error/Connection not established", "")
			 }else if(error.status==403){
			  this.router.navigate(['/forbidden']);
			 }else if(error.status==401){
		 	 this.router.navigate(['/signin']);
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
					str=error.error.message;
					str=str.substring(str.indexOf(":")+1);
				  }
				  console.log("Error:",str);
				  this.errorMsg=str;
			  }
			  if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
			}
		  });
	}

	getOwnersCardDetails(){
		this.authService.checkLoginUserVlidaate();
		this.appService.getOwnersCardDetails().subscribe((result) => {
			this.ownerDetails=result;
		  },error =>{
			if(error.status == 0) {
				this.notifyService.showError("Internal Server Error/Connection not established", "")
			 }else if(error.status==403){
			  this.router.navigate(['/forbidden']);
			 }else if(error.status==401){
		 	 this.router.navigate(['/signin']);
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
					str=error.error.message;
					str=str.substring(str.indexOf(":")+1);
				  }
				  console.log("Error:",str);
				  this.errorMsg=str;
			  }
			  if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
			}
		  });
	}

	getPropertiesCardDetails(){
		this.authService.checkLoginUserVlidaate();
		this.appService.getPropertiesCardDetails().subscribe((result) => {
			this.propertiesDetails=result;
		  },error =>{
			if(error.status == 0) {
				this.notifyService.showError("Internal Server Error/Connection not established", "")
			 }else if(error.status==403){
			  this.router.navigate(['/forbidden']);
			 }else if(error.status==401){
		 	 this.router.navigate(['/signin']);
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
					str=error.error.message;
					str=str.substring(str.indexOf(":")+1);
				  }
				  console.log("Error:",str);
				  this.errorMsg=str;
			  }
			  if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
			}
		  });
	}

	totalBookingDetails :TotalBookingDetailsModel= new TotalBookingDetailsModel();
	
	getTotalBookings(){
		this.authService.checkLoginUserVlidaate();
		this.appService.getTotalBookings().subscribe((result) => {
			this.totalBookingDetails=result;
			this.dataService.setDashboardBookingDetails(this.totalBookingDetails);
		  },error =>{
			if(error.status == 0) {
				this.notifyService.showError("Internal Server Error/Connection not established", "")
			 }else if(error.status==403){
			  this.router.navigate(['/forbidden']);
			 }else if(error.status==401){
		  	  this.router.navigate(['/signin']);
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
					str=error.error.message;
					str=str.substring(str.indexOf(":")+1);
				  }
				  console.log("Error:",str);
				  this.errorMsg=str;
			  }
			  if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
			}
		  });
	}

	 getLastThreeFinancialYears() {
		const today = new Date();
		const currentYear = today.getFullYear();
		const currentMonth = today.getMonth(); 
		let startYear = currentMonth >= 3 ? currentYear : currentYear - 1;
	
		const financialYears: string[] = [];
	
		for (let i = 0; i < 3; i++) {
			const endYear = startYear + 1;
			financialYears.push(startYear +"-"+endYear);
			startYear--; 
		}
		this.revenueFilterYears=financialYears
		this.revenuefy=financialYears[0];
		this.getRevenueCardDetails();
	}
	
	getRevenueCardDetails(){
		this.authService.checkLoginUserVlidaate();
		this.appService.getRevenueCardDetails(this.revenuefy).subscribe((result) => {
			this.zoyQuarterRevenue = result.data;			 
		  },error =>{
			if(error.status == 0) {
				this.notifyService.showError("Internal Server Error/Connection not established", "")
			 }else if(error.status==403){
			  this.router.navigate(['/forbidden']);
			 }else if(error.status==401){
		 	 this.router.navigate(['/signin']);
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
					str=error.error.message;
					str=str.substring(str.indexOf(":")+1);
				  }
				  console.log("Error:",str);
				  this.errorMsg=str;
			  }
			  if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
			}
		  });
	}
	
	getTotalRevenueDetails(){
	this.appService.getTotalRevenueDetails().subscribe((result) => {
			this.totalRevenueLast7days = result.data?.reduce((acc, item) =>acc + (isNaN(item.revenueInThousands) ? 0 : item.revenueInThousands), 0);
			this.dataService.setTotalRevenueDetails(result.data);
		  },error =>{
			if(error.status == 0) {
				this.notifyService.showError("Internal Server Error/Connection not established", "")
			 }else if(error.status==403){
			  this.router.navigate(['/forbidden']);
			 }else if(error.status==401){
		  	  this.router.navigate(['/signin']);
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
					str=error.error.message;
					str=str.substring(str.indexOf(":")+1);
				  }
				  console.log("Error:",str);
				  this.errorMsg=str;
			  }
			  if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
			}
		  });
	}

	issuesDetails:IssuesDetails=new IssuesDetails();
	progress(issues): number {
		const total = Number(this.issuesDetails.total_issues);
		const  issuesCount = Number(issues)?Number(issues):0;
		return total > 0 ? Math.round((issuesCount / total) * 100) : 0;
	  }

	getTotalIssuesDetails(){
		this.appService.getTotalIssuesDetails().subscribe((result) => {
			this.issuesDetails = result;
		  },error =>{
			if(error.status == 0) {
				this.notifyService.showError("Internal Server Error/Connection not established", "")
			 }else if(error.status==403){
			  this.router.navigate(['/forbidden']);
			 }else if(error.status==401){
				this.router.navigate(['/signin']);
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
					str=error.error.message;
					str=str.substring(str.indexOf(":")+1);
				  }
				  console.log("Error:",str);
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

 