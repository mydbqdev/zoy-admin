import { AfterViewInit, Component, OnInit } from '@angular/core';
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
  constructor( private userService: UserService, private router: Router,private dataService:DataService,private  authService: AuthService,private menuService: MenuService,) {
   this.userInfo=this.userService.getUserinfo();
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

}
