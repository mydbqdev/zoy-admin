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

	public menu11: boolean = true;
	public menu21: boolean = false;
	public menu31: boolean = false;
	public menu41: boolean = false;
	public menu51: boolean = false;
	public menu61: boolean = false;
	public menu71: boolean = false;

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

  expandMenu(id: number): void {
		switch (id) {
			case 1: {
				if (!this.menu1) {
					this.menu1 = true;
					this.menu2 = false;
					this.menu3 = false;
					this.menu4 = false;
					this.menu5 = false;
					this.menu6 = false;
					this.menu7 = false;
				} else {
					this.menu1 = false;
					this.menu2 = false;
					this.menu3 = false;
					this.menu4 = false;
					this.menu5 = false;
					this.menu6 = false;
					this.menu7 = false;
				}
				break;
			}
			case 2: {
				if (!this.menu2) {
					this.menu1 = false;
					this.menu2 = true;
					this.menu3 = false;
					this.menu4 = false;
					this.menu5 = false;
					this.menu6 = false;
					this.menu7 = false;
				} else {
					this.menu1 = false;
					this.menu2 = false;
					this.menu3 = false;
					this.menu4 = false;
					this.menu5 = false;
					this.menu6 = false;
					this.menu7 = false;
				}
				break;
			}
			case 3: {
				if (!this.menu3) {
					this.menu1 = false;
					this.menu2 = false;
					this.menu3 = true;
					this.menu4 = false;
					this.menu5 = false;
					this.menu6 = false;
					this.menu7 = false;
				} else {
					this.menu1 = false;
					this.menu2 = false;
					this.menu3 = false;
					this.menu4 = false;
					this.menu5 = false;
					this.menu6 = false;
					this.menu7 = false;
				}
				break;
			}
			case 4: {
				if (!this.menu4) {
					this.menu1 = false;
					this.menu2 = false;
					this.menu3 = false;
					this.menu4 = true;
					this.menu5 = false;
					this.menu6 = false;
					this.menu7 = false;
				} else {
					this.menu1 = false;
					this.menu2 = false;
					this.menu3 = false;
					this.menu4 = false;
					this.menu5 = false;
					this.menu6 = false;
					this.menu7 = false;
				}
				break;
			}
			case 5: {
				if (!this.menu5) {
					this.menu1 = false;
					this.menu2 = false;
					this.menu3 = false;
					this.menu4 = false;
					this.menu5 = true;
					this.menu6 = false;
					this.menu7 = false;
				} else {
					this.menu1 = false;
					this.menu2 = false;
					this.menu3 = false;
					this.menu4 = false;
					this.menu5 = false;
					this.menu6 = false;
					this.menu7 = false;
				}
				break;
			}
			case 6: {
				if (!this.menu6) {
					this.menu1 = false;
					this.menu2 = false;
					this.menu3 = false;
					this.menu4 = false;
					this.menu5 = false;
					this.menu6 = true;
					this.menu7 = false;
				} else {
					this.menu1 = false;
					this.menu2 = false;
					this.menu3 = false;
					this.menu4 = false;
					this.menu5 = false;
					this.menu6 = false;
					this.menu7 = false;
				}
				break;
			}
			case 7: {
				if (!this.menu7) {
					this.menu1 = false;
					this.menu2 = false;
					this.menu3 = false;
					this.menu4 = false;
					this.menu5 = false;
					this.menu6 = false;
					this.menu7 = true;
				} else {
					this.menu1 = false;
					this.menu2 = false;
					this.menu3 = false;
					this.menu4 = false;
					this.menu5 = false;
					this.menu6 = false;
					this.menu7 = false;
				}
				break;
			}
			
			default: {
				this.menu1 = false;
				this.menu2 = false;
				this.menu3 = false;
				this.menu4 = false;
				this.menu5 = false;
				this.menu6 = false;
				this.menu7 = false;
				break;
			}
    }
	}

	activeMenu(id: number, menuName: string): void {
		switch (id) {
			case 1: {
				this.menu11 = true;
				this.menu21 = false;
				this.menu31 = false;
				this.menu41 = false;
				this.menu51 = false;
				this.menu61 = false;
				this.menu71 = false;
				break;
			}
			case 2: {
				this.menu11 = false;
				this.menu21 = true;
				this.menu31 = false;
				this.menu41 = false;
				this.menu51 = false;
				this.menu61 = false;
				this.menu71 = false;
				break;
			}
			case 3: {
				this.menu11 = false;
				this.menu21 = false;
				this.menu31 = true;
				this.menu41 = false;
				this.menu51 = false;
				this.menu61 = false;
				this.menu71 = false;
				break;
			}
			case 4: {
				this.menu11 = false;
				this.menu21 = false;
				this.menu31 = false;
				this.menu41 = true;
				this.menu51 = false;
				this.menu61 = false;
				this.menu71 = false;
				break;
			}
			case 5: {
				this.menu11 = false;
				this.menu21 = false;
				this.menu31 = false;
				this.menu41 = false;
				this.menu51 = true;
				this.menu61 = false;
				this.menu71 = false;
				break;
			}
			case 6: {
				this.menu11 = false;
				this.menu21 = false;
				this.menu31 = false;
				this.menu41 = false;
				this.menu51 = false;
				this.menu61 = true;
				this.menu71 = false;
				break;
			}
			case 7: {
				this.menu11 = false;
				this.menu21 = false;
				this.menu31 = false;
				this.menu41 = false;
				this.menu51 = false;
				this.menu61 = false;
				this.menu71 = true;
				break;
			}

			default: {
				this.menu11 = false;
				this.menu21 = false;
				this.menu31 = false;
				this.menu41 = false;
				this.menu51 = false;
				this.menu61 = false;
				this.menu71 = false;
				break;
			}

		}
    this.activeSubNenuName = menuName;
	}
}
