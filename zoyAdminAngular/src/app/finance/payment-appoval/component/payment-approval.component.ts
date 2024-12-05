import { HttpClient } from '@angular/common/http';
import { AfterViewInit, Component, inject, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { AuthService } from 'src/app/common/service/auth.service';
import { DataService } from 'src/app/common/service/data.service';
import { UserService } from 'src/app/common/service/user.service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import { UserInfo } from 'src/app/common/shared/model/userinfo.service';
import { SidebarComponent } from 'src/app/components/sidebar/sidebar.component';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';
import { PaymentApprovalModel } from '../model/payment-approval-model';
import { MatPaginator } from "@angular/material/paginator";
import { MatSort, Sort } from "@angular/material/sort";
import { LiveAnnouncer } from "@angular/cdk/a11y";

@Component({
  selector: 'app-payment-approval',
  templateUrl: './payment-approval.component.html',
  styleUrl: './payment-approval.component.css'
})
export class PaymentApprovalComponent  implements OnInit,AfterViewInit{
	public ELEMENT_DATA:PaymentApprovalModel[]=[];
	dataSource:MatTableDataSource<PaymentApprovalModel>=new MatTableDataSource<PaymentApprovalModel>();
	displayedColumns: string[] = [ 'owner_id','owner_name', 'owner_share', 'status', 'zoy_share', 'acknowledgment']; 
	pageSizeOptions: number[] = [10, 25, 50];
	pageSize = 10;
	@ViewChild(MatSort) sort: MatSort;
	@ViewChild(MatPaginator) paginator: MatPaginator;
	columnSortDirectionsOg: { [key: string]: string | null } = {
		owner_id: null,
		owner_name: null,
		owner_share: null,
		status: null,
		zoy_share: null,
		acknowledgment:null
	  };
	columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);
	  private _liveAnnouncer = inject(LiveAnnouncer);
	
  	public userNameSession: string = "";
	errorMsg: any = "";
	mySubscription: any;
	isExpandSideBar:boolean=true;
	@ViewChild(SidebarComponent) sidemenuComp;
	public rolesArray: string[] = [];
	userInfo:UserInfo=new UserInfo();
  constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,private confirmationDialogService :ConfirmationDialogService,
		private spinner: NgxSpinnerService,private formBuilder: FormBuilder, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService) {
			this.authService.checkLoginUserVlidaate();
			this.userInfo=this.userService.getUserinfo();
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

		if(this.userService.getUserinfo()==undefined){
			this.dataService.getUserDetails.subscribe(name=>{
					  this.userInfo=name;
					});
		  }
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
		this.getPaymentApproval();
  }

  ngAfterViewInit(){
    this.sidemenuComp.expandMenu(5);
    this.sidemenuComp.activeMenu(5,'payment-approval');
    this.dataService.setHeaderName("Owner Transaction Approval");
	this.dataSource.sort = this.sort;
	this.dataSource.paginator = this.paginator;
  }
  announceSortChange(sortState: Sort): void {
	this.columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);
	this.columnSortDirections[sortState.active] = sortState.direction;

	  if (sortState.direction) {
		this._liveAnnouncer.announce(`Sorted ${sortState.direction}ending`);
	  } else {
		this._liveAnnouncer.announce('Sorting cleared');
	  }
  }

  getPaymentApproval(){
	this.ELEMENT_DATA = Object.assign([],mockData);
	this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
	this.dataSource.sort = this.sort;
	this.dataSource.paginator = this.paginator;
  }
}

const mockData = [
	{
	  owner_id: "OWNR001",
	  owner_name: "John Doe",
	  owner_share: 25000,
	  status: "Received",
	  zoy_share: 7000,
	  acknowledgment: "Received",
	},
	{
	  owner_id: "OWNR002",
	  owner_name: "Jane Smith",
	  owner_share: 35000,
	  status: "Failed",
	  zoy_share: 15000,
	  acknowledgment: "Pending",
	},
	{
	  owner_id: "OWNR003",
	  owner_name: "Michael Johnson",
	  owner_share: 40000,
	  status: "Received",
	  zoy_share: 25000,
	  acknowledgment: "Received",
	},
	{
	  owner_id: "OWNR004",
	  owner_name: "Emily Davis",
	  owner_share: 30000,
	  status: "Processing",
	  zoy_share: 10000,
	  acknowledgment: "Pending",
	},
	{
	  owner_id: "OWNR005",
	  owner_name: "David Wilson",
	  owner_share: 50000,
	  status: "Received",
	  zoy_share: 28000,
	  acknowledgment: "Received",
	},
	{
	  owner_id: "OWNR006",
	  owner_name: "Sarah Brown",
	  owner_share: 20000,
	  status: "Failed",
	  zoy_share: 15000,
	  acknowledgment: "Pending",
	},
	{
	  owner_id: "OWNR007",
	  owner_name: "Robert Taylor",
	  owner_share: 60000,
	  status: "Received",
	  zoy_share: 40000,
	  acknowledgment: "Received",
	},
	{
	  owner_id: "OWNR008",
	  owner_name: "Olivia Martinez",
	  owner_share: 45000,
	  status: "Processing",
	  zoy_share: 18000,
	  acknowledgment: "Pending",
	},
	{
	  owner_id: "OWNR009",
	  owner_name: "Daniel White",
	  owner_share: 55000,
	  status: "Received",
	  zoy_share: 31000,
	  acknowledgment: "Received",
	},
	{
	  owner_id: "OWNR0012",
	  owner_name: "Sophia Hall",
	  owner_share: 25000,
	  status: "Pending",
	  zoy_share: 9000,
	  acknowledgment: "Pending",
	},
	{
		owner_id: "OWNR0013",
		owner_name: "Sophia Hall",
		owner_share: 27000,
		status: "Pending",
		zoy_share: 7000,
		acknowledgment: "Pending",
	  },
  ];
  