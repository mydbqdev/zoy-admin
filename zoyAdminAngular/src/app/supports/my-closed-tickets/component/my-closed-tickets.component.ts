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
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { MatSort, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { Filter, SupportRequestParam } from '../../model/support-request-model';
import { SupportList, UpdateStatus } from '../../model/suppot-list-model';
import { SupportService } from '../../service/support.service';
import { SupportDetails } from '../../model/suppot-details-model';


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
	@ViewChild(MatSort) sort: MatSort;
	@ViewChild(MatPaginator) paginator: MatPaginator;
	public rolesArray: string[] = [];
	searchText:string='';
	fromDate:string='';
	toDate:string='';
	param:SupportRequestParam=new SupportRequestParam();
	pageSize: number = 10; 
	pageSizeOptions: number[] = [10, 20, 50]; 
	totalProduct: number = 0;
	public lastPageSize:number=0;
	paramFilter:Filter=new Filter();
	displayedColumns: string[] = ['ticket_id', 'created_date', 'ticket_type', 'priority', 'closedOn','status','action'];
	public ELEMENT_DATA:SupportList[]=[];
	dataSource:MatTableDataSource<SupportList>=new MatTableDataSource<SupportList>();
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
	public updateStatus:UpdateStatus=new UpdateStatus();
	public supportTicketDetails:SupportDetails=new SupportDetails();
	public selectTicket:SupportList;
	constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,
		private spinner: NgxSpinnerService,private supportService : SupportService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService) {
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
		this.getRetrieveData();
	}

	statuses = [
		{ id: 1, name: 'Closed', selected: false },
		{ id: 3, name: 'Cancelled', selected: false },
		{ id: 4, name: 'Resolved', selected: false }
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
		.map(status => status.name.toLocaleLowerCase());
	  }
	  // Apply and process the selected statuses
	  applyStatuses(): void {
		this.paginator.pageIndex=0;	
		this.param.filter.status="('"+this.selectedStatuses.join("','")+"')";
		this.getTicketsList();

	  }
	  applyDates(): void {
		this.param.pageIndex=0
		this.paginator.pageIndex=0;
		this.param.filter.startDate=  (this.fromDate)+' 00:00:00';
		this.param.filter.endDate=  (this.toDate)+' 23:59:59';
		this.getTicketsList();
	  }

	  filterData($event: KeyboardEvent){
		if ($event.keyCode === 13) {
		if(this.searchText==''){
			this.paramFilter.searchText=null;
			this.paramFilter.status=null;
		}else{
			this.paramFilter.searchText=this.searchText;
			this.paramFilter.status=null;
		}
		this.param.pageIndex=0
		this.paginator.pageIndex=0;
		this.param.filter=this.paramFilter;
		this.getTicketsList();
	  }
	}
	resetFilter(){
		this.fromDate='';
		this.toDate='';
		this.searchText='';
		this.param.pageIndex=0
		this.paginator.pageIndex=0;
		this.param.filter=new Filter();
		this.getTicketsList();
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
		  this.param.sortActive=sortState.active;
		  this.param.sortDirection=sortState.direction!="" ? sortState.direction:"asc";
		  this.param.pageIndex=0
		  this.paginator.pageIndex=0;
		  this.getTicketsList();
	  }

	  pageChanged(event:any){
		this.dataSource=new MatTableDataSource<SupportList>();
		if(this.lastPageSize!=event.pageSize){
		this.paginator.pageIndex=0;
		event.pageIndex=0;
		}
		this.param.pageIndex=this.paginator.pageIndex;
		this.param.pageSize= event.pageSize;
		this.getTicketsList();
		}

		getRetrieveData(){
			this.dataSource.sort = this.sort;
			this.dataSource.paginator = this.paginator;
			this.param.pageIndex=this.paginator.pageIndex;
			this.param.pageSize= this.paginator.pageSize;
			this.param.sortDirection="desc";
			this.param.sortActive="created_date";
			this.paramFilter.searchText=null;
			this.paramFilter.status=null;
			this.param.filter=this.paramFilter;
			setTimeout(()=>{
			  this.getTicketsList();
			 },100);
			 this.columnSortDirections["created_date"] = "desc";
		  }

		getTicketsList(){
			this.authService.checkLoginUserVlidaate();
			this.spinner.show();
			this.lastPageSize=this.param.pageSize;
			this.param.isUserActivity=true;
			this.supportService.getTicketsCloseList(this.param).subscribe(data => {
			  
				//this.orginalFetchData=  Object.assign([],data.data);
				this.ELEMENT_DATA = Object.assign([],data.data);
				this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
				//this.dataService.setOwenerListFilter(this.orginalFetchData);
				this.totalProduct=data.count;
				//this.dataService.setOwenerListFilterTotal(this.totalProduct);
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
		public assignTicketNumber:string='';

	downloadAllTicketImages(element: any): void {
		const inquiryNumber = element.ticket_id;

		if (!inquiryNumber) {
			this.notifyService.showWarning("Ticket ID is missing.", "Download Failed");
			return;
		}

		this.spinner.show(); // Show loading spinner

		this.supportService.getTicketImages(inquiryNumber).subscribe({
			next: (response: string) => {
				this.spinner.hide(); // Hide spinner once done
				const imageUrls = response.split(',').map(url => url.trim());

				if (imageUrls.length === 0 || !imageUrls[0]) {
					this.notifyService.showInfo("No images found for this ticket.", "Info");
					return;
				}

				imageUrls.forEach((url, index) => {
					const anchor = document.createElement('a');
					anchor.href = url;
					anchor.download = `ticket_${inquiryNumber}_image_${index + 1}`;
					anchor.target = '_blank'; // Fallback for cross-origin
					document.body.appendChild(anchor);
					anchor.click();
					document.body.removeChild(anchor);
				});
			},
			error: (error) => {
				this.spinner.hide();
				this.notifyService.showError("Failed to download images.", "Error");
				console.error("Image download error:", error);
			}
		});
	}


	downloadImageUrls(imageUrlsString: string): void {
		if (!imageUrlsString) {
			// If you have a notification service, use it
			console.warn("No image URLs found");
			return;
		}

		const imageUrls = imageUrlsString.split(',').map(url => url.trim());

		if (imageUrls.length === 0 || !imageUrls[0]) {
			console.info("No valid image URLs to download");
			return;
		}

		imageUrls.forEach((url, index) => {
			const anchor = document.createElement('a');
			anchor.href = url;
			anchor.download = `image_${index + 1}`;
			anchor.target = '_blank';
			document.body.appendChild(anchor);
			anchor.click();
			document.body.removeChild(anchor);
		});
	}

		
		getDetails(element:any){
			this.assignTicketNumber=element.ticket_id;
			this.selectTicket=Object.assign(element);
			this.authService.checkLoginUserVlidaate();
			this.updateStatus.status=this.selectTicket.status;
			this.updateStatus.inquiryNumber=element.ticket_id;
			this.updateStatus.inquiryType=this.selectTicket.type;
			this.supportService.getInquiryDeatils(this.updateStatus).subscribe(data => {
				this.supportTicketDetails = Object.assign([],data.data);
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
}
