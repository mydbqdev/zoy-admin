import { HttpClient } from "@angular/common/http";
import { Component, OnInit, AfterViewInit, ViewChild, ElementRef, Inject, Renderer2, inject } from "@angular/core";
import { FormGroup, FormBuilder, Validators } from "@angular/forms";
import { ActivatedRoute, Router, NavigationEnd } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { AuthService } from "src/app/common/service/auth.service";
import { UserService } from "src/app/common/service/user.service";
import { AlertDialogService } from "src/app/common/shared/alert-dialog/alert-dialog.service";
import { NotificationService } from "src/app/common/shared/message/notification.service";
import { RoleModel } from "../models/role-model";
import { RoleMasterService } from "../service/role-master.service";
import * as $ from 'jquery';
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort, Sort } from "@angular/material/sort";
import { DataService } from 'src/app/common/service/data.service';
import { RoleScreenModel } from "src/app/common/models/rolescreen-model";
import { SidebarComponent } from 'src/app/components/sidebar/sidebar.component';
import { ConfirmationDialogService } from "src/app/common/shared/confirm-dialog/confirm-dialog.service";

@Component({
  selector: 'app-role-master',
  templateUrl: './role-master.component.html',
  styleUrls: ['./role-master.component.css']
})
export class RoleMasterComponent implements OnInit,AfterViewInit{
 @ViewChild(SidebarComponent) sidemenuComp;
  form !: FormGroup;
  roleModel: RoleModel = new RoleModel();
  getRoles : RoleModel[] = [];
  editRoles : RoleModel = new RoleModel();
  deleteRoles : RoleModel = new RoleModel();
  role : string = '';
  desc : string = '';
  editDesc : string = '';
  id : number = 0;
  dataSelectedRead: string[] = [];
  dataSelectedWrite: string[] = [];
  pageSize: number = 10; 
  pageSizeOptions: number[] = [10, 20, 50]; 
  totalProduct: number = 0; 
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild('myForm') myFormRef!: ElementRef; 
  @ViewChild('closeNewModal') closeNewModal : ElementRef;
  @ViewChild('editNewModal') editNewModal : ElementRef; 
  errorMsg: any;
  submitted: boolean;
  public userNameSession:string="";
  mySubscription: any;
  public rolesArray: string[] = []; 
  isExpandSideBar:boolean=true;
  public ELEMENT_DATA:RoleModel[];
  dataSource:MatTableDataSource<RoleModel>=new MatTableDataSource<RoleModel>();
  displayedColumns: string[] = [ 'roleName', 'desc','action'];
  columnSortDirectionsOg: { [key: string]: string | null } = {
    roleName: null
  };
  columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);
