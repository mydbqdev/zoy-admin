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
import { MatSort, Sort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { ZoyOwner } from '../models/zoy-owner-model';
import { MatTableDataSource } from '@angular/material/table';
import { FormBuilder } from '@angular/forms';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';
import { GenerateZoyCodeService } from '../../service/zoy-code.service';

@Component({
  selector: 'app-managing-owner',
  templateUrl: './managing-owner.component.html',
  styleUrl: './managing-owner.component.css'
})
export class ManageOwnerComponent implements OnInit, AfterViewInit {
	displayedColumns: string[] = ['zoy_code', 'owner_name', 'email_id', 'mobile_no','noof_properties', 'status','action'];
	public ELEMENT_DATA:ZoyOwner[]=[];
	orginalFetchData:ZoyOwner[]=[];
	searchText:string='';
	dataSource:MatTableDataSource<ZoyOwner>=new MatTableDataSource<ZoyOwner>();
	columnSortDirectionsOg: { [key: string]: string | null } = {
	  zoy_code: null,
	  owner_name: null,
	  email_id: null,
	  noof_properties: null,
	  status: null
	};
	stopPropagation(event: MouseEvent): void {
		event.stopPropagation();
	  }
	generateZCode : ZoyOwner=new ZoyOwner();
	public userNameSession: string = "";
	  errorMsg: any = "";
	  mySubscription: any;
	  isExpandSideBar:boolean=true;
	  @ViewChild(SidebarComponent) sidemenuComp;
	  @ViewChild(MatSort) sort: MatSort;
	  @ViewChild(MatPaginator) paginator: MatPaginator;
	  public rolesArray: string[] = [];
	  submitted=false;
	  columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);
	  private _liveAnnouncer = inject(LiveAnnouncer);

	  pageSize: number = 10; 
	  pageSizeOptions: number[] = [10, 20, 50]; 
	  totalProduct: number = 0;

	  constructor(private generateZoyCodeService : GenerateZoyCodeService,private route: ActivatedRoute, private router: Router,private formBuilder: FormBuilder, private http: HttpClient, private userService: UserService,
		  private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService, private confirmationDialogService:ConfirmationDialogService) {
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
		  this.getZoyOwnerList()
	  }
	  ngAfterViewInit() {
		  this.sidemenuComp.expandMenu(2);
		  this.sidemenuComp.activeMenu(2, 'manage-owner');
		  this.dataService.setHeaderName("Manage Owner");
	  }
   
	  getZoyOwnerList(){
		  this.authService.checkLoginUserVlidaate();
		  this.spinner.show();
		  this.generateZoyCodeService.getGeneratedZoyCodeDetails().subscribe(data => {
			  this.orginalFetchData=  Object.assign([],data);
			  this.ELEMENT_DATA = Object.assign([],data);
			  this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
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
			  //if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
			  this.notifyService.showError(this.errorMsg, "");
		  }
		  });
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

	filterData(){
		console.info("searchText:"+this.searchText);
		if(this.searchText==''){
			this.ELEMENT_DATA = Object.assign([],this.orginalFetchData);
			this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
		}else{
			const pagedData = Object.assign([],this.orginalFetchData.filter(data =>
				data.owner_name.toLowerCase().includes(this.searchText.toLowerCase()) || data.email_id.toLowerCase().includes(this.searchText.toLowerCase()) || data.mobile_no?.toLowerCase().includes(this.searchText.toLowerCase()) || data.noof_properties?.toLowerCase().includes(this.searchText.toLowerCase()) || data.zoy_code?.toLowerCase().includes(this.searchText.toLowerCase())
			));
			this.ELEMENT_DATA = Object.assign([],pagedData);
			this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
		}

		this.dataSource.sort = this.sort;
		this.dataSource.paginator = this.paginator;
	}
	resetFilter(){
		this.searchText='';
		this.filterData();
		this.statuses.filter(data=>data.selected=false);
		this. selectedFilterStatus();
	}

	statuses = [
		{ id: 1, name: 'Active', selected: false },
		{ id: 2, name: 'Inactive', selected: false },
		{ id: 3, name: 'Blocked', selected: false }
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
		.map(status => status.name);
	  }
	  // Apply and process the selected statuses
	  applyStatuses(): void {
		console.log('Selected Statuses:', this.selectedStatuses);
	  }
	  
  }  