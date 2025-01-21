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
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { FilterData, FiltersRequestModel } from 'src/app/finance/reports/model/report-filters-model';
import { ReportService } from 'src/app/finance/reports/service/reportService';
import { TenantService } from '../../tenant.service';
import { TenantDetailPortfolio } from '../model/tenant-details';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';

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
	pageSize = 25;
	pageSizeOptions: number[] = [25, 50,100,200];
	fromDate:string='';
	toDate:string='';
	reportName:string ='';
	filtersRequest :FiltersRequestModel = new FiltersRequestModel();
	public lastPageSize:number=0;
	public totalProduct:number=0;
	sortActive:string="transactionDate";
	sortDirection:string="desc";
	selectedReportColumns: any[] = this.getColumnsForSelectedReport('Tenant Transactions Report');
	reportDataList :any[]=[];
	reportDataSource: MatTableDataSource<any>=new MatTableDataSource(this.reportDataList);
	displayedColumns: string[] = [];
	columnHeaders = {} ;
	reportColumnsList = [] ;
	reportNamesList = this.reportService.reportNamesList;
	transactionHeader:string="";
	@ViewChild(MatSort) sort: MatSort;
	@ViewChild(MatPaginator) paginator: MatPaginator;
	tdpf : TenantDetailPortfolio = new TenantDetailPortfolio();
	reason :string ='';
	constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,private reportService : ReportService,private confirmationDialogService:ConfirmationDialogService,
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
		this.fromDate=this.getLastMonthDate();
		this.toDate=this.getCurrentDate();
		this.reportColumnsList=reportService.reportColumnsList;
		this.columnHeaders = reportService.columnHeaders;

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

		this.getTenantsDetails();
	}
	selectProfile(selectTab:number,header:string){
		this.selectedTab=selectTab;
		this.sectionTabHeader=header;
	}

	getTenantsDetails(){
		this.spinner.show();
		this.tenantService.getTenantsDetails(this.tenantId).subscribe((data) => {
		if(data?.data){
			this.tdpf = data.data;
		  }else{
			this.tdpf = new TenantDetailPortfolio();
		  }
		this.spinner.hide();
		},error =>{
			console.log("Error:",error);
			console.log("Error.status:",error.status);
				console.log("Error.error:",error.error);
				console.log("error.error.error:",error.error.error);
		  this.spinner.hide();
		  this.router.navigate(['/tenants']);
		  if(error.status == 0) {
			this.notifyService.showError("Internal Server Error/Connection not established", "")
		 }else if(error.status==401){
			console.error("Unauthorised");
		}else if(error.status==403){
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
					console.log("error",error)
				str=error.error.error;
				}else{
				  str=error.error.message;
				  str=str.substring(str.indexOf(":")+1);
				}
				console.log("Error:",str);
				this.errorMsg=str;
			}
		
			if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
		  }
		}); 	
	 }
	
	  
	  selectTransaction(header:string){
		this.transactionHeader=header;
		if(header=='Due History'){
			this.sortActive="pendingDueDate";
			this.reportName = "Tenant Dues Report";
		}else if(header=='Refund History'){
			this.sortActive="pendingDueDate";
			this.reportName = "Tenant Refunds Report";
		}else{
			this.sortActive="transactionDate";
			this.reportName = "Tenant Transactions Report";
		}

		this.getReportSearchBy();
	}

	

	getColumnsForSelectedReport(name:string) {
		const report = this.reportService.reportColumnsList.find(n => n.reportName === name);
		return report?report.columns:[];
	 }

	  getCurrentDate(): string {
		const today = new Date();
		return this.formatDate(today);
	  }
	
	  getLastMonthDate(): string {
		const today = new Date();
		today.setMonth(today.getMonth() - 1); 
		return this.formatDate(today);
	  }
	
	  formatDate(date: Date): string {
		const year = date.getFullYear();
		const month = (date.getMonth() + 1).toString().padStart(2, '0'); 
		const day = date.getDate().toString().padStart(2, '0');
		const hours = date.getHours().toString().padStart(2, '0');
  		const minutes = date.getMinutes().toString().padStart(2, '0');

		return `${year}-${month}-${day}T${hours}:${minutes}`
	  }	

	  pageChanged(event:any){
		this.reportDataSource=new MatTableDataSource<any>();
		if(this.lastPageSize!=event.pageSize){
			this.paginator.pageIndex=0;
			event.pageIndex=0;
		   }
		 this.pageSize=event.pageSize;
		 this.getReportDetails(this.paginator.pageIndex , event.pageSize,this.sortActive,this.sortDirection);
	   }

 	 onSortData(sort: Sort) {
		this.sortActive=sort.active;
		this.sortDirection=sort.direction;
		this.paginator.pageIndex=0;
		 this.getReportDetails(this.paginator.pageIndex, this.pageSize,this.sortActive,this.sortDirection);
	   }

	getReportSearchBy(){
		this.selectedReportColumns= this.getColumnsForSelectedReport(this.reportName);
		this.reportDataSource.paginator = this.paginator;
		this.getReportDetails(this.paginator.pageIndex , this.paginator.pageSize,this.sortActive,this.sortDirection);
	}

	getReportDetails(pageIndex:number,pageSize:number,sortActive:string,sortDirection:string){
		this.authService.checkLoginUserVlidaate();
		if(!this.fromDate || !this.toDate || new Date(this.fromDate)> new Date(this.toDate)){
			return;
		}
		this.lastPageSize=pageSize;
		this.filtersRequest.pageIndex=pageIndex;
		this.filtersRequest.pageSize=pageSize;
		this.filtersRequest.sortActive=sortActive;
		this.filtersRequest.sortDirection=sortDirection.toUpperCase();
		let filterData = new FilterData();
		filterData.tenantContactNum = this.tdpf.profile.contactNumber;
		this.filtersRequest.filterData = JSON.stringify(filterData) ;
		this.filtersRequest.fromDate = (this.fromDate.replace('T',' '))+':00';
		this.filtersRequest.toDate = (this.toDate.replace('T',' '))+':00';
		this.filtersRequest.reportType=this.reportNamesList.filter(n=>n.name == this.reportName)[0].key;
	
		this.spinner.show();
		this.reportService.getReportsDetails(this.filtersRequest).subscribe((data) => {
		  if(data?.data?.length >0){
				this.totalProduct=data.count;
				this.reportDataList=Object.assign([],data.data);
				this.reportDataSource = new MatTableDataSource(this.reportDataList);
			}else{
			  this.totalProduct=0;
			  this.reportDataList=Object.assign([]);
			  this.reportDataSource =  new MatTableDataSource(this.reportDataList);
			}
			this.spinner.hide();
		},error =>{
		  this.spinner.hide();
		  if(error.status == 0) {
			this.notifyService.showError("Internal Server Error/Connection not established", "")
		 }else if(error.status==401){
			console.error("Unauthorised");
		}else if(error.status==403){
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
				str=error.error.error;
				}else{
				  str=error.error.message;
				  str=str.substring(str.indexOf(":")+1);
				}
				
				console.log("Error:",str);
				this.errorMsg=str;
			}
		
			if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
		  }
		}); 
	}
	
	doDeactivateActivate(): void {
		this.submitted =true;
		if(!this.reason){
			return ;
		}
		this.tdpf.profile.resoan = this.reason ;
		this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want to '+(this.tdpf.profile.status == 'Active' ? 'Deactivate' : 'Activate') + ' '+this.tdpf.profile.tenantName+' ?')
		.then(
		   (confirmed) =>{
			if(confirmed){
				this.authService.checkLoginUserVlidaate();
				this.spinner.show();
				this.tenantService.doDeactivateActivate(this.tdpf.profile).subscribe(res => {

				this.submitted =false;
				this.spinner.hide();
				}, error => {
				 this.spinner.hide();
				if(error.status == 0) {
					this.notifyService.showError("Internal Server Error/Connection not established", "")
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
			}).catch(
				() => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
			);
	  }
}
