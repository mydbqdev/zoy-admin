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
  lastActionTime: number = 0;
   nun=0
   countdown=0;
   
   getTimeSinceLastAction(): number {
   // console.log("this.sessionTime ",this.sessionTime );
    const time = this.userActivityService.getTimeSinceLastAction();
    // console.log("time",time ,"----",this.nun)
    if(time >30000 && this.nun ==0){
      this.nun=this.nun+1;
      this.countdown = 30;
      this.showModal();
      this.startSessionTimeout();
    }
    return time;
  }

  

  ngOnInit(): void {
    if(this.userNameSession==null || this.userNameSession==undefined || this.userNameSession==''){
     // this.router.navigate(['/']);
      }
     // this.resetTimeOut();
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

  
  startSessionTimeout() {
   // console.log("this.countdown",this.countdown)
    const interval = setInterval(() => {
      if (this.countdown <= 0) {
        this.nun=0;
        clearInterval(interval); 
        this.logout();
      } else {
        this.countdown--;
      }
    }, 1000); 
  }


  timeoutId: any;
   
      startValidateToken() {
    //    console.log("this.setTimeouttimeoutId",this.timeoutId);
    this.sessionTime = this.userService.getSessionTime();
    const diff =  new Date().getTime() - this.sessionTime.getTime();
    console.log(new Date(),",>>>",this.sessionTime,"this.startValidateToken",diff);
        this.timeoutId = setTimeout(() => {
          this.sessionTime = this.userService.getSessionTime();
          const diff =  new Date().getTime() - this.sessionTime.getTime();
          if(diff>50000){
            this.stay();
          }
          this.startValidateToken();
        }, 10000); 
      }
      stopValidateToken() {
        console.log("this.timeoutId",this.timeoutId)
        if (this.timeoutId) {
          clearTimeout(this.timeoutId);
        }
      }


  @ViewChild('sessionModelOpen') sessionModelOpen: any;
  @ViewChild('sessionModelClose') sessionModelClose: any;

  showModal() {
    this.sessionModelOpen.nativeElement.click();    
  }

  stay() {
    this.nun=0;
    console.log('User chose to stay logged in');
    this.sessionModelClose.nativeElement.click(); 
   this.authService.checkLoginUserVlidaate();
  }

  logout() {
    this.nun=0;
    console.log('User logged out');
    this.sessionModelClose.nativeElement.click(); 
    this.stopValidateToken();
    clearTimeout(this.timeoutId);  
    this.doSignout();
  }

    

}
