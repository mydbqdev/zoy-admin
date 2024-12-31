import { AfterViewInit, Component, OnInit, ViewChild, inject } from '@angular/core';
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
import { AlertNotificationModel } from '../model/alert-notification-model';

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
	
	selectedNotification: AlertNotificationModel = new AlertNotificationModel();
	public lastPageSize:number=0;
	public totalProduct:number=0;
	pageSize:number=10;
	pageSizeOptions=[10,20,50];
	public rolesArray: string[] = [];
	constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,
		private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService) {
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
		//if (this.userNameSession == null || this.userNameSession == undefined || this.userNameSession == '') {
		///	this.router.navigate(['/']);
		//}
		
	}
	ngAfterViewInit() {
		this.sidemenuComp.expandMenu(0);
		this.sidemenuComp.activeMenu(0, 'alert-notification');
		this.dataService.setHeaderName("Alert & Notification");
		
		this.totalProduct = this.notifications.length; 
		this.pageSize = this.pageSizeOptions[0]; 
		this.updatePageData();
	}

	pageChanged(event: any) {
		if (this.lastPageSize !== event.pageSize) {
		  this.paginator.pageIndex = 0;
		  event.pageIndex = 0;
		}
		this.pageSize = event.pageSize;
		this.updatePageData(); 
	  }
	  updatePageData() {
		this.lastPageSize = this.pageSize;
		const startIndex = this.paginator.pageIndex * this.pageSize;
		const endIndex = startIndex + this.pageSize;
		this.dataSource.data = this.notifications.slice(startIndex, endIndex); 
	  }
		  
	   selectNotification(notification: AlertNotificationModel): void {
		this.selectedNotification = notification;
	  }
	  notifications: AlertNotificationModel[] = [
		  {
			id: 1,
			title:"Mothly report",
			date: "July 12, 2021",
			message: "A new monthly report is ready to download!",
			iconClass: "fas fa-file-alt",
			bgColor: "bg-primary",
		  },
		  {
			id: 2,
			title:"2 Years report",
			date: "December 7, 2012",
			message: "$290.29 has been deposited into your account!",
			iconClass: "fas fa-donate",
			bgColor: "bg-success",
		  },
		  {
			id: 3,
			title:"Mothly report",
			date: "October 2, 2019",
			message: "Spending Alert: We've noticed unusually high spending for your account. Spending Alert: We've noticed unusually high spending for your account.",
			iconClass: "fas fa-exclamation-triangle",
			bgColor: "bg-warning",
		  },
		  {
			id: 4,
			title:"Mothly report",
			date: "May 7, 2019",
			message: "$290.29 has been deposited into your account!",
			iconClass: "fas fa-donate",
			bgColor: "bg-success",
		  },
		  {
			id: 5,
			title:"Yearly report",
			date: "December 31, 2024",
			message: "Spending Alert: We've noticed unusually high spending for your account.",
			iconClass: "fas fa-exclamation-triangle",
			bgColor: "bg-warning",
		  },
		  {
			id: 6,
			title:"Mothly report",
			date: "May 7, 2019",
			message: "$290.29 has been deposited into your account!",
			iconClass: "fas fa-donate",
			bgColor: "bg-success",
		  },
		  {
			id: 7,
			title:"Yearly report",
			date: "December 31, 2024",
			message: "Spending Alert: We've noticed unusually high spending for your account.",
			iconClass: "fas fa-exclamation-triangle",
			bgColor: "bg-warning",
		  },
		  {
			id: 8,
			title:"Mothly report",
			date: "May 7, 2019",
			message: "$290.29 has been deposited into your account!",
			iconClass: "fas fa-donate",
			bgColor: "bg-success",
		  },
		  {
			id: 9,
			title:"Yearly report",
			date: "December 31, 2024",
			message: "Spending Alert: We've noticed unusually high spending for your account.",
			iconClass: "fas fa-exclamation-triangle",
			bgColor: "bg-warning",
		  },
		  {
			id: 10,
			title:"Mothly report",
			date: "May 7, 2019",
			message: "$290.29 has been deposited into your account!",
			iconClass: "fas fa-donate",
			bgColor: "bg-success",
		  },
		  {
			id: 11,
			title:"Yearly report",
			date: "December 31, 2024",
			message: "Spending Alert: We've noticed unusually high spending for your account.",
			iconClass: "fas fa-exclamation-triangle",
			bgColor: "bg-warning",
		  },
		  {
			id: 12,
			title:"Mothly report",
			date: "July 12, 2021",
			message: "A new monthly report is ready to download!",
			iconClass: "fas fa-file-alt",
			bgColor: "bg-primary",
		  },
		  {
			id: 13,
			title:"2 Years report",
			date: "December 7, 2012",
			message: "$290.29 has been deposited into your account!",
			iconClass: "fas fa-donate",
			bgColor: "bg-success",
		  },
		  {
			id: 14,
			title:"Mothly report",
			date: "October 2, 2019",
			message: "Spending Alert: We've noticed unusually high spending for your account. Spending Alert: We've noticed unusually high spending for your account.",
			iconClass: "fas fa-exclamation-triangle",
			bgColor: "bg-warning",
		  },
		  {
			id: 15,
			title:"Mothly report",
			date: "May 7, 2019",
			message: "$290.29 has been deposited into your account!",
			iconClass: "fas fa-donate",
			bgColor: "bg-success",
		  },
		  {
			id: 16,
			title:"Yearly report",
			date: "December 31, 2024",
			message: "Spending Alert: We've noticed unusually high spending for your account.",
			iconClass: "fas fa-exclamation-triangle",
			bgColor: "bg-warning",
		  },
		  {
			id: 17,
			title:"Mothly report",
			date: "May 7, 2019",
			message: "$290.29 has been deposited into your account!",
			iconClass: "fas fa-donate",
			bgColor: "bg-success",
		  },
		  {
			id: 18,
			title:"Yearly report",
			date: "December 31, 2024",
			message: "Spending Alert: We've noticed unusually high spending for your account.",
			iconClass: "fas fa-exclamation-triangle",
			bgColor: "bg-warning",
		  },
		  {
			id: 19,
			title:"Mothly report",
			date: "May 7, 2019",
			message: "$290.29 has been deposited into your account!",
			iconClass: "fas fa-donate",
			bgColor: "bg-success",
		  },
		  {
			id: 20,
			title:"Yearly report",
			date: "December 31, 2024",
			message: "Spending Alert: We've noticed unusually high spending for your account.",
			iconClass: "fas fa-exclamation-triangle",
			bgColor: "bg-warning",
		  },
		  {
			id: 21,
			title:"Mothly report",
			date: "May 7, 2019",
			message: "$290.29 has been deposited into your account!",
			iconClass: "fas fa-donate",
			bgColor: "bg-success",
		  },
		  {
			id: 22,
			title:"Yearly report",
			date: "December 31, 2024",
			message: "Spending Alert: We've noticed unusually high spending for your account.",
			iconClass: "fas fa-exclamation-triangle",
			bgColor: "bg-warning",
		  },
		];
	  
	  }

