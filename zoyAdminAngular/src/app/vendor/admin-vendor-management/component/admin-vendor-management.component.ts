import { AfterViewInit, Component, ElementRef, inject, OnInit, Renderer2, ViewChild } from '@angular/core';
import { NgxSpinnerService } from 'ngx-spinner';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatPaginator } from '@angular/material/paginator';
import { AuthService } from 'src/app/common/service/auth.service';
import { UserService } from 'src/app/common/service/user.service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';
import { MatSort, Sort } from '@angular/material/sort';
import { DataService } from 'src/app/common/service/data.service';
import { SidebarComponent } from 'src/app/components/sidebar/sidebar.component';
import { AlertDialogService } from "src/app/common/shared/alert-dialog/alert-dialog.service";
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { MenuService } from 'src/app/components/header/menu.service';
import {  VendorService } from '../service/admin-vendor-management.service';
import {  Vendor, VendorModel, VendorStatus } from '../model/admin-vendor-management.model';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Filter, OwnerRequestParam } from 'src/app/owners/managing-owner/models/owner-details-request-model';
import { MatTableDataSource } from '@angular/material/table';
import { UserDesignation } from 'src/app/sales-person/sales/models/sales-model';

@Component({
  selector: 'app-admin-vendor-management',
  templateUrl: './admin-vendor-management.component.html',
  styleUrl: './admin-vendor-management.component.css'
})
export class AdminVendorManagementComponent implements OnInit,AfterViewInit{
  private _liveAnnouncer = inject(LiveAnnouncer);
  @ViewChild(SidebarComponent) sidemenuComp;
 
