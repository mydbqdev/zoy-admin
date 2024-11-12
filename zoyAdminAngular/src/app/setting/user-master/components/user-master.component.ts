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
  displayedColumns: string[] = ['firstName', 'userEmail', 'designation', 'status', 'action'];
  public ELEMENT_DATA:UserDetails[];
  dataSource:MatTableDataSource<UserDetails>=new MatTableDataSource<UserDetails>();
  columnSortDirectionsOg: { [key: string]: string | null } = {
    userName: null,
    userMail: null,
    designation: null,
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

  @ViewChild(SidebarComponent) sidemenuComp;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild('editRoleCloseModal') editRoleCloseModal : ElementRef;
  @ViewChild('registerCloseModal') registerCloseModal : ElementRef;

  constructor(private userMasterService : UserMasterService,private formBuilder: FormBuilder,private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,private notifyService:NotificationService,
    private spinner:NgxSpinnerService,private renderer: Renderer2 ,private authService:AuthService,private confirmationDialogService:ConfirmationDialogService,private dataService:DataService){

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


//   data():any{
//     let data=[
//       {
//         "firstName": "Madhan",
//         "lastName": "Doe",
//         "userEmail": "madhan@mydbq.com",
//         "contactNumber": "+1234567890",
//         "status": "active",
//         "designation": "Software Engineer",
//         "approveStatus": "true",
//         "roleModel": [
//           { "id": 1, "roleName": "ALL ACCESS" },
//           { "id": 2, "roleName": "ADMIN" }
//         ]
//       },
//       {
//         "firstName": "Rajesh",
//         "lastName": "Kumar",
//         "userEmail": "rajesh.kumar@mydbq.com",
//         "contactNumber": "+9876543210",
//         "status": "inactive",
//         "designation": "Project Manager",
//         "approveStatus": "true",
//         "roleModel": [
//           { "id": 3, "roleName": "ADMIN" }
//         ]
//       },
//       {
//         "firstName": "John",
//         "lastName": "Smith",
//         "userEmail": "john.smith@mydbq.com",
//         "contactNumber": "+1234598765",
//         "status": "active",
//         "designation": "Developer",
//         "approveStatus": "false",
//         "roleModel": [
//           { "id": 4, "roleName": "USER" }
//         ]
//       },
//       {
//         "firstName": "Priya",
//         "lastName": "Sharma",
//         "userEmail": "priya.sharma@mydbq.com",
//         "contactNumber": "+1234567891",
//         "status": "active",
//         "designation": "UI/UX Designer",
//         "approveStatus": "true",
//         "roleModel": [
//           { "id": 5, "roleName": "DESIGNER" }
//         ]
//       },
//       {
//         "firstName": "Vinay",
//         "lastName": "Singh",
//         "userEmail": "vinay.singh@mydbq.com",
//         "contactNumber": "+1234567892",
//         "status": "inactive",
//         "designation": "System Administrator",
//         "approveStatus": "false",
//         "roleModel": [
//           { "id": 6, "roleName": "ADMIN" }
//         ]
//       },
//       {
//         "firstName": "Anjali",
//         "lastName": "Verma",
//         "userEmail": "anjali.verma@mydbq.com",
//         "contactNumber": "+1234567893",
//         "status": "active",
//         "designation": "QA Engineer",
//         "approveStatus": "true",
//         "roleModel": [
//           { "id": 7, "roleName": "QA" }
//         ]
//       },
//       {
//         "firstName": "Ravi",
//         "lastName": "Patel",
//         "userEmail": "ravi.patel@mydbq.com",
//         "contactNumber": "+1234567894",
//         "status": "inactive",
//         "designation": "Software Engineer",
//         "approveStatus": "true",
//         "roleModel": [
//           { "id": 8, "roleName": "DEVELOPER" }
//         ]
//       },
//       {
//         "firstName": "Sneha",
//         "lastName": "Reddy",
//         "userEmail": "sneha.reddy@mydbq.com",
//         "contactNumber": "+1234567895",
//         "status": "active",
//         "designation": "Marketing Manager",
//         "approveStatus": "false",
//         "roleModel": [
//           { "id": 9, "roleName": "MARKETING" }
//         ]
//       },
//       {
//         "firstName": "Amit",
//         "lastName": "Gupta",
//         "userEmail": "amit.gupta@mydbq.com",
//         "contactNumber": "+1234567896",
//         "status": "active",
//         "designation": "DevOps Engineer",
//         "approveStatus": "true",
//         "roleModel": [
//           { "id": 10, "roleName": "DEVOPS" }
//         ]
//       },
//       {
//         "firstName": "Kavya",
//         "lastName": "Singh",
//         "userEmail": "kavya.singh@mydbq.com",
//         "contactNumber": "+1234567897",
//         "status": "inactive",
//         "designation": "HR Manager",
//         "approveStatus": "true",
//         "roleModel": [
//           { "id": 11, "roleName": "HR" }
//         ]
//       },
//         {
//           "firstName": "Madhan",
//           "lastName": "Doe",
//           "userEmail": "madhan@mydbq.com",
//           "contactNumber": "+1234567890",
//           "status": "active",
//           "designation": "Software Engineer",
//           "approveStatus": "true",
//           "roleModel": [
//             { "id": 1, "roleName": "ALL ACCESS" },
//             { "id": 2, "roleName": "ADMIN" }
//           ]
//         },
//         {
//           "firstName": "Rajesh",
//           "lastName": "Kumar",
//           "userEmail": "rajesh.kumar@mydbq.com",
//           "contactNumber": "+9876543210",
//           "status": "inactive",
//           "designation": "Project Manager",
//           "approveStatus": "true",
//           "roleModel": [
//             { "id": 3, "roleName": "ADMIN" }
//           ]
//         },
//         {
//           "firstName": "John",
//           "lastName": "Smith",
//           "userEmail": "john.smith@mydbq.com",
//           "contactNumber": "+1234598765",
//           "status": "active",
//           "designation": "Developer",
//           "approveStatus": "false",
//           "roleModel": [
//             { "id": 4, "roleName": "USER" }
//           ]
//         },
//         {
//           "firstName": "Priya",
//           "lastName": "Sharma",
//           "userEmail": "priya.sharma@mydbq.com",
//           "contactNumber": "+1234567891",
//           "status": "active",
//           "designation": "UI/UX Designer",
//           "approveStatus": "true",
//           "roleModel": [
//             { "id": 5, "roleName": "DESIGNER" }
//           ]
//         },
//         {
//           "firstName": "Vinay",
//           "lastName": "Singh",
//           "userEmail": "vinay.singh@mydbq.com",
//           "contactNumber": "+1234567892",
//           "status": "inactive",
//           "designation": "System Administrator",
//           "approveStatus": "false",
//           "roleModel": [
//             { "id": 6, "roleName": "ADMIN" }
//           ]
//         },
//         {
//           "firstName": "Anjali",
//           "lastName": "Verma",
//           "userEmail": "anjali.verma@mydbq.com",
//           "contactNumber": "+1234567893",
//           "status": "active",
//           "designation": "QA Engineer",
//           "approveStatus": "true",
//           "roleModel": [
//             { "id": 7, "roleName": "QA" }
//           ]
//         },
//         {
//           "firstName": "Ravi",
//           "lastName": "Patel",
//           "userEmail": "ravi.patel@mydbq.com",
//           "contactNumber": "+1234567894",
//           "status": "inactive",
//           "designation": "Software Engineer",
//           "approveStatus": "true",
//           "roleModel": [
//             { "id": 8, "roleName": "DEVELOPER" }
//           ]
//         },
//         {
//           "firstName": "Sneha",
//           "lastName": "Reddy",
//           "userEmail": "sneha.reddy@mydbq.com",
//           "contactNumber": "+1234567895",
//           "status": "active",
//           "designation": "Marketing Manager",
//           "approveStatus": "false",
//           "roleModel": [
//             { "id": 9, "roleName": "MARKETING" }
//           ]
//         },
//         {
//           "firstName": "Amit",
//           "lastName": "Gupta",
//           "userEmail": "amit.gupta@mydbq.com",
//           "contactNumber": "+1234567896",
//           "status": "active",
//           "designation": "DevOps Engineer",
//           "approveStatus": "true",
//           "roleModel": [
//             { "id": 10, "roleName": "DEVOPS" }
//           ]
//         },
//         {
//           "firstName": "Kavya",
//           "lastName": "Singh",
//           "userEmail": "kavya.singh@mydbq.com",
//           "contactNumber": "+1234567897",
//           "status": "inactive",
//           "designation": "HR Manager",
//           "approveStatus": "true",
//           "roleModel": [
//             { "id": 11, "roleName": "HR" }
//           ]
//         },
//         {
//           "firstName": "Sushant",
//           "lastName": "Shukla",
//           "userEmail": "sushant.shukla@mydbq.com",
//           "contactNumber": "+1234567898",
//           "status": "active",
//           "designation": "Security Specialist",
//           "approveStatus": "true",
//           "roleModel": [
//             { "id": 12, "roleName": "SECURITY" }
//           ]
//         },
//         {
//           "firstName": "Deepika",
//           "lastName": "Patel",
//           "userEmail": "deepika.patel@mydbq.com",
//           "contactNumber": "+1234567899",
//           "status": "inactive",
//           "designation": "Compliance Officer",
//           "approveStatus": "false",
//           "roleModel": [
//             { "id": 13, "roleName": "COMPLIANCE" }
//           ]
//         },
//         {
//           "firstName": "Tariq",
//           "lastName": "Mehmood",
//           "userEmail": "tariq.mehmood@mydbq.com",
//           "contactNumber": "+1234567800",
//           "status": "active",
//           "designation": "Data Analyst",
//           "approveStatus": "true",
//           "roleModel": [
//             { "id": 14, "roleName": "ANALYST" }
//           ]
//         },
//         {
//           "firstName": "Asha",
//           "lastName": "Gupta",
//           "userEmail": "asha.gupta@mydbq.com",
//           "contactNumber": "+1234567801",
//           "status": "inactive",
//           "designation": "Accountant",
//           "approveStatus": "true",
//           "roleModel": [
//             { "id": 15, "roleName": "ACCOUNTANT" }
//           ]
//         },
//         {
//           "firstName": "Neha",
//           "lastName": "Verma",
//           "userEmail": "neha.verma@mydbq.com",
//           "contactNumber": "+1234567802",
//           "status": "active",
//           "designation": "Business Analyst",
//           "approveStatus": "false",
//           "roleModel": [
//             { "id": 16, "roleName": "ANALYST" }
//           ]
//         },
//         {
//           "firstName": "Prakash",
//           "lastName": "Singh",
//           "userEmail": "prakash.singh@mydbq.com",
//           "contactNumber": "+1234567803",
//           "status": "active",
//           "designation": "Legal Advisor",
//           "approveStatus": "true",
//           "roleModel": [
//             { "id": 17, "roleName": "LEGAL" }
//           ]
//         },
//         {
//           "firstName": "Ritika",
//           "lastName": "Sharma",
//           "userEmail": "ritika.sharma@mydbq.com",
//           "contactNumber": "+1234567804",
//           "status": "inactive",
//           "designation": "Sales Manager",
//           "approveStatus": "true",
//           "roleModel": [
//             { "id": 18, "roleName": "SALES" }
//           ]
//         },
//         {
//           "firstName": "Sandeep",
//           "lastName": "Gupta",
//           "userEmail": "sandeep.gupta@mydbq.com",
//           "contactNumber": "+1234567805",
//           "status": "active",
//           "designation": "Frontend Developer",
//           "approveStatus": "false",
//           "roleModel": [
//             { "id": 19, "roleName": "DEVELOPER" }
//           ]
//         },
//         {
//           "firstName": "Hina",
//           "lastName": "Ali",
//           "userEmail": "hina.ali@mydbq.com",
//           "contactNumber": "+1234567806",
//           "status": "inactive",
//           "designation": "Content Writer",
//           "approveStatus": "true",
//           "roleModel": [
//             { "id": 20, "roleName": "CONTENT WRITER" }
//           ]
//         },
//         {
//           "firstName": "Vikram",
//           "lastName": "Choudhary",
//           "userEmail": "vikram.choudhary@mydbq.com",
//           "contactNumber": "+1234567807",
//           "status": "active",
//           "designation": "Product Manager",
//           "approveStatus": "true",
//           "roleModel": [
//             { "id": 21, "roleName": "PRODUCT" }
//           ]
//         }
//     ]
//     ;
//      return data;
//  }

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

  resetForm() {
  this.submitted=false;
  this.createOrUpdate=true;
  this.form.reset(); 
  }

  createOrUpdate:boolean=true;
  submittUserDetails(){
    this.submitted=true;	
    console.log(this.userReg);
    if (this.form.invalid) {
      return;
      }
      if (this.userReg.password !== this.userReg.repeatPassword) {
      return;
      }
    if(this.createOrUpdate){
      console.log("this.createUser()>",this.userReg);
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
      this.resetForm();
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
      this.resetForm();
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
  

  editRole(row:any ){
  this.resetForm();
  this.createOrUpdate=false;
  this.checkedApplications = {};
  this.selectedItems= [];
  this.selectedRoleIds.splice(0);
  this.userReg = Object.assign(new UserDetails(),row)  ;
  this.selectedItems= this.userReg.roleModel;
  for(let role of this.userReg.roleModel){
     this.selectedRoleIds.push(role.id);
  }

  }
   updateRolesUser(){
   // this.authService.checkLoginUserVlidaate();
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

  sendInfo(data:any){
  //  this.authService.checkLoginUserVlidaate();
  
  this.confirmationDialogService.confirm('Confirmation!!', 'Are you sure to delete the User?')
  .then(
    (confirmed) =>{
     if(confirmed){
      this.spinner.show();
  //   this.userMasterService.saveData(role).subscribe(res => {
  //    this.spinner.hide();
  //    this.notifyService.showSuccess(res.message, "");
  //    this.editRoleCloseModal.nativeElement.click();    
  //  }, error => {
  //   this.spinner.hide();
  //   if(error.status==403){
  //     this.router.navigate(['/forbidden']);
  //   }else if (error.error && error.error.message) {
  //     this.errorMsg = error.error.message;
  //     console.log("Error:" + this.errorMsg);
  //     this.notifyService.showError(this.errorMsg, "");
  //   } else {
  //     if (error.status == 500 && error.statusText == "Internal Server Error") {
  //       this.errorMsg = error.statusText + "! Please login again or contact your Help Desk.";
  //     } else {
  //       let str;
  //       if (error.status == 400) {
  //         str = error.error;
  //       } else {
  //         str = error.message;
  //         str = str.substring(str.indexOf(":") + 1);
  //       }
  //       console.log("Error:" + str);
  //       this.errorMsg = str;
  //     }
  //     if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
  //   }
  // });
  }
  }).catch(
    () => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
    ); 
  } 


  deleteUser(element:any): any {
    console.log("deleteUser>>",element)
  
    this.confirmationDialogService.confirm('Confirmation!!', 'Are you sure to delete the User?')
    .then(
      (confirmed) =>{
       if(confirmed){
    return;
    this.userMasterService.updateInActiveUser(element).subscribe((data: any) => {

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
    });
  }
}).catch(
  () => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
  ); 
} 


}
