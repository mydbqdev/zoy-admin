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
 
@Component({
	selector: 'app-report-list',
	templateUrl: './report-list.component.html',
	styleUrls: ['./report-list.component.css']
})

export class ReportListComponent implements OnInit, AfterViewInit {
	dataSource:MatTableDataSource<ReportsModel>=new MatTableDataSource<ReportsModel>();
	private _liveAnnouncer = inject(LiveAnnouncer);
	displayedColumns: string[] = ['customerID', 'pgId', 'transactionDate', 'transactionStatus','totalAmount','actions'];
	@ViewChild(SidebarComponent) sidemenuComp;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;
  public totalProduct:number=0;
  pageSize:number=10;
  pageSizeOptions=[10,20,50];

	public userNameSession: string = "";
	errorMsg: any = "";
	mySubscription: any;
	columnSortDirections1: { [key: string]: string | null } = {
        customerID: null,
        pgId: null,
        transactionDate: null,
        transactionStatus: null,
        totalAmount: null,
      };
    columnSortDirections = Object.assign({}, this.columnSortDirections1);
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
		this.getRolesData();
	}
	ngAfterViewInit() {
		this.sidemenuComp.expandMenu(5);
		this.sidemenuComp.activeMenu(5, 'report-list');
		this.dataSource.sort = this.sort;
		this.dataSource.paginator = this.paginator;

	}

	ELEMENT_DATA:ReportsModel[]=[]
	getRolesData() {
		this.reportService.getRolesSaved().subscribe(data=>{
this.ELEMENT_DATA=Object.assign([],data);
this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
		});
	}
	announceSortChange(sortState: Sort) {
		this.columnSortDirections = Object.assign({}, this.columnSortDirections1);
        this.columnSortDirections[sortState.active] = sortState.direction;
		
		if (sortState.direction) {
		  this._liveAnnouncer.announce(`Sorted ${sortState.direction}ending`);
		} else {
		  this._liveAnnouncer.announce('Sorting cleared');
		}
	  }

	  onView(element: any): void {
		console.log('View action triggered for:', element);
	}
	
	onExport(element: any): void {
		console.log('Export action triggered for:', element);
	}
	
}
