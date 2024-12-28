import { AfterViewInit, Component, inject, OnInit, ViewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { NavigationEnd } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { UserService } from 'src/app/common/service/user.service';
import { AuthService } from 'src/app/common/service/auth.service';
import { DataService } from 'src/app/common/service/data.service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import { SidebarComponent } from 'src/app/components/sidebar/sidebar.component';
import { ZoyOwnerService } from 'src/app/owners/service/zoy-owner.service';
import { MatTableDataSource } from '@angular/material/table';
import { TenantProfile } from '../model/transaction-history-model';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';

@Component({
	selector: 'app-tenant-profile',
	templateUrl: './tenant-profile.component.html',
	styleUrls: ['./tenant-profile.component.css']
})
export class TenantProfileComponent implements OnInit, AfterViewInit {
	public userNameSession: string = "";
	  errorMsg: any = "";
	  mySubscription: any;
	  isExpandSideBar:boolean=true;
	  @ViewChild(SidebarComponent) sidemenuComp;
	  public rolesArray: string[] = [];
	  submitted=false;
	  public selectedTab:number=1;
	  public sectionTabHeader:string="Persional Details";
	  tenantId:string='';
	  totalRecord:number=0;
	  pageSizeOptions: number[] = [10, 25, 50];
	  pageSize = 10;
	  dataSourceTransaction:MatTableDataSource<TenantProfile>=new MatTableDataSource<TenantProfile>();
	  displayedColumnsTransaction: string[] = [ 'due_type','payment_date', 'paid_amount', 'payment_mode', 'due_date', 'status','action']; 
	  columnSortDirectionsOg: { [key: string]: string | null } = {
		due_type: null,
		payment_date: null,
		paid_amount: null,
		payment_mode: null,
		due_date:null,
	  status: null
	};
	columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);
	_liveAnnouncer: any;
	@ViewChild(MatSort) sort: MatSort;
	@ViewChild(MatPaginator) paginator: MatPaginator;
	constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,
		private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService,private zoyOwnerService : ZoyOwnerService) {
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

		  this.dataService.getTenantId.subscribe(id=>{
			if(id == null || id == undefined || id == ''){
				if(localStorage.getItem('tenantInfo')){
					this.tenantId = localStorage.getItem('tenantInfo') ;
				}else{
					this.router.navigate(['/tenants']);
				}
			}else{
				this.tenantId=id;
			}
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
		this.sidemenuComp.expandMenu(7);
		this.sidemenuComp.activeMenu(7, 'tenants');
		this.dataService.setHeaderName("Tenant Profile");
	}
	selectProfile(selectTab:number,header:string){
		this.selectedTab=selectTab;
		this.sectionTabHeader=header;
	}
	announceSortChange(sortState: Sort): void {
		this.columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);
		this.columnSortDirections[sortState.active] = sortState.direction;
		  if (sortState.direction) {
			this._liveAnnouncer.announce(`Sorted ${sortState.direction}ending`);
		  } else {
			this._liveAnnouncer.announce('Sorting cleared');
		  }
		  //this.param.sortActive=sortState.active;
		  //this.param.sortDirection=sortState.direction!="" ? sortState.direction:"asc";
		 // this.param.pageIndex=0
		  //this.paginator.pageIndex=0;
		  //this.getHistoryReportList();
	  }
	  transactionHeader:string="";
	  transactionHeaderTenantName:string="";
	  selectTransaction(selectTab:number,header:string,tenantName:string){
		//this.selectedTab=selectTab;
		this.transactionHeader=header;
		this.transactionHeaderTenantName=tenantName;
		if(header=='Due History'){
			this.displayedColumnsTransaction = [ 'due_type','payment_date', 'due_date','paid_amount', 'payment_mode']; 
		}else if(header=='Refund History'){
			this.displayedColumnsTransaction = ['payment_date', 'due_date', 'paid_amount', 'payment_mode','status','action']; 
		}else{
			this.displayedColumnsTransaction = [ 'due_type','payment_date', 'paid_amount', 'payment_mode', 'due_date', 'status','action']; 
		}
	}
}
