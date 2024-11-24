import { HttpClient } from '@angular/common/http';
import { Component, ViewChild } from '@angular/core';
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
import { ProfileService } from 'src/app/profile/service/profile-service';
import { BulkUploadModel } from '../model/bulk-upload-model';
import { BulkUploadService } from '../service/bulk_upload.service';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';
import { OwnerPropertyDetails, PropertyDetail } from '../model/owner-property-model';
import { Observable, startWith, map } from 'rxjs';

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
			ownerName: [''],
			propertyname: [''],
			category: ['', [Validators.required]],
			reqDocument: ['', [Validators.required]]
		  });
		  this.getUploadFileDetails();
		  this.getOwnerPropertyDetailsList();
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
		  

	getUploadFileDetails() {
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
			console.log("Selected Owner:", this.selectedOwner);
		console.log("Selected Property:", this.selectedProperty);
		
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


		ownerPropertyDetailsList: OwnerPropertyDetails[] = [];

		OwnerPropertyDetailsMockdata(){
			const ownerPropertyDetails: OwnerPropertyDetails[] = [
				{
					"ownerId": "f820cfe2-7c87-41be-8199-6ccbcf0b8810",
					"ownerName": "John Doe-9876543210",
					"property_details": [
						{ "propertyId": "7c30c9cf-4905-41db-b90f-efc84a63bc44", "propertyname": "Green Valley" },
						{ "propertyId": "cb578db4-36d0-47fd-84ab-492b09e04028", "propertyname": "Sunset Heights" }
					]
				},
				{
					"ownerId": "c3b52a4b-c2a5-4de9-b8b4-fb62f2548d9a",
					"ownerName": "Jane Smith-9876541234",
					"property_details": [
						{ "propertyId": "96a2f37f-5535-4f34-bfbd-0c0b647ed22f", "propertyname": "Ocean View" },
						{ "propertyId": "0ed9c90f-dce4-46ae-a489-2084ff828897", "propertyname": "Mountain Peak" },
						{ "propertyId": "ae8c387b-0a92-4928-bb0b-2e67e9d463fc", "propertyname": "Riverfront Villas" }
					]
				},
				{
					"ownerId": "2d9efcd1-34a0-4d7e-bdb0-e72f72915725",
					"ownerName": "Michael Jordan-9922334455",
					"property_details": [
						{ "propertyId": "edc9c9ea-1ff3-46b9-b944-cadbc53b8f9d", "propertyname": "Golden Gate Tower" },
						{ "propertyId": "af5cd412-9b7f-4239-bac5-bbf433a78dfd", "propertyname": "Silver Brook Estate" }
					]
				},
				{
					"ownerId": "63f08c1c-5a1c-49c7-b981-6c3a0fbd218e",
					"ownerName": "Alice Cooper-8889991111",
					"property_details": [
						{ "propertyId": "7293d7a3-b0ae-41ff-bd52-60c107d3cfe5", "propertyname": "Maple Grove" },
						{ "propertyId": "f4c2f267-ef75-429b-87f2-d8a38bcac3b0", "propertyname": "Pine Ridge Apartments" }
					]
				},
				{
					"ownerId": "4cc02e07-5315-409b-b6f1-f5998e1b9430",
					"ownerName": "David Brown-9112233445",
					"property_details": [
						{ "propertyId": "0597c14b-bf24-4628-b963-9a2827d6c7de", "propertyname": "Sunny Acres" },
						{ "propertyId": "1b7faed0-e1c5-4298-b59d-94b7067b5e88", "propertyname": "Lakeside Retreat" },
						{ "propertyId": "24c1f700-e95a-4751-84fe-84c254d4bc96", "propertyname": "Hilltop Haven" }
					]
				},
				{
					"ownerId": "b812f70f-bc40-4926-b036-d3acb1156c6b",
					"ownerName": "Emma Wilson-7778889999",
					"property_details": [
						{ "propertyId": "cbcb9c6e-3930-4727-b849-16366f7c33b5", "propertyname": "Windmill Heights" },
						{ "propertyId": "ad2a5061-8707-49f4-b5f1-d4587fe348f4", "propertyname": "Crystal Shores" }
					]
				},
				{
					"ownerId": "df7b6d8f-11b3-4c4e-b3f6-f4a4e29a32c7",
					"ownerName": "Lucas Scott-9900112233",
					"property_details": [
						{ "propertyId": "55a2adab-f440-4861-94b9-848b9432205d", "propertyname": "Riverside Plaza" },
						{ "propertyId": "fe0f8f06-40b4-4e75-8f22-0ad50f542a9d", "propertyname": "Garden View Towers" },
						{ "propertyId": "cd1954c8-1e0a-48bb-a3d2-05cc939df1be", "propertyname": "Park Lane Residences" }
					]
				},
				{
					"ownerId": "f7d1f15c-51d7-42d2-9b06-cd8b9038d7b7",
					"ownerName": "Sophia Turner-9988776655",
					"property_details": [
						{ "propertyId": "98e9ad7b-d9f2-402f-b35f-82c79a1a1f94", "propertyname": "Sunrise Meadows" },
						{ "propertyId": "cdb755d7-c520-42ae-9fcb-9cc7e198b268", "propertyname": "Ocean Breeze Condos" }
					]
				},
				{
					"ownerId": "61b7d726-f7c9-47c2-9f4e-88d4289a420a",
					"ownerName": "William King-7779995555",
					"property_details": [
						{ "propertyId": "6a7a2d94-d657-4997-b071-d94d3790d09d", "propertyname": "Eagle Nest Estate" },
						{ "propertyId": "15c1f8d2-2229-4b52-bfc4-b9a40f556f39", "propertyname": "Vista Grand" }
					]
				},
				{
					"ownerId": "2435f7c2-3a56-4a68-8b99-4f7b3765c51a",
					"ownerName": "Charlotte Hall-1122334455",
					"property_details": [
						{ "propertyId": "0bb620cd-3514-4c6e-a4c1-dad5b0b0caad", "propertyname": "Rosewood Hills" },
						{ "propertyId": "81339d13-735b-41d7-92f5-262bfbdef1b3", "propertyname": "Golden Fields" }
					]
				},
				{
					"ownerId": "cdb8b097-bef1-40e7-9f3b-77de85d29d64",
					"ownerName": "Grace Harris-5556667777",
					"property_details": [
						{ "propertyId": "e4a015fc-c456-4067-8d97-dbf2299f5a27", "propertyname": "Birchwood Grove" },
						{ "propertyId": "83df57ae-8f27-46bc-828f-28eab95191a2", "propertyname": "Misty Mountain Villas" }
					]
				},
				{
					"ownerId": "aff9d04d-3d84-48b2-bec9-0e9cc30791b9",
					"ownerName": "Daniel Moore-2333445566",
					"property_details": [
						{ "propertyId": "ab19a4ad-f24b-40f5-a0e2-19f8d44bda10", "propertyname": "Lakeview Lodge" },
						{ "propertyId": "df09e028-1b8d-4929-b8b2-cc51d4e58b36", "propertyname": "Sunset Ridge" },
						{ "propertyId": "56d1d289-4c59-41de-a8f4-df1fa9900c25", "propertyname": "Cityview Apartments" }
					]
				},
				{
					"ownerId": "f7b3fd25-28c1-4522-b9fc-4f609b76ec59",
					"ownerName": "Isabella Scott-4455668899",
					"property_details": [
						{ "propertyId": "a83e60e2-f23d-4ed8-b865-cf7633a41b44", "propertyname": "Amber Ridge" },
						{ "propertyId": "fe4a2e8c-cb1b-4b63-bf17-79839dbb7898", "propertyname": "Silver Oaks" }
					]
				},
				{
					"ownerId": "0d549a04-898f-4ec2-8d35-1e6fbbfc0e50",
					"ownerName": "Olivia Harris-1002003000",
					"property_details": [
						{ "propertyId": "8a6c19de-4029-45bc-ae24-f68855b7fc02", "propertyname": "Whispering Pines" },
						{ "propertyId": "f9bcb35b-b31e-4295-b30c-466f55d62be1", "propertyname": "Crystal Waters" }
					]
				},
				{
					"ownerId": "df597f67-3b0a-4ae5-8686-6d68ca3cc06b",
					"ownerName": "Jack Wilson-4445556666",
					"property_details": [
						{ "propertyId": "b8ae2da0-b322-4c97-b618-1fe468fb5d6b", "propertyname": "The Summit" },
						{ "propertyId": "bf4eaf15-c098-4cde-a406-6dace57b35f7", "propertyname": "Starlight Villas" }
					]
				},
				{
					"ownerId": "fa4208d9-8e09-469d-9b2e-78dfe9e3d9da",
					"ownerName": "Chloe Davis-3334445555",
					"property_details": [
						{ "propertyId": "f3cd0d29-9080-4c58-993e-d5d6e6e6a404", "propertyname": "Forest Pines" },
						{ "propertyId": "b84f8eeb-b4d2-4f62-b70f-b36f4a6b18c2", "propertyname": "Lakeside Lodge" },
						{ "propertyId": "6720e0fe-5700-4604-bbda-36cd57662f92", "propertyname": "Sapphire Bay Residences" }
					]
				},
				{
					"ownerId": "319998ac-8eb9-493f-b180-23f00773b933",
					"ownerName": "Sophia Clark-5550001111",
					"property_details": [
						{ "propertyId": "5d17b380-85fc-497f-a43e-e36a8b13e98b", "propertyname": "Cedar Springs" },
						{ "propertyId": "a23004d4-6b68-4a0f-b5f3-b02a0b2de383", "propertyname": "Palm Grove" }
					]
				}
			]
			
			return ownerPropertyDetails;
			}
			ownerSearchControl = new FormControl();
			propertySearchControl = new FormControl();
			filteredOwners: Observable<OwnerPropertyDetails[]>;
			filteredProperties: Observable<PropertyDetail[]>;
			selectedOwner: OwnerPropertyDetails ;
			selectedProperty: PropertyDetail  ;


	getOwnerPropertyDetailsList(){
		this.ownerPropertyDetailsList=Object.assign([],this.OwnerPropertyDetailsMockdata());
		this.filteredOwners = this.ownerSearchControl.valueChanges.pipe(
			startWith(''),
			map(value => this.filterOwners(value))
		  );
		return;
		this.spinner.show();
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
		this.spinner.hide();
	  },error =>{
		console.log("error>>",error)
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

}
