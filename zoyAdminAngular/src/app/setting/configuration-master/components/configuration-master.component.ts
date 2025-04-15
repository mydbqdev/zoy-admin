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
import { FormBuilder } from '@angular/forms';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';
import { UserInfo } from 'src/app/common/shared/model/userinfo.service';
import { ConfigMasterService } from '../service/config-master-serive';
import { BeforeCheckInCancellationRefundMainObjModel, BeforeCheckInCancellationRefundModel, ConfigMasterModel, DataGroupingModel, EarlyCheckOutRuleDetails, ForceCheckoutModel, GstChargesModel, NoRentalAgreement, OtherChargesModel, SecurityDepositDeadLineAndAutoCancellationModel, SecurityDepositLimitsModel, ShortTermMainModel, ShortTermModel, ShortTermRentingDuration, ShortTermSubModel, TokenDetailsModel} from '../models/config-master-model';
import { CdkDragDrop, CdkDropListGroup, moveItemInArray } from '@angular/cdk/drag-drop';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { DbSettingDataModel} from '../../db-master-configuration/models/db-setting-models';
import { DbMasterConfigurationService } from '../../db-master-configuration/services/db-master-configuration.service';


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
	  securityDepositLimitsDisabled: boolean = true;
	  dataGroupingDisabled:boolean = true;
	  beforeCheckInCRfModel:BeforeCheckInCancellationRefundModel = new BeforeCheckInCancellationRefundModel()
	  beforeCheckInCRfSaveVali:boolean=false;
	
	  configMasterModel :ConfigMasterModel =new ConfigMasterModel();
	  configMasterOrg :ConfigMasterModel =new ConfigMasterModel();
	  securityDepositRefundDisabled: boolean = true;
	  earlyCheckOutRulesDisabled: boolean = true;
	  securityDepositDeadLineDisabled: boolean = true;
	  autoCancellationDisabled: boolean = true;
	  otherChargesDisabled: boolean = true;
	  checkoutDeductionDisabled: boolean = true;
	  forceCheckoutDisabled: boolean = true;
	  gstChargesDisabled : boolean = true;
	  shortTermRentingDurationDisabled: boolean = true;
	  crpEffectiveDate:string='';
	  stpEffectiveDate:string='';

	  triggerCondition : {'id':number,'cond_name':string}[] = []; //['==','>=','<=','>','<','!='];
	  triggerOn : {'id':number,'trigger_on':string}[]=[];//['PaidAmount','Rent','PaidAmount & Rent']; 

	  displayedColumns: string[] = ['trigger_condition', 'before_checkin_days','trigger_value','deduction_percentage','action'];
	  dataSource = new MatTableDataSource<BeforeCheckInCancellationRefundModel>([]);
	  @ViewChild(CdkDropListGroup) listGroup: CdkDropListGroup<HTMLElement[]>;
	  beforeCheckInCRDetails: BeforeCheckInCancellationRefundModel[] = [];
	 
	  canSubmit:boolean = true;
	  canShortSubmit:boolean = true;
	  @ViewChild(MatTable) table: MatTable<BeforeCheckInCancellationRefundModel>;
	more:boolean=true;
	moreEarlyCheckout:boolean=true;
	popupMoreRecordHeader:string="";
	  constructor(private route: ActivatedRoute, private router: Router,private formBuilder: FormBuilder, private http: HttpClient, private userService: UserService, private configMasterService :ConfigMasterService,
		  private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService, private confirmationDialogService:ConfirmationDialogService,
		  private dbMasterConfigurationService:DbMasterConfigurationService
		) {
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
		  this.getTriggerCondition();
		  this.getTriggerOn();
		  this.getPGTypesDetails();
		  this.getDbSettingDetails();
	  }
	  ngAfterViewInit() {
		  this.sidemenuComp.expandMenu(4);
		  this.sidemenuComp.activeMenu(4, 'configuration-master');
		  this.dataService.setHeaderName("Configuration Master");
		  
	  }
	  settingType:string='';
	  pgTypes:DbSettingDataModel[]=[];
	  changeSettingType(){
		this.getBeforeCheckInCRDetails(this.settingType);
	  }

	  navigateInitialConfig(){
		if(this.rolesArray.includes('DB_MASTER_CONFIGURATION_WRITE')){
			this.router.navigate(['/db-master-configuration']);
		}else{
			this.notifyService.showInfo("Please contact a higher-level admin","You do not have permission.");
		}
	  }

	  getPGTypesDetails(){
		this.configMasterService.getPGTypesDetails().subscribe(res => {
		this.pgTypes = res;
		if(	this.pgTypes.length>0){
			this.settingType= this.pgTypes[0].pg_type_id;
			this.getBeforeCheckInCRDetails(this.settingType);
		}
		}, error => {
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
	  getConfigMasterDetails(){
		this.authService.checkLoginUserVlidaate();
		this.spinner.show();
		this.configMasterService.getConfigMasterDetails().subscribe(res => {
		this.configMasterOrg = new ConfigMasterModel();
		const keys = Object.keys(this.configMasterOrg);
		keys.forEach(key => {
			if(!res.data[key] || res.data[key]?.length ==undefined || res.data[key]?.length==0){
				switch (key) {
					case "depositDetails":
						this.configMasterOrg[key].push(new SecurityDepositLimitsModel());
					  break;
					case "tokenDetails":
						this.configMasterOrg[key].push(new TokenDetailsModel());
					  break;
					case "gstCharges":
						this.configMasterOrg[key].push(new GstChargesModel());
					  break;
					case "noRentalAgreement":
						this.configMasterOrg[key].push(new NoRentalAgreement());
					  break;
					case "securityDepositDeadLineDetails":
						this.configMasterOrg[key].push(new SecurityDepositDeadLineAndAutoCancellationModel());
					  break;
					case "cancellationAfterCheckInDetails":
						this.configMasterOrg[key].push(new SecurityDepositDeadLineAndAutoCancellationModel());
					  break;
					case "otherCharges":
						this.configMasterOrg[key].push(new OtherChargesModel());
					  break;
					case "forceCheckOut":
						this.configMasterOrg[key].push(new ForceCheckoutModel());
					  break;
					case "earlyCheckOutRuleDetails":
						this.configMasterOrg[key].push(new EarlyCheckOutRuleDetails());
					  break;
					case "dataGrouping":
						this.configMasterOrg[key].push(new DataGroupingModel());
					  break; 
					case "shortTermRentingDuration":
						this.configMasterOrg[key].push(new ShortTermRentingDuration());
					  break; 
  
				  }
		    }else if (res.data[key]) {
				this.configMasterOrg[key] = res.data[key];
			}			
		});

		this.configMasterModel = JSON.parse(JSON.stringify(this.configMasterOrg));
		this.configMasterModel.securityDepositDeadLineDetails[0].trigger_value="TotalPaidAmount";
		this.configMasterModel.earlyCheckOutRuleDetails[0].trigger_value="Rent";
		this.configMasterModel.cancellationAfterCheckInDetails[0].trigger_value="Rent";
		
		
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
	getTriggerCondition(){		     
		this.configMasterService.getTriggeredCond().subscribe(res => {
		this.triggerCondition = Object.assign([],res );
	  },error =>{
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
   
	getTriggerOn(){
	   this.configMasterService.getTriggerOn().subscribe(res => {
	   this.triggerOn = Object.assign([],res );
	 },error =>{
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

   getBeforeCheckInCRDetails(pgTypeId:string){
	const pgtype={'pgType':pgTypeId};
	this.configMasterService.getBeforeCheckInCRDetails(pgtype).subscribe(res => {
	console.log("!res.data && !res.data[0]",!res.data ,!res.data[0])
	this.cancellationBeforeCheckInDetailsOrg = [];
	this.beforeCheckInCRDatafReset();
	if(!res.data[0]){
		var model: BeforeCheckInCancellationRefundMainObjModel=new BeforeCheckInCancellationRefundMainObjModel();
		this.cancellationBeforeCheckInDetailsOrg.push(model);
	}else{
		this.cancellationBeforeCheckInDetailsOrg = res?.data;
	}
	this.getBeforeCheckInCRData();
		},error =>{
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

   

   invaliedEffectiveDate(key:string,newDate:string):boolean{
	if(newDate){
	 if(new Date(newDate).setHours(0, 0, 0, 0) < new Date().setHours(0, 0, 0, 0)){
		return true;
	  }
	  const rules = this.configMasterOrg[key];
	  const oldDate = rules.filter(d=>d.isApproved).sort((a, b) => new Date(b.effectiveDate).getTime() - new Date(a.effectiveDate).getTime()) .map(date=>date.effectiveDate)[0];
	 if(oldDate && new Date(newDate).setHours(0, 0, 0, 0) <= new Date(oldDate).setHours(0, 0, 0, 0)){
	   return true;
	  }

	 return false;
	}else{
		return true;
	}
   }
	
   isNotValidNumbernAndZero(value: any): boolean {
	return  (value === '' || value == undefined || value == null || isNaN(value) || value === false || value == 0);
  }
	  isNotValidNumber(value: any): boolean {
		return  (value === '' || value == undefined || value == null || isNaN(value) || (value === false && value !== 0));
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
	  
		if ((inputValue.match(/\./g) || []).length> 1 || parseFloat(inputValue) > 100 ) {
		  return false;
		}

		return true;
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

		// approveTheRule(ruleTitle:string,rule:any){
		// 	console.log("ruleTitle::",ruleTitle);
		// 	console.log("rule::",rule);

		// 	if(rule?.creater == this.userInfo.userEmail){
		// 		this.notifyService.showInfo("Rule creator cannot approve the Rule","")
		// 		return ;
		// 	}
		// 	if(this.validationAllData(ruleTitle)){
		// 		this.notifyService.showInfo("The rule is invalid, kindly review it.","");
		// 		return;
		// 	}else{
		// 	this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want Approve ?')
		// 	.then(
		// 	   (confirmed) =>{
		// 		if(confirmed){
		// 	this.spinner.show();
		// 	this.authService.checkLoginUserVlidaate();
		// 	this.configMasterService.updateTokenAdvanceDetails(this.configMasterModel.tokenDetails[0]).subscribe(res => {
		// 		this.configMasterOrg.tokenDetails = Object.assign(new TokenDetailsModel(), res.data );
		// 		this.configMasterModel.tokenDetails = JSON.parse(JSON.stringify(this.configMasterOrg.tokenDetails));
		// 		this.tokenAdvancDisabled = true;
		// 		this.notifyService.showSuccess("Token Advance has been updated successfully", "");
		// 		this.spinner.hide();
		// 		}, error => {
		// 		this.spinner.hide();
		// 		if(error.status == 0) {
		// 		  this.notifyService.showError("Internal Server Error/Connection not established", "");
		// 	   }else if(error.status==403){
		// 			this.router.navigate(['/forbidden']);
		// 		}else if (error.error && error.error.message) {
		// 			this.errorMsg = error.error.message;
		// 			console.log("Error:" + this.errorMsg);
		// 			this.notifyService.showError(this.errorMsg, "");
		// 		} else {
		// 			if (error.status == 500 && error.statusText == "Internal Server Error") {
		// 			this.errorMsg = error.statusText + "! Please login again or contact your Help Desk.";
		// 			} else {
		// 			let str;
		// 			if (error.status == 400) {
		// 				str = error.error.error;
		// 			} else {
		// 				str = error.error.message;
		// 				str = str.substring(str.indexOf(":") + 1);
		// 			}
		// 			console.log("Error:" ,str);
		// 			this.errorMsg = str;
		// 			}
		// 			if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
		// 			//this.notifyService.showError(this.errorMsg, "");
		// 		}
		// 		});	
		// 	}	
		// 	}).catch(
		// 		() => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
		// 	);	
		// 	}

		// 	}

		validationAllData(key:string) :boolean{
			switch (key) {
				case "tokenDetails":
					if(this.isNotValidNumber(this.configMasterModel.tokenDetails[0].fixedToken) || this.isNotValidNumber(this.configMasterModel.tokenDetails[0].variableToken ) || !this.configMasterModel.tokenDetails[0].effectiveDate){
						return true;
					}
					if(this.invaliedEffectiveDate('tokenDetails',this.configMasterModel.tokenDetails[0]?.effectiveDate)){
						return true;
					}
					if(Number(this.configMasterModel.tokenDetails[0].fixedToken) > Number(this.configMasterModel.depositDetails[0].maximumDeposit)){
						this.notifyService.showInfo("The token fixed amount should not be greater than the maximum security deposit.","")
						return true;
					}
				  break;
				case "depositDetails":
					if(this.isNotValidNumber(this.configMasterModel.depositDetails[0].maximumDeposit) || this.isNotValidNumber(this.configMasterModel.depositDetails[0].minimumDeposit) || !this.configMasterModel.depositDetails[0]?.effectiveDate ){
						return true;
					}
					if(this.invaliedEffectiveDate('depositDetails',this.configMasterModel.depositDetails[0]?.effectiveDate)){
						return true;
					}
					if(Number(this.configMasterModel.depositDetails[0].maximumDeposit) < Number(this.configMasterModel.depositDetails[0].minimumDeposit) ){
						this.notifyService.showInfo("The maximum security deposit should not be less than to the minimum security deposit.","")
						return true;
					}
					if(Number(this.configMasterModel.depositDetails[0].maximumDeposit) < Number(this.configMasterModel.tokenDetails[0].fixedToken) ){
						this.notifyService.showInfo("The maximum security deposit should not be less than to the token fixed amount.","")
						return true;
					}
				  break;
				case "gstCharges":
					if( this.isNotValidNumber(this.configMasterModel.gstCharges[0].monthlyRent) || this.isNotValidNumber(this.configMasterModel.gstCharges[0].cgstPercentage) 
						|| this.isNotValidNumber(this.configMasterModel.gstCharges[0].sgstPercentage) || this.isNotValidNumber(this.configMasterModel.gstCharges[0].igstPercentage)
						|| !this.configMasterModel.gstCharges[0]?.effectiveDate ){
						return true;
					}
					if(this.invaliedEffectiveDate('gstCharges',this.configMasterModel.gstCharges[0]?.effectiveDate)){
						return true;
					}
				  break;
				case "noRentalAgreement":
					if(this.isNotValidNumber(this.configMasterModel.noRentalAgreement[0].noRentalAgreementDays) || !this.configMasterModel.noRentalAgreement[0]?.effectiveDate){
						return true;
					 }
					 if(this.invaliedEffectiveDate('noRentalAgreement',this.configMasterModel.noRentalAgreement[0]?.effectiveDate)){
						 return true;
					 }
					 if(Number(this.configMasterModel.noRentalAgreement[0].noRentalAgreementDays) < Number(this.configMasterModel.shortTermRentingDuration[0].rentingDurationDays) ){
						 this.notifyService.showInfo("No Rental agreement upto should be greater than Short term duration.","");
						 return true;
					  }
				  break;
				case "securityDepositDeadLineDetails":
					if(this.isNotValidNumber(this.configMasterModel.securityDepositDeadLineDetails[0].auto_cancellation_day) || this.isNotValidNumber(this.configMasterModel.securityDepositDeadLineDetails[0].deduction_percentage) 
						|| !this.configMasterModel.securityDepositDeadLineDetails[0].trigger_condition|| !this.configMasterModel.securityDepositDeadLineDetails[0].trigger_value || !this.configMasterModel.securityDepositDeadLineDetails[0]?.effectiveDate){
						return true;
					}
					if(this.invaliedEffectiveDate('securityDepositDeadLineDetails',this.configMasterModel.securityDepositDeadLineDetails[0]?.effectiveDate)){
						return true;
					}
				  break;
				case "cancellationAfterCheckInDetails":
					if(this.isNotValidNumbernAndZero(this.configMasterModel.cancellationAfterCheckInDetails[0].auto_cancellation_day) || this.isNotValidNumber(this.configMasterModel.cancellationAfterCheckInDetails[0].deduction_percentage) 
						|| !this.configMasterModel.cancellationAfterCheckInDetails[0].trigger_condition|| !this.configMasterModel.cancellationAfterCheckInDetails[0].trigger_value || !this.configMasterModel.cancellationAfterCheckInDetails[0]?.effectiveDate){
						return true;
					}
					if(this.invaliedEffectiveDate('cancellationAfterCheckInDetails',this.configMasterModel.cancellationAfterCheckInDetails[0]?.effectiveDate)){
						return true;
					}
					if(this.configMasterModel.cancellationAfterCheckInDetails[0].auto_cancellation_day == 1 && this.configMasterModel.cancellationAfterCheckInDetails[0].trigger_condition === '<'){
						return true;
					}
				  break;
				case "otherCharges":
					if(this.isNotValidNumber(this.configMasterModel.otherCharges[0].ownerDocumentCharges) || this.isNotValidNumber(this.configMasterModel.otherCharges[0].tenantDocumentCharges) 
						|| this.isNotValidNumber(this.configMasterModel.otherCharges[0].ownerEkycCharges) || this.isNotValidNumber(this.configMasterModel.otherCharges[0].tenantEkycCharges) || !this.configMasterModel.otherCharges[0]?.effectiveDate ){
						return true;
					}
					if(this.invaliedEffectiveDate('otherCharges',this.configMasterModel.otherCharges[0]?.effectiveDate)){
						return true;
					}
				  break;
				case "forceCheckOut":
					if(this.isNotValidNumber(this.configMasterModel.forceCheckOut[0].forceCheckOutDays) || !this.configMasterModel.forceCheckOut[0]?.effectiveDate){
						return true;
					}
					if(this.invaliedEffectiveDate('forceCheckOut',this.configMasterModel.forceCheckOut[0]?.effectiveDate)){
						return true;
					}
				  break;
				case "earlyCheckOutRuleDetails":
					if(this.isNotValidNumbernAndZero(this.configMasterModel.earlyCheckOutRuleDetails[0].check_out_day) || this.isNotValidNumber(this.configMasterModel.earlyCheckOutRuleDetails[0].deduction_percentage) || !this.configMasterModel.earlyCheckOutRuleDetails[0].trigger_condition || !this.configMasterModel.earlyCheckOutRuleDetails[0].trigger_value ){
						return true;
					}
					if(this.invaliedEffectiveDate('earlyCheckOutRuleDetails',this.configMasterModel.earlyCheckOutRuleDetails[0]?.effectiveDate)){
						return true;
					}
					if(this.configMasterModel.earlyCheckOutRuleDetails[0].check_out_day == 1 && this.configMasterModel.earlyCheckOutRuleDetails[0].trigger_condition === '<'){
						return true;
					}
				  break;
				case "dataGrouping":
					if(this.isNotValidNumber(this.configMasterModel.dataGrouping[0].considerDays) || !this.configMasterModel.dataGrouping[0]?.effectiveDate){
						return true;
					}
					if(this.invaliedEffectiveDate('dataGrouping',this.configMasterModel.dataGrouping[0]?.effectiveDate)){
						return true;
					}
				  break; 
				case "shortTermRentingDuration":
					if(this.isNotValidNumber(this.configMasterModel.shortTermRentingDuration[0].rentingDurationDays) || !this.configMasterModel.shortTermRentingDuration[0]?.effectiveDate){
						return true;
					 }
					 if(this.invaliedEffectiveDate('shortTermRentingDuration',this.configMasterModel.shortTermRentingDuration[0]?.effectiveDate)){
						 return true;
					 }
					 if(Number(this.configMasterModel.noRentalAgreement[0].noRentalAgreementDays) < Number(this.configMasterModel.shortTermRentingDuration[0].rentingDurationDays) ){
						 this.notifyService.showInfo("Short term duration should not be greater than 'No Rental agreement upto'.","");
						 return true;
					  }
				  break; 

			  }
			  return false;
		}

	 tokenAdvancSubmit(task:string) {
		if( task === 'approve' && this.configMasterModel.tokenDetails[0]?.createdBy == this.userInfo.userEmail){
			this.notifyService.showInfo("Rule creator cannot approve the Rule","")
			return ;
		}
		if(this.validationAllData('tokenDetails')){
			this.notifyService.showInfo("The rule is invalid, kindly review it.","");
			return;
		}else{
		this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want '+task+' ?')
		.then(
		   (confirmed) =>{
			if(confirmed){
		if(task === 'approve'){
			this.configMasterModel.tokenDetails[0].isApproved=true;
		}else{
			if(this.configMasterModel.tokenDetails[0].isApproved){
				this.configMasterModel.tokenDetails[0].tokenId="";
			}
		}
		this.authService.checkLoginUserVlidaate();
		this.spinner.show();
		this.configMasterService.updateTokenAdvanceDetails(this.configMasterModel.tokenDetails[0]).subscribe(res => {
			this.configMasterOrg.tokenDetails = JSON.parse(JSON.stringify(res.data ));  
			this.configMasterModel.tokenDetails = JSON.parse(JSON.stringify(this.configMasterOrg.tokenDetails));
			this.tokenAdvancDisabled = true;
			this.notifyService.showSuccess(res.message, "");
			this.spinner.hide();
			}, error => {
			this.spinner.hide();
			if(error.status == 0) {
			  this.notifyService.showError("Internal Server Error/Connection not established", "");
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
			});	}	
		}).catch(
			() => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
		);	
		}				
	  }
	
	  tokenAdvanceReset() {
		this.configMasterModel.tokenDetails = JSON.parse(JSON.stringify(this.configMasterOrg.tokenDetails));
		this.tokenAdvancDisabled = true;
	  }
	  
	  securityDepositLimitsSubmit(task:string) {
		if( task === 'approve' && this.configMasterModel.depositDetails[0]?.createdBy == this.userInfo.userEmail){
			this.notifyService.showInfo("Rule creator cannot approve the Rule","")
			return ;
		}
		if(this.validationAllData('depositDetails')){
			this.notifyService.showInfo("The rule is invalid, kindly review it.","");
			return;
		}else{				
		this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want '+task+' ?')
		.then(
		   (confirmed) =>{
			if(confirmed){
				this.authService.checkLoginUserVlidaate();
				this.spinner.show();
				if(task === 'approve'){
					this.configMasterModel.depositDetails[0].isApproved=true;
				}else{
					if(this.configMasterModel.depositDetails[0].isApproved){
						this.configMasterModel.depositDetails[0].depositId="";
					}
				}
				this.configMasterService.updatesecurityDepositLimitsDetails(this.configMasterModel.depositDetails[0]).subscribe(res => {
					this.configMasterOrg.depositDetails =  JSON.parse(JSON.stringify( res.data));
					this.configMasterModel.depositDetails = JSON.parse(JSON.stringify(this.configMasterOrg.depositDetails));
					this.securityDepositLimitsDisabled = true;
					this.notifyService.showSuccess(res.message, "");
					this.spinner.hide();
					}, error => {
					this.spinner.hide();
					if(error.status == 0) {
					this.notifyService.showError("Internal Server Error/Connection not established", "");
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
	  	}
		
	securityDepositLimitsReset() {
		this.configMasterModel.depositDetails = JSON.parse(JSON.stringify(this.configMasterOrg.depositDetails));
		this.securityDepositLimitsDisabled = true;
	}

	gstChargesSubmit(task:string) {
		if( task === 'approve' && this.configMasterModel.gstCharges[0]?.createdBy == this.userInfo.userEmail){
			this.notifyService.showInfo("Rule creator cannot approve the Rule","")
			return ;
		}
		if(this.validationAllData('gstCharges')){
			this.notifyService.showInfo("The rule is invalid, kindly review it.","");
			return;
		}else{
		this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want '+task+' ?')
		.then(
		   (confirmed) =>{
			if(confirmed){
		this.spinner.show();
		if(task === 'approve'){
			this.configMasterModel.gstCharges[0].isApproved=true;
		}else{
			if(this.configMasterModel.gstCharges[0].isApproved){
				this.configMasterModel.gstCharges[0].rentId="";
			}
		}
		this.authService.checkLoginUserVlidaate();
		this.configMasterService.updategstChargesDetails(this.configMasterModel.gstCharges[0]).subscribe(res => {
			this.configMasterOrg.gstCharges = JSON.parse(JSON.stringify(res.data )); 
			this.configMasterModel.gstCharges = JSON.parse(JSON.stringify(this.configMasterOrg.gstCharges));
			this.gstChargesDisabled = true;
			this.notifyService.showSuccess(res.message, "");
			this.spinner.hide();
			}, error => {
			this.spinner.hide();
			if(error.status == 0) {
			  this.notifyService.showError("Internal Server Error/Connection not established", "");
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
			});	}	
		}).catch(
			() => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
		);	
	  }				
	  }
	
	  gstChargesReset() {
		this.configMasterModel.gstCharges = JSON.parse(JSON.stringify(this.configMasterOrg.gstCharges));
		this.gstChargesDisabled = true;
	  }
	securityDepositDeadLineSubmit(task:string) {
		this.table?.renderRows();
		if( task === 'approve' && this.configMasterModel.securityDepositDeadLineDetails[0]?.createdBy == this.userInfo.userEmail){
			this.notifyService.showInfo("Rule creator cannot approve the Rule","")
			return ;
		}
		if(this.validationAllData('securityDepositDeadLineDetails')){
			this.notifyService.showInfo("The rule is invalid, kindly review it.","");
			return;
		}else{
		this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want '+task+' ?')
		.then(
		   (confirmed) =>{
			if(confirmed){
		this.spinner.show();
		if(task === 'approve'){
			this.configMasterModel.securityDepositDeadLineDetails[0].isApproved=true;
		}else{
			if(this.configMasterModel.securityDepositDeadLineDetails[0].isApproved){
				this.configMasterModel.securityDepositDeadLineDetails[0].auto_cancellation_id="";
			}
		}
		this.authService.checkLoginUserVlidaate();	
		this.spinner.show();
		this.configMasterService.updateSecurityDepositDeadLineDetails(this.configMasterModel.securityDepositDeadLineDetails[0]).subscribe(res => {
			this.configMasterOrg.securityDepositDeadLineDetails = JSON.parse(JSON.stringify(res.data )); 
			this.configMasterModel.securityDepositDeadLineDetails = JSON.parse(JSON.stringify(this.configMasterOrg.securityDepositDeadLineDetails));
			this.securityDepositDeadLineDisabled = true;
			this.notifyService.showSuccess(res.message, "");
			this.spinner.hide();
			}, error => {
			this.spinner.hide();
			if(error.status == 0) {
			  this.notifyService.showError("Internal Server Error/Connection not established", "");
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
		}}).catch(
			() => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
		);	
		}						
	  }
	securityDepositDeadLineReset() {
		this.configMasterModel.securityDepositDeadLineDetails = JSON.parse(JSON.stringify(this.configMasterOrg.securityDepositDeadLineDetails));
		this.securityDepositDeadLineDisabled = true;
	}
	autoCancellationSubmit(task:string) {
		if( task === 'approve' && this.configMasterModel.cancellationAfterCheckInDetails[0]?.createdBy == this.userInfo.userEmail){
			this.notifyService.showInfo("Rule creator cannot approve the Rule","")
			return ;
		}
		if(this.validationAllData('cancellationAfterCheckInDetails')){
			this.notifyService.showInfo("The rule is invalid, kindly review it.","");
			return;
		}else{
		this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want '+task+' ?')
		.then(
		   (confirmed) =>{
			if(confirmed){
		if(task === 'approve'){
			this.configMasterModel.cancellationAfterCheckInDetails[0].isApproved=true;
		}else{
			if(this.configMasterModel.cancellationAfterCheckInDetails[0].isApproved){
				this.configMasterModel.cancellationAfterCheckInDetails[0].auto_cancellation_id="";
			}
		}
		this.authService.checkLoginUserVlidaate();
		this.spinner.show();
		this.configMasterService.updateAutoCancellationDetails(this.configMasterModel.cancellationAfterCheckInDetails[0]).subscribe(res => {
			this.configMasterOrg.cancellationAfterCheckInDetails = JSON.parse(JSON.stringify(res.data )); 
			this.configMasterModel.cancellationAfterCheckInDetails = JSON.parse(JSON.stringify(this.configMasterOrg.cancellationAfterCheckInDetails));
			this.autoCancellationDisabled = true;
			this.notifyService.showSuccess(res.message, "");
			this.spinner.hide();
			}, error => {
			this.spinner.hide();
			if(error.status == 0) {
			this.notifyService.showError("Internal Server Error/Connection not established", "");
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
	  }
	autoCancellationReset() {
		this.configMasterModel.cancellationAfterCheckInDetails = JSON.parse(JSON.stringify(this.configMasterOrg.cancellationAfterCheckInDetails));
		this.autoCancellationDisabled = true;
	}	
	otherChargesSubmit(task:string) {
		if( task === 'approve' && this.configMasterModel.otherCharges[0]?.createdBy == this.userInfo.userEmail){
			this.notifyService.showInfo("Rule creator cannot approve the Rule","")
			return ;
		}
		if(this.validationAllData('otherCharges')){
			this.notifyService.showInfo("The rule is invalid, kindly review it.","");
			return;
		}else{
		this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want '+task+' ?')
		.then(
		   (confirmed) =>{
			if(confirmed){
			if(task === 'approve'){
				this.configMasterModel.otherCharges[0].isApproved=true;
			}else{
				if(this.configMasterModel.otherCharges[0].isApproved){
					this.configMasterModel.otherCharges[0].otherChargesId="";
				}
			}
			this.authService.checkLoginUserVlidaate();
			this.spinner.show();
			this.configMasterService.updateOtherChargesDetails(this.configMasterModel.otherCharges[0]).subscribe(res => {
				this.configMasterOrg.otherCharges = JSON.parse(JSON.stringify( res.data ));
				this.configMasterModel.otherCharges = JSON.parse(JSON.stringify(this.configMasterOrg.otherCharges));
				this.otherChargesDisabled = true;
				this.notifyService.showSuccess(res.message, "");
				this.spinner.hide();
				}, error => {
				this.spinner.hide();
				if(error.status == 0) {
				this.notifyService.showError("Internal Server Error/Connection not established", "");
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
	  }
	  otherChargesReset() {
		this.configMasterModel.otherCharges = JSON.parse(JSON.stringify(this.configMasterOrg.otherCharges));
		this.otherChargesDisabled = true;
	}		  

	trendingPGSubmit(task:string) {
		if( task === 'approve' && this.configMasterModel.dataGrouping[0]?.createdBy == this.userInfo.userEmail){
			this.notifyService.showInfo("Rule creator cannot approve the Rule","")
			return ;
		}
		if(this.validationAllData('dataGrouping')){
			this.notifyService.showInfo("The rule is invalid, kindly review it.","");
			return;
		}else{
		this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want '+task+' ?')
		.then(
		   (confirmed) =>{
			if(confirmed){
			if(task === 'approve'){
				this.configMasterModel.dataGrouping[0].isApproved=true;
			}else{
				if(this.configMasterModel.dataGrouping[0].isApproved){
					this.configMasterModel.dataGrouping[0].dataGroupingId="";
				}
			}
			this.authService.checkLoginUserVlidaate();
			this.spinner.show();
			this.configMasterService.updateDataGroupingDetails(this.configMasterModel.dataGrouping[0]).subscribe(res => {
				this.configMasterOrg.dataGrouping = JSON.parse(JSON.stringify(res.data ));
				this.configMasterModel.dataGrouping = JSON.parse(JSON.stringify(this.configMasterOrg.dataGrouping));
				this.dataGroupingDisabled = true;
				this.notifyService.showSuccess(res.message, "");
				this.spinner.hide();
				}, error => {
				this.spinner.hide();
				if(error.status == 0) {
				this.notifyService.showError("Internal Server Error/Connection not established", "");
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
	  }				
			

	  trendingPGReset() {
		this.configMasterModel.dataGrouping = JSON.parse(JSON.stringify(this.configMasterOrg.dataGrouping));
		this.dataGroupingDisabled = true;
	}		  

	drop(event: CdkDragDrop<BeforeCheckInCancellationRefundModel[]>) {
		if (!this.dataSource || !this.dataSource.data) {
		  return; 
		}

		const previousIndex = event.previousIndex;
		const currentIndex = event.currentIndex;
	   const data = this.dataSource.data[previousIndex];
		if (event.container === event.previousContainer && (data.isDelete === undefined ? true :!data.isDelete)) {
		  moveItemInArray(this.beforeCheckInCRDetails, previousIndex, currentIndex);
		  let i=1;
		   this.dataSource.data.forEach(element => {
			  if(element.isDelete){
				return ;
			  }
			  element.priority = i++;
			  element.isEdit=true;
			  element.isConfirm=false;
			});
			this.canSubmit=false;
		} 
			this.table?.renderRows();
	  }

	  cancellationBeforeCheckInDetailsOrg:BeforeCheckInCancellationRefundMainObjModel[]=[];
	  cancellationBeforeCheckInDetails:BeforeCheckInCancellationRefundMainObjModel[]=[];
	  backUpBeforeCheckInCRList:BeforeCheckInCancellationRefundModel[]=[];
	  getBeforeCheckInCRData(){
		this.backUpBeforeCheckInCRList=[];
		this.cancellationBeforeCheckInDetails= JSON.parse(JSON.stringify(this.cancellationBeforeCheckInDetailsOrg));
			var main :BeforeCheckInCancellationRefundMainObjModel=this.cancellationBeforeCheckInDetails[0];
			main.zoy_before_check_in_cancellation_info?.forEach(element => {
			let sub : BeforeCheckInCancellationRefundModel = new BeforeCheckInCancellationRefundModel();
			sub.cancellation_id = element.cancellation_id; 
			sub.before_checkin_days = element.before_checkin_days; 
			sub.deduction_percentage = element.deduction_percentage; 
			sub.priority = element.priority;
			sub.trigger_condition = element.trigger_condition; 
			sub.trigger_on = element.trigger_on; 
			sub.trigger_value = element.trigger_value; 
			sub.isDelete = false; 

			this.backUpBeforeCheckInCRList.push(sub);
		});

		  this.crpEffectiveDate = this.cancellationBeforeCheckInDetails[0]?.effectiveDate;
		  this.beforeCheckInCRDetails=JSON.parse(JSON.stringify(this.backUpBeforeCheckInCRList));
		  this.dataSource = new MatTableDataSource<BeforeCheckInCancellationRefundModel>(this.beforeCheckInCRDetails);
		  this.table?.renderRows();
		}

	  beforeCheckInCRDatafReset(){
		this.beforeCheckInCRfSaveVali = false ;
		this.beforeCheckInCRfModel = new BeforeCheckInCancellationRefundModel();
		this.beforeCheckInCRfModel.trigger_value='TotalPaidAmount';
		this.canSubmit=true;
		this.crpEffectiveDate = this.cancellationBeforeCheckInDetails[0]?.effectiveDate;
		this.beforeCheckInCRDetails=JSON.parse(JSON.stringify(this.backUpBeforeCheckInCRList));
		this.dataSource = new MatTableDataSource<BeforeCheckInCancellationRefundModel>(this.beforeCheckInCRDetails);
	  }
	  		
	  checkDuplicateBCCR(row):boolean{
		const model= this.beforeCheckInCRDetails.filter(data=>
			data.cancellation_id != row.cancellation_id 
			&& data.trigger_condition == row.trigger_condition 
			&& data.before_checkin_days == row.before_checkin_days 
			&& data.trigger_value == row.trigger_value 
			&& data.deduction_percentage == row.deduction_percentage
			&& !data.isDelete 
		)
		if(model.length>0){
			this.notifyService.showInfo("This Refund Policy is already available.","");
			return true;
		}else{
			return false;
		}

	  }
	beforeCheckInCRfAdd(){
		this.beforeCheckInCRfSaveVali = true ;
		this.beforeCheckInCRfModel.priority = this.beforeCheckInCRDetails.length+1;
		if(!this.beforeCheckInCRfModel.trigger_condition ||  !this.beforeCheckInCRfModel.before_checkin_days ||  !this.beforeCheckInCRfModel.trigger_value ||  !this.beforeCheckInCRfModel.deduction_percentage){
			return ;
		}
		if(this.checkDuplicateBCCR(this.beforeCheckInCRfModel)){
			return;
		}
		this.canSubmit=false;
		this.beforeCheckInCRDetails.push(JSON.parse(JSON.stringify(this.beforeCheckInCRfModel)));
		this.dataSource = new MatTableDataSource<BeforeCheckInCancellationRefundModel>(this.beforeCheckInCRDetails);
		this.beforeCheckInCRfSaveVali = false ;
		this.beforeCheckInCRfModel = new BeforeCheckInCancellationRefundModel();
		this.beforeCheckInCRfModel.trigger_value="TotalPaidAmount";
	}  

	multirullsEffectiveDateValidation(newDate:string):boolean{
	  if(new Date(newDate).setHours(0, 0, 0, 0) < new Date().setHours(0, 0, 0, 0)){
		return true;
	  }
	  return false;
	}
	reason:string='';
	
 beforeCheckInCRfUpDate(task:string){
	if(!this.crpEffectiveDate || this.multirullsEffectiveDateValidation(this.crpEffectiveDate) ){
		return;
	}
	
	var payload :BeforeCheckInCancellationRefundMainObjModel = JSON.parse(JSON.stringify(this.cancellationBeforeCheckInDetails[0]));
	payload.effectiveDate = this.crpEffectiveDate;
	if(task === 'approve'){
		payload.isApproved=true;
	}else{
		payload.iscreate = payload.isApproved ;
	}
	if( task === 'approve' && payload?.createdBy == this.userInfo.userEmail){
		this.notifyService.showInfo("Rule creator cannot approve the Rule","")
		return ;
	}

	const compareIndex = this.cancellationBeforeCheckInDetailsOrg[0].isApproved ? 0 : 1;
	const selectedDate = new Date(payload.effectiveDate).setHours(0, 0, 0, 0);
	if(this.cancellationBeforeCheckInDetailsOrg[compareIndex].effectiveDate){
		const existingDate = new Date(this.cancellationBeforeCheckInDetailsOrg[compareIndex].effectiveDate).setHours(0, 0, 0, 0);	
		 if ( selectedDate < new Date().setHours(0, 0, 0, 0) || (selectedDate <= existingDate && existingDate )) {
			this.notifyService.showInfo("The effective date must be after the last rule's effective date.", "");
			return;
		}
	 }

	const filteredDetails = this.beforeCheckInCRDetails.filter(item => !item.isDelete);
	if(filteredDetails.length == 0){
		this.notifyService.showInfo("","Please add at least one policy.");
         return;
	}
	const duplicateBCCR = filteredDetails.reduce((acc, data, index, self) => {
		const duplicateIndex = self.findIndex((policy) =>
 			policy.trigger_condition == data.trigger_condition &&
			policy.before_checkin_days == data.before_checkin_days &&
			policy.trigger_value == data.trigger_value &&
			policy.deduction_percentage == data.deduction_percentage 
		);

		if (duplicateIndex != index && !acc.includes(index)) {
			acc.push(index); 
		}
		return acc;
	}, []);
	if (duplicateBCCR.length > 0) {
		const indexs = duplicateBCCR.join(', '); 
		this.notifyService.showError("Order "+indexs+" are duplicate roles, please check.","");
		return
	}
	
	
	const list = payload.iscreate ? this.beforeCheckInCRDetails.filter(item => !item.isDelete) : this.beforeCheckInCRDetails ;
	payload.pgType = this.settingType;
	payload.ZoyBeforeCheckInCancellationInfo = list;
	console.log("beforeCheckInCRDetails",this.beforeCheckInCRDetails);
	console.log("payload>>",payload);
	this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want '+(task === 'approve' ? 'Approve':(payload.iscreate?'Create':'Update') ) +' ?')
		.then(
		   (confirmed) =>{
			if(confirmed){
			this.authService.checkLoginUserVlidaate();
			this.spinner.show();
			this.configMasterService.submitBeforeCheckInCRfDetails(payload).subscribe(res => {
				console.log("res>>>",res)
			this.changeSettingType();
			this.canSubmit = true;
			this.getBeforeCheckInCRData();
			this.notifyService.showSuccess("Before check in has been updated successfully", "");
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

	beforeCheckInCRfModify(row:any){
		if(!row.trigger_condition ||  !row.before_checkin_days ||  !row.trigger_value ||  !row.deduction_percentage){
			return ;
		}
		if(this.checkDuplicateBCCR(row)){
			return;
		}
		row.isEdit = true;
		row.isConfirm=false;
		this.dataSource.data = this.beforeCheckInCRDetails;
		this.canSubmit=false;
	}

	undoEditItem(row: BeforeCheckInCancellationRefundModel,i:number) {
		const data = this.backUpBeforeCheckInCRList.filter((d)=>d.cancellation_id === row.cancellation_id ).map(element => ({ ...element }));;
		if(data !=null && data.length >0 ){
			row.cancellation_id= data[0].cancellation_id;
			row.trigger_condition = data[0].trigger_condition;
			row.before_checkin_days =data[0].before_checkin_days;
			row.deduction_percentage =data[0].deduction_percentage;
			row.priority =data[0].priority;
			row.trigger_value = data[0].trigger_value;
			row.isDelete = false;
		}
		row.isEdit = false;
		row.isConfirm=false;
		this.beforeCheckInCRDetails[i] = row ;
		this.dataSource.data[i] = row;
		this.canSubmit=false;
	  }

	beforeCheckInCRfDelete(row: BeforeCheckInCancellationRefundModel,n:number) {
		row.isDelete = true;
		row.isEdit = false;
		if(!row.cancellation_id){
			this.beforeCheckInCRDetails.splice(n, 1)
		}
		this.dataSource.data = this.beforeCheckInCRDetails;
		let i=1;
		this.dataSource.data.forEach(element => {
		  if(element.isDelete){
			return ;
		  }
		//   element.isEdit = true;
		  element.isConfirm=false;
		  element.priority = i++;
		});
		this.canSubmit=false;
	  }
	
	undoDelete(item: BeforeCheckInCancellationRefundModel) {
		item.isDelete = false;
		this.dataSource.data = this.beforeCheckInCRDetails;
		let i=1;
		this.dataSource.data.forEach(element => {
		  if(element.isDelete){
			return ;
		  }
		  element.priority = i++;
		});
		this.canSubmit=false;
	  }
	
	  earlyCheckOutRulesSubmit(task:string) {
		if( task === 'approve' && this.configMasterModel.earlyCheckOutRuleDetails[0]?.createdBy == this.userInfo.userEmail){
			this.notifyService.showInfo("Rule creator cannot approve the Rule","")
			return ;
		}
		if(this.validationAllData('earlyCheckOutRuleDetails')){
			this.notifyService.showInfo("The rule is invalid, kindly review it.","");
			return;
		}else{
		this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want '+task+' ?')
		.then(
		   (confirmed) =>{
			if(confirmed){
			if(task === 'approve'){
				this.configMasterModel.earlyCheckOutRuleDetails[0].isApproved=true;
			}else{
				if(this.configMasterModel.earlyCheckOutRuleDetails[0].isApproved){
					this.configMasterModel.earlyCheckOutRuleDetails[0].early_check_out_id="";
				}
			}
			
			this.authService.checkLoginUserVlidaate();
				this.spinner.show();
				this.configMasterService.updateEarlyCheckOutRulesdDetails(this.configMasterModel.earlyCheckOutRuleDetails[0]).subscribe(res => {
					this.configMasterOrg.earlyCheckOutRuleDetails = JSON.parse(JSON.stringify(res.data ));
					this.configMasterModel.earlyCheckOutRuleDetails = JSON.parse(JSON.stringify(this.configMasterOrg.earlyCheckOutRuleDetails));
					this.earlyCheckOutRulesDisabled = true;
					this.notifyService.showSuccess(res.message, "");
					this.spinner.hide();
					}, error => {
					this.spinner.hide();
					if(error.status == 0) {
					this.notifyService.showError("Internal Server Error/Connection not established", "");
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
	  }

	earlyCheckOutRuleReset() {
		this.configMasterModel.earlyCheckOutRuleDetails = JSON.parse(JSON.stringify(this.configMasterOrg.earlyCheckOutRuleDetails));
		this.earlyCheckOutRulesDisabled = true;
	  }
	  forceCheckoutSubmit(task:string): void {
		if( task === 'approve' && this.configMasterModel.forceCheckOut[0]?.createdBy == this.userInfo.userEmail){
			this.notifyService.showInfo("Rule creator cannot approve the Rule","")
			return ;
		}
		if(this.validationAllData('forceCheckOut')){
			this.notifyService.showInfo("The rule is invalid, kindly review it.","");
			return;
		}else{
		this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want '+task+' ?')
		.then(
		   (confirmed) =>{
			if(confirmed){
			if(task === 'approve'){
				this.configMasterModel.forceCheckOut[0].isApproved=true;
			}else{
				if(this.configMasterModel.forceCheckOut[0].isApproved){
					this.configMasterModel.forceCheckOut[0].forceCheckOutId="";
				}
			}
			this.authService.checkLoginUserVlidaate();
			this.spinner.show();
			this.configMasterService.updateForceCheckOutDetails(this.configMasterModel.forceCheckOut[0]).subscribe(res => {
				this.configMasterOrg.forceCheckOut = JSON.parse(JSON.stringify(res.data));
				this.configMasterModel.forceCheckOut = JSON.parse(JSON.stringify(this.configMasterOrg.forceCheckOut));
				this.forceCheckoutDisabled = true;
				this.notifyService.showSuccess(res.message, "");
				this.spinner.hide();
				}, error => {
				this.spinner.hide();
				if(error.status == 0) {
				this.notifyService.showError("Internal Server Error/Connection not established", "");
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
	  }
	
	  forceCheckoutReset(): void {
		this.configMasterModel.forceCheckOut = JSON.parse(JSON.stringify(this.configMasterOrg.forceCheckOut));
		this.forceCheckoutDisabled = true;
	  }

		onDropdownChange(slab:any) {
			if (slab.slabFrom === 'Input') {
				slab.showInputBox = true;  
			  } else {
				slab.showInputBox = false;   
			  }
		  }

		  shortTermRentingReset(i:number): void {
			this.configMasterModel.shortTerm[i] = JSON.parse(JSON.stringify(this.configMasterOrg.shortTerm[i]));
		}

		  shortTermRentingSubmit(shortTerm:ShortTermModel): void {
			if(this.isNotValidNumber(shortTerm.percentage)){
				return ;
			}
			
			this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want Update ?')
			.then(
			   (confirmed) =>{
				if(confirmed){
					this.authService.checkLoginUserVlidaate();
					this.spinner.show();
					this.configMasterService.updateShortTermRentingDuratioDetails(shortTerm).subscribe(res => {
						this.configMasterOrg.shortTerm = Object.assign([], res.data );
						this.configMasterModel.shortTerm = JSON.parse(JSON.stringify(this.configMasterOrg.shortTerm));
						this.notifyService.showSuccess("Short term Packages Configuration has been updated successfully", "");
						this.spinner.hide();
						}, error => {
						this.spinner.hide();
						if(error.status == 0) {
						this.notifyService.showError("Internal Server Error/Connection not established", "");
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

		shortTermRentingDurationSubmit(task:string): void {
			if( task === 'approve' && this.configMasterModel.shortTermRentingDuration[0]?.createdBy == this.userInfo.userEmail){
				this.notifyService.showInfo("Rule creator cannot approve the Rule","")
				return ;
			}
			if(this.validationAllData('shortTermRentingDuration')){
				this.notifyService.showInfo("The rule is invalid, kindly review it.","");
				return;
			}else{
			this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want '+task+' ?')
			.then(
			   (confirmed) =>{
				if(confirmed){
				if(task === 'approve'){
					this.configMasterModel.shortTermRentingDuration[0].isApproved=true;
				}else{
					if(this.configMasterModel.shortTermRentingDuration[0].isApproved){
						this.configMasterModel.shortTermRentingDuration[0].rentingDurationId="";
					}
				}
				this.authService.checkLoginUserVlidaate();
				this.spinner.show();
				this.configMasterService.updateShortTermRentingDuration(this.configMasterModel.shortTermRentingDuration[0]).subscribe(res => {
					this.configMasterOrg.shortTermRentingDuration = JSON.parse(JSON.stringify(res.data));
					this.configMasterModel.shortTermRentingDuration = JSON.parse(JSON.stringify(this.configMasterOrg.shortTermRentingDuration));
					this.shortTermRentingDurationDisabled = true;
					this.notifyService.showSuccess(res.message, "");
					this.spinner.hide();
					}, error => {
					this.spinner.hide();
					if(error.status == 0) {
					this.notifyService.showError("Internal Server Error/Connection not established", "");
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
		  }
		
	 shortTermRentingDurationReset(): void {
			this.configMasterModel.shortTermRentingDuration = JSON.parse(JSON.stringify(this.configMasterOrg.shortTermRentingDuration));
			this.shortTermRentingDurationDisabled = true;
		  }

		  noRentalAgreementDisabled:boolean=true;
		  noRentalAgreementSubmit(task:string): void {
			if( task === 'approve' && this.configMasterModel.noRentalAgreement[0]?.createdBy == this.userInfo.userEmail){
				this.notifyService.showInfo("Rule creator cannot approve the Rule","")
				return ;
			}
			if(this.validationAllData('noRentalAgreement')){
				this.notifyService.showInfo("The rule is invalid, kindly review it.","");
				return;
			}else{
			this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want '+task+' ?')
			.then(
			   (confirmed) =>{
				if(confirmed){
				if(task === 'approve'){
					this.configMasterModel.noRentalAgreement[0].isApproved=true;
				}else{
					if(this.configMasterModel.noRentalAgreement[0].isApproved){
						this.configMasterModel.noRentalAgreement[0].noRentalAgreementId="";
					}
				}
				this.authService.checkLoginUserVlidaate();
				this.spinner.show();
				this.configMasterService.updateNoRentalAgreement(this.configMasterModel.noRentalAgreement[0]).subscribe(res => {
					this.configMasterOrg.noRentalAgreement = JSON.parse(JSON.stringify(res.data));
					this.configMasterModel.noRentalAgreement = JSON.parse(JSON.stringify(this.configMasterOrg.noRentalAgreement));
					this.noRentalAgreementDisabled = true;
					this.notifyService.showSuccess(res.message, "");
					this.spinner.hide();
					}, error => {
					this.spinner.hide();
					if(error.status == 0) {
					this.notifyService.showError("Internal Server Error/Connection not established", "");
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
		  }
		
		  noRentalAgreementReset(): void {
			this.configMasterModel.noRentalAgreement = JSON.parse(JSON.stringify(this.configMasterOrg.noRentalAgreement));
			this.noRentalAgreementDisabled = true;
		  }	 
		  
		   shortTermData:ShortTermSubModel = new ShortTermSubModel();
	       shortTermDataList:ShortTermSubModel[] = [];		
		  
			convertToNumber(value: any): number {
			  return Number(value);
			}
			isUpdateShortTerm:boolean=false;
			backUpshortTermData :ShortTermMainModel[]=[];
		    getDbSettingDetails(){
			   this.authService.checkLoginUserVlidaate();
			   this.backUpshortTermData=[];
			   this.configMasterService.getShortTermData().subscribe(res => {
				console.log("data",res)
			   if(res.data && res.data?.length>0 && res.data[0]?.zoy_short_term_dto_info?.length>0){
				this.backUpshortTermData=  res.data;
			   }else{
				var model = new ShortTermMainModel();
				model.zoy_short_term_dto_info.push(new ShortTermSubModel());
				this.backUpshortTermData.push(model);
			   }
			   this.getShortTermList();
			   this.isUpdateShortTerm=false;
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
			 shortTermDataMainList :ShortTermMainModel[]=[];
			 shortTermDataSubList:ShortTermSubModel[]=[];
			 backUpShortTermDataSubList:ShortTermSubModel[]=[];
			   getShortTermList(){
				this.backUpShortTermDataSubList=[];
				this.shortTermDataMainList = JSON.parse(JSON.stringify(this.backUpshortTermData));
				var main=this.shortTermDataMainList[0];
				 main.zoy_short_term_dto_info.forEach(element => {
				  let model : ShortTermSubModel = new ShortTermSubModel();
				  model.short_term_id= element.short_term_id; 
				  model.start_day = element.start_day; 
				  model.end_day = element.end_day;  
				  model.isDelete = false; 
			 
				  this.backUpShortTermDataSubList.push(model);
				});
				  this.shortTermDataList=JSON.parse(JSON.stringify(this.backUpShortTermDataSubList));
				  this.stpEffectiveDate=this.shortTermDataMainList[0].effectiveDate;
				}

				shortTermDataReset(){
					this.canShortSubmit=true;
					this.stpEffectiveDate=this.shortTermDataMainList[0].effectiveDate;
					this.shortTermDataList=JSON.parse(JSON.stringify(this.backUpShortTermDataSubList));
				  }
		  
				addShortTermVali:boolean=false
			 addShortTerm() {
			  this.addShortTermVali = true;
			  console.log("this.shortTermData>>",this.shortTermData)
			  if(!this.shortTermData.start_day|| Number(this.shortTermData.start_day)===0 
				  || !this.shortTermData.end_day|| Number(this.shortTermData.end_day)===0
				  || Number(this.shortTermData.start_day) >= Number(this.shortTermData.end_day)
				  || !this.shortTermData.percentage
				){
					return;
				  }
			  this.shortTermDataList.push(JSON.parse(JSON.stringify(this.shortTermData)));
			  this.addShortTermVali = false;
			  this.canShortSubmit = false;
			  this.shortTermData = new ShortTermSubModel();
			 }
		  
			 modifyShortTerm(shortTerm:any,i:number) {
				console.log("shortTerm>>",shortTerm)
			   if(!shortTerm.start_day || Number(shortTerm.start_day)===0 
				  || !shortTerm.end_day || Number(shortTerm.end_day)===0
				  || Number(shortTerm.start_day) >= Number(shortTerm.end_day)
				  || !shortTerm.percentage
				 ){
					console.log(!shortTerm.start_day , Number(shortTerm.start_day)===0 
					, !shortTerm.end_day , Number(shortTerm.end_day)===0
					, Number(shortTerm.start_day) >= Number(shortTerm.end_day)
					, !shortTerm.percentage)
				return;
			  }
			  shortTerm.isEdit = true;
			  shortTerm.isConfirm = false ;
			  this.shortTermDataList[i]=shortTerm;
			  console.log(" this.shortTermDataList", this.shortTermDataList)
			}
			
			removeShortTerm(shortTerm) {
			  shortTerm.isDelete = true;
			}
			
			undoShortTermDelete(shortTerm) {
			  shortTerm.isDelete = false;
			}
			
			undoEditShortTermItem(i:number) {
			  this.shortTermDataList[i]=JSON.parse(JSON.stringify(this.backUpShortTermDataSubList[i]));
			}
		  
			shortTermDataListReset(){
			  this.shortTermDataList=JSON.parse(JSON.stringify(this.backUpShortTermDataSubList));
			}
			submitShortTerm:boolean = false;
			submitShortTermData(task:string) {  
			 this.submitShortTerm = true; 
			 if(!this.stpEffectiveDate || this.multirullsEffectiveDateValidation(this.stpEffectiveDate) ){
				return;
			  }
			  let finalSubmitShortList = [];
			  this.submitShortTerm = true;
			  let startDay = 99999;
			  let endDay = 0;
			 
			  for (let i = 0; i < this.shortTermDataList.length; i++) {
				const term = this.shortTermDataList[i];
		  
				if (!term.isDelete) {
				  startDay = startDay>term.start_day?term.start_day:startDay ;
				  endDay = endDay>term.end_day?endDay:term.end_day ;
		  
				  if (term.isConfirm) {
					this.notifyService.showWarning("Save if term is being edited.","")
					return; 
				  }
		   
				  for (let j = i + 1; j < this.shortTermDataList.length; j++) {
					const otherTerm = this.shortTermDataList[j];
					
					if (!(Number(term.end_day) < Number(otherTerm.start_day) || Number(term.start_day) > Number(otherTerm.end_day))) {
					  this.notifyService.showWarning("The Short term duration period must not Overlapp.","")
					  return;
					}
					
				  }
		  
				//   const ranges = this.shortTermDataList.filter(d=> Number(d.start_day) == (Number(term.end_day)+1));
				//   console.log("ranges",ranges);
				//   if( ranges.length === 0 ){
				// 	 this.notifyService.showWarning('The Short term duration period must be within the defined ranges of 1-'+endDay+' days.',"")
				// 	 return;
				//    }
		  
				  finalSubmitShortList.push(term);
				}
			  }
			
			  if (finalSubmitShortList.length < 1) {
				this.notifyService.showWarning("Please add durations","");
				return;
			  }
			 
			  if (JSON.stringify(finalSubmitShortList) === JSON.stringify(this.backUpShortTermDataSubList)) {
				this.notifyService.showInfo("Short term slabs details are already up to date.", "");
				return;
			  }
			var payload :ShortTermMainModel = JSON.parse(JSON.stringify(this.backUpshortTermData[0]));
			payload.effectiveDate = this.crpEffectiveDate;
			if(task === 'approve'){
				payload.isApproved=true;
			}else{
				payload.iscreate = payload.isApproved ;
			}
			if( task === 'approve' && payload?.createdBy == this.userInfo.userEmail){
				this.notifyService.showInfo("Rule creator cannot approve the Rule","")
				return ;
			}

			const compareIndex = this.backUpshortTermData[0].isApproved ? 0 : 1;
			const selectedDate = new Date(payload.effectiveDate).setHours(0, 0, 0, 0);
			if(this.backUpshortTermData[compareIndex].effectiveDate){
				const existingDate = new Date(this.backUpshortTermData[compareIndex].effectiveDate).setHours(0, 0, 0, 0);	
				if ( selectedDate < new Date().setHours(0, 0, 0, 0) || (selectedDate <= existingDate && existingDate )) {
					this.notifyService.showInfo("The effective date must be after the last rule's effective date.", "");
					return;
				}
			}
			payload.zoy_short_term_dto_info=finalSubmitShortList;
			  this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want Update ?')
			  .then(
				 (confirmed) =>{
				if(confirmed){
					this.spinner.show();
					this.configMasterService.submitShortTermData(payload).subscribe(data => {
					this.getShortTermList();
					this.submitShortTerm = false;
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
			  }).catch(
				() => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
			  );	   
			}

			
			   
  }  