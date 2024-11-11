import { AfterViewInit, Component, ElementRef, inject, OnInit, Renderer2, ViewChild } from '@angular/core';
import { NgxSpinnerService } from 'ngx-spinner';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { AuthService } from 'src/app/common/service/auth.service';
import { UserService } from 'src/app/common/service/user.service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { UserRole } from 'src/app/common/models/user-role';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';
import { MatSort, Sort } from '@angular/material/sort';
import { DataService } from 'src/app/common/service/data.service';
import { SidebarComponent } from 'src/app/components/sidebar/sidebar.component';
import { PermissionApprovalService } from '../service/permission-approval.service';
import { PermissionDetails } from '../model/permission-details';
import { PermissionApproval } from '../model/permission-approval-model';
import { AlertDialogService } from "src/app/common/shared/alert-dialog/alert-dialog.service";

@Component({
  selector: 'app-permission-approval',
  templateUrl: './permission-approval.component.html',
  styleUrl: './permission-approval.component.css'
})
export class PermissionApprovalComponent implements OnInit,AfterViewInit{
  @ViewChild(SidebarComponent) sidemenuComp;
  public ELEMENT_DATA:UserRole[];
  dataSource:MatTableDataSource<UserRole>=new MatTableDataSource<UserRole>();
  pageSize:number=5;
  pageSizeOptions=[5,10,20,50];
  public lastPageSize:number=0;
  public totalProduct:number=0;
  displayedColumns: string[] = [ 'userName', 'userMail', 'roles', 'action'];

 @ViewChild(MatSort) sort: MatSort;
 @ViewChild(MatPaginator) paginator: MatPaginator;

  getUsers : UserRole[] = [];
  editUserRole : UserRole = new UserRole();
  rolesList: PermissionApproval[]=[];

   selectedItems : any[] = [];
   public rolesArray: string[] = [];
   @ViewChild('editCloseModal') editCloseModal : ElementRef;
   @ViewChild('registerCloseModal') registerCloseModal : ElementRef;
  mySubscription: any;
  public userNameSession:string="";
  submitted=false;
	error: string = '';
	form: FormGroup;
	userReg :PermissionDetails=new PermissionDetails();
  isPopupVisible: boolean = false;
  editUserRoleee = { empName: '' };
  userRolePermissions: Array<{ screenName: string; readPrv: boolean; writePrv: boolean }> = [];

