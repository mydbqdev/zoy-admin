import { AfterViewInit, Component, Inject, OnInit, ViewChild } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { DataService } from 'src/app/common/service/data.service';
import { UserService } from 'src/app/common/service/user.service';
import { DefMenu } from 'src/app/common/shared/def-menu';
import { defMenuEnable } from 'src/app/common/shared/variables';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit,AfterViewInit {
  userNameSession: any;
  mySubscription: any;
  isExpandSideBar:boolean=true;
  public rolesArray: string[] = [];
  public menu1: boolean = true;
	public menu2: boolean = false;
	public menu3: boolean = false;
	public menu4: boolean = false;
	public menu5: boolean = false;
	public menu6: boolean = false;
	public menu7: boolean = false;
	public menu8: boolean = false;
	public menu9: boolean = false;
	public menu12: boolean = false;
	public menu13: boolean = false;

	public menu11: boolean = true;
	public menu21: boolean = false;
	public menu31: boolean = false;
	public menu41: boolean = false;
	public menu51: boolean = false;
	public menu61: boolean = false;
	public menu71: boolean = false;
	public menu81: boolean = false;
	public menu91: boolean = false;
	public menu121: boolean = false;
	public menu131: boolean = false;

	public activeSubNenuName: string = '';	
	public defMenu: DefMenu;
	
  constructor(@Inject(defMenuEnable) private defMenuEnable: DefMenu, private userService: UserService, private router: Router,private dataService:DataService) {
    this.userNameSession = userService.getUsername();
	this.defMenu=defMenuEnable;
	if (this.userService.getUserinfo() != undefined) {
		this.rolesArray = this.userService.getUserinfo().privilege;
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

  setHeaderName(headerName:string){
  this.dataService.setHeaderName(headerName);
  }

  collaspeExpand(){
    this.dataService.setIsExpandSideBar(this.isExpandSideBar ? false:true);
  }
  expandMenu(activeMenuKey: number): void {
	const allMenuKeys = ['menu1', 'menu2', 'menu3', 'menu4', 'menu5', 'menu6', 'menu7', 'menu8', 'menu9', 'menu12', 'menu13'];
	const menu= 'menu'+activeMenuKey;
	const value =(this as any)[menu];
	allMenuKeys.forEach((key) => {
		(this as any)[key] = false;
	});
	
	(this as any)[menu] = !value;
}

activeMenu(id: number, menuName: string): void {
	const menuKeys = [
		'menu11', 'menu21', 'menu31', 'menu41', 
		'menu51', 'menu61', 'menu71', 'menu81', 'menu91',
		'menu121', 'menu131'
	];
    const menu= 'menu'+id+1;
    const value =(this as any)[menu];
	menuKeys.forEach((key) => {
		(this as any)[key] = false;
	});
	(this as any)[menu] = !value;
	this.activeSubNenuName = menuName;
 }


	setOwnerResetId(){
		this.dataService.setOwenerId("");
		this.dataService.setTenantId("");
		this.dataService.setOwenerListFilter([]);
		this.dataService.setOwenerListFilterTotal(0);
		this.dataService.setOwenerListFilterParam(null);
	}
}
