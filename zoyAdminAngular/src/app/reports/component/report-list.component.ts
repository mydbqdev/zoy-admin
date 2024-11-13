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
import {MatSort, Sort, MatSortModule} from '@angular/material/sort';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatPaginator, MatPaginatorModule} from '@angular/material/paginator';
import { ReportService } from '../service/reportService';
import { ReportsModel } from '../model/report_Model';
import { SidebarComponent } from 'src/app/components/sidebar/sidebar.component';
import { UserTransactionReportModel } from '../model/user-transaction-report-model';
import { ReportMockData } from '../model/report-mock-data';
import { UserGSTPaymentModel } from '../model/user-payment-model';
 
@Component({
	selector: 'app-report-list',
	templateUrl: './report-list.component.html',
	styleUrls: ['./report-list.component.css']
})

export class ReportListComponent implements OnInit, AfterViewInit {
	dataSource:MatTableDataSource<UserTransactionReportModel>=new MatTableDataSource<UserTransactionReportModel>();
	ELEMENT_DATA:UserTransactionReportModel[]=[]
	userTransactionReport:UserTransactionReportModel=new UserTransactionReportModel();
	displayedColumns: string[] = ['customerName', 'PgPropertyId', 'transactionDate', 'transactionNumber','transactionStatus','actions'];
	columnSortDirections1: { [key: string]: string | null } = {
        customerName: null,
        PgPropertyId: null,
        transactionDate: null,
        transactionStatus: null,
        transactionNumber: null,
      };
    columnSortDirections = Object.assign({}, this.columnSortDirections1);

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


	@ViewChild(SidebarComponent) sidemenuComp;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;
	public totalProduct:number=0;
	pageSize:number=10;
	pageSizeOptions=[10,20,50];

	reportNamesList:string[] = ["USER TRANSACTIONS REPORT","Vendor Dues Report","Consolidated Finance Report","Vendor Payments GST Report","User Payments GST REPORT","Tenant Dues REPORT"]
	cityLocation: string[] = ["Delhi", "Mumbai", "Bangalore", "Kolkata", "Chennai", "Hyderabad", "Vijayawada", "Madurai","Ooty", "Goa"];
	fromDate:string="2023-11-01";
	toDate:string="2025-01-01";
	reportName:string ='USER TRANSACTIONS REPORT';


	public userNameSession: string = "";
	errorMsg: any = "";
	mySubscription: any;
	private _liveAnnouncer = inject(LiveAnnouncer);
	isExpandSideBar:boolean=true;
	constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,
		private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService,private reportService : ReportService) {
		this.userNameSession = userService.getUsername();
		//this.defHomeMenu=defMenuEnable;
		//if (userService.getUserinfo() != undefined) {
		//	this.rolesArray = userService.getUserinfo().previlageList;
		//}
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
		

	}
	
	announceSortChange(sortState: Sort) {

		switch (this.reportName) {
			case 'USER TRANSACTIONS REPORT':
				this.columnSortDirections = Object.assign({}, this.columnSortDirections1);
        		this.columnSortDirections[sortState.active] = sortState.direction;
				break;
			case 'User Payments GST REPORT':
				this.userGSTPaymentSortDirections = Object.assign({}, this.userGSTPaymentColumnSortDirections);
        this.userGSTPaymentSortDirections[sortState.active] = sortState.direction;
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
console.log("changeReport>>this.reportName",this.reportName);
		switch (this.reportName) {
			case 'USER TRANSACTIONS REPORT':
				this.getUserTransactionReport();
				break;
			case 'User Payments GST REPORT':
				this.getUserGSTPaymentReport();
				break;
			default:
				
		}

	  }
	  viewReport(row:any){
		console.log("viewReport>>this.reportName",this.reportName);
		switch (this.reportName) {
			case 'USER TRANSACTIONS REPORT':
				this.userTransactionReport = Object.assign(row);
				break;
			case 'User Payments GST REPORT':
			this.userGSTPaymentReport = Object.assign(row);
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
			if(data==null){
				data=new ReportMockData().tranData();
			}
		   this.ELEMENT_DATA = Object.assign([],data);
		   this.dataSource = new MatTableDataSource(this.ELEMENT_DATA);
		   this.dataSource.sort = this.sort;
		   this.dataSource.paginator = this.paginator;
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
			if(data==null){
				data=new ReportMockData().usergstp();
			}
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

	  
	
}
