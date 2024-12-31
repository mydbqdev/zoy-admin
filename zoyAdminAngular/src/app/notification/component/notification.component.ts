import { Component,OnInit, ViewChild } from '@angular/core';
import { Router, ActivatedRoute, NavigationEnd } from '@angular/router';
import { AuthService } from 'src/app/common/service/auth.service';
import { NgxSpinnerService } from 'ngx-spinner';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import { FormBuilder } from '@angular/forms';
import { AESEncryptDecryptHelper } from 'src/app/common/shared/AESEncryptDecryptHelper';
import { SidebarComponent } from '../../components/sidebar/sidebar.component';
import { DataService } from '../../common/service/data.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from 'src/app/common/service/user.service';
import { MenuService } from 'src/app/components/header/menu.service';
import { ProfileService } from 'src/app/profile/service/profile-service';
import { UserInfo } from 'src/app/common/shared/model/userinfo.service';
import { RoleScreenPrv } from 'src/app/setting/role-master/models/role-screen-model';
import { NotificationModel } from '../model/notification-model';

@Component({
	selector: 'app-notification',
	templateUrl: './notification.component.html',
	styleUrls: ['./notification.component.css']
})
export class NotificationComponent implements OnInit {
	public userNameSession: string = "";
	errorMsg: any = "";
	mySubscription: any;
	isExpandSideBar:boolean=true;
	@ViewChild(SidebarComponent) sidemenuComp;
	@ViewChild('modal', { static: false }) modal: any;
	public rolesArray: string[] = [];
	userInfo:UserInfo=new UserInfo();
	userRolePermissions: RoleScreenPrv[] = [];
	authorization:{ key: string; screen: string; order: number }[]=[];
	selectedNotification: NotificationModel = new NotificationModel();
	constructor(private encryptDecryptHelper:AESEncryptDecryptHelper,private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,private profileService:ProfileService,
		private spinner: NgxSpinnerService,private formBuilder: FormBuilder, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService,private menuService:MenuService) {
		this.authService.checkLoginUserVlidaate();
			this.userInfo=this.userService.getUserinfo();
			
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

		if(this.userService.getUserinfo()==undefined){
			this.dataService.getUserDetails.subscribe(name=>{
					  this.userInfo=name;
					});
		  }
		  this.authorization=this.menuService.getAllAuthorization();
	}
	ngOnDestroy() {
		if (this.mySubscription) {
			this.mySubscription.unsubscribe();
		}
	}

	ngOnInit() {
		
	}
	ngAfterViewInit() {
		this.sidemenuComp.expandMenu(0);
		this.sidemenuComp.activeMenu(0, '');
		this.dataService.setHeaderName("Notification");
	}
	selectNotification(notification: NotificationModel): void {
	  this.selectedNotification = notification;
	}
	notifications: NotificationModel[] = [
		{
		  id: 1,
		  title:"Mothly report",
		  date: "December 12, 2019",
		  message: "A new monthly report is ready to download!",
		  iconClass: "fas fa-file-alt",
		  bgColor: "bg-primary",
		},
		{
		  id: 2,
		  title:"Mothly report",
		  date: "December 7, 2019",
		  message: "$290.29 has been deposited into your account!",
		  iconClass: "fas fa-donate",
		  bgColor: "bg-success",
		},
		{
		  id: 3,
		  title:"Mothly report",
		  date: "December 2, 2019",
		  message: "Spending Alert: We've noticed unusually high spending for your account. Spending Alert: We've noticed unusually  high spending for your account.Spending Alert: We've noticed unusually high spending for your account",
		  iconClass: "fas fa-exclamation-triangle",
		  bgColor: "bg-warning",
		},
		{
		  id: 4,
		  title:"Mothly report",
		  date: "December 7, 2019",
		  message: "$290.29 has been deposited into your account!",
		  iconClass: "fas fa-donate",
		  bgColor: "bg-success",
		},
		{
		  id: 5,
		  title:"Yearly report",
		  date: "December 2, 2019",
		  message: "Spending Alert: We've noticed unusually high spending for your account.",
		  iconClass: "fas fa-exclamation-triangle",
		  bgColor: "bg-warning",
		},
	  ];
	
	}
	