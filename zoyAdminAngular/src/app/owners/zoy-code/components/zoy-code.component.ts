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
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { GenerateZoyCodeService } from '../../service/zoy-code.service';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';

@Component({
  selector: 'app-zoy-code',
  templateUrl: './zoy-code.component.html',
  styleUrl: './zoy-code.component.css'
})
export class ZoyCodeComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = ['zoy_code', 'owner_name', 'email_id', 'mobile_no','created_date', 'status','action'];
  public ELEMENT_DATA:ZoyData[]=[];
  orginalFetchData:ZoyData[]=[];
  searchText:string='';
  dataSource:MatTableDataSource<ZoyData>=new MatTableDataSource<ZoyData>();
  columnSortDirectionsOg: { [key: string]: string | null } = {
    zoy_code: null,
    owner_name: null,
    email_id: null,
    created_date: null,
	status: null
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

	// Method to update the selected button
	selectButton(button: string): void {
	  this.selectedModel = button;
	  if(this.selectedModel =='generated'){
		 this.getZoyCodeDetails();
		 this.submitted=false;
		 this.form.reset();
	  }else{
		this.searchText='';
		this.filterData();
	  }

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
		    contactNumber: ['', [Validators.required]],
			userEmail: ['', [
			  Validators.required,
			  Validators.pattern(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/)
			]],
		  });
	}
	ngAfterViewInit() {
		this.sidemenuComp.expandMenu(2);
		this.sidemenuComp.activeMenu(2, 'zoy-code');
		this.dataService.setHeaderName("Zoy Code");
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
		if (this.form.invalid) {
		return;
		}
		this.spinner.show();		     
		this.submitted=false;
		this.generateZoyCodeService.generateOwnerCode(this.generateZCode).subscribe((res) => {
			this.notifyService.showSuccess(res.message, "");			
			this.spinner.hide();
			this.form.reset();
		  },error =>{
			this.spinner.hide();
			console.log("error.error",error)
			if(error.status == 0) {
				this.notifyService.showError("Internal Server Error/Connection not established", "")
			 }else if(error.status==409){
				this.confirmationDialogService.confirm('Confirmation!!', 'A Zoycode has already been generated for this email Id, Would you like to resend the code?')
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
			  str=error.error;
			  }else{
				str=error.message;
				str=str.substring(str.indexOf(":")+1);
			  }
			  console.log("Error:"+str);
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
				  str=error.error;
				  }else{
					str=error.message;
					str=str.substring(str.indexOf(":")+1);
				  }
				  console.log("Error:"+str);
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
				str = error.error;
			} else {
				str = error.message;
				str = str.substring(str.indexOf(":") + 1);
			}
			console.log("Error:" + str);
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
				  str=error.error;
				  }else{
					str=error.message;
					str=str.substring(str.indexOf(":")+1);
				  }
				  console.log("Error:"+str);
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
}
