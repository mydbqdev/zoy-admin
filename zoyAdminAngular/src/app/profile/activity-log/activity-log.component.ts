import { AfterViewInit, Component, OnInit, ViewChild, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { NavigationEnd } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { UserService } from 'src/app/common/service/user.service';
import { AuthService } from 'src/app/common/service/auth.service';
import { DataService } from 'src/app/common/service/data.service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import { SidebarComponent } from 'src/app/components/sidebar/sidebar.component';
import { ActivityLogModel } from '../model/activity-log-model';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort, Sort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { ActivityLogService } from '../service/activity-log-service';
import { FiltersRequestModel } from 'src/app/finance/reports/model/report-filters-model';


@Component({
	selector: 'app-activity-log',
	templateUrl: './activity-log.component.html',
	styleUrls: ['./activity-log.component.css']
})
export class ActivityLogComponent implements OnInit, AfterViewInit {
	private _liveAnnouncer = inject(LiveAnnouncer);
	public userNameSession: string = "";
	errorMsg: any = "";
	mySubscription: any;
	isExpandSideBar:boolean=true;
	displayedColumns: string[] = ['created_on','history_data'];
	public ELEMENT_DATA:ActivityLogModel[]=[];
	dataSource:MatTableDataSource<ActivityLogModel>=new MatTableDataSource<ActivityLogModel>();
	filterActivitiesData: ActivityLogModel[] = [];
	columnSortDirectionsOg: { [key: string]: string | null } = {
		created_on: null,
		history_data: null
	  };
	  columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);
	searchText:string='';
	@ViewChild(SidebarComponent) sidemenuComp;
	@ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;
	public lastPageSize:number=0;
	public totalProduct:number=0;
	pageSize:number=10;
	pageSizeOptions=[10,20,50];
	sortActive:string="created_on";
	sortDirection:string="desc";
	public rolesArray: string[] = [];
	filtersRequest :FiltersRequestModel = new FiltersRequestModel();
	constructor(private activityLogService: ActivityLogService, private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,
		private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService) {
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
		// this.loadDummyData();

	}
	ngAfterViewInit() {
		this.sidemenuComp.expandMenu(0);
		this.sidemenuComp.activeMenu(0, '');
		this.dataService.setHeaderName("Activity Log");
		
		this.dataSource.paginator = this.paginator;
		this.getActivityLogDetails(this.paginator.pageIndex , this.paginator.pageSize,this.sortActive,this.sortDirection);
	}
	
	filterData($event: KeyboardEvent){
		if ($event.keyCode === 13) {
		if(this.searchText==''){
			//this.paramFilter.searchText=null;
		}else{
			//this.paramFilter.searchText=this.searchText;
		}
		this.paginator.pageIndex=0;
		// server side call for filter
	  }
	}
		  
	  resetFilter(){
		  this.searchText='';
		  this.paginator.pageIndex=0;
	  }

	  pageChanged(event:any){
		this.dataSource=new MatTableDataSource<any>();
		if(this.lastPageSize!=event.pageSize){
			this.paginator.pageIndex=0;
			event.pageIndex=0;
		   }
		 this.pageSize=event.pageSize;
		 this.getActivityLogDetails(this.paginator.pageIndex , event.pageSize,this.sortActive,this.sortDirection);
	   }

 	 onSortData(sort: Sort) {
		this.columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);
		this.columnSortDirections[sort.active] = sort.direction;
		  if (sort.direction) {
			this._liveAnnouncer.announce(`Sorted ${sort.direction}ending`);
		  } else {
			this._liveAnnouncer.announce('Sorting cleared');
		  }
		this.sortActive=sort.active;
		this.sortDirection=sort.direction!="" ? sort.direction:"asc";
		this.paginator.pageIndex=0;
		 this.getActivityLogDetails(this.paginator.pageIndex, this.pageSize,this.sortActive,this.sortDirection);
	   }

	  getActivityLogDetails(pageIndex:number,pageSize:number,sortActive:string,sortDirection:string){
		this.authService.checkLoginUserVlidaate();
		this.spinner.show();
		this.lastPageSize=pageSize;
		this.filtersRequest.pageIndex=pageIndex;
		this.filtersRequest.pageSize=pageSize;
		this.filtersRequest.sortActive=sortActive;
		this.filtersRequest.sortDirection=sortDirection.toUpperCase();
		this.columnSortDirections[sortActive] = sortDirection;
		this.activityLogService.getActivityLogdetails(this.filtersRequest).subscribe(data => {
			if(data?.data?.length >0){
				  this.totalProduct=data.count;
				  this.ELEMENT_DATA=Object.assign([],data.data);
				  this.dataSource = new MatTableDataSource(this.ELEMENT_DATA);
			  }else{
				this.totalProduct=0;
				this.ELEMENT_DATA=Object.assign([]);
				this.dataSource =  new MatTableDataSource(this.ELEMENT_DATA);
			  }
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
			  str = error.error;
			} else {
			  str = error.message;
			  str = str.substring(str.indexOf(":") + 1);
			}
			console.log("Error:" + str);
			this.errorMsg = str;
		  }
		  if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
		}
	  });
	  
	  }
}
