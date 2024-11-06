import { AfterViewInit, Component, ElementRef, OnInit, Renderer2, ViewChild } from '@angular/core';
//import { SideNavMenuComponent } from 'src/app/components/sideBar/side-nav.component';
import { NgxSpinnerService } from 'ngx-spinner';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { AuthService } from 'src/app/common/service/auth.service';
import { UserService } from 'src/app/common/service/user.service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import { UserMasterService } from '../service/user-master.service';
import { AppRole } from '../models/app-role-model';
import { RoleSave } from '../models/rolesave-model';

import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { UserRole } from 'src/app/common/models/user-role';
import { RegisterDetails } from '../models/register-details';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';

@Component({
  selector: 'app-user-master',
  templateUrl: './user-master.component.html',
  styleUrls: ['./user-master.component.css']
})
export class UserMasterComponent implements OnInit,AfterViewInit{

  public ELEMENT_DATA:UserRole[];
  @ViewChild("paginator",{static:true}) paginator: MatPaginator;
  dataSource:MatTableDataSource<UserRole>=new MatTableDataSource<UserRole>();
  pageSize:number=5;
  pageSizeOptions=[5,10,20,50];
  public lastPageSize:number=0;
  public totalProduct:number=0;
  displayedColumns: string[] = [ 'userName', 'userMail', 'roles', 'action'];
 // @ViewChild(SideNavMenuComponent) sidemenuComp;
  empCodeFilter:string="";
  message:string;
  extraApplications:boolean=false;
  getUsers : UserRole[] = [];
  editUserRole : UserRole = new UserRole();
  rolesList: AppRole[]=[];
  dropdownOptions: any[];
  selectedRoleIds: any[]=[38];
  errorMsg: any;
  dropdownList = [];
   settings = {};
   activeApplicationList:string[]=[]
   selectedItems : any[] = [];
   public rolesArray: string[] = [];
   @ViewChild('editCloseModal') editCloseModal : ElementRef;
   @ViewChild('registerCloseModal') registerCloseModal : ElementRef;
  mySubscription: any;
  public userNameSession:string="";
  userActiveApplicationList: string[] = [];
  shouldBeChecked = [];
  checkedApplications: { [key: string]: boolean } = {};
  submitted=false;
	error: string = '';
	form: FormGroup;
	userReg :RegisterDetails=new RegisterDetails();
	roles:string[]=['Super Admin','Finance Admin','Support Admin']
	desigRole:string="";
  constructor(private userMasterService : UserMasterService,private formBuilder: FormBuilder,private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,private notifyService:NotificationService,
    private spinner:NgxSpinnerService,private renderer: Renderer2 ,private authService:AuthService,private confirmationDialogService:ConfirmationDialogService){

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
     this.getRlesList();
    // this.getApplicationAccessList();
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
		  email: ['', [
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
		this.form.reset(); 
	  }
	doRegister() {
		this.submitted=true;	
		console.log(this.userReg);
		if (this.form.invalid) {
			return;
		  }
		  if (this.userReg.password !== this.userReg.repeatPassword) {
			return;
		   }
		//    oftergetting succs message;
		this.notifyService.showSuccess("Account created Successfully","congratulations");
		this.resetForm();
    this.registerCloseModal.nativeElement.click(); 
return;
		this.spinner.show();		     
		this.submitted=false;
		this.authService.signupUser(this.userReg).subscribe((data) => {
		this.notifyService.showSuccess("your account has been succssfully created.", "Congratulations,");
		this.spinner.hide();
		},error =>{
			this.spinner.hide();
			console.log("error.error",error)
			this.error = (error.error.error !=undefined?(error.error.error  +"."):"")
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

   
ngAfterViewInit(){
  // this.sidemenuComp.expandMenu(9);
  // this.sidemenuComp.activeMenu(9,'user-master');
  this.dataSource.paginator=this.paginator;
   setTimeout(()=>{
     this.loadInitialData(this.paginator.pageIndex +1, this.paginator.pageSize);
    },100);
}
 
onCheckboxChange(application: string) {
  if (this.checkedApplications[application]) {
    this.userActiveApplicationList.push(application);
  } else {
    const index = this.userActiveApplicationList.indexOf(application);
    if (index !== -1) {
      this.userActiveApplicationList.splice(index, 1);
    }
  }
}

getRlesListMock(){
  this.rolesList =
  [
   {
       "id": 225,
       "roleName": "ADMIN"
   },
   {
       "id": 227,
       "roleName": "EMS HR"
   },
   {
       "id": 230,
       "roleName": "ALL ACCESS"
   },
   {
       "id": 238,
       "roleName": "RM"
   },
   {
       "id": 258,
       "roleName": "ADMIN READ"
   },
   {
       "id": 260,
       "roleName": "SUPER ADMIN "
   },
   {
       "id": 261,
       "roleName": "READ ONLY"
   },
   {
       "id": 262,
       "roleName": "SUPER ADMIN@123"
   },
   {
       "id": 263,
       "roleName": "ADMIN1"
   },
   {
       "id": 268,
       "roleName": "ADMIN REPORTS"
   },
   {
       "id": 269,
       "roleName": "ADMIN REPORTS1"
   },
   {
       "id": 270,
       "roleName": "ADMIN2"
   },
   {
       "id": 272,
       "roleName": "TEST"
   },
   {
       "id": 273,
       "roleName": "FINANCE MANAGER"
   },
   {
       "id": 276,
       "roleName": "FINANCE_ MANAGER"
   },
   {
       "id": 277,
       "roleName": "HR"
   },
   {
       "id": 278,
       "roleName": "CEO"
   },
   {
       "id": 280,
       "roleName": "H_R"
   },
   {
       "id": 281,
       "roleName": "AUDIT"
   },
   {
       "id": 282,
       "roleName": "ACCOUNTANT"
   },
   {
       "id": 284,
       "roleName": "HR ACCOUNTANT"
   },
   {
       "id": 293,
       "roleName": "TEST LOAD"
   },
   {
       "id": 294,
       "roleName": "REG TEST"
   },
   {
       "id": 295,
       "roleName": "TEST SUPER ADMIN"
   },
   {
       "id": 297,
       "roleName": "ADMIN11"
   },
   {
       "id": 299,
       "roleName": "TECHNICAL LEAD"
   }
];
 this.dropdownList = this.rolesList; 

}

  getRlesList(): any {
  this.getRlesListMock();
    return;
    this.authService.checkLoginUserVlidaate();
    this.userMasterService.rolesDropdown().subscribe((data: any) => {
      this.rolesList =
       [
        {
            "id": 225,
            "roleName": "ADMIN"
        },
        {
            "id": 227,
            "roleName": "EMS HR"
        },
        {
            "id": 230,
            "roleName": "ALL ACCESS"
        },
        {
            "id": 238,
            "roleName": "RM"
        },
        {
            "id": 258,
            "roleName": "ADMIN READ"
        },
        {
            "id": 260,
            "roleName": "SUPER ADMIN "
        },
        {
            "id": 261,
            "roleName": "READ ONLY"
        },
        {
            "id": 262,
            "roleName": "SUPER ADMIN@123"
        },
        {
            "id": 263,
            "roleName": "ADMIN1"
        },
        {
            "id": 268,
            "roleName": "ADMIN REPORTS"
        },
        {
            "id": 269,
            "roleName": "ADMIN REPORTS1"
        },
        {
            "id": 270,
            "roleName": "ADMIN2"
        },
        {
            "id": 272,
            "roleName": "TEST"
        },
        {
            "id": 273,
            "roleName": "FINANCE MANAGER"
        },
        {
            "id": 276,
            "roleName": "FINANCE_ MANAGER"
        },
        {
            "id": 277,
            "roleName": "HR"
        },
        {
            "id": 278,
            "roleName": "CEO"
        },
        {
            "id": 280,
            "roleName": "H_R"
        },
        {
            "id": 281,
            "roleName": "AUDIT"
        },
        {
            "id": 282,
            "roleName": "ACCOUNTANT"
        },
        {
            "id": 284,
            "roleName": "HR ACCOUNTANT"
        },
        {
            "id": 293,
            "roleName": "TEST LOAD"
        },
        {
            "id": 294,
            "roleName": "REG TEST"
        },
        {
            "id": 295,
            "roleName": "TEST SUPER ADMIN"
        },
        {
            "id": 297,
            "roleName": "ADMIN11"
        },
        {
            "id": 299,
            "roleName": "TECHNICAL LEAD"
        }
    ];
      this.dropdownList = this.rolesList; 
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

  getApplicationAccessList(): any {
    this.userMasterService.applicationNamesDropdown().subscribe((data: any) => {
      this.activeApplicationList=data;
      if(this.activeApplicationList.length<1){
        this.extraApplications=false;
      }else{
        this.extraApplications=true;
      }
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
  this.checkedApplications = {};
  this.userActiveApplicationList = [];
  this.selectedItems= [];
  this.selectedRoleIds.splice(0);
 this.editUserRole = this.getUsers.find(user => user.empCode == row.empCode);
 this.selectedItems= this.editUserRole.roleModel;
 for(let role of this.editUserRole.roleModel){
 this.selectedRoleIds.push(role.id);
 }
 if (row && row.applicationNames) {
  this.userActiveApplicationList = Array.from(row.applicationNames.split(","));
  this.shouldBeChecked = Array.from(row.applicationNames.split(","));
  for (let i = 0; i < this.shouldBeChecked.length; i++) {
    const checkboxValue = this.shouldBeChecked[i];
    this.checkedApplications[checkboxValue] = true;
  }
}
}

saveUser(){
  this.authService.checkLoginUserVlidaate();
  let role : RoleSave  = new RoleSave();
  role.userName = this.editUserRole.empMail;
  role.roles = this.selectedRoleIds;
  role.applicationNames = this.userActiveApplicationList;
  this.spinner.show();
  this.userMasterService.saveData(role).subscribe(res => {
   this.spinner.hide();
  if (res.status == '1'){
    this.notifyService.showSuccess("Role assign has been updated successfully", "");
    this.editCloseModal.nativeElement.click(); 
    this.loadInitialData(this.paginator.pageIndex +1, this.paginator.pageSize);
  }else{
    this.notifyService.showWarning(res.status, "");
  }
  
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


loadInitialMockData(){
var  data ={
    "totalCount": 72,
    "listObj": [
        {
            "empCode": "1010",
            "empName": "Aparna  Muvvala",
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
            "empName": "likki  A",
            "roles": "",
            "empMail": "likki@mydbq.com",
            "roleModel": [],
            "applicationNames": ""
        },
        {
            "empCode": "1015",
            "empName": "rajesh  A",
            "roles": "",
            "empMail": "rajesh@mydbq.com",
            "roleModel": [],
            "applicationNames": ""
        },
        {
            "empCode": "1021",
            "empName": "keerthi K lakshmi L Dasari D",
            "roles": "ALL ACCESS",
            "empMail": "l.dasari@mydbq.com",
            "roleModel": [
                {
                    "id": 230,
                    "roleName": "ALL ACCESS"
                }
            ],
            "applicationNames": "HR PORTAL"
        },
        {
            "empCode": "1022",
            "empName": "ramesh  A",
            "roles": "",
            "empMail": "ramesh@mydbq.com",
            "roleModel": [],
            "applicationNames": ""
        }
    ]
};

  this.getUsers  = data.listObj;
  this.totalProduct=data.totalCount;
  this.ELEMENT_DATA=Object.assign([],data.listObj);
   this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
  this.spinner.hide();

}
  loadInitialData(pageIndex:number,pageSize:number){  
    this.loadInitialMockData();
    return;
   // this.authService.checkLoginUserVlidaate();
    this.lastPageSize=pageSize;
      this.spinner.show();
      this.userMasterService.find(this.empCodeFilter,pageIndex,pageSize).subscribe((data) => {
        data ={
        "totalCount": 72,
        "listObj": [
            {
                "empCode": "1010",
                "empName": "Aparna  Muvvala",
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
                "empName": "likki  A",
                "roles": "",
                "empMail": "likki@mydbq.com",
                "roleModel": [],
                "applicationNames": ""
            },
            {
                "empCode": "1015",
                "empName": "rajesh  A",
                "roles": "",
                "empMail": "rajesh@mydbq.com",
                "roleModel": [],
                "applicationNames": ""
            },
            {
                "empCode": "1021",
                "empName": "keerthi K lakshmi L Dasari D",
                "roles": "ALL ACCESS",
                "empMail": "l.dasari@mydbq.com",
                "roleModel": [
                    {
                        "id": 230,
                        "roleName": "ALL ACCESS"
                    }
                ],
                "applicationNames": "HR PORTAL"
            },
            {
                "empCode": "1022",
                "empName": "ramesh  A",
                "roles": "",
                "empMail": "ramesh@mydbq.com",
                "roleModel": [],
                "applicationNames": ""
            }
        ]
    };
        this.getUsers  = data.listObj;
        this.totalProduct=data.totalCount;
        this.ELEMENT_DATA=Object.assign([],data.listObj);
         this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
        this.spinner.hide();
      },error =>{
        if(error.status==403){
          this.router.navigate(['/forbidden']);
        }else if (error.error && error.error.message) {
          this.errorMsg =error.error.message;
          console.log("Error:"+this.errorMsg);
          this.notifyService.showError(this.errorMsg, "");
          this.spinner.hide();
        } else {
          this.spinner.hide();
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
      }
      );
    }
  
   
    filterData(){
      this.paginator.pageIndex=0;
      this.loadInitialData(this.paginator.pageIndex +1, this.paginator.pageSize);
    }
    pageChanged(event:any){
      this.dataSource=new MatTableDataSource<UserRole>();
      if(this.lastPageSize!=event.pageSize){
      this.paginator.pageIndex=0;
       event.pageIndex=0;
       }
      this.loadInitialData(this.paginator.pageIndex +1, event.pageSize);
    }


    sendInfo(element:any): any {
      console.log("sendInfo>>>",element)
      return;
      this.userMasterService.applicationNamesDropdown().subscribe((data: any) => {
        this.activeApplicationList=data;
        if(this.activeApplicationList.length<1){
          this.extraApplications=false;
        }else{
          this.extraApplications=true;
        }
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

    deleteUser(element:any): any {
      console.log("deleteUser>>",element)
    

      this.confirmationDialogService.confirm('Confirmation!!', 'Are you sure to delete the User?')
      .then(
        (confirmed) =>{
         if(confirmed){
      return;
      this.userMasterService.applicationNamesDropdown().subscribe((data: any) => {
        this.activeApplicationList=data;
        if(this.activeApplicationList.length<1){
          this.extraApplications=false;
        }else{
          this.extraApplications=true;
        }
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
