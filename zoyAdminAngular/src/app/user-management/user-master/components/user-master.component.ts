import { Component, ElementRef, inject, OnInit, Renderer2, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort, Sort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { AuthService } from 'src/app/common/service/auth.service';
import { DataService } from 'src/app/common/service/data.service';
import { UserService } from 'src/app/common/service/user.service';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import { UserMasterService } from '../service/user-master.service';
import { UserDetails } from '../models/register-details';
import { SidebarComponent } from 'src/app/components/sidebar/sidebar.component';
import {  RoleUpdateModel } from '../models/rolesave-model';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { RoleScreenPrv } from 'src/app/setting/role-master/models/role-screen-model';

@Component({
  selector: 'app-user-master',
  templateUrl: './user-master.component.html',
  styleUrls: ['./user-master.component.css']
})
export class UserMasterComponent implements OnInit {
  private _liveAnnouncer = inject(LiveAnnouncer);
  pageSize: number = 10; 
  pageSizeOptions: number[] = [10, 20, 50]; 
  totalProduct: number = 0; 
  displayedColumns: string[] = ['firstName', 'userEmail', 'designation', 'status','roleName', 'action'];
  public ELEMENT_DATA:UserDetails[];
  dataSource:MatTableDataSource<UserDetails>=new MatTableDataSource<UserDetails>();
  columnSortDirectionsOg: { [key: string]: string | null } = {
    userName: null,
    userMail: null,
    designation: null,
    roleName:null,
    status: null
  };
  columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);
  isExpandSideBar:boolean=true;

  mySubscription: any;
  userNameSession:string="";
  rolesArray: string[] = [];
  errorMsg : string;

	form: FormGroup;
  userReg : UserDetails=new UserDetails();
  roles:string[]=['Super Admin','Finance Admin','Support Admin']
  submitted=false;
  dropdownList = [];
  settings = {};
  selectedItems : any[] = [];
  selectedRoleIds: any[]= [];
  userActiveApplicationList: string[] = [];
  shouldBeChecked = [];
  checkedApplications: { [key: string]: boolean } = {};
  createOrUpdate:boolean=true;

  @ViewChild(SidebarComponent) sidemenuComp;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild('editRoleCloseModal') editRoleCloseModal : ElementRef;
  @ViewChild('registerCloseModal') registerCloseModal : ElementRef;

  constructor(private userMasterService : UserMasterService,private formBuilder: FormBuilder,private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,private notifyService:NotificationService,
    private spinner:NgxSpinnerService,private renderer: Renderer2 ,private authService:AuthService,private confirmationDialogService:ConfirmationDialogService,private dataService:DataService){
      this.authService.checkLoginUserVlidaate();
      this.userNameSession=this.userService.getUsername();
      if (this.userService.getUserinfo() != undefined) {
        this.rolesArray = this.userService.getUserinfo().privilege;
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
  ngOnInit(): void {
    
    this.getUserDetais();
    this.getRlesList();
    this.settings = {
      singleSelection: false,
      idField: 'id',
      textField: 'roleName',
      enableCheckAll: true,
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      allowSearchFilter: true,
      limitSelection: -1,
      clearSearchFilter: true,
      maxHeight: 197,
      itemsShowLimit: 3, 
      closeDropDownOnSelection: false,
      showSelectedItemsAtTop: false,
      defaultOpen: false,
    };
    this.form = this.formBuilder.group({
		  firstName: ['', [Validators.required]],
		  lastName: ['', [Validators.required]],
		  designation: ['', [Validators.required]],
      contactNumber: ['', [Validators.required]],
		  userEmail: ['', [
			Validators.required,
			Validators.pattern(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/)
		  ]],
		  password: ['', [
			Validators.required,
			Validators.minLength(8),
			Validators.maxLength(16),
			Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/)
		  ]],
		  repeatPassword: ['', Validators.required]
		}, { validator: this.passwordsMatch });
  }
  ngOnDestroy() {
		if (this.mySubscription) {
		  this.mySubscription.unsubscribe();
		}
	}

  ngAfterViewInit() {
    this.sidemenuComp.expandMenu(3);
    this.sidemenuComp.activeMenu(3,'user-master');
    this.dataService.setHeaderName("Managing Users");
  }
  getRoleNames(roleModel: any[]): string {
    return roleModel.map(role => role.roleName).join(', ');
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

 getRlesList(): any {

  // this.authService.checkLoginUserVlidaate();
   this.userMasterService.rolesDropdown().subscribe((data: any) => {
     this.dropdownList = Object.assign([],data); 
   },error =>{
     if(error.status==403){
       this.router.navigate(['/forbidden']);
     }else if (error.error && error.error.message) {
       this.errorMsg =error.error.message;
       console.log("Error:"+this.errorMsg);
       this.notifyService.showError(this.errorMsg, "");
     } else {
       if(error.status==500 && error.statusText=="Internal Server Error"){
         this.errorMsg=error.statusText+"! Please login again or contact your Help Desk.";
       }else{
         let str;
           if(error.status==400){
           str=error.error;
           }else{
             str=error.message;
             str=str.substring(str.indexOf(":")+1);
           }
           console.log("Error:"+str);
           this.errorMsg=str;
       }
       if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
     }
   });
 }
 numberOnly(event): boolean {
  const charCode = (event.which) ? event.which : event.keyCode;
  if (charCode > 31 && (charCode < 48 || charCode > 57)) {
    return false;
  }
  return true;
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
 
passwordsMatch(formGroup: FormGroup) {
  const password = formGroup.get('password')?.value;
  const repeatPassword = formGroup.get('repeatPassword')?.value;
  return password === repeatPassword ? null : { passwordsMismatch: true };
  }
  nameValidation(event: any, inputId: string) {
  const clipboardData = event.clipboardData || (window as any).clipboardData;
  const pastedText = clipboardData.getData('text/plain');
  const clString = pastedText.replace(/[^a-zA-Z\s.]/g, '');
   event.preventDefault();
  switch (inputId) {
    case 'firstName':
    this.userReg.firstName=clString;
    break;
    case 'lastName':
    this.userReg.lastName=clString;
    break;
  }
  }
  
  isPasswordVisible:boolean=false;

  togglePasswordVisibility(){
    this.isPasswordVisible=!this.isPasswordVisible;
  }

  resetForm(isCreate:boolean) {
  this.submitted=false;
  this.createOrUpdate=isCreate;
  this.form.reset(); 
  }

  submittUserDetails(){
    this.submitted=true;	
    if (this.form.invalid) {
      return;
      }
      if (this.createOrUpdate && this.userReg.password !== this.userReg.repeatPassword) {
      return;
      }
    if(this.createOrUpdate){
      this.createUser();
    }else{
      this.updateUserDetails();
    }
  }

  createUser() {
    this.spinner.show();		     
    this.submitted=false;
    this.userMasterService.createUser(this.userReg).subscribe((res) => {
      this.notifyService.showSuccess(res.message, "");
      this.getUserDetais();
      this.resetForm(true);
      this.registerCloseModal.nativeElement.click(); 
      this.spinner.hide();
    },error =>{
      this.spinner.hide();
      console.log("error.error",error)
      this.errorMsg = (error.error.error !=undefined?(error.error.error  +"."):"")
      + (error.error.userEmail!=undefined?(error.error.userEmail+"."):"")
      +(error.error.password!=undefined?(error.error.password  +"."):"");
      if(error.status==403){
      this.router.navigate(['/forbidden']);
      }else if (error.error && error.error.message) {
      this.errorMsg =error.error.message;
      console.log("Error:"+this.errorMsg);
      // this.notifyService.showError(this.errorMsg, "");
      // this.spinner.hide();
      } else {
      //this.spinner.hide();
      if(error.status==500 && error.statusText=="Internal Server Error"){
        this.errorMsg=error.statusText+"! Please login again or contact your Help Desk.";
      }else{
      //  this.spinner.hide();
        let str;
        if(error.status==400){
        str=error.error;
        }else{
          str=error.message;
          str=str.substring(str.indexOf(":")+1);
        }
        console.log("Error:"+str);
        this.errorMsg=str;
      }
    //	if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
      }
    }
    );  
  }  

  updateUserDetails() {
    this.spinner.show();		     
    this.submitted=false;
    this.userMasterService.updateUser(this.userReg).subscribe((res) => {
      this.notifyService.showSuccess(res.message, "");
      this.getUserDetais();
      this.resetForm(false);
      this.registerCloseModal.nativeElement.click(); 
      this.spinner.hide();
    },error =>{
      this.spinner.hide();
      console.log("error.error",error)
      this.errorMsg = (error.error.error !=undefined?(error.error.error  +"."):"")
      + (error.error.userEmail!=undefined?(error.error.userEmail+"."):"")
      +(error.error.password!=undefined?(error.error.password  +"."):"");
      if(error.status==403){
      this.router.navigate(['/forbidden']);
      }else if (error.error && error.error.message) {
      this.errorMsg =error.error.message;
      console.log("Error:"+this.errorMsg);
      // this.notifyService.showError(this.errorMsg, "");
      // this.spinner.hide();
      } else {
      //this.spinner.hide();
      if(error.status==500 && error.statusText=="Internal Server Error"){
        this.errorMsg=error.statusText+"! Please login again or contact your Help Desk.";
      }else{
      //  this.spinner.hide();
        let str;
        if(error.status==400){
        str=error.error;
        }else{
          str=error.message;
          str=str.substring(str.indexOf(":")+1);
        }
        console.log("Error:"+str);
        this.errorMsg=str;
      }
    //	if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
      }
    }
    );  
  }

    
  onItemSelect(item : any){
    this.selectedRoleIds.push(item.id);
  }
  public onDeSelect(item: any) {
  let index= this.selectedRoleIds.indexOf(item.id);
  if(index!=-1){
  this.selectedRoleIds.splice(index,1);
  }
  }

  public onSelectAll(items: any) {  
  for(let item of items){   
    this.selectedRoleIds.push(item.id);
  }  
  }
  public onDeSelectAll(items: any) {
  this.selectedRoleIds.splice(0);
  }
  
oldRoles:any=[];
  editRole(row:any ){
  this.resetForm(false);
  this.createOrUpdate=false;
  this.checkedApplications = {};
  this.selectedItems= [];
  this.selectedRoleIds.splice(0);
  this.oldRoles.splice(0);
  this.userReg = Object.assign(new UserDetails(),row)  ;
 
  // for temporary fixed added below two line
  this.form.controls["password"].setValue("Zoyadmin$123");
  this.form.controls["repeatPassword"].setValue("Zoyadmin$123");

  this.selectedItems= this.userReg.roleModel;
  for(let role of this.userReg.roleModel){
     this.selectedRoleIds.push(role.id);
     this.oldRoles.push(role.id);
  }
 

  }
   updateRolesUser(){
   // this.authService.checkLoginUserVlidaate();

   this.selectedRoleIds = this.selectedRoleIds.filter(roleId => !this.oldRoles.includes(roleId));
  
   if(this.selectedRoleIds.length==0){
    this.notifyService.showWarning("Atleast one role is require to assign/update.", "");
    return;
   }

    let role : RoleUpdateModel  = new RoleUpdateModel();
    role.userEmail = this.userReg.userEmail;
    role.roleId = this.selectedRoleIds;
    this.spinner.show();
    this.userMasterService.updateRolesUser(role).subscribe(res => {
     this.spinner.hide();
     this.getUserDetais();
     this.notifyService.showSuccess(res.message, "");
     this.editRoleCloseModal.nativeElement.click();    
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

  sendInfo(userEmail:string){
  //  this.authService.checkLoginUserVlidaate();
  
  this.confirmationDialogService.confirm('Confirmation!!', 'Are you sure to send the login information to the user ?')
  .then(
    (confirmed) =>{
     if(confirmed){
      this.spinner.show();
    this.userMasterService.sendLoginInfo(userEmail).subscribe(res => {
     this.spinner.hide();
     this.notifyService.showSuccess(res.message, "");
     this.editRoleCloseModal.nativeElement.click();    
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
  }).catch(
    () => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
    ); 
  } 


  deleteUser(element:any): any {
    this.confirmationDialogService.confirm('Confirmation!!', 'Are you sure to delete the User?')
    .then(
      (confirmed) =>{
       if(confirmed){
    /*this.userMasterService.updateInActiveUser(element).subscribe((data: any) => {
    },error =>{
      if(error.status==403){
        this.router.navigate(['/forbidden']);
      }else if (error.error && error.error.message) {
        this.errorMsg =error.error.message;
        console.log("Error:"+this.errorMsg);
        this.notifyService.showError(this.errorMsg, "My DBQ!");
      } else {
        if(error.status==500 && error.statusText=="Internal Server Error"){
          this.errorMsg=error.statusText+"! Please login again or contact your Help Desk.";
        }else{
          let str;
            if(error.status==400){
            str=error.error;
            }else{
              str=error.message;
              str=str.substring(str.indexOf(":")+1);
            }
            console.log("Error:"+str);
            this.errorMsg=str;
        }
        this.notifyService.showError(this.errorMsg, "My DBQ!");
      }
    });*/
  }
}).catch(
  () => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
  ); 
} 

getReadIcon(readPrv: boolean): string {
  return readPrv ? 'fa fa-check text-success' : 'fa fa-times text-danger';
}

getWriteIcon(writePrv: boolean): string {
  return writePrv ? 'fa fa-check text-success' : 'fa fa-times text-danger';
}

  userRolePermissions: RoleScreenPrv[] = [];
  viewUser(user: any): void {   
    this.userReg = Object.assign(new UserDetails(),user) ; 
    this.userRolePermissions = [];

    if (user && user.roleModel && Array.isArray(user.roleModel)) {
      console.log("user.roleModel",user.roleModel);
      user.roleModel.forEach(role => {
        if (role.screens && Array.isArray(role.screens)) {
          role.screens.forEach(privilege => {
            let screenBaseName = privilege.replace(/_READ|_WRITE$/, '').replace(/_/g, ' ').toUpperCase();
            let existingPermission = this.userRolePermissions.find(
              permission => permission.screenName === screenBaseName
            );  
            if (existingPermission) {
              if (privilege.endsWith('_READ')) {
                existingPermission.readPrv = true;
              }
              if (privilege.endsWith('_WRITE')) {
                existingPermission.writePrv = true;
              }
              existingPermission.approveStatus = role.approveStatus === 'Approved';
            } else {
              this.userRolePermissions.push({
                screenName: screenBaseName,
                readPrv: privilege.endsWith('_READ'),
                writePrv: privilege.endsWith('_WRITE'),
                approveStatus: role.approveStatus === 'Approved'
              });
            }
          });
        }
      });
    }
  }

}
