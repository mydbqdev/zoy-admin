import { AfterViewInit, Component, inject, OnInit, ViewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { NavigationEnd } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { UserService } from 'src/app/common/service/user.service';
import { AuthService } from 'src/app/common/service/auth.service';
import { DataService } from 'src/app/common/service/data.service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import {LiveAnnouncer} from '@angular/cdk/a11y';
import {MatSort, Sort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {MatPaginator} from '@angular/material/paginator';
import { ReportService } from '../service/reportService';
import { SidebarComponent } from 'src/app/components/sidebar/sidebar.component';
import { UserTransactionReportModel } from '../model/user-transaction-report-model';
import { UserGSTPaymentModel } from '../model/user-payment-model';
import { ConsilidatedFinanceDetailsModel } from '../model/consilidated-finance-details-model';
import { TenantDuesDetailsModel } from '../model/tenant-dues-details-model';
import { VendorPaymentsModel } from '../model/vendor-payments-model';
import { VendorPaymentsDues } from '../model/vendor-payments-dues-model';
import { VendorPaymentsGst } from '../model/vendor-payments-gst-model';
import { FiltersRequestModel } from '../model/report-filters-model';
 
@Component({
	selector: 'app-report-list',
	templateUrl: './report-list.component.html',
	styleUrls: ['./report-list.component.css']
})

export class ReportListComponent implements OnInit, AfterViewInit {
	userTransactionDataSource:MatTableDataSource<UserTransactionReportModel>=new MatTableDataSource<UserTransactionReportModel>();
	userTransactionData:UserTransactionReportModel[]=[]
	userTransactionReport:UserTransactionReportModel=new UserTransactionReportModel();
	userTransactionDataColumns: string[] = ['customerName', 'PgPropertyId', 'transactionDate', 'transactionNumber','transactionStatus','actions'];
	userTransactioncolumnSortDirections: { [key: string]: string | null } = {
        customerName: null,
        PgPropertyId: null,
        transactionDate: null,
        transactionStatus: null,
        transactionNumber: null,
      };
	  userTransactionSortDirections = Object.assign({}, this.userTransactioncolumnSortDirections);

	// User Payment Report
	userGSTPaymentDataSource: MatTableDataSource<UserGSTPaymentModel> = new MatTableDataSource<UserGSTPaymentModel>();
    userGSTPaymentData: UserGSTPaymentModel[] = [];
    userGSTPaymentReport: UserGSTPaymentModel = new UserGSTPaymentModel();
    userGSTPaymentReportColumns: string[] = ['transactionDate', 'PgPropertyId', 'transactionNumber', 'totalAmount', 'gstAmount', 'actions'];
    userGSTPaymentColumnSortDirections: { [key: string]: string | null } = {
        transactionDate: null,
        PgPropertyId: null,
        transactionNumber: null,
        totalAmount: null,
        gstAmount: null,
    };
    userGSTPaymentSortDirections = Object.assign({}, this.userGSTPaymentColumnSortDirections);

	// Consolidated Finance Report
	consilidatedFinanceDataSource: MatTableDataSource<ConsilidatedFinanceDetailsModel> = new MatTableDataSource<ConsilidatedFinanceDetailsModel>();
	consilidatedFinanceData: ConsilidatedFinanceDetailsModel[] = [];
	consilidatedFinanceDetails: ConsilidatedFinanceDetailsModel = new ConsilidatedFinanceDetailsModel();
	consilidatedFinanceReportColumns: string[] = ['transactionDate', 'customerId', 'transactionNumber', 'customerName', 'creditAmount', 'debitAmount', 'actions'];
	consilidatedFinanceColumnSortDirections: { [key: string]: string | null } = {
		transactionDate: null,
		customerId: null,
		transactionNumber: null,
		customerName: null,
		creditAmount: null,
		debitAmount: null,
	};
	consilidatedFinanceSortDirections = Object.assign({}, this.consilidatedFinanceColumnSortDirections);

	// Tenant Dues Details Report
	tenantDuesDataSource: MatTableDataSource<TenantDuesDetailsModel> = new MatTableDataSource<TenantDuesDetailsModel>();
	tenantDuesData: TenantDuesDetailsModel[] = [];
	tenantDuesDetails: TenantDuesDetailsModel = new TenantDuesDetailsModel();
	tenantDuesReportColumns: string[] = ['customerId', 'PgPropertyId', 'bedNumber', 'pendingAmount', 'pendingDueDate', 'actions'];
	tenantDuesColumnSortDirections: { [key: string]: string | null } = {
	  customerId: null,
	  PgPropertyId: null,
	  bedNumber: null,
	  pendingAmount: null,
	  pendingDueDate: null,
	};
	tenantDuesSortDirections = Object.assign({}, this.tenantDuesColumnSortDirections);

	// Vendor Payments Report
	vendorPaymentsDataSource: MatTableDataSource<VendorPaymentsModel> = new MatTableDataSource<VendorPaymentsModel>();
	vendorPaymentsData: VendorPaymentsModel[] = [];
	vendorPaymentsDetails: VendorPaymentsModel = new VendorPaymentsModel();
	vendorPaymentsReportColumns: string[] = ['ownerId', 'pgId', 'pgName', 'amountPaidToOwner',  'transactionDate', 'paymentStatus', 'actions'];
	vendorPaymentsColumnSortDirections: { [key: string]: string | null } = {
	ownerId: null,
	pgId: null,
	amountPaidToOwner: null,
	transactionDate: null,
	paymentStatus: null
	};
	vendorPaymentsSortDirections = Object.assign({}, this.vendorPaymentsColumnSortDirections);

	//Vendor Payments Dues
	vendorPaymentsDuesDataSource: MatTableDataSource<VendorPaymentsDues> = new MatTableDataSource<VendorPaymentsDues>();
	vendorPaymentsDuesData: VendorPaymentsDues[] = [];
	vendorPaymentsDuesDetails: VendorPaymentsDues = new VendorPaymentsDues();
	vendorPaymentsDuesReportColumns: string[] = ['ownerId', 'pgId', 'totalAmountPayable', 'totalAmountPaid', 'pendingAmount', 'pendingDueDate', 'actions'];
	vendorPaymentsDuesColumnSortDirections: { [key: string]: string | null } = {
	ownerId: null,
	pgId: null,
	totalAmountPayable: null,
	totalAmountPaid: null,
	pendingAmount: null,
	pendingDueDate: null
	};
	vendorPaymentsDuesSortDirections = Object.assign({}, this.vendorPaymentsDuesColumnSortDirections);

// Vendor Payments Gst
	vendorPaymentsGstDataSource: MatTableDataSource<VendorPaymentsGst> = new MatTableDataSource<VendorPaymentsGst>();
	vendorPaymentsGstData: VendorPaymentsGst[] = [];
	vendorPaymentsGstDetails: VendorPaymentsGst = new VendorPaymentsGst();
	vendorPaymentsGstColumns: string[] = ['transactionDate','transactionNo','pgId','totalAmount','gstAmount','actions'];
	vendorPaymentsGstColumnSortDirections: { [key: string]: string | null } = {
	  transactionDate: null,
	  transactionNo: null,
	  pgId: null,
	  totalAmount: null,
	  gstAmount: null
	};
	vendorPaymentsGstSortDirections = Object.assign({}, this.vendorPaymentsGstColumnSortDirections);
	


	@ViewChild(SidebarComponent) sidemenuComp;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;
	public totalProduct:number=0;
	pageSize:number=10;
	pageSizeOptions=[10,20,50];

	reportNamesList:string[] = ["User Transactions Report","User Payments GST Report","Vendor Payments Report","Vendor Payments Gst Report","Vendor Payments Dues Report","Consolidated Finance Report","Tenant Dues Report"]
	cityLocation: string[] = ["Delhi", "Mumbai", "Bangalore", "Kolkata", "Chennai", "Hyderabad", "Vijayawada", "Madurai","Ooty", "Goa"];
	cityLocationName:string='Bangalore';
	fromDate:string="2023-11-01";
	toDate:string="2025-01-01";
	reportName:string ='User Transactions Report';
	downloadType :string='';
	searchText:string='';

	filtersRequest :FiltersRequestModel = new FiltersRequestModel();
	public userNameSession: string = "";
	errorMsg: any = "";
	mySubscription: any;
	rolesArray:string[]=[];
	private _liveAnnouncer = inject(LiveAnnouncer);
	isExpandSideBar:boolean=true;
	constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,
		private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService,private reportService : ReportService) {
			this.authService.checkLoginUserVlidaate();
			this.userNameSession = userService.getUsername();
		//this.defHomeMenu=defMenuEnable;
		if (userService.getUserinfo() != undefined) {
			this.rolesArray = userService.getUserinfo().privilege;
		}else{
			this.dataService.getUserDetails.subscribe(name=>{
				this.rolesArray =name.privilege;
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
		//	this.router.navigate(['/']);
		//}
	
		this.getUserTransactionReport();
	}
	ngAfterViewInit() {
		this.sidemenuComp.expandMenu(5);
		this.sidemenuComp.activeMenu(5, 'report-list');
		this.dataService.setHeaderName("Reports");
	}
	
	announceSortChange(sortState: Sort) {//
		switch (this.reportName) {
			case 'User Transactions Report':
					this.userTransactionSortDirections = Object.assign({}, this.userTransactioncolumnSortDirections);
					this.userTransactionSortDirections[sortState.active] = sortState.direction;
					break;
			case 'User Payments GST Report':
					this.userGSTPaymentSortDirections = Object.assign({}, this.userGSTPaymentColumnSortDirections);
					this.userGSTPaymentSortDirections[sortState.active] = sortState.direction;
					break;
			case 'Consolidated Finance Report': 
					this.consilidatedFinanceSortDirections = Object.assign({}, this.consilidatedFinanceColumnSortDirections);
					this.consilidatedFinanceSortDirections[sortState.active] = sortState.direction;
					break;
			case 'Tenant Dues Report': 
					this.tenantDuesSortDirections = Object.assign({}, this.tenantDuesColumnSortDirections);
					this.tenantDuesSortDirections[sortState.active] = sortState.direction;
					break;
			case 'Vendor Payments Report': 
					this.vendorPaymentsSortDirections = Object.assign({}, this.vendorPaymentsColumnSortDirections);
					this.vendorPaymentsSortDirections[sortState.active] = sortState.direction;
					break;
			case 'Vendor Payments Dues Report': 
					this.vendorPaymentsDuesSortDirections = Object.assign({}, this.vendorPaymentsDuesColumnSortDirections);
					this.vendorPaymentsDuesSortDirections[sortState.active] = sortState.direction;
					break;
			case 'Vendor Payments Gst Report': 
					this.vendorPaymentsGstSortDirections = Object.assign({}, this.vendorPaymentsGstColumnSortDirections);
					this.vendorPaymentsGstSortDirections[sortState.active] = sortState.direction;
					break;
			default:
				
		}

		if (sortState.direction) {
		  this._liveAnnouncer.announce(`Sorted ${sortState.direction}ending`);
		} else {
		  this._liveAnnouncer.announce('Sorting cleared');
		}
	  }

	  changeReport(){

		 this.totalProduct=0;

		if(this.fromDate == "" || this.toDate == "" || this.fromDate == null || this.toDate == null || (this.fromDate >this.toDate)){
			this.notifyService.showError("Please select a valid date range.","");
			return;
		}
       
		switch (this.reportName) {
			case 'User Transactions Report':
				this.getUserTransactionReport();
				break;
			case 'User Payments GST Report':
				this.getUserGSTPaymentReport();
				break; 
			case 'Consolidated Finance Report':
				this.getconsilidatedFinanceReport();
				break;
			case 'Tenant Dues Report':
				this.getTenantDuesDetailsReport();
				break;
			case 'Vendor Payments Report':
				this.getVendorPaymentDetailsReport();
				break;
			case 'Vendor Payments Dues Report':
				this.getVendorPaymentDuesReport();
				break;
			case 'Vendor Payments Gst Report':
					this.getVendorPaymentGSTReport();
					break;
			default:
				
		}
	  }

	  getReport(){


		if(this.fromDate == "" || this.toDate == "" || this.fromDate == null || this.toDate == null || (this.fromDate >this.toDate)){
	
			this.notifyService.showError("Please select a valid date range.","");
		return;
		}
       
		switch (this.reportName) {
			case 'User Transactions Report':
				this.getUserTransactionReport();
				break;
			case 'User Payments GST Report':
				this.getUserGSTPaymentReport();
				break; 
			case 'Consolidated Finance Report':
				this.getconsilidatedFinanceReport();
				break;
			case 'Tenant Dues Report':
				this.getTenantDuesDetailsReport();
				break;
			case 'Vendor Payments Report':
				this.getVendorPaymentDetailsReport();
				break;
			case 'Vendor Payments Dues Report':
				this.getVendorPaymentDuesReport();
				break;
			case 'Vendor Payments Gst Report':
					this.getVendorPaymentGSTReport();
					break;
			default:
				
		}
	  }

	  viewReport(row:any){
		
		switch (this.reportName) {
			case 'User Transactions Report':
				this.userTransactionReport = Object.assign(row);
				break;
			case 'User Payments GST Report':
				this.userGSTPaymentReport = Object.assign(row);
				break;
			case 'Consolidated Finance Report':
				this.consilidatedFinanceDetails = Object.assign(row);
				break;
			case 'Tenant Dues Report':
				this.tenantDuesDetails = Object.assign(row);
				break;
			case 'Vendor Payments Report':
				this.vendorPaymentsDetails = Object.assign(row);
				break;
			case 'Vendor Payments Dues Report':
				this.vendorPaymentsDuesDetails = Object.assign(row);
				break;
			case 'Vendor Payments Gst Report':
				this.vendorPaymentsGstDetails = Object.assign(row);
				break;

			default:		
		}
	   }
	   
	
	onExport(element: any): void {
		console.log('Export action triggered for:', element);
	}


	  getUserTransactionReport(){
			// this.authService.checkLoginUserVlidaate();
			this.spinner.show();
			this.reportService.getUserTransactionReport(this.fromDate,this.toDate).subscribe(data => {
		
			this.userTransactionData = Object.assign([],data);
			this.userTransactionDataSource = new MatTableDataSource(this.userTransactionData);
			this.userTransactionDataSource.sort = this.sort;
			this.userTransactionDataSource.paginator = this.paginator;
			this.spinner.hide();
			}, error => {
			this.spinner.hide();
			if(error.status==403){
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

	   getUserGSTPaymentReport(){
		// this.authService.checkLoginUserVlidaate();
		 this.spinner.show();
		 this.reportService.getUserGSTPaymentReport(this.fromDate,this.toDate).subscribe(data => {
			
		   this.userGSTPaymentData = Object.assign([],data);
		   this.userGSTPaymentDataSource = new MatTableDataSource(this.userGSTPaymentData);
		   this.userGSTPaymentDataSource.sort = this.sort;
		   this.userGSTPaymentDataSource.paginator = this.paginator;
		   this.spinner.hide();
		}, error => {
		 this.spinner.hide();
		 if(error.status==403){
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
	   
	   getconsilidatedFinanceReport(){
		// this.authService.checkLoginUserVlidaate();
		 this.spinner.show();
		 this.reportService.getconsilidatedFinanceReport(this.fromDate,this.toDate).subscribe(data => {
			
		   this.consilidatedFinanceData = Object.assign([],data);
		   this.consilidatedFinanceDataSource = new MatTableDataSource(this.consilidatedFinanceData);
		   this.consilidatedFinanceDataSource.sort = this.sort;
		   this.consilidatedFinanceDataSource.paginator = this.paginator;
		   this.spinner.hide();
		}, error => {
		 this.spinner.hide();
		 if(error.status==403){
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
	   
	   getTenantDuesDetailsReport(){
		// this.authService.checkLoginUserVlidaate();
		 this.spinner.show();
		 this.reportService.getTenantDuesDetailsReport(this.fromDate,this.toDate).subscribe(data => {
			
		   this.tenantDuesData = Object.assign([],data);
		   this.tenantDuesDataSource = new MatTableDataSource(this.tenantDuesData);
		   this.tenantDuesDataSource.sort = this.sort;
		   this.tenantDuesDataSource.paginator = this.paginator;
		   this.spinner.hide();
		}, error => {
		 this.spinner.hide();
		 if(error.status==403){
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

	   getVendorPaymentDetailsReport(){
		// this.authService.checkLoginUserVlidaate();
		 this.spinner.show();
		 this.reportService.getVendorPaymentDetailsReport(this.fromDate,this.toDate).subscribe(data => {

		   this.vendorPaymentsData = Object.assign([],data);
		   this.vendorPaymentsDataSource = new MatTableDataSource(this.vendorPaymentsData);
		   this.vendorPaymentsDataSource.sort = this.sort;
		   this.vendorPaymentsDataSource.paginator = this.paginator;
		   this.spinner.hide();
		}, error => {
		 this.spinner.hide();
		 if(error.status==403){
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

	   getVendorPaymentDuesReport(){
		// this.authService.checkLoginUserVlidaate();
		 this.spinner.show();
		 this.reportService.getVendorPaymentDuesReport(this.fromDate,this.toDate).subscribe(data => {

		   this.vendorPaymentsDuesData = Object.assign([],data);
		   this.vendorPaymentsDuesDataSource = new MatTableDataSource(this.vendorPaymentsDuesData);
		   this.vendorPaymentsDuesDataSource.sort = this.sort;
		   this.vendorPaymentsDuesDataSource.paginator = this.paginator;
		   this.spinner.hide();
		}, error => {
		 this.spinner.hide();
		 if(error.status==403){
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

	   getVendorPaymentGSTReport(){
		// this.authService.checkLoginUserVlidaate();
		 this.spinner.show();
		 this.reportService.getVendorPaymentGSTReport(this.fromDate,this.toDate).subscribe(data => {

		   this.vendorPaymentsGstData = Object.assign([],data);
		   this.vendorPaymentsGstDataSource = new MatTableDataSource(this.vendorPaymentsGstData);
		   this.vendorPaymentsGstDataSource.sort = this.sort;
		   this.vendorPaymentsGstDataSource.paginator = this.paginator;
		   this.spinner.hide();
		}, error => {
		 this.spinner.hide();
		 if(error.status==403){
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

	   downloadPdf(){

		console.log("this.reportName",this.reportName);
				if(this.fromDate == "" || this.toDate == "" || this.fromDate == null || this.toDate == null || (this.fromDate >this.toDate)){
			
					this.notifyService.showError("Please select a valid date range.","");
				return;
				}
			   
				switch (this.reportName) {
					case 'User Transactions Report':
						this.getUserTransactionReport();
						break;
					case 'User Payments GST Report':
						this.getUserGSTPaymentReport();
						break; 
					case 'Consolidated Finance Report':
						this.getconsilidatedFinanceReport();
						break;
					case 'Tenant Dues Report':
						this.getTenantDuesDetailsReport();
						break;
					case 'Vendor Payments Report':
						this.downloadPaymentTransferDetailsPdf();
						break;
					case 'Vendor Payments Dues Report':
						this.getVendorPaymentDuesReport();
						break;
					case 'Vendor Payments Gst Report':
							this.getVendorPaymentGSTReport();
							break;
					default:
						
				}
			  }
		
		
			  downloadPaymentTransferDetailsPdf(){   
				//	this.authService.checkLoginUserVlidaate();
			
				const dateTime = new Date().toISOString().replace(/[-T:.Z]/g, '').slice(0, 14);
					this.reportService.downloadPaymentTransferDetailsPdf(this.fromDate,this.toDate).subscribe((data) => { 
		
						if(data!=null && data!=undefined && data!='' && data.size!=0){ 
						var blob = new Blob([data], {type : 'application/pdf'});
						var fileURL=URL.createObjectURL(blob);
						//window.open(fileURL);
						const link = document.createElement("a");
						link.href = fileURL;
						link.target = '_blank';
						link.download = this.reportName+'_'+dateTime+'.pdf';
						document.body.appendChild(link);
						link.click();
						document.body.removeChild(link); 
						  
					  }else{
						this.notifyService.showWarning(this.reportName+" is not available", "");
					  }
					}, error => {
						this.spinner.hide();
						if(error.status==403){
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
		
	  

		  userTransactionsDataSource:MatTableDataSource<UserTransactionReportModel>=new MatTableDataSource<UserTransactionReportModel>();
		  userTransactionsData:UserTransactionReportModel[]=[]
		  userTransactionsDetails:UserTransactionReportModel=new UserTransactionReportModel();
		  UserTransactionsDisplayedColumns: string[] = ['customerName', 'PgPropertyId', 'transactionDate', 'transactionNumber','transactionStatus','actions'];
		  sortActive:string="name";
		  sortDirection:string="asc";
		  public lastPageSize:number=0;

		  pageChanged(event:any){
			this.userTransactionsDataSource=new MatTableDataSource<UserTransactionReportModel>();
			if(this.lastPageSize!=event.pageSize){
				this.paginator.pageIndex=0;
				event.pageIndex=0;
			   }
			 this.pageSize=event.pageSize;
			 this.loadInitialData(this.paginator.pageIndex +1, event.pageSize,this.sortActive,this.sortDirection);
		   }
		   onSortData(sort: Sort) {
			this.sortActive=sort.active;
			this.sortDirection=sort.direction;
			this.paginator.pageIndex=0;
			 this.loadInitialData(this.paginator.pageIndex +1, this.pageSize,this.sortActive,this.sortDirection);
		   }

		   searchEnter(event: { which: any; keyCode: any;}) {
			if(event.which=='13'){
			  this.search();
			}
		}
		   search(){
			this.paginator.pageIndex=0;
			this.loadInitialData(this.paginator.pageIndex +1, this.pageSize,this.sortActive,this.sortDirection);
		  }



		  loadInitialData(pageIndex:number,pageSize:number,sortActive:string,sortDirection:string){
		//	this.authService.checkLoginUserVlidaate();
			this.lastPageSize=pageSize;
			this.filtersRequest.pageIndex=pageIndex;
			this.filtersRequest.pageSize=pageSize;
			this.filtersRequest.sortActive=sortActive;
			this.filtersRequest.sortDirection=sortDirection;

			this.filtersRequest.fromDate=this.fromDate;
			this.filtersRequest.toDate=this.toDate;
			this.filtersRequest.cityLocation = this.cityLocationName;
			this.filtersRequest.reportType=this.reportName;

			this.filtersRequest.searchText = this.searchText;

			console.log("this.filtersRequest",this.filtersRequest);

			this.totalProduct=this.mockData.length;
					  this.userTransactionsData=Object.assign([],this.mockData);
					this.userTransactionsDataSource = new MatTableDataSource(this.userTransactionsData);
			return;
			
			this.spinner.show();
			this.reportService.getReportsDetails(this.filtersRequest).subscribe((data) => {
			  if(data?.listObj?.length >0){
					this.totalProduct=data.totalCount;
					  this.userTransactionsData=Object.assign([],data.listObj);
					this.userTransactionsDataSource = new MatTableDataSource(this.userTransactionsData);
				}else{
				  this.totalProduct=data.totalCount;
				  this.userTransactionsData=Object.assign([],[]);
				  this.userTransactionsDataSource =  new MatTableDataSource(this.userTransactionsData);
				}
				this.spinner.hide();
			},error =>{
			  this.spinner.hide();
			  if(error.status==403){
				this.router.navigate(['/forbidden']);
			  }else if (error.error && error.error.message) {
				this.errorMsg =error.error.message;
				console.log("Error:"+this.errorMsg);
				this.notifyService.showError(this.errorMsg, "");
				this.spinner.hide();
			  } else {
				this.spinner.hide();
				if(error.status==500 && error.statusText=="Internal Server Error"){
				  this.errorMsg=error.statusText+"! Please login again or contact your Help Desk.";
				}else{
				  let str;
					if(error.status==400){
					str=error.error;
					}else{
					  str=error.message;
					  str=str.substring(str.indexOf(":")+1);
					}
					console.log("Error:"+str);
					this.errorMsg=str;
				}
				if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
			  }
			}); 
		  }

		  downloadReportPdf(){   
			//	this.authService.checkLoginUserVlidaate();
			this.filtersRequest.fromDate=this.fromDate;
			this.filtersRequest.toDate=this.toDate;
			this.filtersRequest.cityLocation = this.cityLocationName;
			this.filtersRequest.reportType=this.reportName;
			this.filtersRequest.downloadType=this.downloadType;

			this.filtersRequest.searchText = this.searchText;
			console.log("this.filtersRequest",this.filtersRequest);


			this.reportService.downloadReportPdf(this.filtersRequest).subscribe((data) => { 
				if(data!=null && data!=undefined && data!='' && data.size!=0){ 
				  var blob = new Blob([data], {type : 'application/pdf'});
				  var fileURL=URL.createObjectURL(blob);
				  
				  const link = document.createElement("a");
				  link.href = fileURL;
				  link.target = '_blank';
				  link.download = this.reportName+'.pdf';
				  document.body.appendChild(link);
				  link.click();
				}else{
				  this.notifyService.showWarning(this.reportName+" is not available", "");
				}
			  }, error => {
				  this.spinner.hide();
				  if(error.status==403){
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





		   mockData: UserTransactionReportModel[] = [
			{
			  customerId: 'CUST001',
			  transactionDate: '2024-11-01',
			  transactionNumber: 'TXN123456',
			  transactionStatus: 'Completed',
			  baseAmount: 1000,
			  gstAmount: 180,
			  totalAmount: 1180,
			  customerName: 'John Doe',
			  PgPropertyName: 'Pg Property A',
			  PgPropertyId: 'PG001',
			  bedNumber: '101',
			  category: 'Deluxe',
			  paymentMethod: 'Credit Card'
			},
			{
			  customerId: 'CUST008',
			  transactionDate: '2024-11-08',
			  transactionNumber: 'TXN123463',
			  transactionStatus: 'Completed',
			  baseAmount: 1250,
			  gstAmount: 225,
			  totalAmount: 1475,
			  customerName: 'Frank Black',
			  PgPropertyName: 'Pg Property D',
			  PgPropertyId: 'PG004',
			  bedNumber: '108',
			  category: 'Suite',
			  paymentMethod: 'Cash'
			},
			{
			  customerId: 'CUST009',
			  transactionDate: '2024-11-09',
			  transactionNumber: 'TXN123464',
			  transactionStatus: 'Pending',
			  baseAmount: 900,
			  gstAmount: 162,
			  totalAmount: 1062,
			  customerName: 'Grace Lee',
			  PgPropertyName: 'Pg Property A',
			  PgPropertyId: 'PG001',
			  bedNumber: '109',
			  category: 'Economy',
			  paymentMethod: 'Credit Card'
			},
			{
			  customerId: 'CUST010',
			  transactionDate: '2024-11-10',
			  transactionNumber: 'TXN123465',
			  transactionStatus: 'Cancelled',
			  baseAmount: 600,
			  gstAmount: 108,
			  totalAmount: 708,
			  customerName: 'Henry Brown',
			  PgPropertyName: 'Pg Property B',
			  PgPropertyId: 'PG002',
			  bedNumber: '110',
			  category: 'Standard',
			  paymentMethod: 'Debit Card'
			},
			{
			  customerId: 'CUST011',
			  transactionDate: '2024-11-11',
			  transactionNumber: 'TXN123466',
			  transactionStatus: 'Completed',
			  baseAmount: 1450,
			  gstAmount: 261,
			  totalAmount: 1711,
			  customerName: 'Isla Green',
			  PgPropertyName: 'Pg Property C',
			  PgPropertyId: 'PG003',
			  bedNumber: '111',
			  category: 'Deluxe',
			  paymentMethod: 'Net Banking'
			},
			{
			  customerId: 'CUST012',
			  transactionDate: '2024-11-12',
			  transactionNumber: 'TXN123467',
			  transactionStatus: 'Completed',
			  baseAmount: 950,
			  gstAmount: 171,
			  totalAmount: 1121,
			  customerName: 'Jake Blue',
			  PgPropertyName: 'Pg Property D',
			  PgPropertyId: 'PG004',
			  bedNumber: '112',
			  category: 'Economy',
			  paymentMethod: 'UPI'
			},
			{
			  customerId: 'CUST013',
			  transactionDate: '2024-11-13',
			  transactionNumber: 'TXN123468',
			  transactionStatus: 'Pending',
			  baseAmount: 1100,
			  gstAmount: 198,
			  totalAmount: 1298,
			  customerName: 'Liam Yellow',
			  PgPropertyName: 'Pg Property A',
			  PgPropertyId: 'PG001',
			  bedNumber: '113',
			  category: 'Luxury',
			  paymentMethod: 'Cash'
			}
		  ];
		  
}
