import { AfterViewInit, Component, inject, OnInit, Renderer2, ViewChild } from '@angular/core';
import { NgxSpinnerService } from 'ngx-spinner';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatPaginator } from '@angular/material/paginator';
import { AuthService } from 'src/app/common/service/auth.service';
import { UserService } from 'src/app/common/service/user.service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';
import { MatSort } from '@angular/material/sort';
import { DataService } from 'src/app/common/service/data.service';
import { SidebarComponent } from 'src/app/components/sidebar/sidebar.component';
import { AlertDialogService } from "src/app/common/shared/alert-dialog/alert-dialog.service";
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { MenuService } from 'src/app/components/header/menu.service';
import {  VendorService } from '../service/admin-vendor-management.service';
import { Vendor, VendorStatus } from '../model/admin-vendor-management.model';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

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
 @ViewChild(MatPaginator) paginator: MatPaginator;
   selectedItems : any[] = [];
   public rolesArray: string[] = [];
  mySubscription: any;
  public userNameSession:string="";
  submitted=false;
	error: string = '';
  isExpandSideBar:boolean=true;
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

       //   this.vendorService = new MockVendorService(this.http);

    // Initialize the reactive forms
    this.rejectionReasonForm = this.fb.group({
      reason: ['', Validators.required]
    });
    this.statusChangeForm = this.fb.group({
      newStatus: ['', Validators.required],
      reason: ['']
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
 
 this.loadVendors();
}

ngAfterViewInit(){
  this.sidemenuComp.expandMenu(3);
  this.sidemenuComp.activeMenu(3,'admin-vendor-management');
  this.dataService.setHeaderName("Vendor Management");
}

vendors: Vendor[] = [];
  filteredVendors: Vendor[] = [];
  isLoading = false;
  errorMessage: string | null = null;
  currentFilter: VendorStatus | 'All' = 'All';

  selectedVendor: Vendor | null = null; // Holds the vendor currently being viewed/edited

  rejectionReasonForm!: FormGroup;
  statusChangeForm!: FormGroup;

  isApproving = false;
  isRejecting = false;
  isChangingStatus = false;
  actionMessage: string | null = null; // Message for action results (success/error)

  // Options for status change dropdown (excluding 'Pending Approval' as you can't change to it)
  statusOptions: VendorStatus[] = [
    VendorStatus.Active,
    VendorStatus.Inactive,
    VendorStatus.Rejected
  ];

  // Expose VendorStatus enum to the template
  VendorStatus = VendorStatus;





  /**
   * Loads all vendors from the backend.
   * Sorts 'Pending Approval' vendors to the top.
   */
  loadVendors(): void {
    this.isLoading = true;
    this.errorMessage = null;
    this.vendorService.getVendors().subscribe({
      next: (data) => {
        this.isLoading = false;
        // Sort: Pending Approval first, then by creation date (newest first)
        this.vendors = data.sort((a, b) => {
          if (a.status === VendorStatus.PendingApproval && b.status !== VendorStatus.PendingApproval) {
            return -1;
          }
          if (a.status !== VendorStatus.PendingApproval && b.status === VendorStatus.PendingApproval) {
            return 1;
          }
          return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
        });
        this.applyFilter();
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || 'Failed to load vendors. Please try again.';
        console.error('Error loading vendors:', err);
      }
    });
  }

  /**
   * Applies the currently selected filter to the vendors list.
   */
  applyFilter(): void {
    if (this.currentFilter === 'All') {
      this.filteredVendors = [...this.vendors];
    } else {
      this.filteredVendors = this.vendors.filter(vendor => vendor.status === this.currentFilter);
    }
  }

  /**
   * Sets the active filter and re-applies it.
   * @param status The status to filter by, or 'All'.
   */
  setFilter(status: VendorStatus | 'All'): void {
    this.currentFilter = status;
    this.applyFilter();
  }

  /**
   * Sets the selected vendor to display its details.
   * @param vendor The vendor object to display.
   */
  viewVendorDetails(vendor: Vendor): void {
    this.selectedVendor = { ...vendor }; // Create a copy to avoid direct mutation
    // Reset forms and messages when viewing new details
    this.rejectionReasonForm.reset();
    this.statusChangeForm.reset({ newStatus: vendor.status, reason: '' });
    this.actionMessage = null;
  }

  /**
   * Clears the selected vendor, returning to the list view.
   */
  clearSelectedVendor(): void {
    this.selectedVendor = null;
    this.actionMessage = null; // Clear any action messages
  }

  /**
   * Approves the selected vendor.
   */
  approveVendor(): void {
    if (!this.selectedVendor) return;

    this.isApproving = true;
    this.actionMessage = null;

    this.vendorService.approveVendor(this.selectedVendor.id).subscribe({
      next: () => {
        this.isApproving = false;
        this.actionMessage = 'Vendor approved successfully!';
        // Update the status in the main vendors array and selected vendor
        const index = this.vendors.findIndex(v => v.id === this.selectedVendor?.id);
        if (index > -1) {
          this.vendors[index].status = VendorStatus.Active;
          this.selectedVendor!.status = VendorStatus.Active;
        }
        this.applyFilter(); // Re-apply filter to update list view
        setTimeout(() => this.clearSelectedVendor(), 1500); // Go back to list after delay
      },
      error: (err) => {
        this.isApproving = false;
        this.actionMessage = err.error?.message || 'Failed to approve vendor. Please try again.';
        console.error('Approval error:', err);
      }
    });
  }

  /**
   * Rejects the selected vendor with a reason.
   */
  rejectVendor(): void {
    if (!this.selectedVendor || this.rejectionReasonForm.invalid) {
    //  this.rejectionReasonForm.markAllAsDirty();
      return;
    }

    this.isRejecting = true;
    this.actionMessage = null;
    const reason = this.rejectionReasonForm.get('reason')?.value;

    this.vendorService.rejectVendor(this.selectedVendor.id, reason).subscribe({
      next: () => {
        this.isRejecting = false;
        this.actionMessage = 'Vendor rejected successfully!';
        // Update status and reason in main vendors array and selected vendor
        const index = this.vendors.findIndex(v => v.id === this.selectedVendor?.id);
        if (index > -1) {
          this.vendors[index].status = VendorStatus.Rejected;
          this.vendors[index].rejectionReason = reason;
          this.selectedVendor!.status = VendorStatus.Rejected;
          this.selectedVendor!.rejectionReason = reason;
        }
        this.applyFilter();
        setTimeout(() => this.clearSelectedVendor(), 1500);
      },
      error: (err) => {
        this.isRejecting = false;
        this.actionMessage = err.error?.message || 'Failed to reject vendor. Please try again.';
        console.error('Rejection error:', err);
      }
    });
  }

  /**
   * Changes the selected vendor's status (e.g., to Inactive) with a reason.
   */
  changeVendorStatus(): void {
    if (!this.selectedVendor || this.statusChangeForm.invalid) {
     // this.statusChangeForm.markAllAsDirty();
      return;
    }

    const newStatus: VendorStatus = this.statusChangeForm.get('newStatus')?.value;
    const reason: string = this.statusChangeForm.get('reason')?.value;

    // Reason is required for Inactive or Rejected status changes
    if ((newStatus === VendorStatus.Inactive || newStatus === VendorStatus.Rejected) && !reason) {
        this.statusChangeForm.get('reason')?.setErrors({ required: true });
        return;
    }

    this.isChangingStatus = true;
    this.actionMessage = null;

    this.vendorService.changeVendorStatus(this.selectedVendor.id, newStatus, reason).subscribe({
      next: () => {
        this.isChangingStatus = false;
        this.actionMessage = `Vendor status changed to "${newStatus}" successfully!`;
        // Update status and reason in main vendors array and selected vendor
        const index = this.vendors.findIndex(v => v.id === this.selectedVendor?.id);
        if (index > -1) {
          this.vendors[index].status = newStatus;
          this.selectedVendor!.status = newStatus;
          if (newStatus === VendorStatus.Inactive) {
              this.vendors[index].inactiveReason = reason;
              this.selectedVendor!.inactiveReason = reason;
              delete this.vendors[index].rejectionReason;
              delete this.selectedVendor!.rejectionReason;
          } else if (newStatus === VendorStatus.Rejected) {
              this.vendors[index].rejectionReason = reason;
              this.selectedVendor!.rejectionReason = reason;
              delete this.vendors[index].inactiveReason;
              delete this.selectedVendor!.inactiveReason;
          } else { // For Approved/Active
              delete this.vendors[index].inactiveReason;
              delete this.vendors[index].rejectionReason;
              delete this.selectedVendor!.inactiveReason;
              delete this.selectedVendor!.rejectionReason;
          }
        }
        this.applyFilter();
        setTimeout(() => this.clearSelectedVendor(), 1500);
      },
      error: (err) => {
        this.isChangingStatus = false;
        this.actionMessage = err.error?.message || 'Failed to change vendor status. Please try again.';
        console.error('Status change error:', err);
      }
    });
  }

  /**
   * Dynamically sets/clears validators for the reason field based on selected status.
   */
  onStatusChangeSelection(): void {
    const selectedStatus = this.statusChangeForm.get('newStatus')?.value;
    const reasonControl = this.statusChangeForm.get('reason');

    if (selectedStatus === VendorStatus.Inactive || selectedStatus === VendorStatus.Rejected) {
      reasonControl?.setValidators(Validators.required);
    } else {
      reasonControl?.clearValidators();
    }
    reasonControl?.updateValueAndValidity();
  }

  /**
   * Helper to get Bootstrap badge class based on vendor status.
   * @param status The current status of the vendor.
   * @returns A string of Bootstrap classes.
   */
  getStatusBadgeClass(status: VendorStatus): string {
    switch (status) {
      case VendorStatus.PendingApproval: return 'bg-warning text-dark';
      case VendorStatus.Approved: return 'bg-success';
      case VendorStatus.Active: return 'bg-primary';
      case VendorStatus.Rejected: return 'bg-danger';
      case VendorStatus.Inactive: return 'bg-secondary';
      default: return 'bg-info';
    }
  }
 

doUnlock(user:any){
  this.confirmationDialogService.confirm('Confirmation!!', 'Are you sure you want to unlock the "'+user.firstName +' '+user.lastName+'" ?')
  .then(
    (confirmed) =>{
     if(confirmed){
  this.spinner.show();
  this.vendorService.doUnlock(user).subscribe(data => {
  
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
