import { AfterViewInit, Component, inject, OnInit, ViewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { NavigationEnd } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { UserService } from 'src/app/common/service/user.service';
import { AuthService } from 'src/app/common/service/auth.service';
import { DataService } from 'src/app/common/service/data.service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import {MatSort, Sort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {MatPaginator} from '@angular/material/paginator';
import { ReportService } from '../service/reportService';
import { SidebarComponent } from 'src/app/components/sidebar/sidebar.component';
import { FilterData, FiltersRequestModel } from '../model/report-filters-model';
import { ReviewsModel } from '../model/reviews-model';
 
@Component({
	selector: 'app-report-list',
	templateUrl: './report-list.component.html',
	styleUrls: ['./report-list.component.css']
})

export class ReportListComponent implements OnInit, AfterViewInit {

	reportDataList :any[]=[];
	reportDataSource: MatTableDataSource<any>=new MatTableDataSource(this.reportDataList);;
	reportData={};
	displayedColumns: string[] = [];
	columnHeaders = {} ;
	reportColumnsList = [] ;
	sortActive:string="customerName";
	sortDirection:string="asc";
	@ViewChild(SidebarComponent) sidemenuComp;
    @ViewChild(MatSort) sort: MatSort;
	@ViewChild("paginator",{static:true}) paginator: MatPaginator;
	public lastPageSize:number=0;
	public totalProduct:number=0;
	pageSize:number=10;
	pageSizeOptions=[10,20,50];
	selectedReportColumns: any[] = this.getColumnsForSelectedReport('Tenant Transactions Report');
	filterData :FilterData=new FilterData();
	cityLocation: string[] = [];
	reportNamesList = this.reportService.reportNamesList;
	cityLocationName:string='';
	fromDate:string='';
	toDate:string='';
	reportName:string ='Tenant Transactions Report';
	@ViewChild('reviewsModelClose') reviewsModelClose: any;
	filtersRequest :FiltersRequestModel = new FiltersRequestModel();
	public userNameSession: string = "";
	errorMsg: any = "";
	mySubscription: any;
	rolesArray:string[]=[];
	isExpandSideBar:boolean=true;
	reviewsReplyDetails:ReviewsModel=new ReviewsModel();

	constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,
		private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService,private reportService : ReportService) {
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
		//	this.router.navigate(['/']);
		//}
		this.getCityList();
			
	}
	ngAfterViewInit() {
		this.sidemenuComp.expandMenu(5);
		this.sidemenuComp.activeMenu(5, 'report-list');
		this.dataService.setHeaderName("Reports");
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

	  getCityList(){
		this.authService.checkLoginUserVlidaate();
		this.spinner.show();
		this.reportService.getCityList().subscribe(data => {
		this.cityLocation=data;
		this.cityLocationName =this.cityLocation?.length>0?this.cityLocation[0]:"";
		this.generateReport();
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
			console.log("Error:" , str);
			this.errorMsg = str;
		}
		if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
		}
	});
	
	}

	generateReport(){
		this.sortActive = this.getColumnsForSelectedReport(this.reportName)[0] || this.sortActive;
		this.paginator.pageIndex=0;
		this.pageSize = this.paginator.pageSize;
		this.getReportDetails(this.paginator.pageIndex , this.paginator.pageSize,this.sortActive,this.sortDirection);
	}

	objectKeys(obj: any) {
		return Object.keys(obj);
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
		this.sortDirection= this.sortActive==sort.active ? 
							((this.sortDirection === 'asc' && sort.direction === 'asc')?"desc"
							:(this.sortDirection === 'desc' && sort.direction === 'desc'?"asc":'desc')):this.sortDirection;
		this.sortActive=sort.active;
		this.paginator.pageIndex=0;
		 this.getReportDetails(this.paginator.pageIndex, this.pageSize,this.sortActive,this.sortDirection);
	   }


	  changeReport(){
		this.filterData =new FilterData();
	  }
	

	getReportDetails(pageIndex:number,pageSize:number,sortActive:string,sortDirection:string){
			//	this.authService.checkLoginUserVlidaate();
				if(!this.fromDate || !this.toDate || new Date(this.fromDate)> new Date(this.toDate)){
					return;
				}
				this.lastPageSize=pageSize;
				this.filtersRequest.pageIndex=pageIndex;
				this.filtersRequest.pageSize=pageSize;
				this.filtersRequest.sortActive=sortActive;
				this.filtersRequest.sortDirection=sortDirection.toUpperCase();

				this.filtersRequest.fromDate = (this.fromDate.replace('T',' '))+':00';
				this.filtersRequest.toDate = (this.toDate.replace('T',' '))+':00';
				this.filtersRequest.cityLocation = this.cityLocationName;
				this.filtersRequest.reportType=this.reportNamesList.filter(n=>n.name == this.reportName)[0].key;
				this.filtersRequest.filterData = JSON.stringify(this.filterData) ;
			
				if( this.reportName =='Owner Payments Dues Report' || this.reportName =='Owner Payments Gst Report'){
					this.selectedReportColumns= this.getColumnsForSelectedReport(this.reportName);
					this.totalProduct=0;
					this.reportDataList=Object.assign([]);
					this.reportDataSource = new MatTableDataSource(this.reportDataList);

				}else{
				this.spinner.show();
				this.reportService.getReportsDetails(this.filtersRequest).subscribe((data) => {
				this.selectedReportColumns= this.getColumnsForSelectedReport(this.reportName);

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
						console.log("Error:"+str);
						this.errorMsg=str;
					}
					if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
				  }
				}); 
			}
	 	}	 
	
	onExport(element: any): void {
		console.log('Export action triggered for:', element);
	}
	
	downloadProgress:boolean=false;
	downloadPdf(type:string){   
		this.authService.checkLoginUserVlidaate();
		if(!this.fromDate || !this.toDate || new Date(this.fromDate)> new Date(this.toDate)){
			this.downloadProgress=false;
			return;
		}
		
		this.filtersRequest.sortActive=this.sortActive;
		this.filtersRequest.sortDirection=this.sortDirection.toUpperCase();

		this.filtersRequest.fromDate= (this.fromDate.replace('T',' '))+':00';
		this.filtersRequest.toDate= (this.toDate.replace('T',' '))+':00';
		this.filtersRequest.cityLocation = this.cityLocationName;
		this.filtersRequest.reportType=this.reportNamesList.filter(n=>n.name == this.reportName)[0].key;
		this.filtersRequest.filterData = JSON.stringify(this.filterData) ;
		this.filtersRequest.downloadType = type ;
		this.downloadProgress=true;
		this.reportService.downloadReportPdf(this.filtersRequest).subscribe((data) => { 
			if(data!=null && data!=undefined && data!='' && data.size!=0){ 
				let extension= 'application/pdf';
				switch (type) {
					case 'pdf':
						extension='application/pdf'
						break;
						case 'excel':
						extension='application/vnd.ms-excel'
						type='xlsx'
						break;
						case 'csv':
						extension='text/csv'
						break;
					default:
						break;
				}
			  var blob = new Blob([data], {type : extension});
			  var fileURL=URL.createObjectURL(blob);
			  
			  const link = document.createElement("a");
			  link.href = fileURL;
			  link.target = '_blank';
			  link.download = this.reportName+'.'+type;
			  document.body.appendChild(link);
			  link.click();
			  this.downloadProgress=false;
			}else{
				this.downloadProgress=false;
			  this.notifyService.showWarning("The record is not available", "");
			}
		  }, async error => {
			  this.downloadProgress=false;
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
				  console.log("Error:" + await str.text());
				  this.errorMsg =  await new Response(str).text()// or use await str.text();
				}
				if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
			  }
			});	
		}
		  
 viweReviewsReplyDetails(element: any): void {
	this.reviewsReplyDetails = element;	
}

		  
}
