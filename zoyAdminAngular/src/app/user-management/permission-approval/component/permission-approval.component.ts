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
import { AlertDialogService } from "src/app/common/shared/alert-dialog/alert-dialog.service";
import { UserDetails } from '../../user-master/models/register-details';
import { UserMasterService } from '../../user-master/service/user-master.service';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { RoleScreenPrv } from 'src/app/setting/role-master/models/role-screen-model';
import { MenuService } from 'src/app/components/header/menu.service';

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
    firstName: null,
    userMail: null,
    designation: null
  };
  columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);

  errorMsg:string="";
 @ViewChild(MatSort) sort: MatSort;
 @ViewChild(MatPaginator) paginator: MatPaginator;

  getUsers : UserDetails[] = [];
  editUserRole : UserDetails = new UserDetails();

   selectedItems : any[] = [];
   public rolesArray: string[] = [];
   @ViewChild('editCloseModal') editCloseModal : ElementRef;
   @ViewChild('registerCloseModal') registerCloseModal : ElementRef;
  mySubscription: any;
  public userNameSession:string="";
  submitted=false;
	error: string = '';
	form: FormGroup;
	userReg :UserDetails=new UserDetails();
  isPopupVisible: boolean = false;
  editUserRoleee = { empName: '' };
  userRolePermissions: RoleScreenPrv[] = [];
  isExpandSideBar:boolean=true;
  authorization:{ key: string; screen: string; order: number }[]=[];
  constructor(private userMasterService : UserMasterService,private formBuilder: FormBuilder,private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,private notifyService:NotificationService,private menuService:MenuService,
    private spinner:NgxSpinnerService,private renderer: Renderer2 ,private authService:AuthService,private confirmationDialogService:ConfirmationDialogService,private dataService:DataService,private alertDialogService: AlertDialogService,private permissionService: PermissionApprovalService){
      this.authService.checkLoginUserVlidaate();
      this.userNameSession=this.userService.getUsername();
      if (this.userService.getUserinfo() != undefined) {
        this.rolesArray = this.userService.getUserinfo().privilege;
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
			  this.router.navigated = false;
			}
      });
      this.dataService.getIsExpandSideBar.subscribe(name=>{
        this.isExpandSideBar=name;
      });
      this.authorization=this.menuService.getAllAuthorization();
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

}

	  

ngAfterViewInit(){
  this.sidemenuComp.expandMenu(3);
  this.sidemenuComp.activeMenu(3,'permission-approval');
  this.dataService.setHeaderName("Permission Approval");
}
 
getUserDetais(){
   this.authService.checkLoginUserVlidaate();
   this.spinner.show();
   this.permissionService.zoyAdminNotApprovedRoles().subscribe(data => {
    
  if(data !=null && data.length>0){

    this.ELEMENT_DATA = Object.assign([],data);
     this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
     this.dataSource.sort = this.sort;
     this.dataSource.paginator = this.paginator;
     this.dataSource.sortingDataAccessor = (data, sortHeaderId) => data[sortHeaderId].toLocaleLowerCase();
  }else{
    this.ELEMENT_DATA = Object.assign([]);
     this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
     this.dataSource.sort = this.sort;
     this.dataSource.paginator = this.paginator;
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
    announceSortChange(sortState: Sort): void {
      this.columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);
      this.columnSortDirections[sortState.active] = sortState.direction;
  
        if (sortState.direction) {
          this._liveAnnouncer.announce(`Sorted ${sortState.direction}ending`);
        } else {
          this._liveAnnouncer.announce('Sorting cleared');
        }
    }
    getRoleNames(roleModel: any[]): string {
      return roleModel.map(role => role.roleName).join(', ');
    }

    viewUser(user: any): void {   
      this.userReg = Object.assign(new UserDetails(),user) ; 
      this.userRolePermissions = [];
  
      if (user && user.roleModel && Array.isArray(user.roleModel)) {
        user.roleModel.forEach(role => {
          if (role.screens && Array.isArray(role.screens)) {
            role.screens.forEach(privilege => {
              let screenBaseName = privilege.replace(/_READ|_WRITE$/, '').replace(/_/g, ' ').toUpperCase();
              const authorization = this.authorization.find(auth=>auth.key == screenBaseName);
              if (authorization) {
              let existingPermission = this.userRolePermissions.find(
               permission => permission.screenName === authorization.screen 
                );   
              if (existingPermission) {
                if (privilege.endsWith('_READ')) {
                  existingPermission.readPrv = true;
                }
                if (privilege.endsWith('_WRITE')) {
                  existingPermission.writePrv = true;
                }
                existingPermission.approveStatus = role.approveStatus == 'Approved';
                existingPermission.order = authorization.order ;
              } else {
                this.userRolePermissions.push({
                  screenName: authorization.screen,
                  readPrv: privilege.endsWith('_READ'),
                  writePrv: privilege.endsWith('_WRITE'),
                  approveStatus: role.approveStatus == 'Approved',
                  order : authorization?.order
                });
              }
            }
            });
          }
        });
      }
      this.userRolePermissions.sort((a, b) => a.order - b.order);
    }
     

  getReadIcon(readPrv: boolean): string {
    return readPrv ? 'fa fa-check text-success' : 'fa fa-times text-danger';
  }

  getWriteIcon(writePrv: boolean): string {
    return writePrv ? 'fa fa-check text-success' : 'fa fa-times text-danger';
  }



  approveRejectRole(status:string): void {
    this.confirmationDialogService.confirm('Confirmation!!', 'Are you sure to '+status+' the User?')
    .then(
      (confirmed) =>{
       if(confirmed){
  this.spinner.show();
  
  this.permissionService.approveRejectRole(this.userReg.userEmail,status).subscribe((response) => 
    {
      this.notifyService.showSuccess(response.message, "");
      this.registerCloseModal.nativeElement.click(); 
      this.spinner.hide();
      this.getUserDetais();
    },
    (error) => {
      this.spinner.hide();
      if (error.status === 403) {
        this.router.navigate(['/forbidden']);
      }else if(error.status==401){
				console.error("Unauthorised");
			} else if (error.error && error.error.message) {
        this.errorMsg = error.error.message;
        console.error('Error approving roles:', this.errorMsg);
        this.notifyService.showError(this.errorMsg, "");
      } else {
        if (error.status === 500 && error.statusText === 'Internal Server Error') {
          this.errorMsg = `${error.statusText}! Please login again or contact your Help Desk.`;
        } else {
          let str;
          if (error.status === 400) {
            str = error.error;
          } else {
            str = error.message;
            str = str.substring(str.indexOf(':') + 1);
          }
          console.error('Error approving roles:', str);
          this.errorMsg = str;
        }
        if (error.status !== 401) {
          this.notifyService.showError(this.errorMsg, "");
        }
      }
    }
  );
}
}).catch(
  () => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
  ); 
}




}
