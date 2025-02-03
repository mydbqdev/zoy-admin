import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { AuthService } from 'src/app/common/service/auth.service';
import { DataService } from 'src/app/common/service/data.service';
import { UserService } from 'src/app/common/service/user.service';
import { UserInfo } from 'src/app/common/shared/model/userinfo.service';
import { startWith, map } from 'rxjs/operators';
import { FormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { Menu } from './menu';
import { MenuService } from './menu.service';
import { UserActivityService } from 'src/app/user-activity.service';
import { ProfileService } from 'src/app/profile/service/profile-service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import { AlertNotificationModel } from 'src/app/alert-notification/model/alert-notification-model';
import { WebsocketService } from 'src/app/common/service/websocket.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit,AfterViewInit {
  userNameSession: any;
  mySubscription: any;
  headerName:string='';
  userInfo:UserInfo=new UserInfo();
  isExpandSideBar:boolean=true;
  menus: Menu[] = [];
	searchQuery: string = '';
	filteredMenus: Menu[] = [];
	searchControl = new FormControl();
	filteredOptions: Observable<Menu[]>;
  sessionTime:Date= new Date();
  interval:any = null;
  timeoutId: any = null;
  timeSinceLastAction:any =null;
  lastActionTime: number = 0;
  nun:number=0
  countdown:number=0;
  errorMsg: any = "";
  imgeURL:any="";
  @ViewChild('sessionModelOpen') sessionModelOpen: any;
  @ViewChild('sessionModelClose') sessionModelClose: any;
  @ViewChild('modal', { static: false }) modal: any;
  selectedNotification: AlertNotificationModel = new AlertNotificationModel();
  onFlyNotification: AlertNotificationModel = new AlertNotificationModel();
  constructor( private userService: UserService, private router: Router,private dataService:DataService,private  authService: AuthService,private websocketService:WebsocketService,
    private menuService: MenuService,private userActivityService: UserActivityService,private profileService:ProfileService,private notifyService: NotificationService
    ) {
     
    this.userInfo=this.userService.getUserinfo();
    this.sessionTime = this.userService.getSessionTime();
    this.userNameSession = this.userService.getUsername();
    this.router.routeReuseStrategy.shouldReuseRoute = function () {
      return false;
    };

    this.mySubscription = this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        // Trick the Router into believing it's last link wasn't previously loaded
        this.router.navigated = false;
      }
    });
    this.dataService.getHeaderName.subscribe(name=>{
			this.headerName=name;
    });	
    
    this.dataService.getIsExpandSideBar.subscribe(name=>{
			this.isExpandSideBar=name;
    });
    if(this.userService.getUserinfo()==undefined){
      this.dataService.getUserDetails.subscribe(name=>{
				this.userInfo=name;
			  });
    }
    this.filteredOptions = this.searchControl.valueChanges.pipe(
			startWith(''),
			map(value => this.filter(value))
		  );
  
  }

  ngAfterViewInit(): void {
    
  }
 
  ngOnInit(): void {
    if(this.userNameSession==null || this.userNameSession==undefined || this.userNameSession==''){
     // this.router.navigate(['/']);
      }
      this.menus = this.menuService.getAllMenus();
      	this.filteredMenus = this.menus ;
        this.userActivityService.lastActionTime$.subscribe(time => {
          this.lastActionTime = time;
        });
        this.getTimeSinceLastAction();
        this.startValidateToken();
        this.loadProfilePhoto();
       
        setTimeout(() => {
          this.connectWebsocket();
        }, 5000);
       
  }

  ngOnDestroy() {
    if (this.mySubscription) {
      this.mySubscription.unsubscribe();
    }
  //  this.websocketService.closeConnection('AlertNotification');
  }

  connectWebsocket(){
    this.authService.connectWebsocket(this.userInfo.userEmail,'AlertNotification');
    this.websocketService.getMessages('AlertNotification')?.subscribe((notifi) => {
      console.log("notifi",notifi)
      this.onFlyNotification =  notifi;
    });
    console.log("this.onFlyNotification",this.onFlyNotification);
  }

  doSignout() {
		this.authService.checkLogout();
  }

  searchMenus(event: Event): void {
    event.preventDefault();
		this.filteredOptions.subscribe(options => {
		  this.filteredMenus=options.filter(menu => menu.name.toLowerCase().includes(this.searchControl.value.toLowerCase()));
		  if (this.filteredMenus.length === 1) {
			this.navigateTo(this.filteredMenus[0]);
		  } 
	  });
	  }
	  
	  navigateTo(menu: Menu): void {
		this.router.navigate([menu.link]);
	  }

	  private filter(value: string): Menu[] {
		const filterValue = value.toLowerCase();
		return this.menus.filter(menu => menu.name.toLowerCase().includes(filterValue));
	  }

    getTimeSinceLastAction() {
      this.timeSinceLastAction = setInterval(() => {
        if(!this.userService.getSessionTime()){
          this.clearAllIntervals();
          return;
        }
      const time = this.userActivityService.getTimeSinceLastAction();
      if(time > 780000 && this.nun == 0){
        this.nun=this.nun+1;
        this.countdown = 120;
        this.sessionModelOpen.nativeElement.click(); 
        this.startSessionTimeout();
      }
    }, 1000); 
    }	

 
  startSessionTimeout() {
    console.log(new Date(),"time>>",this.userActivityService.getTimeSinceLastAction());
    console.log(new Date(),"this.nun",this.nun,'<this.interval>',this.interval);
    if (this.interval) {
      return;
    }
    this.interval = setInterval(() => {
      if (this.countdown <= 0) {
        console.log(new Date(),"startSessionTimeout >this.countdown>>",this.countdown);
        this.nun=0;
        this.logout();
      } else {
        console.log(new Date(),"startSessionTimeout >this.countdown>>",this.countdown);
        this.countdown--;
      }
    }, 1000); 
  }

  startValidateToken() {
    this.timeoutId = setInterval(() => {
      this.sessionTime = this.userService.getSessionTime() || new Date();
      const diff =  new Date().getTime() - this.sessionTime.getTime() ;
      if(diff>800000 && !this.userService.isLoggedOut() && this.userService.getSessionTime()){
       this.authService.checkLoginUserVlidaate();
      }
    }, 60000); 
   }
    

  stay() {
    if (this.interval) {
      clearInterval(this.interval);
      this.interval = null;
    }
    this.sessionModelClose.nativeElement.click(); 
    this.nun=0;
    this.countdown = 120;
    this.authService.checkLoginUserVlidaate();
  }

  logout() {
    this.clearAllIntervals();
    this.doSignout();
    this.sessionModelClose.nativeElement.click(); 
    this.nun = 0;
    this.countdown = 120;
  }

  clearAllIntervals() {
    if (this.timeSinceLastAction) {
      clearInterval(this.timeSinceLastAction);
      this.timeSinceLastAction = null; 
    }
    if (this.timeoutId) {
      clearInterval(this.timeoutId);
      this.timeoutId = null; 
    }
    if (this.interval) {
      clearInterval(this.interval);
      this.interval = null; 
    }
  }

  
  loadProfilePhoto(){
    this.profileService.loadProfilePhoto().subscribe((data) => {
      if(data && data.size!=0){ 
        const reader =new FileReader();
        reader.readAsDataURL(new Blob([data]));
        reader.onload=(e)=>this.imgeURL=e.target.result; 
      }else{
        this.imgeURL="";
      }

    }, error => {
			if(error.status == 0) {
				this.notifyService.showError("Internal Server Error/Connection not established", "")
			}else if(error.status==401){
				console.error("Unauthorised");
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
    

  selectNotification(notification: AlertNotificationModel): void {
    this.selectedNotification = notification;
  }
   notifications: AlertNotificationModel[] = [
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
      message: "Spending Alert: We've noticed unusually high spending for your account.Spending Alert: We've noticed unusually high spending for your account.Spending Alert: We've noticed unusually high spending for your account.Spending Alert: We've noticed unusually high spending for your account.Spending Alert: We've noticed unusually high spending for your account.Spending Alert: We've noticed unusually high spending for your account.Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting",
      iconClass: "fas fa-exclamation-triangle",
      bgColor: "bg-warning",
    },
    {
      id: 4,
      title:"Quaterly report",
      date: "December 7, 2019",
      message: "$290.29 has been deposited into your account!",
      iconClass: "fas fa-donate",
      bgColor: "bg-success",
    },
    {
      id: 5,
      title:"Year report",
      date: "December 2, 2019",
      message: "Spending Alert: We've noticed unusually high spending for your account.",
      iconClass: "fas fa-exclamation-triangle",
      bgColor: "bg-warning",
    },
  ];

}
