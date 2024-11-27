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
import { DbMasterConfiguration } from '../models/dbmaster-confuguration-model';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort, Sort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { DbMasterConfigurationService } from '../services/db-master-configuration.service';
import { settingTypeObjClmApiDetailsModel } from '../models/db-setting-models';

@Component({
	selector: 'app-db-master-configuration',
	templateUrl: './db-master-configuration.component.html',
	styleUrls: ['./db-master-configuration.component.css']
})
export class DbMasterConfigurationComponent implements OnInit, AfterViewInit {
    private _liveAnnouncer = inject(LiveAnnouncer);
	public userNameSession: string = "";
    pageSize: number = 10; 
    pageSizeOptions: number[] = [10, 20, 50]; 
    totalProduct: number = 0; 
	errorMsg: any = "";
	mySubscription: any;
	isExpandSideBar:boolean=true;
	@ViewChild(SidebarComponent) sidemenuComp;
	public rolesArray: string[] = [];
    public ELEMENT_DATA:DbMasterConfiguration[];
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    displayedColumns: string[] = ['firstName', 'lastName', 'userEmail', 'designation', 'action'];
    dataSource:MatTableDataSource<DbMasterConfiguration>=new MatTableDataSource<DbMasterConfiguration>();
    columnSortDirectionsOg: { [key: string]: string | null } = { 
      firstName:null,
      lastName: null,
      userEmail: null,
      designation: null,
      action: null
    };
    columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);


    settingType :string ='';
    settingTypeDetails:settingTypeObjClmApiDetailsModel=new settingTypeObjClmApiDetailsModel();
    settingTypeObjClmApiDetailsList:settingTypeObjClmApiDetailsModel[]=[];
    selectedsettingColumns :string[]=[];
    dbSettingDataList :any[]=[];
    dbSettingDataSource: MatTableDataSource<any>=new MatTableDataSource(this.dbSettingDataList);;
    dbSettingDataObj={};
    columnHeaders = {} ;
    
  
	constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,
		private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService,
    private dbMasterConfigurationService:DbMasterConfigurationService) {
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
    this.columnHeaders = dbMasterConfigurationService.columnHeaders;
    this.settingTypeObjClmApiDetailsList = dbMasterConfigurationService.settingTypeObjClmApiDetails;
    this.settingTypeDetails = this.settingTypeObjClmApiDetailsList[0];
    this.selectedsettingColumns = this.settingTypeDetails.columns ;
    this.settingType = this.settingTypeDetails.type ;
    this.getDbSettingDetails() ;
    console.log("createSetting>this.selectedsettingColumns",this.selectedsettingColumns);
    console.log("createSetting>this.settingTypeObjClmApiDetailsList",this.settingTypeObjClmApiDetailsList);
	}

	ngOnDestroy() {
		if (this.mySubscription) {
			this.mySubscription.unsubscribe();
		}
	}
	ngOnInit() {
		if (this.userNameSession == null || this.userNameSession == undefined || this.userNameSession == '') {
			this.router.navigate(['/']);
		}
        // this.dataSource = new MatTableDataSource(mockData);
        // this.getMasterConfigurationList();
	}
	ngAfterViewInit() {
		this.sidemenuComp.expandMenu(4);
		this.sidemenuComp.activeMenu(4,'db-master-configuration');
		this.dataService.setHeaderName("DB Master Configuration");
        // this.dataSource.paginator = this.paginator;
        // this.dataSource.sort = this.sort;
	}

    getMasterConfigurationList(){
        // this.authService.checkLoginUserVlidaate();
         this.spinner.show();
        //  this.DbMasterConfigurationService.getDbMasterConfigurationList().subscribe(data => {
          this.ELEMENT_DATA = Object.assign([],mockData);
          console.log("this.ELEMENT_DATA",this.ELEMENT_DATA);
           this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
           this.dataSource.sort = this.sort;
           this.dataSource.paginator = this.paginator;
           this.spinner.hide();
    //     }else{
    //       this.ELEMENT_DATA = Object.assign([]);
    //        this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
    //        this.dataSource.sort = this.sort;
    //        this.dataSource.paginator = this.paginator;
    //     }
    //        this.spinner.hide();
    //     }, error => {
    //      this.spinner.hide();
    //      if(error.status==403){
    //        this.router.navigate(['/forbidden']);
    //      }else if (error.error && error.error.message) {
    //        this.errorMsg = error.error.message;
    //        console.log("Error:" + this.errorMsg);
    //        this.notifyService.showError(this.errorMsg, "");
    //      } else {
    //        if (error.status == 500 && error.statusText == "Internal Server Error") {
    //          this.errorMsg = error.statusText + "! Please login again or contact your Help Desk.";
    //        } else {
    //          let str;
    //          if (error.status == 400) {
    //            str = error.error;
    //          } else {
    //            str = error.message;
    //            str = str.substring(str.indexOf(":") + 1);
    //          }
    //          console.log("Error:" + str);
    //          this.errorMsg = str;
    //        }
    //        if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
    //      }
    //    });
       
      
    }

    changeSettingType(){
      this.settingTypeDetails = this.settingTypeObjClmApiDetailsList.find(t=>t.type == this.settingType);
      this.selectedsettingColumns = this.settingTypeDetails.columns ;
      this.getDbSettingDetails() ;
    }
    createSetting(){
      let data = this.settingTypeDetails.obj;
      this.dbSettingDataObj = Object.assign(data);
      console.log("createSetting>this.dbSettingDataObj",this.dbSettingDataObj);
    }
    getElement(row:any){
      let data = this.settingTypeDetails.obj;
      data = Object.assign(row); 
      this.dbSettingDataObj = Object.assign(data);
      console.log("getElement>this.dbSettingDataObj",this.dbSettingDataObj);
    }  
    objectKeys(obj: any) {
      return Object.keys(obj);
      }
    
    getDbSettingDetails(){
        // this.authService.checkLoginUserVlidaate();
        this.spinner.show();
        this.dbMasterConfigurationService.getDbSettingDetails(this.settingTypeDetails.api).subscribe(data => {
          this.dbSettingDataList=Object.assign([],data);
					this.dbSettingDataSource = new MatTableDataSource(this.dbSettingDataList);
          console.log("getDbSettingDetails>>this.dbSettingDataList",this.dbSettingDataList);
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
       

       announceSortChange(sortState: Sort): void {
        this.columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);
        this.columnSortDirections[sortState.active] = sortState.direction;
    
          if (sortState.direction) {
            this._liveAnnouncer.announce(`Sorted ${sortState.direction}ending`);
          } else {
            this._liveAnnouncer.announce('Sorting cleared');
          }
      }

	test(){
		this.notifyService.showNotification("Success","");
	}
    
	
}
const mockData = [
    {
      firstName: "John",
      lastName: "Doe",
      userEmail: "john.doe@example.com",
      designation: "Software Engineer",
    },
    {
      firstName: "Jane",
      lastName: "Smith",
      userEmail: "jane.smith@example.com",
      designation: "Project Manager",
    },
    {
      firstName: "Michael",
      lastName: "Brown",
      userEmail: "michael.brown@example.com",
      designation: "UI/UX Designer",
    },
    {
      firstName: "Emily",
      lastName: "Johnson",
      userEmail: "emily.johnson@example.com",
      designation: "Business Analyst",
    },
    {
      firstName: "Chris",
      lastName: "Lee",
      userEmail: "chris.lee@example.com",
      designation: "QA Engineer",
    },
    {
      firstName: "Sarah",
      lastName: "Miller",
      userEmail: "sarah.miller@example.com",
      designation: "Scrum Master",
    },
    {
      firstName: "David",
      lastName: "Wilson",
      userEmail: "david.wilson@example.com",
      designation: "DevOps Engineer",
    },
    {
      firstName: "Sophia",
      lastName: "Moore",
      userEmail: "sophia.moore@example.com",
      designation: "Full-Stack Developer",
    },
    {
      firstName: "Liam",
      lastName: "Taylor",
      userEmail: "liam.taylor@example.com",
      designation: "Database Administrator",
    },
    {
      firstName: "Olivia",
      lastName: "Anderson",
      userEmail: "olivia.anderson@example.com",
      designation: "Data Scientist",
    },
    {
        firstName: "Liam",
        lastName: "Taylor",
        userEmail: "liam.taylor@example.com",
        designation: "Database Administrator",
      },
      {
        firstName: "Olivia",
        lastName: "Anderson",
        userEmail: "olivia.anderson@example.com",
        designation: "Data Scientist",
      },
  ];
  
