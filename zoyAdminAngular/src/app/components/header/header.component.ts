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
  interval:any;
  timeoutId: any;
  lastActionTime: number = 0;
  nun:number=0
  countdown:number=0;
  @ViewChild('sessionModelOpen') sessionModelOpen: any;
  @ViewChild('sessionModelClose') sessionModelClose: any;
  constructor( private userService: UserService, private router: Router,private dataService:DataService,private  authService: AuthService,
    private menuService: MenuService,private userActivityService: UserActivityService,
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
        setInterval(() => {
          const timeSinceLastAction = this.userActivityService.getTimeSinceLastAction();
          if (timeSinceLastAction > 5 * 60 * 1000) {
          }
        }, 1000); 

        this.startValidateToken();
  }
  ngOnDestroy() {
    if (this.mySubscription) {
      this.mySubscription.unsubscribe();
    }
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

    getTimeSinceLastAction(): number {
      const time = this.userActivityService.getTimeSinceLastAction();
      if(time > 720000 && this.nun == 0){
        this.nun=this.nun+1;
        this.countdown = 120;
        this.sessionModelOpen.nativeElement.click();   
        this.startSessionTimeout();
      }
      return time;
    }	

  
  startSessionTimeout() {
    this.interval = setInterval(() => {
      if (this.countdown <= 0) {
        this.nun=0;
        this.logout();
      } else {
        this.countdown--;
      }
    }, 1000); 
  }

    startValidateToken() {
        this.timeoutId = setTimeout(() => {
          this.sessionTime = this.userService.getSessionTime();
          const diff =  new Date().getTime() - this.sessionTime.getTime();
          if(diff>800000){
           this.authService.checkLoginUserVlidaate();
          }
          this.startValidateToken();
        }, 60000); 
      }

  stay() {
    this.nun=0;
    this.countdown = 120;
    clearInterval(this.interval); 
    this.sessionModelClose.nativeElement.click(); 
    this.authService.checkLoginUserVlidaate();
  }

  logout() {
    this.nun = 0;
    this.countdown = 120;
    clearTimeout(this.timeoutId);  
    clearInterval(this.interval); 
    this.sessionModelClose.nativeElement.click(); 
    this.doSignout();
  }

    

}
