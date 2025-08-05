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
import { ZoyData } from '../models/zoy-code-model';
import { MatTableDataSource } from '@angular/material/table';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { GenerateZoyCodeService } from '../../service/zoy-code.service';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';
import { GoogleAPIService } from 'src/app/setting/organization-info-config/services/google.api.service';


@Component({
  selector: 'app-zoy-code',
  templateUrl: './zoy-code.component.html',
  styleUrl: './zoy-code.component.css'
})
export class ZoyCodeComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = ['zoy_code', 'owner_name', 'email_id', 'mobile_no','created_date', 'zoy_share','action'];
  public ELEMENT_DATA:ZoyData[]=[];
  orginalFetchData:ZoyData[]=[];
  searchText:string='';
  dataSource:MatTableDataSource<ZoyData>=new MatTableDataSource<ZoyData>();
  columnSortDirectionsOg: { [key: string]: string | null } = {
    zoy_code: null,
    owner_name: null,
    email_id: null,
    created_date: null,
	zoy_share: null,
	// status: null
  };
  generateZCode : ZoyData=new ZoyData();
  public userNameSession: string = "";
	errorMsg: any = "";
	mySubscription: any;
	isExpandSideBar:boolean=true;
	@ViewChild(SidebarComponent) sidemenuComp;
	@ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;
	public rolesArray: string[] = [];
	form: FormGroup;
	submitted=false;
	columnSortDirections = Object.assign({}, this.columnSortDirectionsOg);
	private _liveAnnouncer = inject(LiveAnnouncer);
	constructor(private generateZoyCodeService : GenerateZoyCodeService,private route: ActivatedRoute, private router: Router,private formBuilder: FormBuilder, private http: HttpClient, private userService: UserService,
		private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService, private confirmationDialogService:ConfirmationDialogService,private googleAPIService:GoogleAPIService) {
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
	}
	selectedModel: string = 'generate';

	// Method to update the selected button
	selectButton(button: string): void {
	  this.selectedModel = button;
	  this.submitted=false;
	  if(this.selectedModel =='generated'){
		 this.getZoyCodeDetails();
		 this.form.reset();
	  }else if(this.selectedModel=='ticket'){
		this.revenueTypeTicket='fixed';
		this.ticket= new ZoyData(); 
   		this.searchInput ="";
	  }else{
		this.searchText='';
		this.filterData();
	  }
	  this.generateZCode=new ZoyData();
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
		this.form = this.formBuilder.group({
			firstName: ['', [Validators.required]],
			lastName: ['', [Validators.required]],
		    contactNumber: ['', [Validators.required],],
			pgName: ['', [Validators.required],],
			pincode: ['', [Validators.required],],
			state: [''],
			city: [''],
			areaAddress: ['', [Validators.required],],
			cityCode: ['', [Validators.required],],
			areaCode: ['', [Validators.required],],
			userEmail: ['', [
			  Validators.required,
			  Validators.pattern(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/)
			]],
			zoyShare:['', [Validators.required]],
			property_street_name:['', [Validators.required]],
			property_door_number:['', [Validators.required]],
			revenueType: new FormControl('fixed')
		  });
	}
	ngAfterViewInit() {
		this.sidemenuComp.expandMenu(2);
		this.sidemenuComp.activeMenu(2, 'zoy-code');
		this.dataService.setHeaderName("Zoy Code");
	}
	emailLower(event){
		if(this.generateZCode.userEmail!=undefined && this.generateZCode.userEmail!=null && this.generateZCode.userEmail!=''){
		this.generateZCode.userEmail=this.generateZCode.userEmail.toLocaleLowerCase();
		}
	}
	numberOnly(event): boolean {
		const charCode = (event.which) ? event.which : event.keyCode;
		if (charCode > 31 && (charCode < 48 || charCode > 57)) {
		  return false;
		}
		return true;
	   }

	   generateZoyCode() {
		this.submitted=true;	
		if (this.form.invalid || this.generateZCode.contactNumber.length !=10) {
		return;
		}
		this.spinner.show();		     
		this.submitted=false;
		this.generateZCode.userEmail=this.generateZCode.userEmail.toLocaleLowerCase();
		this.generateZCode.property_city_code=this.generateZCode.property_city_code?.toUpperCase();
		this.generateZCode.property_locality_code=this.generateZCode.property_locality_code?.toUpperCase();
		this.generateZoyCodeService.generateOwnerCode(this.generateZCode,this.revenueType.value).subscribe((res) => {
			this.notifyService.showSuccess(res.message, "");			
			this.spinner.hide();
			this.form.reset();
		  },error =>{
			this.spinner.hide();
			console.log("error.error",error)
			if(error.status == 0) {
				this.notifyService.showError("Internal Server Error/Connection not established", "")
			 }else if(error.status==409){
				this.confirmationDialogService.confirm('Confirmation!!', 'A Zoycode has already been generated for this email Id/Mobile number, Would you like to resend the code?')
				.then(
				  (confirmed) =>{
				   if(confirmed){
					this.resendZoyCode()
				   }
				}).catch(
					() => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
				); 
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

		resendZoyCode() {
			this.submitted=true;	
			if (this.form.invalid) {
			return;
			}
			this.spinner.show();		     
			this.submitted=false;
			this.generateZoyCodeService.resendOwnerCode(this.generateZCode.userEmail).subscribe((res) => {
				this.notifyService.showSuccess(res.message, "");	
				this.form.reset();
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
				//  this.spinner.hide();
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
			  });  
	    }  
		filterData(){
				if(this.searchText==''){
					this.ELEMENT_DATA = Object.assign([],this.orginalFetchData);
    				this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
				}else{
					const pagedData = Object.assign([],this.orginalFetchData.filter(data =>
						data.owner_name.toLowerCase().includes(this.searchText.toLowerCase()) || data.email_id.toLowerCase().includes(this.searchText.toLowerCase()) || data.mobile_no.toLowerCase().includes(this.searchText.toLowerCase()) || data.created_date.toLowerCase().includes(this.searchText.toLowerCase()) || data.zoy_code.toLowerCase().includes(this.searchText.toLowerCase())
					));
					this.ELEMENT_DATA = Object.assign([],pagedData);
    				this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
				}
				this.dataSource.sort = this.sort;
				this.dataSource.sortingDataAccessor = (data, sortHeaderId) => data[sortHeaderId].toLocaleLowerCase();
		}
	getZoyCodeDetails(){
		this.authService.checkLoginUserVlidaate();
		this.spinner.show();
		this.generateZoyCodeService.getGeneratedZoyCodeDetails().subscribe(data => {
			this.orginalFetchData=  Object.assign([],data);
			this.ELEMENT_DATA = Object.assign([],data);
			this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
			this.dataSource.sort = this.sort;
			this.columnSortDirections['created_date']='desc';
			this.dataSource.sortingDataAccessor = (data, sortHeaderId) => data[sortHeaderId].toLocaleLowerCase();
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
nameValidation(event: any, inputId: string) {
  const clipboardData = event.clipboardData || (window as any).clipboardData;
  const pastedText = clipboardData.getData('text/plain');
  const clString = pastedText.replace(/[^a-zA-Z\s.]/g, '');
   event.preventDefault();
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

  resend(element:any){
	this.confirmationDialogService.confirm('Confirmation!!', 'Would you like to resend the code for '+element.owner_name+' ?')
				.then(
				  (confirmed) =>{
				   if(confirmed){
					this.spinner.show();		     
			    this.generateZoyCodeService.resendOwnerCode(element.email_id).subscribe((res) => {
				this.notifyService.showSuccess(res.message, "");
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
				//  this.spinner.hide();
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
			  });  
				   }
				}).catch(
					() => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
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

  
  onPincodeChange(event: any) {
        const pincode = event.target.value;
        if (pincode && pincode.length === 6) {
		  this.generateZCode.property_city_code = '';
          this. getCityAndState(pincode);
        } else {
		  this.generateZCode.property_city = '';
		  this.generateZCode.property_city_code_id = '';
		this.generateZCode.property_city_code = '';
          this.generateZCode.property_state = '';
		  this.generateZCode.property_state_short_name = '';
		  this.generateZCode.property_locality ='';
		  this.generateZCode.property_locality_code ='';
		this.generateZCode.property_locality_code_id ='';
		  this.generateZCode.property_house_area=''
		  this.generateZCode.property_location_latitude='';
		  this.generateZCode.property_location_longitude='';
        }
      }
	  zoycodeDisableField:boolean=true;
	  areaList:string[];
	  areaTypeOption:boolean=true;
      getCityAndState(pincode){
		this.spinner.show();
        this.googleAPIService.getArea(pincode).then(result => {
		console.info("Component :"+result.data);
			const res=result.data;
        if (res.results && res.results?.length > 0 ) {
          const addressComponents = res.results[0].address_components;
          this.generateZCode.property_city = this.generateZoyCodeService.extractCity(addressComponents);
          this.generateZCode.property_state = this.generateZoyCodeService.extractState(addressComponents);
		  this.generateZCode.property_state_short_name=this.generateZoyCodeService.extractStateShortName(addressComponents);
		  this.generateZCode.property_house_area=res.results[0].formatted_address;
		  this.generateZCode.property_location_latitude=res.results[0].geometry.location.lat;
		  this.generateZCode.property_location_longitude=res.results[0].geometry.location.lng;
		   if(res.results[0].postcode_localities!=undefined && res.results[0]?.postcode_localities){
		   this.areaList=Object.assign([],res.results[0].postcode_localities);
		   this.generateZCode.property_locality ="";
		   this.areaTypeOption=true;
		   }else{
			 this.generateZCode.property_locality = this.generateZoyCodeService.extractArea(addressComponents);
			 this.areaList=Object.assign([]);
			 this.getAreaDetails(this.generateZCode.property_locality)
			 this.areaTypeOption=false;
		   }

		   this.getLocationDetails(this.generateZCode.property_city);
		   if(this.areaTypeOption){
		   this.getAreaDetails(this.generateZCode.property_locality);
		   }
        } else {
		  this.generateZCode.property_city = '';
		  this.generateZCode.property_city_code_id = '';
		  this.generateZCode.property_city_code = '';
          this.generateZCode.property_state = '';
		  this.generateZCode.property_state_short_name = '';
		  this.generateZCode.property_locality ='';
		  this.generateZCode.property_locality_code ='';
		  this.generateZCode.property_locality_code_id ='';
		  this.generateZCode.property_house_area=''
		  this.generateZCode.property_location_latitude='';
		  this.generateZCode.property_location_longitude='';
        }  
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
        }
      );

	  }
	  isEditCityCode:boolean=true;
  		isEditAreaCode:boolean=true;

		//getAreaDetails
	  getLocationDetails(loc:string){
		this.spinner.show();
		this.generateZoyCodeService.getLocationDetails(loc).subscribe(data => {
			if(data!="" && data!=null && data!=undefined && data?.location_short_name!=''){
				this.isEditCityCode=false;
				this.generateZCode.property_city_code_id = data.location_code_id;
				this.generateZCode.property_city_code = data.location_short_name;
			}else{
				this.isEditCityCode=true;
				this.generateZCode.property_city_code_id = '';
				this.generateZCode.property_city_code = '';
		
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
	  getAreaDetails(loc:string){
		this.spinner.show();
		this.generateZoyCodeService.getAreaDetails(loc).subscribe(data => {
			if(data!="" && data!=null && data!=undefined && data?.area_short_name !=''){
				this.isEditAreaCode=false;
				this.generateZCode.property_locality_code_id = data.area_code_id;
				this.generateZCode.property_locality_code = data.area_short_name;
			}else{
				this.isEditAreaCode=true;
				this.generateZCode.property_locality_code_id = '';
				this.generateZCode.property_locality_code = '';
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
	isFixed:boolean=false;
	revenueType = new FormControl('fixed');

isInvalidZoyShare(): boolean {
  const rawValue = this.generateZCode.zoyShare;
  const value = Number(rawValue);
  if (this.revenueType.value === 'fixed') {
    return isNaN(value) || value < 0 || value.toString().length > 8;
  } else {
    return isNaN(value) || value < 0 || value > 100;
  }
}

percentageOnly(event: KeyboardEvent) {
	const inputElement = event.target as HTMLInputElement;
	const input = inputElement.value;
	const char = event.key;
  
	if (['Backspace', 'ArrowLeft', 'ArrowRight', 'Tab', 'Delete'].includes(char)) {
	  return;
	}
  
	if (!char.match(/[0-9.]/)) {
	  event.preventDefault();
	  return;
	}
  
	if (char === '.' && input.includes('.')) {
	  event.preventDefault();
	  return;
	}
  
	const futureValue = input + char;
  
	if (/^0\d/.test(futureValue)) {
	  event.preventDefault();
	  return;
	}
  
	const numericValue = parseFloat(futureValue);
	if (isNaN(numericValue) || numericValue > 100) {
	  event.preventDefault();
	}
  }

  zoyShare(element:any):string{
		return   Number(element.zoy_fixed_share) === 0 
			? (Number(element.zoy_variable_share).toFixed(2) + ' %') 
			: ('Rs. ' + Number(element.zoy_fixed_share).toFixed(2));
  }
   revenueTypeTicket: 'fixed' | 'variable' = 'fixed';
   ticket:ZoyData= new ZoyData(); 
   searchInput :string="";
   submitZoyCode(): void {
	this.submitted =true;
 
    if (!this.generateZCode.zoyShare) {
      return;
    }

		let  model = new ZoyData();
		model.firstName = this.ticket.first_name
		model.lastName = this.ticket.last_name || '';
		model.contactNumber = this.ticket.mobile_no
		model.userEmail = this.ticket.email_id
		model.zoyShare = this.generateZCode.zoyShare
		model.property_name = this.ticket.property_name
		model.property_pincode = this.ticket.property_pincode
		model.property_state = this.ticket.property_state
		model.property_locality = this.ticket.property_locality
		model.property_house_area = this.ticket.property_house_area
		model.property_location_latitude = this.ticket.property_location_latitude
		model.property_location_longitude = this.ticket.property_location_longitude
		model.property_state_short_name = ""
		model.property_city_code = this.ticket.property_city_code
		model.property_city= this.ticket.property_city
		model.property_city_code_id = this.ticket.property_city_code_id
		model.property_locality_code = this.ticket.property_locality_code
		model.property_locality_code_id = this.ticket.property_locality_code_id
		model.property_street_name="";
		model.property_door_number="";
	this.spinner.show();
  	this.generateZoyCodeService.generateOwnerCode(model,this.revenueTypeTicket).subscribe((res) => {
			this.notifyService.showSuccess(res.message, "");
			this.ticket=new ZoyData();
			this.searchText="";
			this.submitted =false			
			this.spinner.hide();
		  },error =>{
			this.spinner.hide();
			console.log("error.error",error)
			if(error.status == 0) {
				this.notifyService.showError("Internal Server Error/Connection not established", "")
			 }else if(error.status==409){
				this.confirmationDialogService.confirm('Confirmation!!', 'A Zoycode has already been generated for this email Id/Mobile number, Would you like to resend the code?')
				.then(
				  (confirmed) =>{
				   if(confirmed){
					this.resendZoyCode();
				   }
				}).catch(
					() => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
				); 
			}else if(error.status==403){
			this.router.navigate(['/forbidden']);
			}else if (error.error &&( error.error.message || error.error.error )) {
			this.errorMsg =error.error.message || error.error.error;
			console.log("Error:"+this.errorMsg);
			if(error.status==500 && error.statusText=="Internal Server Error"){
			  this.errorMsg=error.statusText+"! Please login again or contact your Help Desk.";
			}else{
			  let str;
			  if(error.status==400){
			  str=error.error.error ;
			  }else{
				str=error.error.message || error.error.error;
				str=str.substring(str.indexOf(":")+1);
			  }
			  console.log("Error:",str);
			  this.errorMsg=str;
			}
		  }
		  if(error.status !== 401 && error.status !=409 ){this.notifyService.showError(this.errorMsg, "");}
		  	//this.notifyService.showError(this.errorMsg, "");
			}
		  );  
  }
  
    searchTicket(): void {
    if (!this.searchInput.trim()) {
      this.notifyService.showInfo('Please enter a Ticket Number or Email','');
      return;
    }
	this.ticket= new ZoyData(); 
		this.spinner.show();
		this.generateZoyCodeService.fetchPGDetails(this.searchInput).subscribe(data => {
			if(data!="" && data!=null && data!=undefined){
			   this.ticket=data;
			if(this.ticket.property_city){
				this.getLocationDetailsForTicket(this.ticket.property_city) ;
			}
			if(this.ticket.property_locality){
				this.getAreaDetailsForTicket(this.ticket.property_locality);
			}
		this.revenueTypeTicket='fixed';
		this.submitted=false;
		this.generateZCode = new ZoyData();
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
	
	  getLocationDetailsForTicket(loc:string) {
		this.generateZoyCodeService.getLocationDetails(loc).subscribe(data => {
			if(data!="" && data!=null && data!=undefined && data?.location_short_name!=''){
				this.ticket.property_city_code_id = data.location_code_id;
				this.ticket.property_city_code = data.location_short_name;
			}else{
				this.ticket.property_city_code_id = "";
				this.ticket.property_city_code = "";
			}
			
		}, error => {
			  console.error('Error fetching location details:', error);
		});
    }
	 getAreaDetailsForTicket(loc:string) {
		this.generateZoyCodeService.getAreaDetails(loc).subscribe(data => {
			if(data!="" && data!=null && data!=undefined &&  data?.location_short_name!=''){
				this.ticket.property_locality_code_id = data.area_code_id;
				this.ticket.property_locality_code = data.area_short_name;
			}else{
				this.ticket.property_locality_code_id = '';
				this.ticket.property_locality_code = '';
			}
			
		}, error => {
			  console.error('Error fetching location details:', error);
		});
    }

}
