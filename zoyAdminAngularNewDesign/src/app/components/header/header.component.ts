import { AfterViewInit, Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { AuthService } from 'src/app/common/service/auth.service';
import { DataService } from 'src/app/common/service/data.service';
import { UserService } from 'src/app/common/service/user.service';
import { UserInfo } from 'src/app/common/shared/model/userinfo.service';

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
  constructor( private userService: UserService, private router: Router,private dataService:DataService,private  authService: AuthService,) {
   this.userInfo=this.userService.getUserinfo();
   console.log("this.userInfo:"+this.userInfo);
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
  }
  ngAfterViewInit(): void {
    
  }
  ngOnInit(): void {
    if(this.userNameSession==null || this.userNameSession==undefined || this.userNameSession==''){
     // this.router.navigate(['/']);
      }
  }
  ngOnDestroy() {
    if (this.mySubscription) {
      this.mySubscription.unsubscribe();
    }
  }

  doSignout() {
		this.authService.checkLogout();
  }

}
