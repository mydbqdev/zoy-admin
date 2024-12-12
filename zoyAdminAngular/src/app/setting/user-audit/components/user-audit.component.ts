import { AfterViewInit, Component, OnInit, ViewChild, inject } from '@angular/core';
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
import { MatSort, Sort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { FiltersRequestModel } from 'src/app/finance/reports/model/report-filters-model';
import { UserAuditService } from '../service/user-audit-service';
import { UserAuditModel } from '../models/user-audit-model';
import { FormControl } from '@angular/forms';
import { UserListModel } from '../models/userlist-model';


@Component({
	selector: 'app-user-audit',
	templateUrl: './user-audit.component.html',
	styleUrls: ['./user-audit.component.css']
})

export class UserAuditComponent implements OnInit, AfterViewInit {
	private _liveAnnouncer = inject(LiveAnnouncer);
	public userNameSession: string = "";
	errorMsg: any = "";
	mySubscription: any;
	isExpandSideBar:boolean=true;
	displayedColumns: string[] = ['created_on','user_name','type','history_data'];
	public ELEMENT_DATA:UserAuditModel[]=[];
	reportDataList :any[]=[];
	reportDataSource: MatTableDataSource<any>=new MatTableDataSource(this.reportDataList);
	dataSource:MatTableDataSource<UserAuditModel>=new MatTableDataSource<UserAuditModel>();
	filterActivitiesData: UserAuditModel[] = [];
	columnSortDirectionsOg: { [key: string]: string | null } = {
		created_on: null,
        user_name:null,
        type:null,
		history_data: null
	  };
	  columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);
	searchText:string='';
	@ViewChild(SidebarComponent) sidemenuComp;
	@ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;
	public lastPageSize:number=0;
	public totalProduct:number=0;
	pageSize:number=10;
	pageSizeOptions=[10,20,50];
	sortActive:string="created_on";
	sortDirection:string="desc";
	public rolesArray: string[] = [];
    searchControl = new FormControl();
	filtersRequest :FiltersRequestModel = new FiltersRequestModel();
    username: string = '';  
    userNameList: UserListModel[] = []; 
    selectedValue: string = '';   
	reportName:string ='User Audit Report';
	downloadType :string='';
	constructor(private userAuditService: UserAuditService, private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,
		private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService) {
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
		
        this.getUserNameList();

	}
	ngAfterViewInit() {
		this.sidemenuComp.expandMenu(4);
		this.sidemenuComp.activeMenu(4, 'user-audit');
		this.dataService.setHeaderName("User Audit");
		
        this.dataSource.paginator = this.paginator;
		this.getUserAuditdetails(this.paginator.pageIndex , this.paginator.pageSize,this.sortActive,this.sortDirection);
	}


    submit(): void {
        this.paginator.pageIndex=0;
        this.getUserAuditdetails(this.paginator.pageIndex, this.pageSize,this.sortActive,this.sortDirection);
      }
       
	filterData(event){
        const charCode = (event.which) ? event.which : event.keyCode;
        const inputValue = event.target.value + String.fromCharCode(charCode);
        if (inputValue.startsWith(' ')) {
               return false;
             }
		if (charCode === 13) {
		this.submit();
	  }
	}
      pageChanged(event:any){
		this.dataSource=new MatTableDataSource<any>();
		if(this.lastPageSize!=event.pageSize){
			this.paginator.pageIndex=0;
			event.pageIndex=0;
		   }
		 this.pageSize=event.pageSize;
		 this.getUserAuditdetails(this.paginator.pageIndex , event.pageSize,this.sortActive,this.sortDirection);
	   }
       onSortData(sort: Sort) {
		this.columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);
		this.columnSortDirections[sort.active] = sort.direction;
		  if (sort.direction) {
			this._liveAnnouncer.announce(`Sorted ${sort.direction}ending`);
		  } else {
			this._liveAnnouncer.announce('Sorting cleared');
		  }
		this.sortActive=sort.active;
		this.sortDirection=sort.direction!="" ? sort.direction:"asc";
		this.paginator.pageIndex=0;
		this.getUserAuditdetails(this.paginator.pageIndex, this.pageSize,this.sortActive,this.sortDirection);
	   }

       getUserAuditdetails(pageIndex:number,pageSize:number,sortActive:string,sortDirection:string){
		this.authService.checkLoginUserVlidaate();
		this.spinner.show();
		this.lastPageSize=pageSize;
		this.filtersRequest.pageIndex=pageIndex;
		this.filtersRequest.pageSize=pageSize;
		this.filtersRequest.sortActive=sortActive;
		this.filtersRequest.sortDirection=sortDirection.toUpperCase();
		this.columnSortDirections[sortActive] = sortDirection;
        this.filtersRequest.searchText=this.searchText;
        this.filtersRequest.userEmail=this.username;
		this.filtersRequest.activity=this.selectedValue;
		this.filtersRequest.downloadType="";
		this.userAuditService.getUserAuditdetails(this.filtersRequest).subscribe(data => {
			if(data?.data?.length >0){
				  this.totalProduct=data.count;
				  this.ELEMENT_DATA=Object.assign([],data.data);
				  this.dataSource = new MatTableDataSource(this.ELEMENT_DATA);
			  }else{
				this.totalProduct=0;
				this.ELEMENT_DATA=Object.assign([]);
				 this.dataSource =  new MatTableDataSource(this.ELEMENT_DATA);
			  }
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

      getUserNameList(){
       this.authService.checkLoginUserVlidaate();
       this.spinner.show();
       this.userAuditService.getUserNameList().subscribe(data => {
       this.userNameList=Object.assign([],data);
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

	 download(){   
		this.authService.checkLoginUserVlidaate();
	    this.filtersRequest.sortActive=this.sortActive;
		this.filtersRequest.sortDirection=this.sortDirection.toUpperCase();
		this.filtersRequest.searchText=this.searchText;
        this.filtersRequest.userEmail=this.username;
		this.filtersRequest.activity=this.selectedValue;
		this.filtersRequest.downloadType = this.downloadType;
		this.userAuditService.downloadReport(this.filtersRequest).subscribe((data) => { 
	
			if(data!=null && data!=undefined && data!='' && data.size!=0){ 
				let extension= 'text/csv';
				switch (this.downloadType) {
					
						case 'excel':
						extension='application/vnd.ms-excel'
						this.downloadType='xlsx'
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
			  link.download = this.reportName+'.'+this.downloadType;
			  document.body.appendChild(link);
			  link.click();
			}else{
			  this.notifyService.showWarning("The record is not available", "");
			}
			this.downloadType='';
		  }, async error => {
			this.downloadType='';
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
					str = error.error;
				  } else {
					str = error.message;
					str = str.substring(str.indexOf(":") + 1);
				  }
				  console.log("Error:" + await str.text());
				  this.errorMsg =  await new Response(str).text()// or use await str.text();
				}
				if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
			  }
			});	
		}


constantType: { key: string, value: string }[] = [
	{ key: 'USER_LOGIN', value: 'User Login' },
	{ key: 'USER_LOGOUT', value: 'User Logout' },
	{ key: 'USER_ADD', value: 'Create User' },
	{ key: 'USER_UPDATE', value: 'Update User' },
	{ key: 'USER_ACTIVE', value: 'Activate User' },
	{ key: 'USER_INACTIVE', value: 'Deactivate User' },
	{ key: 'USER_DELETE', value: 'Delete User' },	
	{ key: 'USER_ROLE_APPROVED', value: 'Role Approved for User' },
	{ key: 'USER_ROLE_REJECTED', value: 'Role Rejected for User' },
	{ key: 'ZOY_CODE_GENERATE', value: 'Generate Zoy Code' },
	{ key: 'ADMIN_ROLE_CREATE', value: 'Create Admin Role' },
	{ key: 'ADMIN_ROLE_UPDATE', value: 'Update Admin Role' },
	{ key: 'ADMIN_ROLE_DELETE', value: 'Delete Admin Role' },
	{ key: 'USER_ROLE_ASSIGN', value: 'Assign Role to User' },
	{ key: 'ZOY_CODE_RESEND', value: 'Resend Zoy Code' },
	{ key: 'ADMIN_DB_CONFIG_CREATE',value:'Create Database Configuration'},
	{ key: 'ADMIN_DB_CONFIG_UPDATE',value:'Update Database Configuration'},
	{ key: 'ADMIN_MASTER_CONFIG_CREATE',value:'Create Master Configuration'},
	{ key: 'ADMIN_MASTER_CONFIG_UPDATE',value:'Update Master Configuration'},
	{ key: 'ADMIN_MASTER_CONFIG_DELETE',value:'Delete Master Configuration'}
	
  ];
}

