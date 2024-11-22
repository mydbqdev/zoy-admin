import { HttpClient } from '@angular/common/http';
import { Component, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { AuthService } from 'src/app/common/service/auth.service';
import { DataService } from 'src/app/common/service/data.service';
import { UserService } from 'src/app/common/service/user.service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import { UserInfo } from 'src/app/common/shared/model/userinfo.service';
import { SidebarComponent } from 'src/app/components/sidebar/sidebar.component';
import { ProfileService } from 'src/app/profile/service/profile-service';
import { BulkUploadModel } from '../model/bulk-upload-model';
import { BulkUploadService } from '../service/bulk_upload.service';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';

@Component({
  selector: 'app-bulk-upload',
  templateUrl: './bulk-upload.component.html',
  styleUrl: './bulk-upload.component.css'
})
export class BulkUploadComponent {
	public ELEMENT_DATA:BulkUploadModel[];
	dataSource:MatTableDataSource<BulkUploadModel>=new MatTableDataSource<BulkUploadModel>();
	displayedColumns: string[] = [ 'fileName','category', 'status',  'createdAt']; 
	pageSizeOptions: number[] = [5, 10, 25, 50];
	pageSize = 10;
	bulkUpload :BulkUploadModel=new BulkUploadModel();
	@ViewChild(MatPaginator) paginator: MatPaginator;
	@ViewChild(MatSort) sort: MatSort;
	upfile:File;
	filevali:boolean=false;
	submitted = false;
	form !: FormGroup;
  	public userNameSession: string = "";
	errorMsg: any = "";
	mySubscription: any;
	isExpandSideBar:boolean=true;
	@ViewChild(SidebarComponent) sidemenuComp;
	public rolesArray: string[] = [];
	userInfo:UserInfo=new UserInfo();
  constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,private  bulkUploadService:BulkUploadService,private confirmationDialogService :ConfirmationDialogService,
		private spinner: NgxSpinnerService,private formBuilder: FormBuilder, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService) {
			this.authService.checkLoginUserVlidaate();
			this.userInfo=this.userService.getUserinfo();
			this.userNameSession = userService.getUsername();
		//this.defHomeMenu=defMenuEnable;
		if (userService.getUserinfo() != undefined) {
			this.rolesArray = userService.getUserinfo().privilege;
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

		if(this.userService.getUserinfo()==undefined){
			this.dataService.getUserDetails.subscribe(name=>{
					  this.userInfo=name;
					});
		  }
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
			fileName: ['', [Validators.required]],
			category: ['', [Validators.required]],
			reqDocument: ['', [Validators.required]]
		  });
		  this.getRolesData();
  }

  ngAfterViewInit(){
    this.sidemenuComp.expandMenu(7);
    this.sidemenuComp.activeMenu(7,'bulk-upload');
    this.dataService.setHeaderName("Bulk Upload ");
  }

  onFileChange(event: any) {
	this.filevali=false;
		this.upfile=event.target.files[0]; 
		const fileType = this.upfile.type;
		if (fileType !== 'text/csv' && fileType !== 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' && fileType !== 'application/vnd.ms-excel') {
		  this.filevali=true;
	   	}    
		}

		 mockData = [
			{ fileName: 'Narayana PG Tenant Details', category: 'Tenant', status: 'uploaded', createdAt:'2024-11-20T12:30:00'},
			{ fileName: 'Sri Sai PG Room Booking', category: 'Tenant', status: 'uploaded', createdAt:'2024-11-20T12:40:10'},
			{ fileName: 'Lakshmi PG Tenant Agreement', category: 'Tenant', status: 'upload failed', createdAt:'2024-11-20T12:50:20'},
			{ fileName: 'Krishna PG Rental Agreement', category: 'Tenant', status: 'uploaded', createdAt:'2024-11-20T13:00:30'},
			{ fileName: 'Raj PG Tenant Application', category: 'Tenant', status: 'upload failed', createdAt:'2024-11-20T13:10:40'},
			{ fileName: 'Sai PG Property ', category: 'PG Property', status: 'uploaded', createdAt:'2024-11-20T13:20:50'},
			{ fileName: 'Vijay PG Property', category: 'Tenant', status: 'uploaded', createdAt:'2024-11-20T13:30:00'},
			{ fileName: 'Srinivas PG Documents', category: 'Tenant', status: 'upload failed', createdAt:'2024-11-20T13:40:10'},
			{ fileName: 'Surya PG Tenant Details', category: 'Tenant', status: 'uploaded', createdAt:'2024-11-20T13:50:20'},
			{ fileName: 'Anand PG Application', category: 'Tenant', status: 'uploaded', createdAt:'2024-11-20T14:00:30'},
			{ fileName: 'Durga PG Tenant Registration', category: 'Tenant', status: 'upload failed', createdAt:'2024-11-20T14:10:40'},
			{ fileName: 'Bhaskar PG Lease Agreement', category: 'Tenant', status: 'uploaded', createdAt:'2024-11-20T14:20:50'},
			{ fileName: 'Ramesh PG Tenant Info', category: 'Tenant', status: 'upload failed', createdAt:'2024-11-20T14:30:00' }
		  ];
		  

		getRolesData() {
			// this.authService.checkLoginUserVlidaate();
			this.ELEMENT_DATA=Object.assign([],this.mockData);
			this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
			this.dataSource.sort = this.sort;
			this.dataSource.paginator = this.paginator;
			return;
				   this.spinner.show();
				   this.bulkUploadService.getUploadFileDetails().subscribe(res => {
					 this.ELEMENT_DATA=Object.assign([],res);
					 this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
					 this.dataSource.sort = this.sort;
					 this.dataSource.paginator = this.paginator;
				   this.spinner.hide();
				 },error =>{
				   this.spinner.hide();
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

		   submitUploadFile(){ 
			this.submitted = true;
		
		//	this.authService.checkLoginUserVlidaate();
		  
			if (this.form.invalid || this.filevali) {
			  return;
			}
			var form_data = new FormData();
			for ( var key in this.bulkUpload ) {
			form_data.append(key, this.bulkUpload[key]);
			}
			form_data.append("uploadFile",this.upfile);
	
			this.confirmationDialogService.confirm('Confirmation!!', "Are you sure to upload the Data?")
			.then(
			  (confirmed) =>{ 
				if(confirmed){
			 this.spinner.show();
		   this.bulkUploadService.uploadFileDetails(form_data).subscribe((response) => {
			
		   this.spinner.hide();
		   }, error => {
			this.spinner.hide();
			 if(error.status==403){
			   this.router.navigate(['/forbidden']);
			 }else if (error.error && error.error.message) {
			   this.errorMsg = error.error.message;
			   console.log("Error:" + this.errorMsg);
			   this.notifyService.showError(this.errorMsg, "");
			   this.spinner.hide();
			 } else {
			   this.spinner.hide();
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

		downloadSampleFile(fileName:string){
		const filePath = `assets/sample_files/${fileName+'.csv'}`;
		const link = document.createElement('a');
		link.href = filePath;
		link.download = fileName+'.csv';
		link.click();
		}

		getTenentSampleFile(){
			this.spinner.show();
			this.bulkUploadService.getTenentSampleFile().subscribe(data => {
				console.log("getTenentSampleFile data",data);
				if(data!=null && data!=undefined && data!='' && data.size!=0){ 
					var blob = new Blob([data], {type: 'text/csv'});
					var fileURL=URL.createObjectURL(blob);				  
					const link = document.createElement("a");
					link.href = fileURL;
					link.target = '_blank';
					link.download = 'Tenent_sample_File.csv';
					document.body.appendChild(link);
					link.click();
					document.body.removeChild(link);
				}
			this.spinner.hide();
			sessionStorage.setItem('zoyadminapi','yes');
		  },error =>{
			console.log("error>>",error)
			sessionStorage.setItem('zoyadminapi','yes');
			this.spinner.hide();
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

		getPgPropertysSampleFile(){
			this.spinner.show();
			this.bulkUploadService.getPgPropertysSampleFile().subscribe(data => {
				
				if(data!=null && data!=undefined && data!='' && data.size!=0){ 
					var blob = new Blob([data], {type: 'application/vnd.ms-excel'});
					var fileURL=URL.createObjectURL(blob);				  
					const link = document.createElement("a");
					link.href = fileURL;
					link.target = '_blank';
					link.download = 'PG_Propertys_sample_File.csv';
					document.body.appendChild(link);
					link.click();
					document.body.removeChild(link);
				}
			this.spinner.hide();
			sessionStorage.setItem('zoyadminapi','yes');
		  },error =>{
			console.log("error>>",error)
			sessionStorage.setItem('zoyadminapi','yes');
			this.spinner.hide();
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

}