  errorMsg:string="";
 @ViewChild(MatSort) sort: MatSort;
  selectedItems : any[] = [];
  public rolesArray: string[] = [];
  mySubscription: any;
  public userNameSession:string="";
  submitted=false;
	error: string = '';
  isExpandSideBar:boolean=true;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  displayedColumns: string[] = ['companyName', 'contactPerson', 'email', 'contactNumber', 'status'];
  public ELEMENT_DATA:VendorModel[]=[];
  orginalFetchData:VendorModel[]=[];
  dataSource:MatTableDataSource<VendorModel>=new MatTableDataSource<VendorModel>();
  columnSortDirectionsOg: { [key: string]: string | null } = {
    companyName: null,
    contactPerson: null,
    contactNumber: null,
    email: null,
    status: null
  };
  stopPropagation(event: MouseEvent): void {
    event.stopPropagation();
    }
  pageSize: number = 10; 
	pageSizeOptions: number[] = [10, 20, 50]; 
	totalProduct: number = 0;
	public lastPageSize:number=0;
  columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);
  param:OwnerRequestParam=new OwnerRequestParam();
  paramFilter:Filter=new Filter();
  venderGroupName:string='';
  venderDesignation:string='';
  designationList :UserDesignation[]=[];
  @ViewChild('detailsCloseModal') detailsCloseModal : ElementRef;
  statusChangeReason:string='';
  constructor(private route: ActivatedRoute, private router: Router, private userService: UserService,private notifyService:NotificationService,private menuService:MenuService,   private fb: FormBuilder,private http: HttpClient ,
    private spinner:NgxSpinnerService,private renderer: Renderer2 ,private authService:AuthService,private confirmationDialogService:ConfirmationDialogService,private dataService:DataService,private alertDialogService: AlertDialogService,private vendorService: VendorService){
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
  this.sidemenuComp.expandMenu(12);
  this.sidemenuComp.activeMenu(12,'admin-vendor-management');
  this.dataService.setHeaderName("Manage Vendor");

  this.getRetrieveData();
 this.getUserDesignation();
}

  isLoading = false;
  errorMessage: string | null = null;
  currentFilter: VendorStatus | 'All' = 'All';

  selectedVendor: VendorModel  = new VendorModel(); 
  isApproving = false;
  isRejecting = false;
  actionMessage: string | null = null; 

  statusOptions: VendorStatus[] = [
    VendorStatus.Active,
    VendorStatus.Inactive,
    VendorStatus.Rejected
  ];

  VendorStatus = VendorStatus;
 isValidGroupName(name: string): boolean {
	if(name){
	const value = name.split('-');
 	 return name && (value.length < 3 || !value[2] || !value[1] ) ;
	}else{
		return false;
	} 
}

  viewVendorDetails(vendor: VendorModel): void {
    this.selectedVendor = { ...vendor }; 
    this.statusChangeReason=''
    this.actionMessage = null;
  }

  clearSelectedVendor(): void {
    this.selectedVendor = null;
    this.actionMessage = null; 
  }
  changeVendorStatus(): void {
    if (!this.selectedVendor || !this.statusChangeReason) {
      return;
    }
   let request= {
    "vendorId": this.selectedVendor.vendor_id,
    "status": this.newUpdateStatus(),
    "reason": this.statusChangeReason
   }
    this.actionMessage = null;
console.log("request",request)
   this.confirmationDialogService.confirm('Confirmation!!', 'Are you sure you want to unlock the "'+this.selectedVendor.vendor_first_name +' '+this.selectedVendor.vendor_last_name+'" ?')
  .then(
    (confirmed) =>{
     if(confirmed){
  this.spinner.show();
  this.vendorService.changeVendorStatus(request).subscribe(data => {
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

  newUpdateStatus():string{
    var status=''
    if(this.selectedVendor.vendor_status === 'Approved' || this.selectedVendor.vendor_status === 'Active' ){
      status = 'Inactive';
    }else if( this.selectedVendor.vendor_status === 'Inactive'){
      status = 'Active';
    }
    return status;
  }

  // onStatusChangeSelection(): void {
  //   const selectedStatus = this.statusChangeForm.get('newStatus')?.value;
  //   const reasonControl = this.statusChangeForm.get('reason');

  //   if (selectedStatus === VendorStatus.Inactive || selectedStatus === VendorStatus.Rejected) {
  //     reasonControl?.setValidators(Validators.required);
  //   } else {
  //     reasonControl?.clearValidators();
  //   }
  //   reasonControl?.updateValueAndValidity();
  // }


    getRetrieveData(){
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;
      this.param.pageIndex=this.paginator.pageIndex;
      this.param.pageSize= this.paginator.pageSize;
      this.param.sortDirection="desc";
      this.param.sortActive="companyName";
      this.paramFilter.searchText=null;
      this.paramFilter.status=null;
      this.param.filter=this.paramFilter;
      setTimeout(()=>{
        this.getVendorDetails();
       },100);
       this.columnSortDirections["companyName"] = "asc";
      }

  applyFilter(): void {
    this.param.pageIndex=0
		this.paginator.pageIndex=0;
    this.paramFilter.status = this.currentFilterStatus ||null;
		this.param.filter=this.paramFilter;
		this.getVendorDetails();
  }
  currentFilterStatus:string='';

  setFilter(status: VendorStatus | 'All',S:string): void {
    this.currentFilter = status;
    this.currentFilterStatus= S;
    this.applyFilter();
  }

  services():string{
   return this.selectedVendor.services?.map(s => s.service_name).join(', ') || 'N/A'
  }
   getUserDesignation() {
			this.vendorService.getUserDesignation().subscribe((res) => {
				this.designationList = res.data || [];
			  },error =>{
				this.spinner.hide();
				console.log("error.error",error)
				if(error.status == 0) {
					this.notifyService.showError("Internal Server Error/Connection not established", "")
				 }else if(error.status==403){
				this.router.navigate(['/forbidden']);
				}else if (error.error && error.error.message) {
				this.errorMsg =error.error.message;
				console.log("Error:"+this.errorMsg);
		  
				if(error.status==500 && error.statusText=="Internal Server Error"){
				  this.errorMsg=error.statusText+"! Please login again or contact your Help Desk.";
				}else{
				//  this.spinner.hide();
				  let str;
				  if(error.status==400){
				  str=error.error.error;
				  }else{
					str=error.error.message;
					str=str.substring(str.indexOf(":")+1);
				  }
				  console.log("Error:",str);
				  this.errorMsg=str;
				}
			  	if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
			    //this.notifyService.showError(this.errorMsg, "");
				}
			  });  
	    }
      getVendorDetails(){
        this.param.activity="vendor_list";
        this.authService.checkLoginUserVlidaate();
        this.spinner.show();
        this.lastPageSize=this.param.pageSize;
        this.vendorService.getVendorDetails(this.param).subscribe(data => {
          this.orginalFetchData=  Object.assign([],data.content);
          this.ELEMENT_DATA = Object.assign([],data.content);
          this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
          this.totalProduct=data.total;
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
    
    announceSortChange(sortState: Sort): void {
      this.columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);
      this.columnSortDirections[sortState.active] = sortState.direction;
      if (sortState.direction) {
        this._liveAnnouncer.announce(`Sorted ${sortState.direction}ending`);
      } else {
        this._liveAnnouncer.announce('Sorting cleared');
      }
      this.param.sortActive=sortState.active;
      this.param.sortDirection=sortState.direction!="" ? sortState.direction:"asc";
      this.param.pageIndex=0
      this.paginator.pageIndex=0;
      this.getVendorDetails();
    }
  pageChanged(event:any){
    this.dataSource=new MatTableDataSource<VendorModel>();
     if(this.lastPageSize!=event.pageSize){
       this.paginator.pageIndex=0;
       event.pageIndex=0;
      }
      this.param.pageIndex=this.paginator.pageIndex;
      this.param.pageSize= event.pageSize;
      this.getVendorDetails();
     }

rejectionReason:string='';

@ViewChild('statusFormDiv') statusFormDiv!: ElementRef;
@ViewChild('rejectionForm') rejectionForm!: ElementRef;

scrollTo(form:string) {
 if ("rejectionForm" == form && this.rejectionForm?.nativeElement) {
    setTimeout(() => this.rejectionForm?.nativeElement?.scrollIntoView({ behavior: 'smooth', block: 'start' }), 100);
  }else if("statusFormDiv" == form && this.statusFormDiv?.nativeElement){
    setTimeout(() => this.statusFormDiv?.nativeElement?.scrollIntoView({ behavior: 'smooth', block: 'start' }), 100);
  }
}

rejectingVendorDetails(){
   this.isRejecting = true;
    if (!this.selectedVendor || this.rejectionReason) {
      return;
    }
  let request= {
    "vendorId": this.selectedVendor.vendor_id,
    "rejectedReason": this.rejectionReason
  }
  this.confirmationDialogService.confirm('Confirmation!!', 'Are you sure you want to unlock the "'+this.selectedVendor.vendor_first_name +' '+this.selectedVendor.vendor_last_name+'" ?')
  .then(
    (confirmed) =>{
     if(confirmed){
  this.spinner.show();
  this.vendorService.rejectingVendorDetails(request).subscribe(data => {
     this.isRejecting = false;
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


approveVendorDetails(){
this.isApproving = true;
if (!this.selectedVendor ||!this.venderDesignation || !this.venderGroupName || this.isValidGroupName(this.venderGroupName)) return;
  
  let request= {
    "vendorId": this.selectedVendor.vendor_id,
    "userDesignation": this.venderDesignation,
    "userDesignationName": this.designationList.find(d=>d.id==this.venderDesignation)?.name || '',
    "userGroupName": 'Vendor-'+this.venderGroupName
  }
  this.confirmationDialogService.confirm('Confirmation!!', 'Are you sure you want to unlock the "'+this.selectedVendor.vendor_first_name +' '+this.selectedVendor.vendor_last_name+'" ?')
  .then(
    (confirmed) =>{
     if(confirmed){
  this.spinner.show();
  this.vendorService.approveVendorDetails(request).subscribe(data => {
   this.isApproving = false;
    this.detailsCloseModal.nativeElement.click(); 
    this.notifyService.showSuccess(data.message,"")
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


}
