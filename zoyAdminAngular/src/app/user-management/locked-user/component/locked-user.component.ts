import { AfterViewInit, Component, inject, OnInit, Renderer2, ViewChild } from '@angular/core';
import { NgxSpinnerService } from 'ngx-spinner';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { AuthService } from 'src/app/common/service/auth.service';
import { UserService } from 'src/app/common/service/user.service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';
import { MatSort, Sort } from '@angular/material/sort';
import { DataService } from 'src/app/common/service/data.service';
import { SidebarComponent } from 'src/app/components/sidebar/sidebar.component';
import { AlertDialogService } from "src/app/common/shared/alert-dialog/alert-dialog.service";
import { UserDetails } from '../../user-master/models/register-details';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { MenuService } from 'src/app/components/header/menu.service';
import { PermissionApprovalService } from '../../permission-approval/service/permission-approval.service';

@Component({
  selector: 'app-locked-user',
  templateUrl: './locked-user.component.html',
  styleUrl: './locked-user.component.css'
})
export class LockedUserComponent implements OnInit,AfterViewInit{
  private _liveAnnouncer = inject(LiveAnnouncer);
  @ViewChild(SidebarComponent) sidemenuComp;
  pageSize: number = 10; 
  pageSizeOptions: number[] = [10, 20, 50]; 
  totalProduct: number = 0; 
  displayedColumns: string[] = ['firstName', 'userEmail', 'designation', 'status', 'action'];
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
   selectedItems : any[] = [];
   public rolesArray: string[] = [];
  mySubscription: any;
  public userNameSession:string="";
  submitted=false;
	error: string = '';
	userReg :UserDetails=new UserDetails();
  isExpandSideBar:boolean=true;
  authorization:{ key: string; screen: string; order: number }[]=[];
  constructor(private route: ActivatedRoute, private router: Router, private userService: UserService,private notifyService:NotificationService,private menuService:MenuService,
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
  this.sidemenuComp.activeMenu(3,'locked-user');
  this.dataService.setHeaderName("Locked User");
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
   
}
