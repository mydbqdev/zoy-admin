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
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';
import { MatSort, Sort } from '@angular/material/sort';
import { DataService } from 'src/app/common/service/data.service';
import { SidebarComponent } from 'src/app/components/sidebar/sidebar.component';
import { PermissionApprovalService } from '../service/permission-approval.service';
import { PermissionDetails } from '../model/permission-details';
import { PermissionApproval } from '../model/permission-approval-model';
import { AlertDialogService } from "src/app/common/shared/alert-dialog/alert-dialog.service";
import { UserDetails } from '../../user-master/models/register-details';
import { UserMasterService } from '../../user-master/service/user-master.service';
import { LiveAnnouncer } from '@angular/cdk/a11y';

@Component({
  selector: 'app-permission-approval',
  templateUrl: './permission-approval.component.html',
  styleUrl: './permission-approval.component.css'
})
export class PermissionApprovalComponent implements OnInit,AfterViewInit{
  private _liveAnnouncer = inject(LiveAnnouncer);
  @ViewChild(SidebarComponent) sidemenuComp;
  pageSize: number = 10; 
  pageSizeOptions: number[] = [10, 20, 50]; 
  totalProduct: number = 0; 
  displayedColumns: string[] = ['firstName', 'userEmail', 'designation', 'roles', 'action'];
  public ELEMENT_DATA:UserDetails[];
  dataSource:MatTableDataSource<UserDetails>=new MatTableDataSource<UserDetails>();
  columnSortDirectionsOg: { [key: string]: string | null } = {
    userName: null,
    userMail: null,
    designation: null
  };
  columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);

  errorMsg:string="";
 @ViewChild(MatSort) sort: MatSort;
 @ViewChild(MatPaginator) paginator: MatPaginator;

  getUsers : UserDetails[] = [];
  editUserRole : UserDetails = new UserDetails();
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
  constructor(private userMasterService : UserMasterService,private formBuilder: FormBuilder,private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,private notifyService:NotificationService,
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
    this.getUserDetais();
    const empCode = 'EMP123';  // Replace with your hardcoded employee code
    this.loadUserPermissions(empCode);
}

	  

ngAfterViewInit(){
  this.sidemenuComp.expandMenu(3);
  this.sidemenuComp.activeMenu(3,'permission-approval');
}
 
getUserDetais(){
  // this.authService.checkLoginUserVlidaate();
   this.spinner.show();
   this.userMasterService.getUserList().subscribe(data => {
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
    this.editUserRole.userEmail = hardcodedResponse.empName;
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

}

