import { AfterViewInit, Component, inject, OnInit, ViewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { NavigationEnd } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { UserService } from 'src/app/common/service/user.service';
import { AuthService } from 'src/app/common/service/auth.service';
import { DataService } from 'src/app/common/service/data.service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import { SidebarComponent } from 'src/app/components/sidebar/sidebar.component';
import { MatSort, Sort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { ZoyOwner } from '../models/zoy-owner-model';
import { MatTableDataSource } from '@angular/material/table';
import { FormBuilder } from '@angular/forms';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';
import { ZoyOwnerService } from '../../service/zoy-owner.service';
import { Filter, OwnerRequestParam } from '../models/owner-details-request-model';
import { GoogleAPIService } from 'src/app/setting/organization-info-config/services/google.api.service';
import { GenerateZoyCodeService } from '../../service/zoy-code.service';
import { ZoyData } from '../../zoy-code/models/zoy-code-model';

@Component({
  selector: 'app-managing-owner',
  templateUrl: './managing-owner.component.html',
  styleUrl: './managing-owner.component.css'
})
export class ManageOwnerComponent implements OnInit, AfterViewInit {
	displayedColumns: string[] = [ 'owner_name', 'owner_email', 'owner_contact','number_of_properties', 'status','action'];
	public ELEMENT_DATA:ZoyOwner[]=[];
	orginalFetchData:ZoyOwner[]=[];
	searchText:string='';
	dataSource:MatTableDataSource<ZoyOwner>=new MatTableDataSource<ZoyOwner>();
	columnSortDirectionsOg: { [key: string]: string | null } = {
		// owner_id: null,
	  owner_name: null,
	  owner_email: null,
	  number_of_properties: null,
	  status: null
	};
	stopPropagation(event: MouseEvent): void {
		event.stopPropagation();
	  }
	generateZCode : ZoyOwner=new ZoyOwner();
	generateZoyCode : ZoyData=new ZoyData();
	public userNameSession: string = "";
	  errorMsg: any = "";
	  mySubscription: any;
	  isExpandSideBar:boolean=true;
	  @ViewChild(SidebarComponent) sidemenuComp;
	  @ViewChild(MatSort) sort: MatSort;
	  @ViewChild(MatPaginator) paginator: MatPaginator;
	  public rolesArray: string[] = [];
	  submitted=false;
	  columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);
	  private _liveAnnouncer = inject(LiveAnnouncer);
	  param:OwnerRequestParam=new OwnerRequestParam();
	  paramFilter:Filter=new Filter();
	  pageSize: number = 10; 
	  pageSizeOptions: number[] = [10, 20, 50]; 
	  totalProduct: number = 0;
	  public lastPageSize:number=0;
	  public ownerIdBackFormDetails:string="";
	  paramFilterBack:OwnerRequestParam=new OwnerRequestParam();
	  
	  totalProductFilterBack: number = 0;
	  @ViewChild('closeModelAddProperty') closeModelAddProperty: any;
	  @ViewChild('closeModelResendZoyCode') closeModelResendZoyCode: any;
	  
	  constructor(private zoyOwnerService : ZoyOwnerService,private route: ActivatedRoute, private router: Router,private formBuilder: FormBuilder, private http: HttpClient, private userService: UserService,
		  private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService, private confirmationDialogService:ConfirmationDialogService,private generateZoyCodeService : GenerateZoyCodeService,private googleAPIService:GoogleAPIService) {
			  this.authService.checkLoginUserVlidaate();
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
		  this.dataService.getOwenerId.subscribe(id=>{
			this.ownerIdBackFormDetails=id;
		  });

		  this.dataService.getOwenerListFilter.subscribe(data=>{
			this.orginalFetchData= data;
		  });
		  this.dataService.getOwenerListFilterTotal.subscribe(data=>{
			this.totalProductFilterBack= data;
		  });
		  this.dataService.getOwenerListFilterParam.subscribe(data=>{
			this.paramFilterBack= data;
		  });
		  
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
		  
		  //this.getZoyOwnerList()
	  }
	  ngAfterViewInit() {
		  this.sidemenuComp.expandMenu(2);
		  this.sidemenuComp.activeMenu(2, 'manage-owner');
		  this.dataService.setHeaderName("Manage Owner");
		  if(this.ownerIdBackFormDetails==''){
		  	this.getRetrieveData();
		  }else{
			  this.ELEMENT_DATA = Object.assign([],this.orginalFetchData);
			  this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
			  this.totalProduct=this.totalProductFilterBack;
			  this.columnSortDirections[this.paramFilterBack.sortActive] = this.paramFilterBack.sortDirection;
			  this.paginator.pageIndex=this.paramFilterBack.pageIndex;
			  this.paginator.pageSize=this.paramFilterBack.pageSize;
			  this.lastPageSize=this.paramFilterBack.pageSize;
			  this.param=this.paramFilterBack;
		  }
	  }
	  
	  getRetrieveData(){
		this.dataSource.sort = this.sort;
		this.dataSource.paginator = this.paginator;
		this.param.pageIndex=this.paginator.pageIndex;
		this.param.pageSize= this.paginator.pageSize;
		this.param.sortDirection="asc";
		this.param.sortActive="owner_name";
		this.paramFilter.searchText=null;
		this.paramFilter.status=null;
		this.param.filter=this.paramFilter;
		setTimeout(()=>{
		  this.getZoyOwnerList();
		 },100);
		 this.columnSortDirections["owner_name"] = "asc";
	  }


	  getZoyOwnerList(){
		  this.authService.checkLoginUserVlidaate();
		  this.spinner.show();
		  this.lastPageSize=this.param.pageSize;
		  this.zoyOwnerService.getZoyOwnerList(this.param).subscribe(data => {
			
			  this.orginalFetchData=  Object.assign([],data.content);
			  this.ELEMENT_DATA = Object.assign([],data.content);
			  this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
			  //this.dataSource.sort = this.sort;
			  //this.dataSource.paginator = this.paginator;
			  this.dataService.setOwenerListFilter(this.orginalFetchData);
			  this.totalProduct=data.total;
			  this.dataService.setOwenerListFilterTotal(this.totalProduct);
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
			  //this.notifyService.showError(this.errorMsg, "");
		  }
		  });
	  }
	
	announceSortChange(sortState: Sort): void {
	  this.columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);
	  this.columnSortDirections[sortState.active] = sortState.direction;
		if (sortState.direction) {
		  this._liveAnnouncer.announce(`Sorted ${sortState.direction}ending`);
		} else {
		  this._liveAnnouncer.announce('Sorting cleared');
		}
		this.param.sortActive=sortState.active;
		this.param.sortDirection=sortState.direction!="" ? sortState.direction:"asc";
		this.param.pageIndex=0
		this.paginator.pageIndex=0;
		this.getZoyOwnerList();
	}

	filterData($event: KeyboardEvent){
		if ($event.keyCode === 13) {
		if(this.searchText==''){
			//this.ELEMENT_DATA = Object.assign([],this.orginalFetchData);
			//this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
			this.paramFilter.searchText=null;
			this.paramFilter.status=null;
		}else{
			//const pagedData = Object.assign([],this.orginalFetchData.filter(data =>
			//	data.owner_name.toLowerCase().includes(this.searchText.toLowerCase()) || data.email_id.toLowerCase().includes(this.searchText.toLowerCase()) || data.mobile_no?.toLowerCase().includes(this.searchText.toLowerCase()) || data.noof_properties?.toLowerCase().includes(this.searchText.toLowerCase()) || data.zoy_code?.toLowerCase().includes(this.searchText.toLowerCase())
			//));
			//this.ELEMENT_DATA = Object.assign([],pagedData);
			//this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
			this.paramFilter.searchText=this.searchText;
			this.paramFilter.status=null;
		}
		this.param.pageIndex=0
		this.paginator.pageIndex=0;
		this.param.filter=this.paramFilter;
		this.getZoyOwnerList();
		//this.dataSource.sort = this.sort;
		//this.dataSource.paginator = this.paginator;
	  }
	}
	resetFilter(){
		this.searchText='';
		this.paramFilter.searchText=null;
		this.paramFilter.status=null;
		this.param.pageIndex=0
		this.paginator.pageIndex=0;
		this.param.filter=this.paramFilter;
		this.getZoyOwnerList();
		this.statuses.filter(data=>data.selected=false);
		this. selectedFilterStatus();
	}

	statuses = [
		{ id: 1, name: 'Active', selected: false },
		{ id: 2, name: 'Inactive', selected: false },
		{ id: 3, name: 'Blocked', selected: false }
	  ];
	  selectedStatuses:string[]=[]; 
	   // Toggle the selected status for a button
	   toggleStatus(status: any): void {
		status.selected = !status.selected;
		this.selectedFilterStatus();
	  }
      selectedFilterStatus(){
		this.selectedStatuses = this.statuses
		.filter(status => status.selected)
		.map(status => status.name);
	  }
	  // Apply and process the selected statuses
	  applyStatuses(): void {
		//console.log('Selected Statuses:', this.selectedStatuses);
	  }

	  pageChanged(event:any){
		this.dataSource=new MatTableDataSource<ZoyOwner>();
		if(this.lastPageSize!=event.pageSize){
		this.paginator.pageIndex=0;
		event.pageIndex=0;
		}
		this.param.pageIndex=this.paginator.pageIndex;
		this.param.pageSize= event.pageSize;
		this.getZoyOwnerList();
		}

		 setOwnerId(ownerId: string) {
			this.dataService.setOwenerId(ownerId);
			localStorage.setItem('ownerInfo', ownerId);
			this.dataService.setOwenerListFilterParam(this.param);
			this.router.navigateByUrl('/manage-owner-details');
		  }


		  onPincodeChange(event: any) {
        const pincode = event.target.value;
        if (pincode && pincode.length === 6) {
		  this. getCityAndState(pincode);
        } else {
		  this.generateZoyCode.property_city = '';
		  this.generateZoyCode.property_city_code_id = '';
		  this.generateZoyCode.property_city_code = '';
          this.generateZoyCode.property_state = '';
		  this.generateZoyCode.property_state_short_name = '';
		  this.generateZoyCode.property_locality ='';
		  this.generateZoyCode.property_house_area=''
		  this.generateZoyCode.property_location_latitude='';
		  this.generateZoyCode.property_location_longitude='';
        }
      }
	  zoycodeDisableField:boolean=true;
	  areaList:string[];
	  areaTypeOption:boolean=true;
      getCityAndState(pincode){
        this.googleAPIService.getArea(pincode).then(result => {
			console.info("Component owner:"+result.data);
			const res=result.data;
        if (res.results && res.results?.length > 0 ) {
          const addressComponents = res.results[0].address_components;
          this.generateZoyCode.property_city = this.generateZoyCodeService.extractCity(addressComponents);
          this.generateZoyCode.property_state = this.generateZoyCodeService.extractState(addressComponents);
		  this.generateZoyCode.property_state_short_name=this.generateZoyCodeService.extractStateShortName(addressComponents);
		  this.generateZoyCode.property_house_area=res.results[0].formatted_address;
		  this.generateZoyCode.property_location_latitude=res.results[0].geometry.location.lat;
		  this.generateZoyCode.property_location_longitude=res.results[0].geometry.location.lng;
		   if(res.results[0].postcode_localities!=undefined && res.results[0]?.postcode_localities){
		   this.areaList=Object.assign([],res.results[0].postcode_localities);
		   this.generateZoyCode.property_locality ="";
		   this.areaTypeOption=true;
		   }else{
			 this.generateZoyCode.property_locality = this.generateZoyCodeService.extractArea(addressComponents);
			 this.areaList=Object.assign([]);
			 this.getLocationDetails(this.generateZoyCode.property_locality,2)
			 this.areaTypeOption=false;
		   }

		   this.getLocationDetails(this.generateZoyCode.property_city,1);
		   if(this.areaTypeOption){
		   this.getLocationDetails(this.generateZoyCode.property_locality,2);
		   }
        } else {
		  this.generateZoyCode.property_city = '';
		  this.generateZoyCode.property_city_code_id = '';
		this.generateZoyCode.property_city_code = '';
          this.generateZoyCode.property_state = '';
		  this.generateZoyCode.property_state_short_name = '';
		  this.generateZoyCode.property_locality ='';
		  this.generateZoyCode.property_locality_code ='';
		this.generateZoyCode.property_locality_code_id ='';
		  this.generateZoyCode.property_house_area=''
		  this.generateZoyCode.property_location_latitude='';
		  this.generateZoyCode.property_location_longitude='';
        }      
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
        }
      );

      }
      
	  ownerNameForPopup:string='';
	  ownerMobileForPopup:string='';
	  submittedAddProperty:boolean=false;
	  addProperty(data:any){
		this.submittedAddProperty=false;
		this.ownerNameForPopup=data.owner_name;
		this.ownerMobileForPopup=data.owner_contact;
		this.generateZoyCode.contactNumber=data.owner_contact;
		this.generateZoyCode.userEmail=data.owner_email;
		this.generateZoyCode.firstName=data.owner_name.split(" ")[0];
		this.generateZoyCode.lastName=data.owner_name.split(" ")[1];
	  }

	  resetProperty(){
		this.generateZoyCode.property_pincode =null;	
		this.generateZoyCode.property_name = '';
		this.generateZoyCode.property_city = '';
		this.generateZoyCode.property_city_code_id = '';
		this.generateZoyCode.property_city_code = '';
		this.generateZoyCode.property_state = '';
		this.generateZoyCode.property_state_short_name = '';
		this.generateZoyCode.property_locality ='';
		this.generateZoyCode.property_locality_code ='';
		this.generateZoyCode.property_locality_code_id ='';
		this.generateZoyCode.property_house_area=''
		this.generateZoyCode.property_location_latitude='';
		this.generateZoyCode.property_location_longitude='';
		this.generateZoyCode.zoyShare='';
		this.areaList=Object.assign([]);
		this.areaTypeOption=true;
	  }
	  generateCodeForProperty(){
		this.submittedAddProperty=true;
		if (this.generateZoyCode.property_name==undefined || this.generateZoyCode.property_name==null || this.generateZoyCode.property_name=='' || this.generateZoyCode.property_pincode==undefined || this.generateZoyCode.property_pincode==null || this.generateZoyCode.property_locality==undefined || this.generateZoyCode.property_locality==null || this.generateZoyCode.property_locality=='' || this.generateZoyCode.zoyShare==undefined || this.generateZoyCode.zoyShare==null || this.generateZoyCode.zoyShare=='' || this.isNotValidNumber(this.generateZoyCode.zoyShare) || this.generateZoyCode.property_city_code==undefined || this.generateZoyCode.property_city_code==null || this.generateZoyCode.property_city_code=='' || this.generateZoyCode.property_locality_code==undefined || this.generateZoyCode.property_locality_code==null || this.generateZoyCode.property_locality_code=='') {
		return;
		}
		this.spinner.show();		     
		this.submittedAddProperty=false;
		this.generateZoyCode.property_city_code=this.generateZoyCode.property_city_code.toUpperCase();
		this.generateZoyCode.property_locality_code=this.generateZoyCode.property_locality_code.toUpperCase();
		this.generateZoyCodeService.generateOwnerCodeForMoreProperty(this.generateZoyCode).subscribe((res) => {
			this.notifyService.showSuccess(res.message, "");
		    this.resetProperty();
			this.closeModelAddProperty.nativeElement.click();			
			this.spinner.hide();
		  },error =>{
			this.spinner.hide();
			console.log("error.error",error)
			if(error.status == 0) {
				this.notifyService.showError("Internal Server Error/Connection not established", "")
			 }else if(error.status==403){
			this.router.navigate(['/forbidden']);
			}else if (error.error && error.error.message) {
			this.errorMsg =error.error.message;
			console.log("Error:"+this.errorMsg);
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
		  	//this.notifyService.showError(this.errorMsg, "");
			}
		  }
		  ); 
	  }

  isNotValidNumber(value: any): boolean {
	return  (value === '' || value == undefined || value == null || isNaN(value) || (value === false && value !== 0));
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
  propertyListForResend:ZoyOwner[]=[];
  resendZoycode:string='';
  resendPropertyList(data:any){
	this.ownerNameForPopup=data.owner_name;
	this.ownerMobileForPopup=data.owner_contact;  
	this.resendZoycode='';  
	this.spinner.show();		     
	this.zoyOwnerService.getExistingPgOwnerData(data.owner_id).subscribe((res) => {
		this.propertyListForResend=	Object.assign([],res);	
		this.spinner.hide();
	  },error =>{
		this.spinner.hide();
		console.log("error.error",error)
		if(error.status == 0) {
			this.notifyService.showError("Internal Server Error/Connection not established", "")
		 }else if(error.status==403){
		this.router.navigate(['/forbidden']);
		}else if (error.error && error.error.message) {
		this.errorMsg =error.error.message;
		console.log("Error:"+this.errorMsg);
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
		  //this.notifyService.showError(this.errorMsg, "");
		}
	  }
	  ); 
  }

  resendZoycodeEmail(){
	this.spinner.show();		     
	this.zoyOwnerService.resendOwnerCode(this.resendZoycode).subscribe((res) => {
		this.notifyService.showSuccess(res.message, "");
		this.resendZoycode='';
		this.closeModelResendZoyCode.nativeElement.click();			
		this.spinner.hide();
	  },error =>{
		this.spinner.hide();
		console.log("error.error",error)
		if(error.status == 0) {
			this.notifyService.showError("Internal Server Error/Connection not established", "")
		 }else if(error.status==403){
		this.router.navigate(['/forbidden']);
		}else if (error.error && error.error.message) {
		this.errorMsg =error.error.message;
		console.log("Error:"+this.errorMsg);
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
		  //this.notifyService.showError(this.errorMsg, "");
		}
	  }
	  ); 
  }
  isEditCityCode:boolean=true;
  isEditAreaCode:boolean=true;
  getLocationDetails(location:string,type:number){
	this.spinner.show();
	this.generateZoyCodeService.getLocationDetails(location).subscribe(data => {
		if(data!="" && data!=null && data!=undefined){
			if(type==1 && data.location_short_name!=''){
			this.isEditCityCode=false;
			this.generateZoyCode.property_city_code_id = data.location_code_id;
			this.generateZoyCode.property_city_code = data.location_short_name;
			}
			if(type==2 && data.location_short_name!=''){
				this.isEditAreaCode=false;
				this.generateZoyCode.property_locality_code_id = data.location_code_id;
				this.generateZoyCode.property_locality_code = data.location_short_name;
			}
		}
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

  }  