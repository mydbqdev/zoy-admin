import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { NavigationEnd } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { UserService } from 'src/app/common/service/user.service';
import { AuthService } from 'src/app/common/service/auth.service';
import { DataService } from 'src/app/common/service/data.service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import { SidebarComponent } from 'src/app/components/sidebar/sidebar.component';
import { LeadsService } from '../service/leads.service';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';


@Component({
	selector: 'app-leads',
	templateUrl: './leads.component.html',
	styleUrls: ['./leads.component.css']
})
export class LeadsComponent implements OnInit, AfterViewInit {
	public userNameSession: string = "";
	errorMsg: any = "";
	mySubscription: any;
	isExpandSideBar:boolean=true;
	@ViewChild(SidebarComponent) sidemenuComp;
	public rolesArray: string[] = [];
	reportDataList :any[]=[];
	reportDataSource: MatTableDataSource<any>=new MatTableDataSource(this.reportDataList);;
	reportData={};
	displayedColumns: string[] = [];
	columnHeaders = {} ;
	reportColumnsList = [] ;
	sortActive:string="customerName";
	sortDirection:string="asc";
    @ViewChild(MatSort) sort: MatSort;
	@ViewChild("paginator",{static:true}) paginator: MatPaginator;
	public lastPageSize:number=0;
	public totalProduct:number=0;
	pageSize:number=10;
	pageSizeOptions=[10,20,50];
	selectedReportColumns: any[] = this.getColumnsForSelectedReport('Leads');
	cityLocation: string[] = [];
	reportNamesList = this.leadService.reportNamesList;
	cityLocationName:string='';
	fromDate:string='';
	toDate:string='';
	reportName:string ='Leads Report';
	constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,
		private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService,private leadService:LeadsService) {
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

		this.reportColumnsList=leadService.reportColumnsList;
		this.columnHeaders = leadService.columnHeaders;
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
		this.sidemenuComp.activeMenu(6, 'leads');
		this.dataService.setHeaderName("Leads");

		this.loadLeads();
	}

	getColumnsForSelectedReport(name:string) {
		const report = this.leadService.reportColumnsList.find(n => n.reportName === name);
		return report?report.columns:[];
	 }
	pageChanged(event:any){
		this.reportDataSource=new MatTableDataSource<any>();
		if(this.lastPageSize!=event.pageSize){
			this.paginator.pageIndex=0;
			event.pageIndex=0;
		   }
		 this.pageSize=event.pageSize;
		 //this.getReportDetails(this.paginator.pageIndex , event.pageSize,this.sortActive,this.sortDirection);
	   }

 	 onSortData(sort: Sort) {
		this.sortDirection= this.sortActive==sort.active ? 
							((this.sortDirection === 'asc' && sort.direction === 'asc')?"desc"
							:(this.sortDirection === 'desc' && sort.direction === 'desc'?"asc":'desc')):this.sortDirection;
		this.sortActive=sort.active;
		this.paginator.pageIndex=0;
		// this.getReportDetails(this.paginator.pageIndex, this.pageSize,this.sortActive,this.sortDirection);
	   }
	   loadLeads(){
		this.sortActive = this.getColumnsForSelectedReport(this.reportName)[0] || this.sortActive;
		this.paginator.pageIndex=0;
		this.pageSize = this.paginator.pageSize;
		this.totalProduct=5;
		this.reportDataList=Object.assign([],this.leadService.leadData);
		this.reportDataSource = new MatTableDataSource(this.reportDataList);
	   }
}
