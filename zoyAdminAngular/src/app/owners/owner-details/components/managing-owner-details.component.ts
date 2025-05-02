import { AfterViewInit, Component, ElementRef, inject, OnInit, ViewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { NavigationEnd } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { UserService } from 'src/app/common/service/user.service';
import { AuthService } from 'src/app/common/service/auth.service';
import { DataService } from 'src/app/common/service/data.service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import { SidebarComponent } from 'src/app/components/sidebar/sidebar.component';
import { FormBuilder } from '@angular/forms';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';
import { GenerateZoyCodeService } from '../../service/zoy-code.service';
import { UserInfo } from 'src/app/common/shared/model/userinfo.service';
import { AadhaarVerif, FloorInformation, PgOwnerData, PgOwnerPropertyInformation, Room } from '../models/owner-full-details';
import { ZoyOwnerService } from '../../service/zoy-owner.service';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort, Sort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { FilterData, FiltersRequestModel } from 'src/app/report/model/report-filters-model';
import { OwnerReportService } from 'src/app/report/owner-reports/owner-reports.service';
import { ReportsService } from 'src/app/report/service/reportService';
import { TenantReportsService } from 'src/app/report/tenant-reports/tenant-reports.service';

@Component({
  selector: 'app-managing-owner-details',
  templateUrl: './managing-owner-details.component.html',
  styleUrl: './managing-owner-details.component.css'
})
export class OwnerDetailsComponent implements OnInit, AfterViewInit {
	
	public userNameSession: string = "";
	  errorMsg: any = "";
	  mySubscription: any;
	  isExpandSideBar:boolean=true;
	  @ViewChild(SidebarComponent) sidemenuComp;
	  public rolesArray: string[] = [];
	  submitted=false;
	  userInfo:UserInfo=new UserInfo();
	  owenerId :string ='' ;
	  pgOwnerData : PgOwnerData = new PgOwnerData();
	  roomList:Room[]=[]
	  roomArrayList:any[]=[];
	  floorInfo:FloorInformation = new FloorInformation();
	  floor_id:string='';
	  property_id:string='';
	  property_status:string='';
	  propertyInfo :PgOwnerPropertyInformation =new PgOwnerPropertyInformation();
	  totalRecord:number=0;
	  reason : string='';
	  status :  string='';
	  doActiveteDeactiveteName :  string='';
	  doActiveteDeactiveteType: string='';

	  pageSize = 25;
	  pageSizeOptions: number[] = [25, 50,100,200];
	  fromDate:string='';
	  toDate:string='';
	  reportName:string ='';
	  filtersRequest :FiltersRequestModel = new FiltersRequestModel();
	  public lastPageSize:number=0;
	  public totalProduct:number=0;
	  sortActive:string="";
	  sortDirection:string="desc";
	  selectedReportColumns: any[] = [];
	  reportDataList :any[]=[];
	  reportDataSource: MatTableDataSource<any>=new MatTableDataSource(this.reportDataList);
	  displayedColumns: string[] = [];
	  columnHeaders = {} ;
	  reportColumnsList = [] ;
	  reportNamesList = this.ownerReportService.reportNamesList;
	  transactionHeader:string="";
	  @ViewChild(MatSort) sort: MatSort;
	  @ViewChild(MatPaginator) paginator: MatPaginator;
	  @ViewChild('closeModel') closeModel : ElementRef;
	  constructor(private generateZoyCodeService : GenerateZoyCodeService,private route: ActivatedRoute, private router: Router,private formBuilder: FormBuilder, private http: HttpClient, private userService: UserService,private zoyOwnerService :ZoyOwnerService,
		  private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService, private confirmationDialogService:ConfirmationDialogService,private ownerReportService : OwnerReportService,private tenantReportService : TenantReportsService,private reportsService : ReportsService) {
			  this.authService.checkLoginUserVlidaate();
			  this.userNameSession = userService.getUsername();
		  //this.defHomeMenu=defMenuEnable;
		  this.userInfo=this.userService.getUserinfo();
		  if (userService.getUserinfo() != undefined) {
			  this.rolesArray = userService.getUserinfo().privilege;
		  }else{
			  this.dataService.getUserDetails.subscribe(name=>{
				  if(name?.privilege){
				this.rolesArray =Object.assign([],name.privilege);
				}
				});
				this.dataService.getUserDetails.subscribe(name=>{
					this.userInfo=name;
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
		  this.dataService.getOwenerId.subscribe(id=>{
			if(id == null || id == undefined || id == ''){
				if(localStorage.getItem('ownerInfo')){
					this.owenerId = localStorage.getItem('ownerInfo') ;
				}else{
					this.router.navigate(['/manage-owner']);
				}
			}else{
				this.owenerId=id;
			}
		});
		this.fromDate=this.getLastMonthDate();
		this.toDate=this.getCurrentDate();
	  }

	  ngOnDestroy() {
		  if (this.mySubscription) {
			  this.mySubscription.unsubscribe();
		  }
	  }
	  ngOnInit() {
		  //if (this.userNameSession == null || this.userNameSession == undefined || this.userNameSession == '') {
		  //	this.router.navigate(['/']);
		  //}
		 
		  
	  }
	  ngAfterViewInit() {
		  this.sidemenuComp.expandMenu(2);
		  this.sidemenuComp.activeMenu(2, 'manage-owner');
		  this.dataService.setHeaderName("Manage Owner Details");
		  
		  this.getZoyOwnerDetails();
	  }
	  
	  collaspeList = [
		{ id: 1, name: 'left1', selected: true },
		{ id: 2, name: 'left2', selected: false },
	  ];
	  collaspeListRight = [
		{ id: 1, name: 'right1', selected: false },
		{ id: 2, name: 'right2', selected: false },
		{ id: 3, name: 'right3', selected: false },
		{ id: 4, name: 'right4', selected: false },
	  ];
	   // Toggle the selected status for a button
	   collaspeExpandPanel(status: any,side:string): void {
		status.selected = !status.selected;
         if(side!='left1' && this.collaspeList[1].selected){
			this.collaspeList[0].selected=false;
		 }
		 if(side!='left2' && this.collaspeList[0].selected){
			this.collaspeList[1].selected=false;
		 }
	  }
	  // Toggle the selected status for a button
	  collaspeExpandPanelRight(status: any,side:string): void {
		status.selected = !status.selected;
         if(side=='right1' && this.collaspeListRight[0].selected){
			this.collaspeListRight[1].selected=false;
			this.collaspeListRight[2].selected=false;
			this.collaspeListRight[3].selected=false;
		 }
		 if(side=='right2' && this.collaspeListRight[1].selected){
			this.collaspeListRight[0].selected=false;
			this.collaspeListRight[2].selected=false;
			this.collaspeListRight[3].selected=false;
		 }
		 if(side=='right3' && this.collaspeListRight[2].selected){
			this.collaspeListRight[0].selected=false;
			this.collaspeListRight[1].selected=false;
			this.collaspeListRight[3].selected=false;
		 }
		 if(side=='right4' && this.collaspeListRight[3].selected){
			this.collaspeListRight[0].selected=false;
			this.collaspeListRight[1].selected=false;
			this.collaspeListRight[2].selected=false;
		 }
	  }

	  pdfSource: string = "";
	  readViewFileForApproval() {
		this.spinner.show();
		  this.pdfSource = '/assets/sample_files/sample.pdf';
		  this.spinner.hide();
	  }
	  
	  selectProperty(){
		this.propertyInfo = this.pgOwnerData.pg_owner_property_information.find(info=>info.property_id == this.property_id);
		this.property_status = this.propertyInfo.status ;
		if(this.propertyInfo.floor_information.length>0){

			this.floorInfo = this.propertyInfo.floor_information[0];
			this.floor_id = this.floorInfo.floor_id; 
			this.showRooms();
		}
		
	  }
	  selectFloor(){
			this.floorInfo = this.propertyInfo.floor_information.find(f=>f.floor_id == this.floor_id);
			this.showRooms();
	  }
	 
 	showRooms(){
		this.roomArrayList=[];
		 for(let i=0;i<this.floorInfo.rooms?.length;i=i+3){
					this.roomList=[];
					for(let j=i;j<i+3 && j<this.floorInfo.rooms.length;j++){
						this.roomList.push(this.floorInfo.rooms[j]);
					}
					this.roomArrayList.push(this.roomList);
				 }
	  }

	  availableBedsArray(n:number) {
		const num = Number(n);
		return new Array(num); 
	  }
	
	  unavailableBedsArray(room:any) {
		const num = Number(room.number_of_beds) - Number(room.beds_available) ;
		return new Array(num); 
	  }

	  getZoyOwnerDetails(){
		this.authService.checkLoginUserVlidaate();
		this.spinner.show();
		this.zoyOwnerService.getZoyOwnerDetails(this.owenerId).subscribe(res => {
		this.pgOwnerData = res?.data ;
		if(this.pgOwnerData.pg_owner_property_information.length>0){
			this.property_id =this.pgOwnerData.pg_owner_property_information[0].property_id; 
			this.propertyInfo = this.pgOwnerData.pg_owner_property_information.find(info=>info.property_id == this.property_id);
			this.property_status = this.propertyInfo.status ;
			if(this.propertyInfo.floor_information.length>0){
				this.floorInfo = this.propertyInfo.floor_information[0];
				this.floor_id = this.floorInfo.floor_id; 
				this.showRooms();
			}
		} 
		this.zoyShare = JSON.parse(JSON.stringify( this.pgOwnerData?.profile?.zoy_share? Number(this.pgOwnerData.profile.zoy_share):0));
		
		this.spinner.hide();
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
	
	openModelDoActiveteDeactivete(type:string){
		this.reason='';
		this.submitted=false;
		this.doActiveteDeactiveteType=type;
		if(this.doActiveteDeactiveteType === 'owner'){
			this.doActiveteDeactiveteName=this.pgOwnerData.pg_ownerbasic_information?.first_name +' '+this.pgOwnerData.pg_ownerbasic_information?.last_name;
			this.status=this.pgOwnerData.profile?.status;
		}else{
			 this.doActiveteDeactiveteName= this.propertyInfo.property_name;
			 this.status=this.propertyInfo.status;
		}
	}

	updateStatus(){
		this.submitted =true;
		if(!this.status || !this.reason){
			return;
		}
		this.confirmationDialogService.confirm('Confirmation!!', 'Are you sure to '+ this.status+' ' +this.doActiveteDeactiveteName+' ?')
		.then(
		   (confirmed) =>{
			if(confirmed){
				this.authService.checkLoginUserVlidaate();
				if(this.doActiveteDeactiveteType === 'owner'){
					this.updateOwnerStatus();
				}else{
					this.updatePropertyStatus();
				}
		}
		}).catch(
			() => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
		);
	}

	updateOwnerStatus(){
		this.submitted =true;
		
		const userStatus={"userId":this.owenerId,"status":this.status,"reasonMessage":this.reason};
		console.log("updateOwnerStatus>>:" , userStatus);
		this.spinner.show();
		this.zoyOwnerService.updateOwnerStatus(userStatus).subscribe(res => {
		this.notifyService.showSuccess(res.massage,"");
		this.closeModel.nativeElement.click(); 
		this.submitted =false;
		this.spinner.hide();
		}, error => {
		 this.spinner.hide();
		 console.log("Error>>:" , error);
		if(error.status == 0) {
			this.notifyService.showError("Internal Server Error/Connection not established", "")
		}else if(error.status==404){
			this.errorMsg = error.error;
			console.log("Error:" , this.errorMsg);
			this.notifyService.showError(this.errorMsg, "");
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
	updatePropertyStatus(){
		this.submitted =true;
		
		if(this.status == 'Suspend' && this.propertyInfo.floor_information.filter(bed=> (Number(bed.occupied) | 0 ) > 0).length > 0 ){
			this.notifyService.showInfo(this.doActiveteDeactiveteName+" has an active tenants, which cannot be suspended.","");
			return;
		}

		const userStatus={"userId":this.property_id,"status":this.status,"reasonMessage":this.reason};
		console.log("userStatus>>:" , userStatus);
		this.spinner.show();
		this.zoyOwnerService.updatePropertyStatus(userStatus).subscribe(res => {
		this.notifyService.showSuccess(res.massage,"");
		this.closeModel.nativeElement.click(); 
		this.submitted =false;
		this.spinner.hide();
		}, error => {
			console.log("Error>>:" , error);
		 this.spinner.hide();
		if(error.status == 0) {
			this.notifyService.showError("Internal Server Error/Connection not established", "")
		}else if(error.status==404){
			this.errorMsg = error.error;
			console.log("Error:" , error?.error?.error);
			this.notifyService.showError(error?.error?.error, "");
		}else if(error.status==403){
				this.router.navigate(['/forbidden']);
			}else if (error.error && error.error.message) {
				this.errorMsg = error.error.message;
				console.log("Error:" , this.errorMsg);
				this.notifyService.showError(this.errorMsg, "Error>>>>>");
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
				if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "Error>>");}
				//this.notifyService.showError(this.errorMsg, "");
				console.log("Error>>>>>:" , error);
			}
			});
				
	  }

	  selectTransaction(header:string){
		this.transactionHeader=header;
		if(header=='Tenant Payment History'){
			this.sortActive="transactionDate";
			this.reportName = "Tenant Transactions Report";
			this.reportNamesList = this.tenantReportService.reportNamesList;
			this.reportColumnsList=this.tenantReportService.reportColumnsList;
		}else if(header=='ZOY to Owner Payment History'){
			this.sortActive="transactionDate";
			this.reportName = "Owner Payments Dues Report";
			this.reportNamesList = this.ownerReportService.reportNamesList;
			this.reportColumnsList=this.ownerReportService.reportColumnsList;
		}
		this.columnHeaders = this.reportsService.columnHeaders;
		this.getReportSearchBy();
	}
	
	getColumnsForSelectedReport(name:string) {
		const report = this.reportColumnsList.find(n => n.reportName === name);
		return report?report.columns:[];
	 }

	  getCurrentDate(): string {
		const today = new Date();
		return this.formatDate(today);
	  }
	
	  getLastMonthDate(): string {
		const today = new Date();
		today.setMonth(today.getMonth() - 1); 
		return this.formatDate(today);
	  }
	
	  formatDate(date: Date): string {
		const year = date.getFullYear();
		const month = (date.getMonth() + 1).toString().padStart(2, '0'); 
		const day = date.getDate().toString().padStart(2, '0');
		const hours = date.getHours().toString().padStart(2, '0');
		const minutes = date.getMinutes().toString().padStart(2, '0');

		return `${year}-${month}-${day}T${hours}:${minutes}`
	  }	

	  pageChanged(event:any){
		this.reportDataSource=new MatTableDataSource<any>();
		if(this.lastPageSize!=event.pageSize){
			this.paginator.pageIndex=0;
			event.pageIndex=0;
		   }
		 this.pageSize=event.pageSize;
		 this.getReportDetails(this.paginator.pageIndex , event.pageSize,this.sortActive,this.sortDirection);
	   }

	 onSortData(sort: Sort) {
		this.sortActive=sort.active;
		this.sortDirection=sort.direction;
		this.paginator.pageIndex=0;
		 this.getReportDetails(this.paginator.pageIndex, this.pageSize,this.sortActive,this.sortDirection);
	   }

	getReportSearchBy(){
		this.selectedReportColumns= this.getColumnsForSelectedReport(this.reportName);
		this.paginator.pageIndex=0;
		this.pageSize = this.paginator.pageSize;
		this.getReportDetails(this.paginator.pageIndex , this.paginator.pageSize,this.sortActive,this.sortDirection);
	}

	getReportDetails(pageIndex:number,pageSize:number,sortActive:string,sortDirection:string){
		if(!this.fromDate || !this.toDate || new Date(this.fromDate)> new Date(this.toDate)){
			return;
		}
		
		this.authService.checkLoginUserVlidaate();
		this.spinner.show();
		this.lastPageSize=pageSize;
		this.filtersRequest.pageIndex=pageIndex;
		this.filtersRequest.pageSize=pageSize;
		this.filtersRequest.sortActive=sortActive;
		this.filtersRequest.sortDirection=sortDirection.toUpperCase();
		this.filtersRequest.propertyId = this.property_id;
		this.filtersRequest.filterData = JSON.stringify(new FilterData()) ;
		this.filtersRequest.fromDate = (this.fromDate.replace('T',' '))+':00';
		this.filtersRequest.toDate = (this.toDate.replace('T',' '))+':00';
		this.filtersRequest.reportType=this.reportNamesList.filter(n=>n.name == this.reportName)[0].key;

		if(this.transactionHeader=='ZOY to Owner Payment History'){
			this.totalProduct=0;
			this.reportDataList=Object.assign([]);
			this.reportDataSource =  new MatTableDataSource(this.reportDataList);
			this.spinner.hide();
			return;
		}
	
		if(this.reportName=='Tenant Transactions Report'){
			this.tenantReportService.getReportsDetails(this.filtersRequest).subscribe((data) => {
				if(data?.data?.length >0){
					  this.totalProduct=data.count;
					  this.reportDataList=Object.assign([],data.data);
					  this.reportDataSource = new MatTableDataSource(this.reportDataList);
				  }else{
					this.totalProduct=0;
					this.reportDataList=Object.assign([]);
					this.reportDataSource =  new MatTableDataSource(this.reportDataList);
				  }
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
				  this.spinner.hide();
				} else {
				  this.spinner.hide();
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
		}else if(this.reportName=='Owner Payments Dues Report'){
			this.ownerReportService.getReportsDetails(this.filtersRequest).subscribe((data) => {
				if(data?.data?.length >0){
					  this.totalProduct=data.count;
					  this.reportDataList=Object.assign([],data.data);
					  this.reportDataSource = new MatTableDataSource(this.reportDataList);
				  }else{
					this.totalProduct=0;
					this.reportDataList=Object.assign([]);
					this.reportDataSource =  new MatTableDataSource(this.reportDataList);
				  }
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
				  this.spinner.hide();
				} else {
				  this.spinner.hide();
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
		}else{
			this.spinner.hide();
		}
	}
		
	isNotValidNumber(value: any): boolean {
		return  (value === '' || value === undefined || value === null || isNaN(value) || value === false || value == 0);
	  }
	percentageOnlyWithZero(event): boolean {
		const charCode = (event.which) ? event.which : event.keyCode;
		const inputValue = event.target.value + String.fromCharCode(charCode); 

		if (inputValue.startsWith('.')) {
			return false;
		  }
		
		if (charCode > 31 && (charCode < 48 || charCode > 57)) {
		  if (charCode !== 46 ) { // Allow decimal point (46) and percent symbol (37)
			return false;
		  }
		}
	  
		if ((inputValue.match(/\./g) || []).length> 1 || parseFloat(inputValue) > 100 ) {
		  return false;
		}

		return true;
	  }

	zoyShare :number=0;
	zoyShareDisabled :boolean;
	zoyShareReset(): void {
		this.zoyShareDisabled = false;
		this.zoyShare =JSON.parse(JSON.stringify( this.pgOwnerData.profile.zoy_share));
		}

	zoyShareSubmit(): void {
		if(this.isNotValidNumber(this.zoyShare)){
			return ;
		}
		
		this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want to Update ?')
		.then(
		   (confirmed) =>{
			if(confirmed){
				this.authService.checkLoginUserVlidaate();
				this.spinner.show();
					this.zoyOwnerService.updateZoyShare(this.pgOwnerData.profile.owner_i_d,this.zoyShare).subscribe(res => {
					this.pgOwnerData.profile.zoy_share = JSON.parse(JSON.stringify(this.zoyShare));
					this.zoyShareDisabled = false;
					this.spinner.hide();
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
			}).catch(
				() => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
			);
	  }

	  aadhaarSession:AadhaarVerif=new AadhaarVerif();
	  aadhaarDetails:AadhaarVerif=new AadhaarVerif();
	  allowOnlyNumbers(event: KeyboardEvent): boolean {
		const charCode = event.which ? event.which : event.keyCode;
		if (charCode < 48 || charCode > 57) {
		event.preventDefault();
		return false;
		}
		return true;
	}
		
  
	isInvalidAadhaar(aadhaar: string): boolean {
		const aadhaarRegex = /^[2-9]{1}[0-9]{11}$/;
		return aadhaar ? !aadhaarRegex.test(aadhaar) : false;
	}
	submitAadhaar:boolean=false;
  
	  generateAadhaarSession(): void {
		console.log(">>>>>>>>>");
		this.submitAadhaar = false ;
		this.aadhaarSession = new AadhaarVerif();
		this.aadhaarDetails = new AadhaarVerif();
		sessionStorage.setItem('zoyadminapi', 'no');
		this.zoyOwnerService.generateAadhaarSession().subscribe(res => {
			this.aadhaarSession= JSON.parse(JSON.stringify(res));
			sessionStorage.setItem('zoyadminapi', 'yes');
		}, error => {
			sessionStorage.setItem('zoyadminapi', 'yes');
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
					str = error.error.message;
				} else {
					str = error.error.message;
					str = str.substring(str.indexOf(":") + 1);
				}
				console.log("Error:" ,str);
				this.errorMsg = str;
				}
			}
		}); 
	
	  }
	  generateAadhaarRecaptcha(): void {
		sessionStorage.setItem('zoyadminapi', 'no');
		this.zoyOwnerService.generateAadhaarRecaptcha(this.aadhaarSession).subscribe(res => {
			this.aadhaarSession= JSON.parse(JSON.stringify(res));
			sessionStorage.setItem('zoyadminapi', 'yes');
		}, error => {
			sessionStorage.setItem('zoyadminapi', 'yes');
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

	  generateAadhaarOtp(): void {
		this.submitAadhaar = true;
		if(!this.aadhaarDetails.aadhaar || this.isInvalidAadhaar(this.aadhaarDetails.aadhaar) || this.aadhaarDetails.captcha != this.aadhaarSession.captcha){
			return;
		}
		this.aadhaarDetails.sessionid = this.aadhaarSession.session_id;
		sessionStorage.setItem('zoyadminapi', 'no');
		this.zoyOwnerService.generateAadhaarOtp(this.aadhaarDetails).subscribe(res => {
		this.notifyService.showSuccess("Please check the message on the mobile number linked to this Aadhaar.",res.message)
		this.submitAadhaar = false ;	
		sessionStorage.setItem('zoyadminapi', 'yes');
	}, error => {
		sessionStorage.setItem('zoyadminapi', 'yes');
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
					str = error.error.message;
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
	  verifyAadhaarOtp(): void {
		if(!this.aadhaarDetails.otp || !this.aadhaarDetails.sessionid){
			return;
		}
		sessionStorage.setItem('zoyadminapi', 'no');
		this.zoyOwnerService.verifyAadhaarOtp(this.aadhaarDetails).subscribe(res => {
			this.notifyService.showSuccess(res.message,"");
			this.updateAadhaar();
		}, error => {
			sessionStorage.setItem('zoyadminapi', 'yes');
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
					str = error.error.message;
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
	  updateAadhaar(): void {
		let model = {"ownerId":this.owenerId,"encodedAadhar":this.aadhaarDetails.aadhaar}
		this.zoyOwnerService.updateAadhaar(model).subscribe(res => {
			sessionStorage.setItem('zoyadminapi', 'yes');
			this.getZoyOwnerDetails();
		}, error => {
			sessionStorage.setItem('zoyadminapi', 'yes');
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
					str = error.error.message;
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

		
  }  