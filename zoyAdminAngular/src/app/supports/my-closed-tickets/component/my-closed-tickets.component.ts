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
import { Filter } from 'src/app/owners/managing-owner/models/owner-details-request-model';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ManageTenant } from 'src/app/tenants/tenant-profile/model/manage.tenant';


@Component({
	selector: 'app-my-closed-tickets',
	templateUrl: './my-closed-tickets.component.html',
	styleUrls: ['./my-closed-tickets.component.css']
})
export class MyClosedTicketsComponent implements OnInit, AfterViewInit {
	public userNameSession: string = "";
	errorMsg: any = "";
	mySubscription: any;
	isExpandSideBar:boolean=true;
	@ViewChild(SidebarComponent) sidemenuComp;
	public rolesArray: string[] = [];
	searchText:string='';
	fromDate:string='';
	toDate:string='';
	//param:OwnerRequestParam=new OwnerRequestParam();
	pageSize: number = 10; 
	pageSizeOptions: number[] = [10, 20, 50]; 
	totalProduct: number = 0;
	public lastPageSize:number=0;
	paramFilter:Filter=new Filter();
	displayedColumns: string[] = ['ticketNo', 'raisedDate', 'type', 'ugency','assignTo', 'status'];
	public ELEMENT_DATA:ManageTenant[]=[];
	dataSource:MatTableDataSource<ManageTenant>=new MatTableDataSource<ManageTenant>();
	columnSortDirectionsOg: { [key: string]: string | null } = {
		ticketNo: null,
		raisedDate: null,
		type: null,
		ugency: null,
		assignTo: null,
		status: null
	};
	columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);
	private _liveAnnouncer = inject(LiveAnnouncer);
	constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,
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
	}
	ngAfterViewInit() {
		this.sidemenuComp.expandMenu(6);
		this.sidemenuComp.activeMenu(6, 'my-closed-tickets');
		this.dataService.setHeaderName("Tickets");
	}

	statuses = [
		{ id: 1, name: 'New', selected: false },
		{ id: 2, name: 'Open', selected: false },
		{ id: 3, name: 'Progress', selected: false },
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
		
		//this.param.pageIndex=0
		//this.paginator.pageIndex=0;
		//this.param.filter.status=this.selectedStatuses.join(",");

	  }
	  applyDates(): void {
		//this.param.pageIndex=0
		//this.paginator.pageIndex=0;
		//this.param.filter.startDate=  (this.fromDate)+' 00:00:00';
		//this.param.filter.endDate=  (this.toDate)+' 23:59:59';

	  }

	  filterData($event: KeyboardEvent){
		if ($event.keyCode === 13) {
		if(this.searchText==''){
			//this.paramFilter.searchText=null;
			//this.paramFilter.status=null;
		}else{
			//this.paramFilter.searchText=this.searchText;
			//this.paramFilter.status=null;
		}
		//this.param.pageIndex=0
		//this.paginator.pageIndex=0;
		//this.param.filter=this.paramFilter;
		//this.getTicketsList();
	  }
	}
	resetFilter(){
		this.searchText='';
		//this.param.pageIndex=0
		//this.paginator.pageIndex=0;
		//this.param.filter=new Filter();
		//this.getTicketsList();
		this.statuses.filter(data=>data.selected=false);
		this. selectedFilterStatus();
	}
	stopPropagation(event: MouseEvent): void {
		event.stopPropagation();
	  }

	  announceSortChange(sortState: Sort): void {
		this.columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);
		this.columnSortDirections[sortState.active] = sortState.direction;
		  if (sortState.direction) {
			this._liveAnnouncer.announce(`Sorted ${sortState.direction}ending`);
		  } else {
			this._liveAnnouncer.announce('Sorting cleared');
		  }
		 // this.param.sortActive=sortState.active;
		  //this.param.sortDirection=sortState.direction!="" ? sortState.direction:"asc";
		//  this.param.pageIndex=0
		//  this.paginator.pageIndex=0;
		 // this.getTicketsList();
	  }

	  pageChanged(event:any){
		this.dataSource=new MatTableDataSource<ManageTenant>();
		//if(this.lastPageSize!=event.pageSize){
		//this.paginator.pageIndex=0;
		//event.pageIndex=0;
		//}
		//this.param.pageIndex=this.paginator.pageIndex;
		//this.param.pageSize= event.pageSize;
		//this.getTicketsList();
		}
}
