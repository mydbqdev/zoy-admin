import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { NavigationEnd } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { UserService } from 'src/app/common/service/user.service';
import { AuthService } from 'src/app/common/service/auth.service';
import { DataService } from 'src/app/common/service/data.service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import { SidebarComponent } from 'src/app/components/sidebar/sidebar.component';
import { ChangePasswordModel } from '../model/change-password-model';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ProfileService } from '../service/profile-service';
import { UserInfo } from 'src/app/common/shared/model/userinfo.service';
import { AESEncryptDecryptHelper } from 'src/app/common/shared/AESEncryptDecryptHelper';
import { RoleScreenPrv } from 'src/app/setting/role-master/models/role-screen-model';
import { MenuService } from 'src/app/components/header/menu.service';



@Component({
	selector: 'app-profile',
	templateUrl: './profile.component.html',
	styleUrls: ['./profile.component.css']
})


export class ProfileComponent implements OnInit, AfterViewInit {    
	submitted=false;
	submittedPicture:boolean=false;
	viewPassword:boolean=false;
	form: FormGroup;
  	changePasswordDetails : ChangePasswordModel=new ChangePasswordModel();
	public userNameSession: string = "";
	errorMsg: any = "";
	mySubscription: any;
	isExpandSideBar:boolean=true;
	@ViewChild(SidebarComponent) sidemenuComp;
	public rolesArray: string[] = [];
	userInfo:UserInfo=new UserInfo();
	userRolePermissions: RoleScreenPrv[] = [];
	authorization:{ key: string; screen: string; order: number }[]=[];
	resetPassword: { 'email': string,'oldPassWord': string,'newPassword': string}={ 'email': '', 'oldPassWord': '', 'newPassword': '', };
	imgeURL2:any="assets/images/NotAvailable.jpg";
	constructor(private encryptDecryptHelper:AESEncryptDecryptHelper,private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,private profileService:ProfileService,
		private spinner: NgxSpinnerService,private formBuilder: FormBuilder, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService,private menuService:MenuService) {
			this.authService.checkLoginUserVlidaate();
			this.userInfo=this.userService.getUserinfo();
			
			this.userNameSession = userService.getUsername();
		//this.defHomeMenu=defMenuEnable;
		if (userService.getUserinfo() != undefined) {
			this.rolesArray = userService.getUserinfo().privilege;
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

		if(this.userService.getUserinfo()==undefined){
			this.dataService.getUserDetails.subscribe(name=>{
					  this.userInfo=name;
					});
		  }
		  this.authorization=this.menuService.getAllAuthorization();
	}

	ngOnDestroy() {
		if (this.mySubscription) {
			this.mySubscription.unsubscribe();
		}
	}
	ngOnInit() {
		//if (this.userNameSession == null || this.userNameSession == undefined || this.userNameSession == '') {
		///	this.router.navigate(['/']);
		//}
		this.form = this.formBuilder.group({
			oldPassword: ['', [Validators.required]],
			newPassword: ['', [
				Validators.required,
				Validators.minLength(8),
				Validators.maxLength(16),
				Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/)
			  ]],
			  confirmPassword: ['', Validators.required]
			}, 
			{ validator: this.passwordsMatch });
			this.loadProfilePhoto();
	}
	ngAfterViewInit() {
		this.sidemenuComp.expandMenu(0);
		this.sidemenuComp.activeMenu(0, '');
		this.dataService.setHeaderName("Profile");
	}

	passwordsMatch(formGroup: FormGroup) {
		const password = formGroup.get('newPassword')?.value;
		const confirmPassword = formGroup.get('confirmPassword')?.value;
		return password === confirmPassword ? null : { passwordsMismatch: true };
		}

