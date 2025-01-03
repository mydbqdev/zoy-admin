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
import { FormBuilder } from '@angular/forms';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';
import { UserInfo } from 'src/app/common/shared/model/userinfo.service';
import { ConfigMasterService } from '../service/config-master-serive';
import { BeforeCheckInCancellationRefundModel, ConfigMasterModel, DataGroupingModel, SecurityDepositLimitsModel, SecurityDepositRefundModel, TokenDetailsModel } from '../models/config-master-model';


@Component({
  selector: 'app-configuration-master',
  templateUrl: './configuration-master.component.html',
  styleUrl: './configuration-master.component.css'
})
export class ConfigurationMasterComponent implements OnInit, AfterViewInit {
	
	public userNameSession: string = "";
	  errorMsg: any = "";
	  mySubscription: any;
	  isExpandSideBar:boolean=true;
	  @ViewChild(SidebarComponent) sidemenuComp;
	  public rolesArray: string[] = [];
	  submitted=false;
	  userInfo:UserInfo=new UserInfo();
	  tokenAdvancDisabled: boolean = true;
	  value: number = 20; 
	  tempValue: number = this.value; 
	  securityDepositLimitsDisabled: boolean = true;
	  dataGroupingDisabled:boolean = true;
	  beforeCheckInCRfModel:BeforeCheckInCancellationRefundModel = new BeforeCheckInCancellationRefundModel()
	  beforeCheckInCRfSaveVali:boolean=false;
	  securityDepositRefundDisabled: boolean = true;
	  configMasterModel :ConfigMasterModel =new ConfigMasterModel();
	  configMasterOrg :ConfigMasterModel =new ConfigMasterModel();

	  constructor(private route: ActivatedRoute, private router: Router,private formBuilder: FormBuilder, private http: HttpClient, private userService: UserService, private configMasterService :ConfigMasterService,
		  private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService, private confirmationDialogService:ConfirmationDialogService) {
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
		 
		  this.getConfigMasterDetails();
	  }
	  ngAfterViewInit() {
		  this.sidemenuComp.expandMenu(4);
		  this.sidemenuComp.activeMenu(4, 'configuration-master');
		  this.dataService.setHeaderName("Configuration Master");
	  }

	  collaspeListRight = [
		{ id: 1, name: 'right1', selected: false },
		{ id: 2, name: 'right2', selected: false },
	  ];
	  // Toggle the selected status for a button
	  collaspeExpandPanelRight(status: any,side:string): void {
		status.selected = !status.selected;
         if(side=='right1' && this.collaspeListRight[0].selected){
			this.collaspeListRight[1].selected=false;
			this.collaspeListRight[2].selected=false;
		 }
		 if(side=='right2' && this.collaspeListRight[1].selected){
			this.collaspeListRight[0].selected=false;
			this.collaspeListRight[2].selected=false;
		 }
		 
	  }
	  collaspeListNoticePeriod = [
		{ id: 1, name: 'right1', selected: false },
	  ];
	  // Toggle the selected status for a button
	  collaspeExpandNoticePeriod(status: any,side:string): void {
		status.selected = !status.selected;
         if(side=='right1' && this.collaspeListNoticePeriod[0].selected){
			this.collaspeListNoticePeriod[1].selected=false;
		 }
		 
	  }

	  collaspeListPromoCode = [
		{ id: 1, name: 'right1', selected: false },
		{ id: 2, name: 'right2', selected: false },
		{ id: 3, name: 'right3', selected: false },
        
	  ];
	  // Toggle the selected status for a button
	  collaspeExpandPromoCode(status: any,side:string): void {
		status.selected = !status.selected;
         if(side=='right1' && this.collaspeListPromoCode[0].selected){
			this.collaspeListPromoCode[1].selected=false;
			this.collaspeListPromoCode[2].selected=false;
		 }
		 if(side=='right2' && this.collaspeListPromoCode[1].selected){
			this.collaspeListPromoCode[0].selected=false;
			this.collaspeListPromoCode[2].selected=false;
		 }
		 if(side=='right3' && this.collaspeListPromoCode[2].selected){
			this.collaspeListPromoCode[0].selected=false;
			this.collaspeListPromoCode[1].selected=false;
			
		 }
		}