  isExpandSideBar:boolean=true;
  constructor(private permissionApprovalService : PermissionApprovalService,private formBuilder: FormBuilder,private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,private notifyService:NotificationService,
    private spinner:NgxSpinnerService,private renderer: Renderer2 ,private authService:AuthService,private confirmationDialogService:ConfirmationDialogService,private dataService:DataService,private alertDialogService: AlertDialogService,private permissionService: PermissionApprovalService){

      this.userNameSession=userService.getUsername();
      if (userService.getUserinfo() != undefined) {
        this.rolesArray = userService.getUserinfo().previlageList;
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
 
  ngOnInit(): void {
    //  if (this.userNameSession == null || this.userNameSession == undefined || this.userNameSession == '') {
    //    this.router.navigate(['/']); 
    //    }
    // this.authService.checkLoginUserVlidaate();
    const empCode = 'EMP123';  // Replace with your hardcoded employee code
    this.loadUserPermissions(empCode);
}

	  

ngAfterViewInit(){
  this.sidemenuComp.expandMenu(3);
  this.sidemenuComp.activeMenu(3,'permission-approval');
  this.dataSource.paginator=this.paginator;
   setTimeout(()=>{
     this.loadInitialData(this.paginator.pageIndex +1, this.paginator.pageSize);
    },100);
}
 
  loadInitialData(pageIndex:number,pageSize:number){  
    this.loadInitialMockData();
    return;
    }
  

  loadUserPermissions(empCode: string): void {
    // Using hardcoded data for demonstration
    const hardcodedResponse = {
      empName: 'John Doe', // Hardcoded employee name
      permissions: [
        { screenName: 'Dashboard', readPrv: true, writePrv: true },
        { screenName: 'Settings', readPrv: true, writePrv: false },
        { screenName: 'Reports', readPrv: false, writePrv: true },
        { screenName: 'user Management', readPrv: true, writePrv: true },
        { screenName: 'Dashboard', readPrv: true, writePrv: true },
        { screenName: 'Settings', readPrv: true, writePrv: false },
        { screenName: 'Reports', readPrv: false, writePrv: true },
        { screenName: 'user Management', readPrv: true, writePrv: true },
        { screenName: 'Dashboard', readPrv: true, writePrv: true },
        { screenName: 'Settings', readPrv: true, writePrv: false },
        { screenName: 'Reports', readPrv: false, writePrv: true },
        { screenName: 'user Management', readPrv: true, writePrv: true },
      ]
    };

    this.userRolePermissions = hardcodedResponse.permissions;
    this.editUserRole.empName = hardcodedResponse.empName;
  }


  getReadIcon(readPrv: boolean): string {
    return readPrv ? 'fa fa-check text-success' : 'fa fa-times text-danger';
  }

  // Return icon class for write permission
  getWriteIcon(writePrv: boolean): string {
    return writePrv ? 'fa fa-check text-success' : 'fa fa-times text-danger';
  }

  // Approve role with the current permissions
  approveRole(): void {
    const dataToSave = {
      empCode: 'EMP123', 
      permissions: this.userRolePermissions
    };

    this.permissionService.saveData(dataToSave).subscribe(
      (response) => {
        this.showPopup('Role has been approved successfully.');
      },
      (error) => {
        console.error('Error approving role:', error);
      }
    );
  }

  rejectRole(): void {
    this.showPopup('Role has been rejected.');
   // this.resetForm();
  }

  showPopup(message: string): void {
    this.isPopupVisible = true;
  }

  loadInitialMockData(){
    var  data ={
        "totalCount": 72,
        "listObj": 
        [
          {
            "empCode": "1010",
            "empName": "Aparna Muvvala",
            "roles": "ALL ACCESS",
            "empMail": "aparna@mydbq.com",
            "roleModel": [
              {
                "id": 230,
                "roleName": "ALL ACCESS"
              }
            ],
            "applicationNames": "HR PORTAL"
          },
          {
            "empCode": "1012",
            "empName": "Likki A",
            "roles": "",
            "empMail": "likki@mydbq.com",
            "roleModel": [],
            "applicationNames": ""
          },
          {
            "empCode": "1015",
            "empName": "Rajesh A",
            "roles": "",
            "empMail": "rajesh@mydbq.com",
            "roleModel": [],
            "applicationNames": ""
          },
          {
            "empCode": "1017",
            "empName": "John Doe",
            "roles": "ADMIN",
            "empMail": "john.doe@mydbq.com",
            "roleModel": [
              {
                "id": 225,
                "roleName": "ADMIN"
              }
            ],
            "applicationNames": "ADMIN PORTAL"
          },
          {
            "empCode": "1020",
            "empName": "Sarah Connor",
            "roles": "USER",
            "empMail": "sarah.connor@mydbq.com",
            "roleModel": [
              {
                "id": 227,
                "roleName": "USER"
              }
            ],
            "applicationNames": "USER DASHBOARD"
          },
          {
            "empCode": "1023",
            "empName": "Amit Verma",
            "roles": "HR MANAGER",
            "empMail": "amit.verma@mydbq.com",
            "roleModel": [
              {
                "id": 238,
                "roleName": "HR MANAGER"
              }
            ],
            "applicationNames": "HR MANAGEMENT"
          },
          {
            "empCode": "1025",
            "empName": "Anjali Gupta",
            "roles": "FINANCE ADMIN",
            "empMail": "anjali.gupta@mydbq.com",
            "roleModel": [
              {
                "id": 230,
                "roleName": "FINANCE ADMIN"
              }
            ],
            "applicationNames": "FINANCE PORTAL"
          },
          {
            "empCode": "1030",
            "empName": "David Clark",
            "roles": "SALES TEAM",
            "empMail": "david.clark@mydbq.com",
            "roleModel": [
              {
                "id": 238,
                "roleName": "SALES TEAM"
              }
            ],
            "applicationNames": "SALES PORTAL"
          },
          {
            "empCode": "1032",
            "empName": "Emily Smith",
            "roles": "SUPPORT",
            "empMail": "emily.smith@mydbq.com",
            "roleModel": [
              {
                "id": 227,
                "roleName": "SUPPORT"
              }
            ],
            "applicationNames": "SUPPORT DASHBOARD"
          },
          {
            "empCode": "1035",
            "empName": "Michael Jordan",
            "roles": "DEVOPS",
            "empMail": "michael.jordan@mydbq.com",
            "roleModel": [
              {
                "id": 230,
                "roleName": "DEVOPS"
              }
            ],
            "applicationNames": "DEVOPS PORTAL"
          },
          {
            "empCode": "1040",
            "empName": "Olivia Brown",
            "roles": "MARKETING",
            "empMail": "olivia.brown@mydbq.com",
            "roleModel": [
              {
                "id": 227,
                "roleName": "MARKETING"
              }
            ],
            "applicationNames": "MARKETING DASHBOARD"
          },
          {
            "empCode": "1045",
            "empName": "Carlos Lopez",
            "roles": "QA LEAD",
            "empMail": "carlos.lopez@mydbq.com",
            "roleModel": [
              {
                "id": 238,
                "roleName": "QA LEAD"
              }
            ],
            "applicationNames": "QA MANAGEMENT"
          },
          {
            "empCode": "1050",
            "empName": "Jennifer Wilson",
            "roles": "PROJECT MANAGER",
            "empMail": "jennifer.wilson@mydbq.com",
            "roleModel": [
              {
                "id": 225,
                "roleName": "PROJECT MANAGER"
              }
            ],
            "applicationNames": "PROJECT MANAGEMENT"
          },
          {
            "empCode": "1055",
            "empName": "Nina Patel",
            "roles": "PRODUCT OWNER",
            "empMail": "nina.patel@mydbq.com",
            "roleModel": [
              {
                "id": 238,
                "roleName": "PRODUCT OWNER"
              }
            ],
            "applicationNames": "PRODUCT MANAGEMENT"
          },
          {
            "empCode": "1060",
            "empName": "Steven Carter",
            "roles": "OPERATIONS",
            "empMail": "steven.carter@mydbq.com",
            "roleModel": [
              {
                "id": 230,
                "roleName": "OPERATIONS"
              }
            ],
            "applicationNames": "OPERATIONS DASHBOARD"
          },
          {
            "empCode": "1065",
            "empName": "Daniel Lee",
            "roles": "DEV TEAM",
            "empMail": "daniel.lee@mydbq.com",
            "roleModel": [
              {
                "id": 227,
                "roleName": "DEV TEAM"
              }
            ],
            "applicationNames": "DEV TEAM PORTAL"
          },
          {
            "empCode": "1070",
            "empName": "Chris Evans",
            "roles": "CTO",
            "empMail": "chris.evans@mydbq.com",
            "roleModel": [
              {
                "id": 230,
                "roleName": "CTO"
              }
            ],
            "applicationNames": "CTO DASHBOARD"
          },
          {
            "empCode": "1075",
            "empName": "Jessica Taylor",
            "roles": "HR ADMIN",
            "empMail": "jessica.taylor@mydbq.com",
            "roleModel": [
              {
                "id": 225,
                "roleName": "HR ADMIN"
              }
            ],
            "applicationNames": "HR ADMIN PORTAL"
          },
          {
            "empCode": "1080",
            "empName": "Liam Williams",
            "roles": "ADMIN",
            "empMail": "liam.williams@mydbq.com",
            "roleModel": [
              {
                "id": 230,
                "roleName": "ADMIN"
              }
            ],
            "applicationNames": "ADMIN PORTAL"
          },
          {
            "empCode": "1085",
            "empName": "Zoe Johnson",
            "roles": "CUSTOMER SUPPORT",
            "empMail": "zoe.johnson@mydbq.com",
            "roleModel": [
              {
                "id": 238,
                "roleName": "CUSTOMER SUPPORT"
              }
            ],
            "applicationNames": "CUSTOMER SUPPORT PORTAL"
          }
        ]
        
    };
    
      this.getUsers  = data.listObj; 
      this.ELEMENT_DATA=Object.assign([],data.listObj);
       this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
       this.totalProduct= this.ELEMENT_DATA.length;
      this.spinner.hide();
    
    }
}