//@Inject(defMenuEnable) private defMenuEnable:
  constructor( private route: ActivatedRoute,private roleService : RoleMasterService , private renderer: Renderer2, private router: Router,private confirmationDialogService:ConfirmationDialogService,
    private http: HttpClient, private userService: UserService, private formBuilder: FormBuilder, private spinner: NgxSpinnerService, private authService: AuthService, private notifyService: NotificationService, private alertDialogService: AlertDialogService,private dataService:DataService) {
      this.authService.checkLoginUserVlidaate();
      this.userNameSession=this.userService.getUsername();
  //  console.log("defMenuEnable",defMenuEnable);
    // this.defRoleMenu=defMenuEnable;
     
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
		 	  // Trick the Router into believing it's last link wasn't previously loaded
			  this.router.navigated = false;
		 	}
		   });
    this.dataService.getIsExpandSideBar.subscribe(name=>{
			this.isExpandSideBar=name;
		});
  }
  
  ngOnInit(){
    // if(this.userNameSession==null || this.userNameSession==undefined || this.userNameSession==''){
    //   this.router.navigate(['/']);
    //   }
    this.getRolesData();   
    this.form = this.formBuilder.group({
      id:[''],
      roleName : ['', [Validators.required]],
      desc : ['', [Validators.required]]
    });
    
  }
  ngOnDestroy() {
		if (this.mySubscription) {
		  this.mySubscription.unsubscribe();
		}
	  }
  ngAfterViewInit(){
    this.sidemenuComp.expandMenu(4);
    this.sidemenuComp.activeMenu(4,'role-master');
    this.dataService.setHeaderName("Role and Permission");
  }

 clearArrays(){
  this.submitted = false;
  this.dataSelectedRead = [];
  this.dataSelectedWrite = [];
 }

 announceSortChange(sortState: Sort): void {
  this.columnSortDirections[sortState.active] = sortState.direction;
}

  saveRoleModel() {
   this.authService.checkLoginUserVlidaate();
   this.submitted = true
   if(this.form.invalid){
     return;
   }
    if(this.ELEMENT_DATA.filter(d=>d.roleName == this.role).length>0){
      this.notifyService.showInfo(this.role+' role name is already available.', "Please change role name.");  
      return;
    }
    this.dataSelectedRead =[];
    this.dataSelectedWrite =[];
    this.getCheckedMenulist("CREATE",'read');
    this.getCheckedMenulist("CREATE",'write');

    const convertToRoleScreen = (dataread: string[], datawrite: string[]): RoleModel=> {
      const roleScreen: RoleModel= new RoleModel();
    
      const allScreens = [...dataread, ...datawrite];
      const screenNames = new Set<string>();
    
      allScreens.forEach(screen => {
        const baseScreenName = screen.replace(/_READ|_WRITE/g, ''); 
        screenNames.add(baseScreenName);
      });
    
      screenNames.forEach(screenName => {
        roleScreen.roleScreen.push({
          screenName,
          readPrv: dataread.some(screen => screen.includes(screenName + '_READ')),
          writePrv: datawrite.some(screen => screen.includes(screenName + '_WRITE')),
          approveStatus:false
        });
      });
    
      return roleScreen;
    };
    
    // Get the result
    this.roleModel = convertToRoleScreen(this.dataSelectedRead, this.dataSelectedWrite);
    this.roleModel.id = this.id;
    this.roleModel.roleName = this.role;
    this.roleModel.desc = this.desc;

    if(this.roleModel.roleScreen.length==0){
      this.notifyService.showWarning("Atleast one screen is require to create.", "");
      return;
    }

    // this.roleModel.dataread = this.dataSelectedRead;
    // this.roleModel.datawrite = this.dataSelectedWrite;
    if(!this.form.invalid){
      
      this.spinner.show();
      this.roleService.saveMyRole(this.roleModel).subscribe((result) =>{
        this.spinner.hide();
        this.notifyService.showSuccess(result.message, "");  
        this.closeNewModal.nativeElement.click(); 
        this.reloadCurrentPage(); 
        // this.getRolesData();
       }, error => {
        this.spinner.hide();
        if(error.status == 0) {
          this.notifyService.showError("Internal Server Error/Connection not established", "")
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
    
  }


    saveEditedData(){  
      this.authService.checkLoginUserVlidaate();
      this.submitted=true;      
      if(this.editRoles.desc=="" || this.editRoles.desc==" "){
        return;
      }
      this.dataSelectedRead =[];
      this.dataSelectedWrite =[];
      this.getCheckedMenulist("EDIT",'read');
      this.getCheckedMenulist("EDIT",'write');
      
      const convertToRoleScreen = (dataread: string[], datawrite: string[]): RoleModel=> {
        const roleScreen: RoleModel= new RoleModel();
      
        const allScreens = [...dataread, ...datawrite];
        const screenNames = new Set<string>();
      
        allScreens.forEach(screen => {
          const baseScreenName = screen.replace(/_READ|_WRITE/g, ''); // Remove '_READ' or '_WRITE' for screen name
          screenNames.add(baseScreenName);
        });
      
        screenNames.forEach(screenName => {
          roleScreen.roleScreen.push({
            screenName,
            readPrv: dataread.some(screen => screen.includes(screenName + '_READ')),
            writePrv: datawrite.some(screen => screen.includes(screenName + '_WRITE')),
            approveStatus:false
          });
        });
      
        return roleScreen;
      };
      

      this.roleModel = convertToRoleScreen(this.dataSelectedRead, this.dataSelectedWrite);
      this.roleModel.id  = this.editRoles.id;
      this.roleModel.roleName = this.editRoles.roleName;
      this.roleModel.desc = this.editRoles.desc;

      if(this.roleModel.roleScreen.length==0){
        this.notifyService.showWarning("Atleast one screen is require to update.", "");
        return;
      }
      // this.roleModel.dataread = this.dataSelectedRead;
      // this.roleModel.datawrite = this.dataSelectedWrite;
      this.spinner.show();
      this.roleService.editRole(this.roleModel).subscribe((result) =>{
        this.spinner.hide();
        this.notifyService.showSuccess(result.message, "");  
       this.editNewModal.nativeElement.click(); 
          this.reloadCurrentPage(); 
          // this.getRolesData();
      }, error => {
        this.spinner.hide();
        if(error.status == 0) {
          this.notifyService.showError("Internal Server Error/Connection not established", "")
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

  getRolesData() {
 // this.authService.checkLoginUserVlidaate();
        this.spinner.show();
        this.roleService.getRolesList().subscribe(res => {
          this.ELEMENT_DATA=Object.assign([],res);
          this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
          this.dataSource.sort = this.sort;
          this.dataSource.paginator = this.paginator;
          this.columnSortDirections['roleName']='asc';
          this.dataSource.sortingDataAccessor = (data, sortHeaderId) => data[sortHeaderId].toLocaleLowerCase();
        this.spinner.hide();
      },error =>{
        this.spinner.hide();
        if(error.status == 0) {
          this.notifyService.showError("Internal Server Error/Connection not established", "")
         }else if(error.status==403){
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


getEditData(data:any){
function convertRoleData(input: RoleModel): any {
  const output: RoleModel = new RoleModel();
  output.id=input.id;
  output.roleName=input.roleName;
  output.desc=input.desc;
  output.roleScreenList=new RoleScreenModel();
  // Iterate over the roleScreen array and build the permissions structure
  input.roleScreen.forEach(screen => {
      if (screen.readPrv) {
          output.roleScreenList[`${screen.screenName.toUpperCase()}_READ`] = true;
      }
      if (screen.writePrv) {
          output.roleScreenList[`${screen.screenName.toUpperCase()}_WRITE`] = true;
      }
  });

  return output;
}

// Apply the conversion
const model = convertRoleData(data);
this.editRoles =Object.assign(model); 
const propertyNames = Object.keys(this.editRoles.roleScreenList).filter((propertyName) => {
  return  this.editRoles.roleScreenList[propertyName] !== false;
});
for(let roles of propertyNames){
  if(roles.includes("READ"))
      this.dataSelectedRead.push(roles);
  else
    this.dataSelectedWrite.push(roles);    
}

this.getMenuSelectedForEDITPOPUP(this.menuListEdit,"read",this.dataSelectedRead);
this.getMenuSelectedForEDITPOPUP(this.menuListEdit,"write",this.dataSelectedWrite);
}


deleteRecord(row : RoleModel){
  this.confirmationDialogService.confirm('Confirmation!!', 'Are you sure to delete the User?')
  .then(
    (confirmed) =>{
     if(confirmed){
 // this.authService.checkLoginUserVlidaate();
  this.spinner.show();
  this.roleService.deleteRole(row.id,row.roleName).subscribe((result) => {
    this.notifyService.showSuccess(result.message, "");
    this.getRolesData();
    this.spinner.hide();
  },error =>{
    this.spinner.hide();
    if(error.status == 0) {
      this.notifyService.showError("Internal Server Error/Connection not established", "")
     }else if(error.status==403){
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
          str=error.error.error;
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
  }).catch(
    () => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
    ); 
  
}


reloadCurrentPage(){
  let currentUrl = this.router.url;
  this.router.navigateByUrl('/', {skipLocationChange: true}).then(() => {
  this.router.navigate([currentUrl]);
  });
 }


 onChangeCheckAll(id,type,sec){
  var str=sec+type;
  let groupCheck=Array.from($<HTMLInputElement>("input[name='"+str+"']"));
  if(id.target.checked){
    for(var i=0;i<groupCheck.length;i++){
      groupCheck[i].checked=true;
    } 
    
    if(type=='write'){
     var strRead= sec+'read';
     let groupCheckRead=Array.from($<HTMLInputElement>("input[name='"+strRead+"']"));
      for(var i=0;i<groupCheckRead.length;i++){
        groupCheckRead[i].checked=true;
      } 
      $<HTMLInputElement>('input#'+sec+'readAll').prop('checked', true); 
    }
  }else{
    if(type=='write'){
      for(var i=0;i<groupCheck.length;i++){
        groupCheck[i].checked=false;
      } 
    }else if(type=='read'){
      var strWrite= sec+'write';
      var countWrite=0;
       let groupCheckwrite=Array.from($<HTMLInputElement>("input[name='"+strWrite+"']"));
        for(var i=0;i<groupCheckwrite.length;i++){
          if(groupCheckwrite[i].checked==true){
            countWrite=1;
            break;
          }
        }
        
        if(countWrite==0){
          for(var i=0;i<groupCheck.length;i++){
            groupCheck[i].checked=false;
          } 
        }else{
          id.target.checked=true;
          this.alertDialogService.alert("Alert!!","You can't change the selection as it's associated with write.").then(
            (confirmed) =>{}).catch(
              () => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
            );
        }
    }
  }
}

onChangeCheck(id,type,sec,page){
  var str=sec+type;
  let groupCheck=Array.from($<HTMLInputElement>("input[name='"+str+"']"));
  let groupCheckRead=Array.from($<HTMLInputElement>("input[name='"+sec+"read']"));
  if(id.target.checked){
    if(type=='write'){
       var strRead= sec+'Read'+page;
       $('input#'+strRead).prop('checked', true);
      }
  }else{
    if(type=='read'){
       var strRead= sec+'Write'+page;
       if($('input#'+strRead).prop('checked')){
         id.target.checked=true;
         this.alertDialogService.alert("Alert!!","You can't change the selection as it's associated with write.").then(
          (confirmed) =>{}).catch(
            () => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
          );
        //  alert("Unable to change as associated with write.");
       }
      } 
  }
  var countCheck=0;
  var countCheckRead=0;
  for(var i=0;i<groupCheck.length;i++){
    if(groupCheck[i].checked){
      countCheck=countCheck+1;
    }
  }
  for(var i=0;i<groupCheckRead.length;i++){
    if(groupCheckRead[i].checked){
      countCheckRead=countCheckRead+1;
    }
  }
  if(countCheck==groupCheck.length){
   $('input#'+str+'All').prop('indeterminate', false);
    $('input#'+str+'All').prop('checked', true);
  }else if(countCheck>0 && countCheck<groupCheck.length){
    $('input#'+str+'All').prop('indeterminate', true);
  }else{
    $('input#'+str+'All').prop('checked', false);
    $('input#'+str+'All').prop('indeterminate', false);
  }
  
  if(countCheckRead==groupCheckRead.length){
      $('input#'+sec+'readAll').prop('checked', true);
    }else{
      $('input#'+sec+'readAll').prop('checked', false);
    }
}

menuListCrete:string[]=['ownersc','usersc','tenantsc','financesc','supportsc','settingsc'];
menuListEdit:string[]=['ownerse','userse','tenantse','financese','supportse','settingse'];
menuSubMenus: string[] = [
 " ZOYCODES",
  "MANAGING_OWNERS",

  "MANAGING_USERS",
  "PERMISSION_APPROVAL",

  "TENANTSZOY",
  "BULK_UPLOAD",
  "REPORTS",
 " PAYMENT_APPROVAL",

  "TICKETS",

  "ROLE_AND_PERMISSION",
  "DB_MASTER_CONFIGURATION_READ",
  "CANCELLATION_AND_REFUND_RULES",
  "PERCENTAGE_AND_CHARGE_CONFIGURATIONS",
  "PROMOTION_AND_OFFER_MANAGEMENT",

];

getCheckedMenulist(id:string,opt:string){
  switch(id){
    case 'CREATE':
      this.getMenuSelectedForSV(this.menuListCrete,opt);
      break;
    case 'EDIT':
      this.getMenuSelectedForSV(this.menuListEdit,opt);
      break;
    default:
      break;
  }
}

getMenuSelectedForSV(menuList:string[],opt:string){
   for(var x=0;x<menuList.length;x++){
    var strWrite= menuList[x]+opt;
     let groupCheckwrite=Array.from($<HTMLInputElement>("input[name='"+strWrite+"']"));
      for(var i=0;i<groupCheckwrite.length;i++){
        if(groupCheckwrite[i].checked==true){
          if(opt=='write'){
            this.dataSelectedWrite.push(groupCheckwrite[i].value);
          }else{
            this.dataSelectedRead.push(groupCheckwrite[i].value);
          }
        }
      }
   }
}

getMenuSelectedForEDITPOPUP(menuList:string[],opt:string,menuListEdit:string[]){
  for(var x=0;x<menuList.length;x++){
   var strWrite= menuList[x]+opt;
    let groupCheckwrite=Array.from($<HTMLInputElement>("input[name='"+strWrite+"']"));
     for(var i=0;i<groupCheckwrite.length;i++){
         if(menuListEdit.includes(groupCheckwrite[i].value)){
          groupCheckwrite[i].checked=true;
         }else{
          groupCheckwrite[i].checked=false;
         }
     }

     var countCheck=0;
      for(var i=0;i<groupCheckwrite.length;i++){
        if(groupCheckwrite[i].checked){
          countCheck=countCheck+1;
        }
      }

      if(countCheck==groupCheckwrite.length){
        $('input#'+strWrite+'All').prop('indeterminate', false);
         $('input#'+strWrite+'All').prop('checked', true);
       }else if(countCheck>0 && countCheck<groupCheckwrite.length){
         $('input#'+strWrite+'All').prop('indeterminate', true);
       }else{
         $('input#'+strWrite+'All').prop('checked', false);
         $('input#'+strWrite+'All').prop('indeterminate', false);
       }
       
  }
}
}