	 tokenAdvancSubmit() {
		this.authService.checkLoginUserVlidaate();
		if(this.configMasterModel.tokenDetails.fixedToken == 0 || this.configMasterModel.tokenDetails.variableToken == 0 || !this.configMasterModel.tokenDetails.fixedToken || !this.configMasterModel.tokenDetails.variableToken ){
			return
		}
		
		this.spinner.show();
		this.configMasterService.updateTokenAdvanceDetails(this.configMasterModel.tokenDetails).subscribe(res => {
			this.configMasterOrg.tokenDetails = Object.assign(new TokenDetailsModel(), res.data );
			this.configMasterModel.tokenDetails = JSON.parse(JSON.stringify(this.configMasterOrg.tokenDetails));
			this.tokenAdvancDisabled = true;
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
	
	  tokenAdvanceReset() {
		this.configMasterModel.tokenDetails = JSON.parse(JSON.stringify(this.configMasterOrg.tokenDetails));
		this.tokenAdvancDisabled = true;
	  }

	  securityDepositLimitsSubmit() {
		this.authService.checkLoginUserVlidaate();
		if(this.configMasterModel.depositDetails.maximumDeposit == 0 || this.configMasterModel.depositDetails.minimumDeposit == 0 || !this.configMasterModel.depositDetails.maximumDeposit || !this.configMasterModel.depositDetails.minimumDeposit ){
			return ;
		}
		
		this.spinner.show();
		this.configMasterService.updatesecurityDepositLimitsDetails(this.configMasterModel.depositDetails).subscribe(res => {
			this.configMasterOrg.depositDetails = Object.assign(new SecurityDepositLimitsModel(), res.data );
			this.configMasterModel.depositDetails = JSON.parse(JSON.stringify(this.configMasterOrg.depositDetails));
			this.securityDepositLimitsDisabled = true;
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
		
	securityDepositLimitsReset() {
			this.configMasterModel.depositDetails = JSON.parse(JSON.stringify(this.configMasterOrg.depositDetails));
			this.securityDepositLimitsDisabled = true;
		}

	trendingPGSubmit() {
		this.authService.checkLoginUserVlidaate();
		if(this.configMasterModel.dataGrouping.considerDays == 0 ||  !this.configMasterModel.dataGrouping.considerDays ){
			return ;
		}
		
		this.spinner.show();
		this.configMasterService.updateDataGroupingDetails(this.configMasterModel.dataGrouping).subscribe(res => {
			this.configMasterOrg.dataGrouping = Object.assign(new DataGroupingModel(), res.data );
			this.configMasterModel.dataGrouping = JSON.parse(JSON.stringify(this.configMasterOrg.dataGrouping));
			this.dataGroupingDisabled = true;
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
			

	  trendingPGReset() {
		this.configMasterModel.dataGrouping = JSON.parse(JSON.stringify(this.configMasterOrg.dataGrouping));
		this.dataGroupingDisabled = true;
	}		  
	beforeCheckInCRfSave(){
		this.beforeCheckInCRfSaveVali = true ;
		this.authService.checkLoginUserVlidaate();
		if(this.beforeCheckInCRfModel.daysBeforeCheckIn == 0 ||  !this.beforeCheckInCRfModel.daysBeforeCheckIn ||  this.beforeCheckInCRfModel.deductionPercentages==undefined || this.beforeCheckInCRfModel.deductionPercentages.toString() ==''){
			return ;
		}
		if(this.configMasterOrg.cancellationDetails.find(c=>c.daysBeforeCheckIn == this.beforeCheckInCRfModel.daysBeforeCheckIn) != undefined){
			this.notifyService.showInfo('This Cancellation and Refund Policy is already available for '+this.beforeCheckInCRfModel.daysBeforeCheckIn+' days.',"");
			return;
		}
		this.configMasterService.submitBeforeCheckInCRfDetails(this.beforeCheckInCRfModel).subscribe(res => {
			this.configMasterOrg.cancellationDetails = Object.assign([], res.data );
			this.configMasterModel.cancellationDetails = JSON.parse(JSON.stringify(this.configMasterOrg.cancellationDetails));
			this.beforeCheckInCRfSaveVali = false ;
			this.beforeCheckInCRfModel = new BeforeCheckInCancellationRefundModel();
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

	beforeCheckInCRfReset(i:number) {
		this.configMasterModel.cancellationDetails[i] = JSON.parse(JSON.stringify(this.configMasterOrg.cancellationDetails[i]));
	}

	beforeCheckInCRfUpDate(i:number){
		const model =this.configMasterModel.cancellationDetails[i] ;
		if(model.daysBeforeCheckIn == 0 ||  !model.daysBeforeCheckIn || model.deductionPercentages == undefined || model.deductionPercentages.toString() ==''	){
			return ;
		}

		if(this.configMasterOrg.cancellationDetails.find(c=>c.daysBeforeCheckIn == model.daysBeforeCheckIn && c.cancellationId != model.cancellationId ) != undefined){
			this.notifyService.showInfo('This Cancellation and Refund Policy is already available for '+model.daysBeforeCheckIn+' days.',"");
			return;
		}
	
		this.authService.checkLoginUserVlidaate();
		this.spinner.show();
		this.configMasterService.submitBeforeCheckInCRfDetails(model).subscribe(res => {
			this.configMasterOrg.cancellationDetails = Object.assign([], res.data );
			this.configMasterModel.cancellationDetails = JSON.parse(JSON.stringify(this.configMasterOrg.cancellationDetails));
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
		
	deleteRefundRule(data:any){
		this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want delete ?')
		.then(
		   (confirmed) =>{
			if(confirmed){
		     this.authService.checkLoginUserVlidaate();
			 this.spinner.show();		     
			 this.configMasterService.deleteRefundRule(data).subscribe(res => {
			 this.configMasterOrg.cancellationDetails = Object.assign([], res.data );
			 this.configMasterModel.cancellationDetails = JSON.parse(JSON.stringify(this.configMasterOrg.cancellationDetails));
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

	securityDepositRefundSubmit() {
		this.authService.checkLoginUserVlidaate();
		if(this.configMasterModel.refundRules.maximumDays == 0 || this.configMasterModel.refundRules.plotformCharges == 0 || !this.configMasterModel.refundRules.maximumDays || !this.configMasterModel.refundRules.plotformCharges ){
			return
		}
		
		this.spinner.show();
		this.configMasterService.updatesecurityDepositRefundDetails(this.configMasterModel.refundRules).subscribe(res => {
			this.configMasterOrg.refundRules = Object.assign(new SecurityDepositRefundModel(), res.data );
			this.configMasterModel.refundRules = JSON.parse(JSON.stringify(this.configMasterOrg.refundRules));
			this.securityDepositRefundDisabled = true;
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
	
	  securityDepositRefundReset() {
		this.configMasterModel.refundRules = JSON.parse(JSON.stringify(this.configMasterOrg.refundRules));
		this.securityDepositRefundDisabled = true;
	  }

		gstHidden: boolean = true; 
		gstValue: number = 10;
		tempGSTValue: number = this.gstValue; 

		editGST() {
			this.beforeCheckInCRfModel.daysBeforeCheckIn
			this.gstHidden = false;
		}
		submitGST() {
			this.gstValue = this.tempGSTValue;
			this.gstHidden = true;
		}
		resetGST() {
			this.tempGSTValue = this.gstValue;
			this.gstHidden = true;
		}


		amountHidden: boolean = true; 
		amountValue: number = 200; 
		tempAmountValue: number = this.amountValue; 

		editAmount() {
			this.amountHidden = false;
		}

		submitAmount() {
			this.amountValue = this.tempAmountValue;
			this.amountHidden = true;
		}

		resetAmount() {
			this.tempAmountValue = this.amountValue;
			this.amountHidden = true;
		}

		numberOnly(event): boolean {
			const charCode = (event.which) ? event.which : event.keyCode;
			if (charCode > 31 && (charCode < 48 || charCode > 57)) {
			  return false;
			}
			return true;
		   }

		percentageOnly(event): boolean {
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
		  
			if ((inputValue.match(/\./g) || []).length> 0 || parseFloat(inputValue) > 100 ) {
			  return false;
			}

			return true;
		  }

		getConfigMasterDetails(){
			this.authService.checkLoginUserVlidaate();
			this.spinner.show();
			this.configMasterService.getConfigMasterDetails().subscribe(res => {
			this.configMasterOrg = Object.assign(new ConfigMasterModel(), res.data );
			this.configMasterModel = JSON.parse(JSON.stringify(this.configMasterOrg));
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
					str = error.eror.message;
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