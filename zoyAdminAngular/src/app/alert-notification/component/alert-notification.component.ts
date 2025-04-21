import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { NavigationEnd } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { UserService } from 'src/app/common/service/user.service';
import { AuthService } from 'src/app/common/service/auth.service';
import { DataService } from 'src/app/common/service/data.service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import { SidebarComponent } from 'src/app/components/sidebar/sidebar.component';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort, Sort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { AlertNotificationDetailsModel} from '../model/alert-notification-model';
import { AlertNotificationService } from '../service/alert-notification-service';
import { Menu } from 'src/app/components/header/menu';
import { MenuService } from 'src/app/components/header/menu.service';
import { FiltersRequestModel } from 'src/app/report/model/report-filters-model';

@Component({
	selector: 'app-alert-notification',
	templateUrl: './alert-notification.component.html',
	styleUrls: ['./alert-notification.component.css']
})

export class AlertNotificationComponent implements OnInit, AfterViewInit {
	public userNameSession: string = "";
	errorMsg: any = "";
	mySubscription: any;
	isExpandSideBar:boolean=true;
	dataSource:MatTableDataSource<any>=new MatTableDataSource<any>();
	@ViewChild(SidebarComponent) sidemenuComp;
	@ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;
	@ViewChild('modal', { static: false }) modal: any;
	selectedNotification: AlertNotificationDetailsModel = new AlertNotificationDetailsModel();
	public lastPageSize:number=0;
	public totalProduct:number=0;
	pageSize:number=5;
	pageSizeOptions=[5,10,25,50];
	public rolesArray: string[] = [];
	allMenuList:Menu[]=[];
	constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,private alertNotificationService:AlertNotificationService,
		private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService,private menuService:MenuService) {
            this.authService.checkLoginUserVlidaate();
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
		this.allMenuList = this.menuService.getAllMenuList();
		//if (this.userNameSession == null || this.userNameSession == undefined || this.userNameSession == '') {
		///	this.router.navigate(['/']);
		//}
		
	}
	ngAfterViewInit() {
		this.sidemenuComp.expandMenu(0);
		this.sidemenuComp.activeMenu(0, 'alert-notification');
		this.dataService.setHeaderName("Alert & Notification");

		this.paginator.pageIndex=0;
		this.pageSize = this.paginator.pageSize;
		this.filtersRequest.pageIndex=this.paginator.pageIndex;
		this.filtersRequest.pageSize=this.pageSize;
		this.getUserNotifications();
	}

	pageChanged(event: any) {
		if (this.lastPageSize !== event.pageSize) {
		  this.paginator.pageIndex = 0;
		  event.pageIndex = 0;
		}
		this.pageSize = event.pageSize;
		this.lastPageSize = this.pageSize;
		this.filtersRequest.pageIndex=this.paginator.pageIndex;
		this.filtersRequest.pageSize=this.pageSize;
		this.getUserNotifications();
	  }

	  
	  getIcon(screen_name:string):string{
		return this.allMenuList.find(menu=>menu.permission.replace('_READ','') === screen_name)?.icon || 'fas fa-exclamation-triangle'
	  }
		  
	   selectNotification(notification: AlertNotificationDetailsModel,i:number): void {
		this.selectedNotification = notification;
		this.updateUserNotificationsSeen();
		this.notifications[i].is_seen = 'true';
		this.dataSource.data =this.notifications
	  }
	  filtersRequest :FiltersRequestModel = new FiltersRequestModel();
	  notifications: AlertNotificationDetailsModel[]=[] ;
		getUserNotifications(){
		this.lastPageSize = this.pageSize;
		this.filtersRequest.isAlert=true;
		this.spinner.show();
		  this.alertNotificationService.getUserNotifications(this.filtersRequest).subscribe((data) => {
			if(data?.notifications?.length >0){
				this.totalProduct = data?.totalCount; 
				this.notifications = Object.assign(data?.notifications);
				this.dataSource.data =this.notifications// new MatTableDataSource(this.notifications);
			}else{
				this.totalProduct=0;
				this.notifications = Object.assign([]);
				this.dataSource.data = this.notifications//new MatTableDataSource(this.notifications);
			}
			this.spinner.hide();
		  }, error => {
			this.spinner.hide();
				if(error.status == 0) {
					this.notifyService.showError("Internal Server Error/Connection not established", "")
				}else if(error.status==401){
					console.error("Unauthorised");
			  this.router.navigate(['/signin']);
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
						str = error.error.error;
					} else {
						str = error.error.message;
						str = str.substring(str.indexOf(":") + 1);
					}
					console.log("Error:" , str);
					this.errorMsg = str;
					}
					if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
				}
				});
		}
	  
		updateUserNotificationsSeen(){

		  this.alertNotificationService.updateUserNotificationsSeen(this.selectedNotification).subscribe((data) => {
			
	  
		  }, error => {
				if(error.status == 0) {
					this.notifyService.showError("Internal Server Error/Connection not established", "")
				}else if(error.status==401){
					console.error("Unauthorised");
			  this.router.navigate(['/signin']);
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
						str = error.error.error;
					} else {
						str = error.error.message;
						str = str.substring(str.indexOf(":") + 1);
					}
					console.log("Error:" , str);
					this.errorMsg = str;
					}
					if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
				}
				});
		}

	  }

