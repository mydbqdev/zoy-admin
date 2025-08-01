import { HttpClient } from '@angular/common/http';
import { Component, ElementRef, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
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
import { BulkUploadModel } from '../model/bulk-upload-model';
import { BulkUploadService } from '../service/bulk_upload.service';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';
import { OwnerPropertyDetails, PropertyDetail } from '../model/owner-property-model';
import { Observable, startWith, map } from 'rxjs';
import { UploadErrorModal } from '../model/bulk-uploaderror.model';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-bulk-upload',
  templateUrl: './bulk-upload.component.html',
  styleUrl: './bulk-upload.component.css'
})
export class BulkUploadComponent {
	public ELEMENT_DATA:BulkUploadModel[];
	dataSource:MatTableDataSource<BulkUploadModel>=new MatTableDataSource<BulkUploadModel>();
	displayedColumns: string[] = [ 'ownerName','propertyName','fileName', 'status',  'createdAt']; 
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
	@ViewChild('openModel') openModel: ElementRef;
	public rolesArray: string[] = [];
	userInfo:UserInfo=new UserInfo();
	uploadErrorModalList : UploadErrorModal[]=[];
	ownerPropertyDetailsList: OwnerPropertyDetails[] = [];
	ownerSearchControl = new FormControl();
	propertySearchControl = new FormControl();
	filteredOwners: Observable<OwnerPropertyDetails[]>;
	filteredProperties: Observable<PropertyDetail[]>;
	selectedOwner: OwnerPropertyDetails ;
	selectedProperty: PropertyDetail  ;
   constructor(public dialog: MatDialog,private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,private  bulkUploadService:BulkUploadService,private confirmationDialogService :ConfirmationDialogService,
		private spinner: NgxSpinnerService,private formBuilder: FormBuilder, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService) {
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
			ownerName: [''],
			propertyname: [''],
			reqDocument: ['', [Validators.required]]
		  });
		  this.getUploadFileDetails();
		  this.getOwnerPropertyDetailsList();
		  
  }

  ngAfterViewInit(){
    this.sidemenuComp.expandMenu(2);
    this.sidemenuComp.activeMenu(2,'bulk-upload');
    this.dataService.setHeaderName("Bulk Upload ");
  }

  onFileChange(event: any) {
		this.filevali=false;
		this.upfile=event.target.files[0]; 
		const fileType = this.upfile.type;
		const fileName = this.upfile.name.toLowerCase();
		if (fileType == 'application/vnd.ms-excel.sheet.macroEnabled.12' || fileName.endsWith('.xlsm') ){
			this.filevali=false;
		  }else{
			this.filevali=true;
		  }
	}
  
	getUploadFileDetails() {
		this.authService.checkLoginUserVlidaate();
		 this.spinner.show();
		 this.bulkUploadService.getUploadFileDetails().subscribe((response) => {
		 this.ELEMENT_DATA=Object.assign([],response);
		 this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
		 this.dataSource.sort = this.sort;
		 this.dataSource.paginator = this.paginator;
		 this.spinner.hide();
			 },error =>{
		   this.spinner.hide();
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
			 
		     } else {
			 if(error.status==500 && error.statusText=="Internal Server Error"){
		     this.errorMsg=error.statusText+"! Please login again or contact your Help Desk.";
			 }else{
			   let str;
				 if(error.status==400){
		   		 str=error.error.error;
			  	 }else{
			     str=error.error.message;
			     str=str.substring(str.indexOf(":")+1);
			 		 }
					 console.log("Error:",str);
					 this.errorMsg=str;
					 this.notifyService.showError(this.errorMsg, "");
			       }
					 if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
				   }
				});
		   }
	

		resetModel(){
			this.submitted = false;
			this.ownerSearchControl = new FormControl();
			this.propertySearchControl = new FormControl();
			this.form.reset();
			this.selectedOwner = null ;
			this.selectedProperty = null;
			this.filteredOwners = this.ownerSearchControl.valueChanges.pipe(
				startWith(''),
				map(value => this.filterOwners(value))
			  );
			this.getUploadFileDetails();
		}

	getOwnerPropertyDetailsList(){
		this.bulkUploadService.getOwnerPropertyDetailsList().subscribe(data => {
		if(data!=null && data!=undefined && data!='' && data.size!=0){ 
			this.ownerPropertyDetailsList=Object.assign([],data)
			}else{
				this.ownerPropertyDetailsList=Object.assign([])
			}	
			this.filteredOwners = this.ownerSearchControl.valueChanges.pipe(
				startWith(''),
				map(value => this.filterOwners(value))
			  );
	    },error =>{
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
		  
		} else {
		  if(error.status==500 && error.statusText=="Internal Server Error"){
			this.errorMsg=error.statusText+"! Please login again or contact your Help Desk.";
		  }else{
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
		}
	  });
	}	

	  filterOwners(value: string): OwnerPropertyDetails[] {
		const filterValue = value.toLowerCase();
		const obj = this.ownerPropertyDetailsList.filter(owner => 
			owner.ownerName.toLowerCase().includes(filterValue)
		  );
		if(obj.length==0){
			this.selectedOwner = null;
		 }
		return obj
	  }

	  selectOwner(owner: OwnerPropertyDetails): void {
		const ownerObj= owner
		this.selectedOwner = ownerObj;
		this.selectedProperty = null;
	  }
		
	  selectProperty(event: Event): void {
		  const selectedPropertyId = (event.target as HTMLSelectElement).value;
		  const list = this.selectedOwner.property_details.find(property => property.propertyId === selectedPropertyId);
		  this.selectedProperty = list;
		}

		downloadProgress:boolean;
	getBulkUploadTemplate(){
		this.downloadProgress=true;
		this.bulkUploadService.getBulkUploadTemplate().subscribe((data) => { 		
		const blob = new Blob([data], {
			type: 'application/vnd.ms-excel.sheet.macroEnabled.12'
		});
			var fileURL=URL.createObjectURL(blob);
			const link = document.createElement("a");
			link.href = fileURL;
			link.target = '_blank';
			link.download = 'BulkUpload.xlsm';
			document.body.appendChild(link);
			link.click();
			this.downloadProgress=false;
		  }, async error => {
			this.downloadProgress=false;
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
				  console.log("Error:" + await str.text());
				  this.errorMsg =  await new Response(str).text()// or use await str.text();
				}
				if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
			  }
			});	
		}

	bulkUploadFile(){
		this.authService.checkLoginUserVlidaate();
		this.submitted = true;
		if (this.form.invalid || this.filevali) {
		  return;
		}

		const duplicate =  this.ELEMENT_DATA.filter(d=>d.ownerId == this.selectedOwner.ownerId && d.propertyId == this.selectedProperty.propertyId && d.status != 'Failed' );

		if(duplicate.length>0){
			this.notifyService.showWarning(" Details have already been uploaded for "+this.selectedProperty.propertyname+"( owned by "+this.selectedOwner.ownerName+" ).","");
			return;
		}
		let bulkUploadModel=new BulkUploadModel();
		bulkUploadModel.ownerId=this.selectedOwner.ownerId
		bulkUploadModel.ownerName=this.selectedOwner.ownerName
		bulkUploadModel.propertyId=this.selectedProperty.propertyId
		bulkUploadModel.propertyName=this.selectedProperty.propertyname

		const model =JSON.stringify(bulkUploadModel);
		var form_data = new FormData();

		form_data.append('data', model);
		form_data.append("file",this.upfile);

		this.confirmationDialogService.confirm('Confirmation!!', "Initial upload can be done once for a property. Make sure all the data is entered properly in the sheet and correct.")
	    .then(
			(confirmed) =>{ 
			  if(confirmed){
				   this.spinner.show();
				   this.bulkUploadService.bulkUploadFile(form_data).subscribe((res) => {
				   this.resetModel();
				   this.notifyService.showSuccess("File has been uploaded sucessfully", "");
				   this.spinner.hide();
				 }, error => {
				  this.spinner.hide();
				  if(error.status == 0) {
					  this.notifyService.showError("Internal Server Error/Connection not established", "")
				   }else if(error.status==401){
					  console.error("Unauthorised");
				  }else if(error?.status==409){
						  if(error?.error?.data){
							  this.resetModel();
							  this.uploadErrorModalList=error.error.data;
							  this.openModel.nativeElement.click();    
						  }
				  }else if(error.status==403){
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


