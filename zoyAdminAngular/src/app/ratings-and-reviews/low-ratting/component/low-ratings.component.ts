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
import { SidebarComponent } from 'src/app/components/sidebar/sidebar.component';
import { LowRatingsService } from '../low-ratings.service';
import { FilterData, FiltersRequestModel } from 'src/app/report/model/report-filters-model';
import { ReviewsModel } from 'src/app/report/model/reviews-model';
import { ReportsService } from 'src/app/report/service/reportService';
 
@Component({
	selector: 'app-low-ratings',
	templateUrl: './low-ratings.component.html',
	styleUrls: ['./low-ratings.component.css']
})

export class LowRatingsComponent implements OnInit, AfterViewInit {
	
	lowRatingDataList :any[]=[];
	lowRatingDataSource: MatTableDataSource<any>=new MatTableDataSource(this.lowRatingDataList);;
	displayedColumns: string[] = ['reviewDate','pgName','ownerName','ownerContactNum', 'customerName','customerMobileNo','cleanliness', 'accommodation', 'amenities', 'maintenance','valueForMoney','overallRating'];
	columnHeaders = {} ;
	sortActive:string="overallRating";
	sortDirection:string="asc";
	@ViewChild(SidebarComponent) sidemenuComp;
    @ViewChild(MatSort) sort: MatSort;
	@ViewChild("paginator",{static:true}) paginator: MatPaginator;
	public lastPageSize:number=0;
	public totalProduct:number=0;
	pageSize:number=10;
	pageSizeOptions=[10,20,50];
	cityLocation: string[] = [];
	cityLocationName:string='';
	@ViewChild('reviewsModelClose') reviewsModelClose: any;
	filtersRequest :FiltersRequestModel = new FiltersRequestModel();
	public userNameSession: string = "";
	errorMsg: any = "";
	mySubscription: any;
	rolesArray:string[]=[];
	isExpandSideBar:boolean=true;
	reviewsReplyDetails:ReviewsModel=new ReviewsModel();
	searchText:string='';
	constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,
		private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService,private supportReportService : LowRatingsService,private reportsService : ReportsService) {
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
		this.columnHeaders = reportsService.columnHeaders;
		
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
		this.sidemenuComp.expandMenu(13);
		this.sidemenuComp.activeMenu(13, 'low-ratings');
		this.dataService.setHeaderName("Low Ratings");
	}

	  getCityList(){
		this.authService.checkLoginUserVlidaate();
		this.spinner.show();
		this.reportsService.getCityList().subscribe(data => {
		this.cityLocation=data;
		this.cityLocationName =this.cityLocation?.length>0?this.cityLocation[0]:"";
		this.callLowRattingDetails();
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

	callLowRattingDetails(){
		this.searchText = '';
		this.paginator.pageIndex=0;
		this.pageSize = this.paginator.pageSize;
		this.getLowRattingDetails(this.paginator.pageIndex , this.paginator.pageSize,this.sortActive,this.sortDirection);
	}
   searchBy($event: KeyboardEvent){
		if ($event.keyCode === 13) {
			this.paginator.pageIndex=0;
			this.pageSize = this.paginator.pageSize;
			this.getLowRattingDetails(this.paginator.pageIndex , this.paginator.pageSize,this.sortActive,this.sortDirection);
	  }
	}

	pageChanged(event:any){
		this.lowRatingDataSource=new MatTableDataSource<any>();
		if(this.lastPageSize!=event.pageSize){
			this.paginator.pageIndex=0;
			event.pageIndex=0;
		   }
		 this.pageSize=event.pageSize;
		 this.getLowRattingDetails(this.paginator.pageIndex , event.pageSize,this.sortActive,this.sortDirection);
	   }

 	 onSortData(sort: Sort) {
		this.sortDirection= this.sortActive==sort.active ? 
							((this.sortDirection === 'asc' && sort.direction === 'asc')?"desc"
							:(this.sortDirection === 'desc' && sort.direction === 'desc'?"asc":'desc')):this.sortDirection;
		this.sortActive=sort.active;
		this.paginator.pageIndex=0;
		 this.getLowRattingDetails(this.paginator.pageIndex, this.pageSize,this.sortActive,this.sortDirection);
	   }

	getLowRattingDetails(pageIndex:number,pageSize:number,sortActive:string,sortDirection:string){
		this.authService.checkLoginUserVlidaate();
		this.lastPageSize=pageSize;
		this.filtersRequest.pageIndex=pageIndex;
		this.filtersRequest.pageSize=pageSize;
		this.filtersRequest.sortActive=sortActive;
		this.filtersRequest.sortDirection=sortDirection.toUpperCase();
		this.filtersRequest.searchText = this.searchText || '';
		this.filtersRequest.lowRating = '3';
		this.filtersRequest.fromDate = '1025-07-07 00:00:00';
		this.filtersRequest.toDate = '9025-08-07 23:59:59';
		this.filtersRequest.cityLocation = this.cityLocationName;
		this.filtersRequest.reportType='reviewsAndRatingReport';
		this.filtersRequest.filterData = JSON.stringify(new FilterData()) ;
		this.spinner.show();
		this.supportReportService.getReportsDetails(this.filtersRequest).subscribe((data) => {
		 if(data?.data?.length >0){
			this.totalProduct=data.count;
			this.lowRatingDataList=Object.assign([],data.data);
			this.lowRatingDataSource = new MatTableDataSource(this.lowRatingDataList);
		  }else{
  		    this.totalProduct=0;
			this.lowRatingDataList=Object.assign([]);
			this.lowRatingDataSource =  new MatTableDataSource(this.lowRatingDataList);
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

	downloadProgress:boolean=false;
	downloadPdf(type:string){   
		this.authService.checkLoginUserVlidaate();
		
		this.filtersRequest.sortActive=this.sortActive;
		this.filtersRequest.sortDirection=this.sortDirection.toUpperCase();
		this.filtersRequest.searchText = this.searchText || '';
		this.filtersRequest.fromDate = '1025-07-07 00:00:00';
		this.filtersRequest.toDate = '9025-08-07 23:59:59';
		this.filtersRequest.cityLocation = this.cityLocationName;
		this.filtersRequest.lowRating = '3';
		this.filtersRequest.reportType='reviewsAndRatingReport';
		this.filtersRequest.filterData = JSON.stringify(new FilterData()) ;
		this.filtersRequest.downloadType = type ;
		this.downloadProgress=true;
		this.reportsService.downloadReportPdf(this.filtersRequest).subscribe((data) => { 
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
			  link.download = 'Reviews-and-RatingReport.'+type;
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
