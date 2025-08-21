import { AfterViewInit, Component, ElementRef, inject, OnInit, Renderer2, ViewChild } from '@angular/core';
import { NgxSpinnerService } from 'ngx-spinner';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatPaginator } from '@angular/material/paginator';
import { AuthService } from 'src/app/common/service/auth.service';
import { UserService } from 'src/app/common/service/user.service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';
import { DataService } from 'src/app/common/service/data.service';
import { SidebarComponent } from 'src/app/components/sidebar/sidebar.component';
import { AlertDialogService } from "src/app/common/shared/alert-dialog/alert-dialog.service";
import { MenuService } from 'src/app/components/header/menu.service';
import { FormBuilder, } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { BlacklistGetModel, BlacklistPostModel, RequestParam } from '../model/blacklist.model';
import { BlackListService } from '../service/blacklist.service';

@Component({
  selector: 'app-blacklist',
  templateUrl: './blacklist.component.html',
  styleUrl: './blacklist.component.css'
})
export class BlacklistComponent implements OnInit,AfterViewInit{
  @ViewChild(SidebarComponent) sidemenuComp;
 
  errorMsg:string="";
  selectedItems : any[] = [];
  public rolesArray: string[] = [];
  mySubscription: any;
  public userNameSession:string="";
  submitted=false;
  error: string = '';
  isExpandSideBar:boolean=true;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  displayedColumns: string[] = ['email', 'mobile','action'];
  public ELEMENT_DATA:BlacklistGetModel[]=[];
  orginalFetchData:BlacklistGetModel[]=[];
  dataSource:MatTableDataSource<BlacklistGetModel>=new MatTableDataSource<BlacklistGetModel>();
  pageSize: number = 10; 
  pageSizeOptions: number[] = [10, 20, 50]; 
  totalProduct: number = 0;
  public lastPageSize:number=0;
  searchText:string='';
  @ViewChild('detailsCloseModal') detailsCloseModal : ElementRef;
  statusChangeReason:string='';
  deleteBlacklistedIds:string[]=[];
  selectedBlacklist:BlacklistGetModel=new BlacklistGetModel();
  param:RequestParam=new RequestParam();
  constructor(private route: ActivatedRoute, private router: Router, private userService: UserService,private notifyService:NotificationService,private menuService:MenuService,   private fb: FormBuilder,private http: HttpClient ,
    private spinner:NgxSpinnerService,private renderer: Renderer2 ,private authService:AuthService,private confirmationDialogService:ConfirmationDialogService,private dataService:DataService,private alertDialogService: AlertDialogService,private blackListService: BlackListService){
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
 

}

ngAfterViewInit(){
  this.sidemenuComp.expandMenu(4);
  this.sidemenuComp.activeMenu(4,'blacklist');
  this.dataService.setHeaderName("Black List");

  this.getRetrieveData();
}


  viewDetails(blacklist: any): void {
    this.selectedBlacklist = { ...blacklist }; 
    this.submitted=false;
  }

  resetForm(){
    this.selectedBlacklist=new BlacklistGetModel();
    this.submitted=false;
  }

    getRetrieveData(){
      this.dataSource.paginator = this.paginator;
      this.param.pageIndex=this.paginator.pageIndex;
      this.param.pageSize= this.paginator.pageSize;
      setTimeout(()=>{
        this.getBlacklistDetails();
       },100);
      }

  applyFilter(): void {
    this.param.pageIndex=0
    this.paginator.pageIndex=0;
    this.getBlacklistDetails();
  }

  deleteSelection(id: string, event: Event) {
     const checked = (event.target as HTMLInputElement).checked;
  if (checked) {
    if (!this.deleteBlacklistedIds.includes(id)) {
      this.deleteBlacklistedIds.push(id);
    }
  } else {
    this.deleteBlacklistedIds = this.deleteBlacklistedIds.filter(x => x !== id);
  }
}

  filterData($event: KeyboardEvent){
		if ($event.keyCode === 13) {
		if(this.searchText==''){
			this.param.searchContent='';
		}else{
			this.param.searchContent=this.searchText;
		}
		this.param.pageIndex=0
		this.paginator.pageIndex=0;
		this.getBlacklistDetails();
	  }
	}


      getBlacklistDetails(){
        this.authService.checkLoginUserVlidaate();
        this.spinner.show();
        this.lastPageSize=this.param.pageSize;
        this.blackListService.getBlacklistDetails(this.param).subscribe(res => {
          this.orginalFetchData=  Object.assign([],res.data);
          this.ELEMENT_DATA = Object.assign([],res.data);
          this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
          this.totalProduct=res.count;
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
    
  pageChanged(event:any){
    this.dataSource=new MatTableDataSource<BlacklistGetModel>();
     if(this.lastPageSize!=event.pageSize){
       this.paginator.pageIndex=0;
       event.pageIndex=0;
      }
      this.param.pageIndex=this.paginator.pageIndex;
      this.param.pageSize= event.pageSize;
      this.getBlacklistDetails();
     }
saveBlacklisted(form: any){
   this.submitted = true;
    if ((!this.selectedBlacklist.email && !this.selectedBlacklist.mobile)|| !form.valid) {
      return;
    }
  let request:BlacklistPostModel= {
    "pg_blacklisted_id": this.selectedBlacklist.id || null,
    "pg_blocklisted_email": this.selectedBlacklist.email || "",
    "pg_blacklisted_mobile":this.selectedBlacklist.mobile || ""
  }
  this.confirmationDialogService.confirm('Confirmation!!', 'Are you sure you want to submitted the Balck List" ?')
  .then(
    (confirmed) =>{
     if(confirmed){
  this.spinner.show();
  this.blackListService.saveBlacklisted(request).subscribe(data => {
     this.submitted = false;
     this.detailsCloseModal.nativeElement.click(); 
     this.notifyService.showSuccess(data.message,"");
  this.applyFilter();
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
  }
});

   }
  }).catch(
    () => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
    ); 
  } 
onDelete(row:any){
  this.deleteBlacklistedIds=[];
  this.deleteBlacklistedIds.push(row.id);
  this.deleteBlacklisted();
}

deleteBlacklisted(){
if (this.deleteBlacklistedIds.length == 0 ) return;
  const model ={
    "ids":this.deleteBlacklistedIds
  }
  this.confirmationDialogService.confirm('Confirmation!!', 'Are you sure you want to delete the Black listeds ?')
  .then(
    (confirmed) =>{
     if(confirmed){
  this.spinner.show();
  this.blackListService.deleteBlacklisted(model).subscribe(data => {
  this.notifyService.showSuccess(data.message,"");
  this.deleteBlacklistedIds=[];
  this.applyFilter();
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
  }
 });

   }else{
    this.deleteBlacklistedIds=[];
   }
  }).catch(
    () => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
    ); 
  } 


}