		changePassword() {
			this.authService.checkLoginUserVlidaate();
			this.submitted=true;		
			this.changePasswordDetails.userEmail=this.userService.getUsername();
		    	if(this.form.invalid){
				return;
	     		}
			this.resetPassword.email= this.changePasswordDetails.userEmail;
			this.resetPassword.oldPassWord= this.encryptDecryptHelper.encrypt(this.changePasswordDetails.oldPassword);
			this.resetPassword.newPassword= this.encryptDecryptHelper.encrypt(this.changePasswordDetails.newPassword);
			this.spinner.show();	
			this.profileService.changePassword(this.resetPassword).subscribe((res) => {
			  this.notifyService.showSuccess(res.message, "");
			  this.form.reset();
			  this.submitted=false;
			  this.spinner.hide();
			},error =>{
			  this.spinner.hide();
			  console.log("error.error",error)
			  this.errorMsg = (error.error.error !=undefined?(error.error.error  +"."):"")
			  + (error.error.userEmail!=undefined?(error.error.userEmail+"."):"")
			  +(error.error.password!=undefined?(error.error.password  +"."):"");
			  if(error.status == 0) {
				this.notifyService.showError("Internal Server Error/Connection not established", "")
			 }else if(error.status==401){
				console.error("Unauthorised");
			}else if(error.status==403){
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
			   this.spinner.hide();
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


		  formPicture = new FormGroup({
			ImageInput : new FormControl('', [Validators.required]),
			fileSource : new FormControl('', [Validators.required])
		  });
 
		  get f(){
			 return this.formPicture.controls;
		   }
 
		  fileData: File = null;
		  previewUrl:any = null;
		  fileUploadProgress: string = null;
		  uploadStatus:boolean=true;
		  fileUploadSize:any;
		  fileUploadSizeStatus:boolean=false;
		  fileWidth:number;
		  fileHeight:number;
 
	 onFileChanged(event) {
		 this.previewUrl=false;
		 if(event.target.files.length>0){
		   const file=event.target.files[0];
		   this.formPicture.patchValue({
			 fileSource:file
		   });
		   this.fileData = <File>event.target.files[0];
		   this.fileUploadSize=file.size/1024;
		   if(this.fileUploadSize <=100){
			 this.fileUploadSizeStatus=false;
		   this.preview();
		   }else{
			   this.fileUploadSizeStatus=true;
		   }
		 }
	 
	 }
	 resetChange(){
		 this.previewUrl=false;
		 this.formPicture.reset();
		 this.fileUploadSizeStatus=false;
		 this.submittedPicture=false;
		 this.fileData = null;
	 }
	 preview() {
		 // Show preview 
		 var mimeType = this.fileData.type;
		 if (mimeType.match(/image\/*/) == null) {
		   return;
		 }
		 this.uploadStatus=false;
		 var reader = new FileReader();      
		 reader.readAsDataURL(this.fileData); 
		 reader.onload = (_event) => { 
		   this.previewUrl = reader.result; 
		   var img=new Image();
		   img.onload=()=>{
			   this.fileHeight=img.height;
			   this.fileWidth=img.width;
		   }
		 }
		 } 
 
	 submit(){
		 this.submittedPicture=true;
		 var form_data = new FormData();
		 if(this.formPicture.invalid){
			return;
		  }
		 form_data.append('image', this.fileData);
		 if( !this.fileUploadSizeStatus){
			this.profileService.uploadProfilePicture(form_data).subscribe((res) => {
				this.router.navigate(['/profile']);
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
		  
	   }
	   loadProfilePhoto(){
		this.profileService.loadProfilePhoto().subscribe((data) => {
		 if(data && data.size!=0){ 
			const reader =new FileReader();
			reader.readAsDataURL(new Blob([data]));
			reader.onload=(e)=>this.imgeURL2=e.target.result; 
		  }else{
			this.imgeURL2="assets/images/NotAvailable.jpg";
		  }
	
		}, error => {
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
 
	 getReadIcon(readPrv: boolean): string {
		return readPrv ? 'fa fa-check text-success' : 'fa fa-times text-danger';
	  }
	  
	  getWriteIcon(writePrv: boolean): string {
		return writePrv ? 'fa fa-check text-success' : 'fa fa-times text-danger';
	  }

	 
	   viewAuthorisedMe(): void {   
		   this.userRolePermissions = [];
		   if (this.userInfo.privilege && this.userInfo.privilege.length>0) {
			this.userInfo.privilege.forEach(privilege => {
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
			 existingPermission.order = authorization.order ;
			// 	existingPermission.approveStatus = role.approveStatus === 'Approved';
			  } else {
				this.userRolePermissions.push({
				  screenName: authorization.screen,
				  readPrv: privilege.endsWith('_READ'),
				  writePrv: privilege.endsWith('_WRITE'),
				  approveStatus:true,
				  order : authorization?.order
				});
			  }
			}
			});
		  }
		  this.userRolePermissions.sort((a, b) => a.order - b.order);
	  }	

	 
			   
		
}
