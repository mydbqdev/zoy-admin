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
import { TenantProfile } from '../model/tenant-profile-model';
import { MatTableDataSource } from '@angular/material/table';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { Filter, OwnerRequestParam } from 'src/app/owners/managing-owner/models/owner-details-request-model';
import { TenantDues } from '../model/tenant-dues-model';
import { TenantRefund } from '../model/tenant-refund-model';
import { TenantProfileService } from '../service/tenant-profile.service';
@Component({
	selector: 'app-tenant-profile',
	templateUrl: './tenant-profile.component.html',
	styleUrls: ['./tenant-profile.component.css']
})
export class TenantProfileComponent implements OnInit, AfterViewInit {
	displayedColumns: string[] = ['due_type','payment_date','paid_amount','paid_mode','due_date','status','action'];
	public ELEMENT_DATA:TenantProfile[]=[];
	displayedDuesColumns: string[] = ['due_type','due_generation_date','due_date','paid_mode','due_created_by'];
	public ELEMENT_DUES_DATA:TenantDues[]=[];
	displayedRefundColumns: string[] = ['cancellation_date','booking_id','refundable_amount','refund_title','status','action'];
	public ELEMENT_REFUND_DATA:TenantRefund[]=[];
	dataSource:MatTableDataSource<TenantProfile>=new MatTableDataSource<TenantProfile>();
	columnSortDirectionsOg: { [key: string]: string | null } = {
	  due_type: null,
	  payment_date: null,
	  paid_amount: null,
	  paid_mode: null,
	  due_date:null,
	  status: null
	};
	dataDuesSource:MatTableDataSource<TenantDues>=new MatTableDataSource<TenantDues>();
	columnSortDirectionsDuesOg: { [key: string]: string | null } = {
	  due_type: null,
	  due_generation_date: null,
	  due_date: null,
	  paid_mode: null,
	  due_created_by:null,
	};
	dataRefundSource:MatTableDataSource<TenantRefund>=new MatTableDataSource<TenantRefund>();
	columnSortDirectionsRefundOg: { [key: string]: string | null } = {
		cancellation_date: null,
		booking_id: null,
		refundable_amount: null,
		refund_title: null,
		status:null,
	  
	};
	stopPropagation(event: MouseEvent): void {
		event.stopPropagation();
	  }
	  paramFilterBack:OwnerRequestParam=new OwnerRequestParam();
	  submitted=false;
	  columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);
	  private _liveAnnouncer = inject(LiveAnnouncer);
	  param:OwnerRequestParam=new OwnerRequestParam();
	  paramFilter:Filter=new Filter();
	  pageSize: number = 10; 
	  pageSizeOptions: number[] = [10, 20, 50]; 
	  totalProduct: number = 0;
	  public lastPageSize:number=0;
	  public userNameSession: string = "";
	  errorMsg: any = "";
	  mySubscription: any;
	  isExpandSideBar:boolean=true;
	  @ViewChild(SidebarComponent) sidemenuComp;
	  @ViewChild(MatSort) sort: MatSort;
	  @ViewChild(MatPaginator) paginator: MatPaginator;
	  public rolesArray: string[] = [];
	  public selectedTab:number=1;
	  public sectionTabHeader:string="Persional Details";
	  tenantId:string='';
	constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,
		private tenantProfileService:TenantProfileService,private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService,private zoyOwnerService : ZoyOwnerService) {
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
		this.getPaymentHistory();
		this.getDuesHistory();
		this.getRefundHistory();
	}
	ngAfterViewInit() {
		this.sidemenuComp.expandMenu(7);
		this.sidemenuComp.activeMenu(7, 'tenants');
		this.dataService.setHeaderName("Tenant Profile");
		this.dataSource = new MatTableDataSource<TenantProfile>(this.ELEMENT_DATA);
		this.dataDuesSource = new MatTableDataSource<TenantDues>(this.ELEMENT_DUES_DATA);
		this.dataRefundSource = new MatTableDataSource<TenantRefund>(this.ELEMENT_REFUND_DATA);
		this.dataSource.sort = this.sort;
		this.dataSource.paginator = this.paginator;
	}
	selectProfile(selectTab:number,header:string){
		this.selectedTab=selectTab;
		this.sectionTabHeader=header;
	}

	getPaymentHistories(){
		this.authService.checkLoginUserVlidaate();
		this.spinner.show();
		this.tenantProfileService.getPaymentHistory().subscribe(data => {
		  this.ELEMENT_DATA = Object.assign([],data);
		//   this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
		//   this.dataSource.sort = this.sort;
		//   this.dataSource.paginator = this.paginator;
		//   this.dataSource.sortingDataAccessor = (data, sortHeaderId) => data[sortHeaderId].toLocaleLowerCase();
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
	  getDueHistory(){
		this.authService.checkLoginUserVlidaate();
		this.spinner.show();
		this.tenantProfileService.getDueHistory().subscribe(data => {
		  this.ELEMENT_DATA = Object.assign([],data);
		//   this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
		//   this.dataSource.sort = this.sort;
		//   this.dataSource.paginator = this.paginator;
		//   this.dataSource.sortingDataAccessor = (data, sortHeaderId) => data[sortHeaderId].toLocaleLowerCase();
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
	  getRefundHistories(){
		this.authService.checkLoginUserVlidaate();
		this.spinner.show();
		this.tenantProfileService.getRefundHistory().subscribe(data => {
		  this.ELEMENT_DATA = Object.assign([],data);
		//   this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
		//   this.dataSource.sort = this.sort;
		//   this.dataSource.paginator = this.paginator;
		//   this.dataSource.sortingDataAccessor = (data, sortHeaderId) => data[sortHeaderId].toLocaleLowerCase();
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
		  this.getPaymentHistory();
		  this.getDuesHistory();
		  this.getRefundHistory();
	  }
  
	  pageChanged(event:any){
		this.param.pageIndex = event.pageIndex;
        this.param.pageSize = event.pageSize;
		this.getPaymentHistory();
		}
		getPaymentHistory(){
			this.ELEMENT_DATA = Object.assign([],mockData);
			this.dataSource.data = this.ELEMENT_DATA;
		}
		getDuesHistory(){
			this.ELEMENT_DUES_DATA = Object.assign([],mockDuesData);
			this.dataDuesSource.data = this.ELEMENT_DUES_DATA;
		}
		getRefundHistory(){
			this.ELEMENT_REFUND_DATA = Object.assign([],mockRefundData);
			this.dataRefundSource.data = this.ELEMENT_REFUND_DATA;
		}
  }

 const mockDuesData=[
	{
		due_type: 'Wifi Bill',
		due_generation_date: '2024-01-01',
		due_date: '2024-01-05',
		paid_mode: '10,000.00',
		due_created_by: 'Owner',
	  },
	  {
		due_type: 'Food Bill',
		due_generation_date: '2024-01-10',
		due_date: '2024-01-15',
		paid_mode: '11,500.00',
		due_created_by: 'Owner',
	  },
	  {
		due_type: 'Electricity Bill',
		due_generation_date: '2024-01-20',
		due_date: '2024-01-25',
		paid_mode: '12,000.00',
		due_created_by: 'Automated',
	  },
	  {
		due_type: 'Security Deposit',
		due_generation_date: '2023-12-15',
		due_date: '2023-12-20',
		paid_mode: '7,000.00',
		due_created_by: 'Owner',
	  },
	  {
		due_type: 'Parking Fee',
		due_generation_date: '2024-02-01',
		due_date: '2024-02-05',
		paid_mode: '9,000.00',
		due_created_by: 'Owner',
	  },
	
 ]
const mockData = [
	{
	  due_type: "Wifi Bill",
	  payment_date: "2024-01-15",
	  paid_amount: "5000",
	  paid_mode: "Credit Card",
	  due_date:"2024-01-15",
	  status: "failed",
	},
	{
	  due_type: "Electricity Bill",
	  payment_date: "2024-03-10",
	  paid_amount: "15000",
	  paid_mode: "Bank Transfer",
	  due_date:"2024-01-15",
	  status: "success",
	},
	{
	  due_type: "Food",
	  payment_date: "2023-12-31",
	  paid_amount: "50000",
	  paid_mode: "UPI",
	  due_date:"2024-01-15",
	  status: "success",
	},
	{
	  due_type: "Maintainance",
	  payment_date: "2024-02-01",
	  paid_amount: "4500",
	  paid_mode: "Cash",
	  due_date:"2024-01-15",
	  status: "success",
	},
	{
	  due_type: "Rent",
	  payment_date: "2024-06-30",
	  paid_amount: "30000",
	  paid_mode: "Debit Card",
	  due_date:"2024-01-15",
	  status: "failed",
	},
	{
		due_type: "Wifi Bill",
		payment_date: "2024-01-15",
		paid_amount: "5000",
		paid_mode: "Credit Card",
		due_date:"2024-01-15",
		status: "success",
	  },
	  {
		due_type: "Electricity Bill",
		payment_date: "2024-03-10",
		paid_amount: "15000",
		paid_mode: "Bank Transfer",
		due_date:"2024-01-15",
		status: "failed",
	  },
	  {
		due_type: "Food",
		payment_date: "2023-12-31",
		paid_amount: "50000",
		paid_mode: "UPI",
		due_date:"2024-01-15",
		status: "success",
	  },
	  {
		due_type: "Rent",
		payment_date: "2024-06-30",
		paid_amount: "30000",
		paid_mode: "Debit Card",
		due_date:"2024-01-15",
		status: "failed",
	  },
  ];
  
  const mockRefundData=[
	{
		cancellation_date: "2024-11-13",
		booking_id: "BK-1001",
		refundable_amount: "5000.00",
		refund_title: "Cancellation Refund",
		status: "success",
	  },
	  {
		cancellation_date: "2024-10-22",
		booking_id: "BK-1002",
		refundable_amount: "7500.00",
		refund_title: "Partial Refund",
		status: "failed",
	  },
	  {
		cancellation_date: "2024-09-15",
		booking_id: "BK-1003",
		refundable_amount: "12000.00",
		refund_title: "Security Deposit Refund",
		status: "success",
	  },
	  {
		cancellation_date: "2024-11-01",
		booking_id: "BK-1004",
		refundable_amount: "3000.00",
		refund_title: "Booking Overlap Refund",
		status: "success",
	  },
	  {
		cancellation_date: "2024-12-05",
		booking_id: "BK-1005",
		refundable_amount: "10000.00",
		refund_title: "Advance Payment Refund",
		status: "failed",
	  },
	
  ];