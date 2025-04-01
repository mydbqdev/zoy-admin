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
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { OwnerRequestParam, Filter } from 'src/app/owners/managing-owner/models/owner-details-request-model';
import { TenantService } from '../../tenant.service';
import { ManageTenant } from '../../tenant-profile/model/manage.tenant';
import { FiltersRequestModel } from 'src/app/report/model/report-filters-model';

@Component({
	selector: 'app-tenants',
	templateUrl: './tenants.component.html',
	styleUrls: ['./tenants.component.css']
})
export class TenantsComponent implements OnInit, AfterViewInit {
	displayedColumns: string[] = ['registeredDate', 'tenantName', 'contactNumber', 'userEmail','appUser', 'status','ekycStatus'];
	public ELEMENT_DATA:ManageTenant[]=[];
	orginalFetchData:ManageTenant[]=[];
	searchText:string='';
	dataSource:MatTableDataSource<ManageTenant>=new MatTableDataSource<ManageTenant>();
	columnSortDirectionsOg: { [key: string]: string | null } = {
		registeredDate: null,
		tenantName: null,
		contactNumber: null,
		userEmail: null,
		appUser: null,
		status: null,
		ekycStatus: null
	};
	stopPropagation(event: MouseEvent): void {
		event.stopPropagation();
	  }
	public userNameSession: string = "";
	errorMsg: any = "";
	mySubscription: any;
	isExpandSideBar:boolean=true;
	@ViewChild(SidebarComponent) sidemenuComp;
	@ViewChild(MatSort) sort: MatSort;
	@ViewChild(MatPaginator) paginator: MatPaginator;
	public rolesArray: string[] = [];
	submitted=false;
	columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);
	private _liveAnnouncer = inject(LiveAnnouncer);
	pageSize: number = 10; 
	pageSizeOptions: number[] = [10, 20, 50]; 
	totalProduct: number = 0;
	public lastPageSize:number=0;
	public tenantIdBackFormDetails:string="";
	paramFilterBack:OwnerRequestParam=new OwnerRequestParam();
	totalProductFilterBack: number = 0;
	filtersRequestModel:FiltersRequestModel=new FiltersRequestModel();
	fromDate:string='';
	toDate:string='';
	param:OwnerRequestParam=new OwnerRequestParam();
	paramFilter:Filter=new Filter();

	constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,
		private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService,private tenantService : TenantService) {
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
			this.tenantIdBackFormDetails=id;
		  });

		  this.dataService.getTenantListFilter.subscribe(data=>{
			this.orginalFetchData= data;
		  });
		  this.dataService.getTenantListFilterTotal.subscribe(data=>{
			this.totalProductFilterBack= data;
		  });
		  this.dataService.getTenantListFilterParam.subscribe(data=>{
			this.paramFilterBack= data;
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
		this.dataService.setHeaderName("Tenants");
		if(this.tenantIdBackFormDetails==''){
			this.getRetrieveData();
		}else{
			this.ELEMENT_DATA = Object.assign([],this.orginalFetchData);
			this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
			this.totalProduct=this.totalProductFilterBack;
			this.columnSortDirections[this.paramFilterBack.sortActive] = this.paramFilterBack.sortDirection;
			this.paginator.pageIndex=this.paramFilterBack.pageIndex;
			this.paginator.pageSize=this.paramFilterBack.pageSize;
			this.lastPageSize=this.paramFilterBack.pageSize;
			this.param=this.paramFilterBack;
		}
	}

	getRetrieveData(){
		this.dataSource.sort = this.sort;
		this.dataSource.paginator = this.paginator;
		this.param.pageIndex=this.paginator.pageIndex;
		this.param.pageSize= this.paginator.pageSize;
		this.param.sortDirection="desc";
		this.param.sortActive="registeredDate";
		this.paramFilter.searchText=null;
		this.paramFilter.status=null;
		this.param.filter=this.paramFilter;
		setTimeout(()=>{
		  this.getTenantsList();
		 },100);
		 this.columnSortDirections["registeredDate"] = "asc";
	  }

	  getTenantsList(){
		  this.authService.checkLoginUserVlidaate();
		  this.spinner.show();
		  this.lastPageSize=this.param.pageSize;
		  this.tenantService.getTenantsList(this.param).subscribe(data => {
			  this.orginalFetchData=  Object.assign([],data.content);
			  this.ELEMENT_DATA = Object.assign([],data.content);
			  this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
			  this.totalProduct=data.total;
			  this.dataService.setTenantListFilter(this.orginalFetchData);
			  this.dataService.setTenantListFilterTotal(this.totalProduct);
			  this.spinner.hide();
		  }, error => {
		  this.spinner.hide();
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
			  console.log("Error:" ,str);
			  this.errorMsg = str;
			  }
			  if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
			  //this.notifyService.showError(this.errorMsg, "");
		  }
		  });
	  }
	
	announceSortChange(sortState: Sort): void {
	  this.columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);
	  this.columnSortDirections[sortState.active] = sortState.direction;
		if (sortState.direction) {
		  this._liveAnnouncer.announce(`Sorted ${sortState.direction}ending`);
		} else {
		  this._liveAnnouncer.announce('Sorting cleared');
		}
		this.param.sortActive=sortState.active;
		this.param.sortDirection=sortState.direction!="" ? sortState.direction:"asc";
		this.param.pageIndex=0
		this.paginator.pageIndex=0;
		this.getTenantsList();
	}

	filterData($event: KeyboardEvent){
		if ($event.keyCode === 13) {
		if(this.searchText==''){
			this.paramFilter.searchText=null;
			this.paramFilter.status=null;
		}else{
			this.paramFilter.searchText=this.searchText;
			this.paramFilter.status=null;
		}
		this.param.pageIndex=0
		this.paginator.pageIndex=0;
		this.param.filter=this.paramFilter;
		this.getTenantsList();
	  }
	}
	resetFilter(){
		this.searchText='';
		this.param.pageIndex=0
		this.paginator.pageIndex=0;
		this.param.filter=new Filter();
		this.getTenantsList();
		this.statuses.filter(data=>data.selected=false);
		this. selectedFilterStatus();
	}

	statuses = [
		{ id: 1, name: 'Active', selected: false },
		{ id: 2, name: 'Inactive', selected: false },
		{ id: 3, name: 'Register', selected: false },
		// { id: 4, name: 'Registered', selected: false },
		// { id: 4, name: 'Suspended', selected: false },
	  ];
	  selectedStatuses:string[]=[]; 
	   // Toggle the selected status for a button
	   toggleStatus(status: any): void {
		status.selected = !status.selected;
		this.selectedFilterStatus();
	  }
      selectedFilterStatus(){
		this.selectedStatuses = this.statuses
		.filter(status => status.selected)
		.map(status => status.name);
	  }
	  // Apply and process the selected statuses
	  applyStatuses(): void {
		console.log('Selected Statuses:', this.selectedStatuses);
		
		this.param.pageIndex=0
		this.paginator.pageIndex=0;
		this.param.filter.status=this.selectedStatuses.join(",");
		this.getTenantsList();
	  }
	  applyDates(): void {
		this.param.pageIndex=0
		this.paginator.pageIndex=0;
		this.param.filter.startDate=  (this.fromDate)+' 00:00:00';
		this.param.filter.endDate=  (this.toDate)+' 23:59:59';

		this.getTenantsList();
	  }

	  pageChanged(event:any){
		this.dataSource=new MatTableDataSource<ManageTenant>();
		if(this.lastPageSize!=event.pageSize){
		this.paginator.pageIndex=0;
		event.pageIndex=0;
		}
		this.param.pageIndex=this.paginator.pageIndex;
		this.param.pageSize= event.pageSize;
		this.getTenantsList();
		}
		getRecord(id:string){
			this.dataService.setTenantId(id);
			localStorage.setItem('tenantInfo', id);
			this.dataService.setTenantListFilterParam(this.param);
			this.router.navigateByUrl('/tenantprofile');
		}
}
