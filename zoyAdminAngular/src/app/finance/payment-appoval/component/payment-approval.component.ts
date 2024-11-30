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
	displayedColumns: string[] = [ 'zoy_code','owner_name', 'total_amount',  'transaction_date','transaction_no','transaction_approval']; 
	pageSizeOptions: number[] = [10, 25, 50];
	pageSize = 10;
	@ViewChild(MatSort) sort: MatSort;
	@ViewChild(MatPaginator) paginator: MatPaginator;
	columnSortDirectionsOg: { [key: string]: string | null } = {
		zoy_code: null,
		owner_name: null,
		total_amount: null,
		transaction_date: null,
		transaction_approval: null
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
	  zoy_code: "ZC001",
	  owner_name: "John Doe",
	  total_amount: "1200.50",
	  transaction_date: "2024-11-15",
	  transaction_no: "TXN12345",
	  transaction_approval: "Received",
	},
	{
	  zoy_code: "ZC002",
	  owner_name: "Jane Smith",
	  total_amount: "850.00",
	  transaction_date: "2024-11-10",
	  transaction_no: "TXN12346",
	  transaction_approval: "Not Receive",
	},
	{
	  zoy_code: "ZC003",
	  owner_name: "Emily Davis",
	  total_amount: "450.75",
	  transaction_date: "2024-11-18",
	  transaction_no: "TXN12347",
	  transaction_approval: "Received",
	},
	{
	  zoy_code: "ZC004",
	  owner_name: "Michael Brown",
	  total_amount: "960.20",
	  transaction_date: "2024-11-12",
	  transaction_no: "TXN12348",
	  transaction_approval: "Not Receive",
	},
	{
	  zoy_code: "ZC005",
	  owner_name: "Sarah Wilson",
	  total_amount: "1500.00",
	  transaction_date: "2024-11-20",
	  transaction_no: "TXN12349",
	  transaction_approval: "Received",
	},
	{
	  zoy_code: "ZC006",
	  owner_name: "David Johnson",
	  total_amount: "700.50",
	  transaction_date: "2024-11-08",
	  transaction_no: "TXN12350",
	  transaction_approval: "Not Receive",
	},
	{
	  zoy_code: "ZC007",
	  owner_name: "Sophia Martinez",
	  total_amount: "2300.00",
	  transaction_date: "2024-11-05",
	  transaction_no: "TXN12351",
	  transaction_approval: "Received",
	},
	{
	  zoy_code: "ZC008",
	  owner_name: "James Taylor",
	  total_amount: "1250.75",
	  transaction_date: "2024-11-16",
	  transaction_no: "TXN12352",
	  transaction_approval: "Not Receive",
	},
	{
	  zoy_code: "ZC009",
	  owner_name: "Olivia Harris",
	  total_amount: "1450.00",
	  transaction_date: "2024-11-18",
	  transaction_no: "TXN12353",
	  transaction_approval: "Received",
	},
	{
	  zoy_code: "ZC010",
	  owner_name: "Liam Scott",
	  total_amount: "950.25",
	  transaction_date: "2024-11-20",
	  transaction_no: "TXN12354",
	  transaction_approval: "Not Receive",
	},
  ];