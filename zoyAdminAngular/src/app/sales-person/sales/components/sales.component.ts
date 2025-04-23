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
import { SalesData } from '../models/sales-model';
import { MatTableDataSource } from '@angular/material/table';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { GenerateSalesService } from '../../service/sales.service';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';
import { Filter, OwnerRequestParam } from 'src/app/owners/managing-owner/models/owner-details-request-model';

@Component({
  selector: 'app-sales',
  templateUrl: './sales.component.html',
  styleUrl: './sales.component.css'
})
export class SalesComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = ['fullName', 'emailId', 'mobile_no','createdAt', 'emp_id','action'];
  public ELEMENT_DATA:SalesData[]=[];
  orginalFetchData:SalesData[]=[];
  searchText:string='';
  param:OwnerRequestParam=new OwnerRequestParam();
  paramFilter:Filter=new Filter();
  pageSize: number = 10; 
  pageSizeOptions: number[] = [10, 20, 50]; 
  totalProduct: number = 0;
  public lastPageSize:number=0;
  dataSource:MatTableDataSource<SalesData>=new MatTableDataSource<SalesData>();
  columnSortDirectionsOg: { [key: string]: string | null } = {
    fullName: null,
    emailId: null,
    createdAt: null,
	emp_id: null,
	// status: null
  };
  generateSalesPerson : SalesData=new SalesData();
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
	constructor(private generateSalesService : GenerateSalesService,private route: ActivatedRoute, private router: Router,private formBuilder: FormBuilder, private http: HttpClient, private userService: UserService,
		private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService, private confirmationDialogService:ConfirmationDialogService) {
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
			middleName:[''],
			lastName: ['', [Validators.required]],
		    contactNumber: ['', [Validators.required]],
			userEmail: ['', [
			  Validators.required,
			  Validators.pattern(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/)
			]],
			empId:[''],
		  });

	}
	ngAfterViewInit() {
		this.sidemenuComp.expandMenu(9);
		this.sidemenuComp.activeMenu(9, 'sales-person');
		this.dataService.setHeaderName("Sales User Creation");
	}

	// Method to update the selected button
	selectButton(button: string): void {
		this.selectedModel = button;
		if(this.selectedModel =='generated'){
		this.param.pageIndex=0;//this.paginator.pageIndex;
		this.param.pageSize=this.pageSize;
		this.param.sortDirection="desc";
		this.param.sortActive="createdAt";
		  this.paramFilter.searchText=null;
		  this.param.filter=this.paramFilter; 
		   this.getSalesPerson();
		   this.columnSortDirections["createdAt"] = "desc";
		   this.submitted=false;
		   this.form.reset();
		}else{
		  this.searchText='';
		}
  
	  }
	numberOnly(event): boolean {
		const charCode = (event.which) ? event.which : event.keyCode;
		if (charCode > 31 && (charCode < 48 || charCode > 57)) {
		  return false;
		}
		return true;
	   }

	   registerSalesPerson() {
		this.submitted=true;	
		if (this.form.invalid || this.generateSalesPerson.contactNumber.length !=10) {
		return;
		}
		this.spinner.show();		     
		this.submitted=false;
		this.generateSalesService.registerSubmitSalesPerson(this.generateSalesPerson).subscribe((res) => {
			this.notifyService.showSuccess(res.message, "");			
			this.spinner.hide();
			this.form.reset();
		  },error =>{
			this.spinner.hide();
			console.log("error.error",error)
			if(error.status == 0) {
				this.notifyService.showError("Internal Server Error/Connection not established", "")
			 }else if(error.status==409){
				this.confirmationDialogService.confirm('Confirmation!!', 'Credential has already been generated for this email Id, Would you like to resend again?')
				.then(
				  (confirmed) =>{
				   if(confirmed){
					this.resendSalesPerson()
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

		resendSalesPerson() {
			this.submitted=true;	
			if (this.form.invalid) {
			return;
			}
			this.spinner.show();		     
			this.submitted=false;
			this.generateSalesService.resendSalesPersonRegistartion(this.generateSalesPerson.userEmail).subscribe((res) => {
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
		filterData($event: KeyboardEvent){
			if ($event.keyCode === 13) {
				if(this.searchText==''){
					this.paramFilter.searchText=null;
				}else{
					this.paramFilter.searchText=this.searchText;
				}

		this.param.pageIndex=0
		this.paginator.pageIndex=0;
		this.param.pageSize= this.paginator.pageSize;
		this.param.filter=this.paramFilter;
		this.getSalesPerson();
			}
		}

	getSalesPerson(){
		this.authService.checkLoginUserVlidaate();
		this.spinner.show();
		this.lastPageSize=this.param.pageSize;
		this.generateSalesService.getSalesPersonDetails(this.param).subscribe(data => {
			this.orginalFetchData=  Object.assign([],data.content);
			this.ELEMENT_DATA = Object.assign([],data.content);
			this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
			this.totalProduct=data.total;
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


  resend(element:any){
	this.confirmationDialogService.confirm('Confirmation!!', 'Would you like to resend the credential for '+element.fullName+' ?')
				.then(
				  (confirmed) =>{
				   if(confirmed){
					this.spinner.show();		     
			    this.generateSalesService.resendSalesPersonRegistartion(element.emailId).subscribe((res) => {
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
	  this.getSalesPerson();
  }

  pageChanged(event:any){
	this.dataSource=new MatTableDataSource<SalesData>();
	if(this.lastPageSize!=event.pageSize){
	this.paginator.pageIndex=0;
	event.pageIndex=0;
	}
	this.param.pageIndex=this.paginator.pageIndex;
	this.param.pageSize= event.pageSize;
	this.getSalesPerson();
	}
}
