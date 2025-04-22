import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
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
import { BeforeCheckInCancellationRefundMainObjModel, BeforeCheckInCancellationRefundModel, ConfigMasterModel, ConfigMasterObjModel,ShortTermMainModel, ShortTermSubModel} from '../models/config-master-model';
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
	  comments:string='';
	  @ViewChild('closeApproveRejectModel') closeApproveRejectModel: ElementRef;
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
			this.setRuleData(key,res.data[key]);
		});
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

    editConfigMaster:ConfigMasterObjModel=new ConfigMasterObjModel();
	showConfigMaster:ConfigMasterObjModel=new ConfigMasterObjModel();

	setRuleData(key: string, data: any[]) {
    const Model = this.configMasterService.rules.find(r=>r.key == key).model;

	if (!Model) return;

    if (!this.configMasterOrg[key]) {
      this.configMasterOrg[key] = [];
    }

    if (!data || data.length === 0) {
      this.configMasterOrg[key].push(new Model());
      this.editConfigMaster[key]= new Model();
      this.showConfigMaster[key] = null;
    } else {
      this.configMasterOrg[key] = data;

      const editModel = data.filter(c => !c.comments && !c.isApproved);
      const showModel = data
        .filter(c => c.isApproved)
        .sort((a, b) => new Date(b.effectiveDate).getTime() - new Date(a.effectiveDate).getTime());

	  this.editConfigMaster[key] = JSON.parse(JSON.stringify(editModel.length > 0 ? editModel[0] : (showModel.length > 0 ? showModel[0] : new Model())));
	  this.showConfigMaster[key]= JSON.parse(JSON.stringify(editModel.length > 0 ? (showModel.length > 0 ? showModel[0] : null):null));
    }

    this.configMasterModel[key] = JSON.parse(JSON.stringify(this.configMasterOrg[key]));
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

		validationAllData(key:string) :boolean{
			switch (key) {
				case "tokenDetails":
					if(this.isNotValidNumber(this.editConfigMaster.tokenDetails.fixedToken) || this.isNotValidNumber(this.editConfigMaster.tokenDetails.variableToken ) || !this.editConfigMaster.tokenDetails.effectiveDate){
						return true;
					}
					if(this.invaliedEffectiveDate('tokenDetails',this.editConfigMaster.tokenDetails?.effectiveDate)){
						return true;
					}
					if(Number(this.editConfigMaster.tokenDetails.fixedToken) > Number(this.editConfigMaster.depositDetails.maximumDeposit)){
						this.notifyService.showInfo("The token fixed amount should not be greater than the maximum security deposit.","")
						return true;
					}
				  break;
				case "depositDetails":
					if(this.isNotValidNumber(this.editConfigMaster.depositDetails.maximumDeposit) || this.isNotValidNumber(this.editConfigMaster.depositDetails.minimumDeposit) || !this.editConfigMaster.depositDetails?.effectiveDate ){
						return true;
					}
					if(this.invaliedEffectiveDate('depositDetails',this.editConfigMaster.depositDetails?.effectiveDate)){
						return true;
					}
					if(Number(this.editConfigMaster.depositDetails.maximumDeposit) < Number(this.editConfigMaster.depositDetails.minimumDeposit) ){
						this.notifyService.showInfo("The maximum security deposit should not be less than to the minimum security deposit.","")
						return true;
					}
					if(Number(this.editConfigMaster.depositDetails.maximumDeposit) < Number(this.editConfigMaster.tokenDetails.fixedToken) ){
						this.notifyService.showInfo("The maximum security deposit should not be less than to the token fixed amount.","")
						return true;
					}
				  break;
				case "gstCharges":
					if( this.isNotValidNumber(this.editConfigMaster.gstCharges.monthlyRent) || this.isNotValidNumber(this.editConfigMaster.gstCharges.cgstPercentage) 
						|| this.isNotValidNumber(this.editConfigMaster.gstCharges.sgstPercentage) || this.isNotValidNumber(this.editConfigMaster.gstCharges.igstPercentage)
						|| !this.editConfigMaster.gstCharges?.effectiveDate ){
						return true;
					}
					if(this.invaliedEffectiveDate('gstCharges',this.editConfigMaster.gstCharges?.effectiveDate)){
						return true;
					}
				  break;
				case "noRentalAgreement":
					if(this.isNotValidNumber(this.editConfigMaster.noRentalAgreement.noRentalAgreementDays) || !this.editConfigMaster.noRentalAgreement?.effectiveDate){
						return true;
					 }
					 if(this.invaliedEffectiveDate('noRentalAgreement',this.editConfigMaster.noRentalAgreement?.effectiveDate)){
						 return true;
					 }
					//  if(Number(this.editConfigMaster.noRentalAgreement.noRentalAgreementDays) < Number(this.configMasterModel.shortTermRentingDuration[0].rentingDurationDays) ){
					// 	 this.notifyService.showInfo("No Rental agreement upto should be greater than Short term duration.","");
					// 	 return true;
					//   }
				  break;
				case "securityDepositDeadLineDetails":
					if(this.isNotValidNumber(this.editConfigMaster.securityDepositDeadLineDetails.auto_cancellation_day) || this.isNotValidNumber(this.editConfigMaster.securityDepositDeadLineDetails.deduction_percentage) 
						|| !this.editConfigMaster.securityDepositDeadLineDetails.trigger_condition|| !this.editConfigMaster.securityDepositDeadLineDetails.trigger_value || !this.editConfigMaster.securityDepositDeadLineDetails?.effectiveDate){
						return true;
					}
					if(this.invaliedEffectiveDate('securityDepositDeadLineDetails',this.editConfigMaster.securityDepositDeadLineDetails?.effectiveDate)){
						return true;
					}
				  break;
				case "cancellationAfterCheckInDetails":
					if(this.isNotValidNumbernAndZero(this.editConfigMaster.cancellationAfterCheckInDetails.auto_cancellation_day) || this.isNotValidNumber(this.editConfigMaster.cancellationAfterCheckInDetails.deduction_percentage) 
						|| !this.editConfigMaster.cancellationAfterCheckInDetails.trigger_condition|| !this.editConfigMaster.cancellationAfterCheckInDetails.trigger_value || !this.editConfigMaster.cancellationAfterCheckInDetails?.effectiveDate){
						return true;
					}
					if(this.invaliedEffectiveDate('cancellationAfterCheckInDetails',this.editConfigMaster.cancellationAfterCheckInDetails?.effectiveDate)){
						return true;
					}
					if(this.editConfigMaster.cancellationAfterCheckInDetails.auto_cancellation_day == 1 && this.editConfigMaster.cancellationAfterCheckInDetails.trigger_condition === '<'){
						return true;
					}
				  break;
				case "otherCharges":
					if(this.isNotValidNumber(this.editConfigMaster.otherCharges.ownerDocumentCharges) || this.isNotValidNumber(this.editConfigMaster.otherCharges.tenantDocumentCharges) 
						|| this.isNotValidNumber(this.editConfigMaster.otherCharges.ownerEkycCharges) || this.isNotValidNumber(this.editConfigMaster.otherCharges.tenantEkycCharges) || !this.editConfigMaster.otherCharges?.effectiveDate ){
						return true;
					}
					if(this.invaliedEffectiveDate('otherCharges',this.editConfigMaster.otherCharges?.effectiveDate)){
						return true;
					}
				  break;
				case "forceCheckOut":
					if(this.isNotValidNumber(this.editConfigMaster.forceCheckOut.forceCheckOutDays) || !this.editConfigMaster.forceCheckOut?.effectiveDate){
						return true;
					}
					if(this.invaliedEffectiveDate('forceCheckOut',this.editConfigMaster.forceCheckOut?.effectiveDate)){
						return true;
					}
				  break;
				case "earlyCheckOutRuleDetails":
					if(this.isNotValidNumbernAndZero(this.editConfigMaster.earlyCheckOutRuleDetails.check_out_day) || this.isNotValidNumber(this.editConfigMaster.earlyCheckOutRuleDetails.deduction_percentage) || !this.editConfigMaster.earlyCheckOutRuleDetails.trigger_condition || !this.editConfigMaster.earlyCheckOutRuleDetails.trigger_value ){
						return true;
					}
					if(this.invaliedEffectiveDate('earlyCheckOutRuleDetails',this.editConfigMaster.earlyCheckOutRuleDetails?.effectiveDate)){
						return true;
					}
					if(this.editConfigMaster.earlyCheckOutRuleDetails.check_out_day == 1 && this.editConfigMaster.earlyCheckOutRuleDetails.trigger_condition === '<'){
						return true;
					}
				  break;
				case "dataGrouping":
					if(this.isNotValidNumber(this.editConfigMaster.dataGrouping.considerDays) || !this.editConfigMaster.dataGrouping?.effectiveDate){
						return true;
					}
					if(this.invaliedEffectiveDate('dataGrouping',this.editConfigMaster.dataGrouping?.effectiveDate)){
						return true;
					}
				  break; 
				// case "shortTermRentingDuration":
				// 	if(this.isNotValidNumber(this.configMasterModel.shortTermRentingDuration[0].rentingDurationDays) || !this.configMasterModel.shortTermRentingDuration[0]?.effectiveDate){
				// 		return true;
				// 	 }
				// 	 if(this.invaliedEffectiveDate('shortTermRentingDuration',this.configMasterModel.shortTermRentingDuration[0]?.effectiveDate)){
				// 		 return true;
				// 	 }
				// 	 if(Number(this.editConfigMaster.noRentalAgreement.noRentalAgreementDays) < Number(this.configMasterModel.shortTermRentingDuration[0].rentingDurationDays) ){
				// 		 this.notifyService.showInfo("Short term duration should not be greater than 'No Rental agreement upto'.","");
				// 		 return true;
				// 	  }
				//   break; 

			  }
			  return false;
		}

	 tokenAdvancSubmit(task:string) {

		if( task === 'approve' && this.editConfigMaster.tokenDetails?.createdBy == this.userInfo.userEmail){
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
			this.editConfigMaster.tokenDetails.isApproved=true;
		}else{
			if(this.editConfigMaster.tokenDetails.isApproved){
				this.editConfigMaster.tokenDetails.tokenId="";
			}
		}
	
		this.authService.checkLoginUserVlidaate();
		this.spinner.show();
		this.configMasterService.updateTokenAdvanceDetails(this.editConfigMaster.tokenDetails).subscribe(res => {
			this.setRuleData('tokenDetails',res.data);
			this.tokenAdvancDisabled = true;
			this.notifyService.showSuccess(res.message, "");
			this.closeApproveRejectModel.nativeElement.click(); 
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
		this.setRuleData('tokenDetails',this.configMasterOrg.tokenDetails);
		this.tokenAdvancDisabled = true;
	  }
	  
	  securityDepositLimitsSubmit(task:string) {
		if( task === 'approve' && this.editConfigMaster.depositDetails?.createdBy == this.userInfo.userEmail){
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
					this.editConfigMaster.depositDetails.isApproved=true;
				}else{
					if(this.editConfigMaster.depositDetails.isApproved){
						this.editConfigMaster.depositDetails.depositId="";
					}
				}
				this.configMasterService.updatesecurityDepositLimitsDetails(this.editConfigMaster.depositDetails).subscribe(res => {
					this.setRuleData('depositDetails',res.data);
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
		this.setRuleData('depositDetails',this.configMasterOrg.depositDetails);
		this.securityDepositLimitsDisabled = true;
	}

	gstChargesSubmit(task:string) {
		if( task === 'approve' && this.editConfigMaster.gstCharges?.createdBy == this.userInfo.userEmail){
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
			this.editConfigMaster.gstCharges.isApproved=true;
		}else{
			if(this.editConfigMaster.gstCharges.isApproved){
				this.editConfigMaster.gstCharges.rentId="";
			}
		}
		this.authService.checkLoginUserVlidaate();
		this.configMasterService.updategstChargesDetails(this.editConfigMaster.gstCharges).subscribe(res => {
			this.setRuleData('gstCharges',res.data);
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
		this.setRuleData('gstCharges',this.configMasterOrg.gstCharges);
		this.gstChargesDisabled = true;
	  }
	securityDepositDeadLineSubmit(task:string) {
		this.table?.renderRows();
		if( task === 'approve' && this.editConfigMaster.securityDepositDeadLineDetails?.createdBy == this.userInfo.userEmail){
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
			this.editConfigMaster.securityDepositDeadLineDetails.isApproved=true;
		}else{
			if(this.editConfigMaster.securityDepositDeadLineDetails.isApproved){
				this.editConfigMaster.securityDepositDeadLineDetails.auto_cancellation_id="";
			}
		}
		this.authService.checkLoginUserVlidaate();	
		this.spinner.show();
		this.configMasterService.updateSecurityDepositDeadLineDetails(this.editConfigMaster.securityDepositDeadLineDetails).subscribe(res => {
			this.setRuleData('securityDepositDeadLineDetails',res.data);
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
		this.setRuleData('securityDepositDeadLineDetails',this.configMasterOrg.securityDepositDeadLineDetails);
		this.securityDepositDeadLineDisabled = true;
	}
	autoCancellationSubmit(task:string) {
		if( task === 'approve' && this.editConfigMaster.cancellationAfterCheckInDetails?.createdBy == this.userInfo.userEmail){
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
			this.editConfigMaster.cancellationAfterCheckInDetails.isApproved=true;
		}else{
			if(this.editConfigMaster.cancellationAfterCheckInDetails.isApproved){
				this.editConfigMaster.cancellationAfterCheckInDetails.auto_cancellation_id="";
			}
		}
		this.authService.checkLoginUserVlidaate();
		this.spinner.show();
		this.configMasterService.updateAutoCancellationDetails(this.editConfigMaster.cancellationAfterCheckInDetails).subscribe(res => {
			this.setRuleData('cancellationAfterCheckInDetails',res.data);
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
		this.setRuleData('cancellationAfterCheckInDetails',this.configMasterOrg.cancellationAfterCheckInDetails);
		this.autoCancellationDisabled = true;
	}	
	otherChargesSubmit(task:string) {
		if( task === 'approve' && this.editConfigMaster.otherCharges?.createdBy == this.userInfo.userEmail){
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
				this.editConfigMaster.otherCharges.isApproved=true;
			}else{
				if(this.editConfigMaster.otherCharges.isApproved){
					this.editConfigMaster.otherCharges.otherChargesId="";
				}
			}
			this.authService.checkLoginUserVlidaate();
			this.spinner.show();
			this.configMasterService.updateOtherChargesDetails(this.editConfigMaster.otherCharges).subscribe(res => {
				this.setRuleData('otherCharges',res.data);
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
		this.setRuleData('otherCharges',this.configMasterOrg.otherCharges);
		this.otherChargesDisabled = true;
	}		  

	trendingPGSubmit(task:string) {
		if( task === 'approve' && this.editConfigMaster.dataGrouping?.createdBy == this.userInfo.userEmail){
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
				this.editConfigMaster.dataGrouping.isApproved=true;
			}else{
				if(this.editConfigMaster.dataGrouping.isApproved){
					this.editConfigMaster.dataGrouping.dataGroupingId="";
				}
			}
			this.authService.checkLoginUserVlidaate();
			this.spinner.show();
			this.configMasterService.updateDataGroupingDetails(this.editConfigMaster.dataGrouping).subscribe(res => {
				this.setRuleData('dataGrouping',res.data);
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
		this.setRuleData('dataGrouping',this.configMasterOrg.dataGrouping);
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

		const editModel = this.cancellationBeforeCheckInDetailsOrg.filter(c => !c.comments && !c.isApproved);
		const showModel = this.cancellationBeforeCheckInDetailsOrg
		  .filter(c => c.isApproved)
		  .sort((a, b) => new Date(b.effectiveDate).getTime() - new Date(a.effectiveDate).getTime());

  		let model = new BeforeCheckInCancellationRefundMainObjModel();
		this.editConfigMaster.beforeCheckInCancellationRefundMainObjModel = JSON.parse(JSON.stringify(editModel.length > 0 ? editModel[0] : (showModel.length > 0 ? showModel[0] : model )));
		this.showConfigMaster.beforeCheckInCancellationRefundMainObjModel= JSON.parse(JSON.stringify(editModel.length > 0 ? (showModel.length > 0 ? showModel[0] : null):null));
	  
			var main :BeforeCheckInCancellationRefundMainObjModel=this.editConfigMaster.beforeCheckInCancellationRefundMainObjModel;
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

		  this.crpEffectiveDate = this.editConfigMaster.beforeCheckInCancellationRefundMainObjModel?.effectiveDate;
		  this.beforeCheckInCRDetails=JSON.parse(JSON.stringify(this.backUpBeforeCheckInCRList));
		  this.dataSource = new MatTableDataSource<BeforeCheckInCancellationRefundModel>(this.beforeCheckInCRDetails);
		  this.table?.renderRows();
		}

	  beforeCheckInCRDatafReset(){
		this.beforeCheckInCRfSaveVali = false ;
		this.beforeCheckInCRfModel = new BeforeCheckInCancellationRefundModel();
		this.beforeCheckInCRfModel.trigger_value='TotalPaidAmount';
		this.canSubmit=true;
		this.getBeforeCheckInCRData();
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
	
 beforeCheckInCRfUpDate(task:string){
	var payload :BeforeCheckInCancellationRefundMainObjModel = JSON.parse(JSON.stringify(this.editConfigMaster.beforeCheckInCancellationRefundMainObjModel));
	if(task != 'reject'){
	if(!this.crpEffectiveDate || this.multirullsEffectiveDateValidation(this.crpEffectiveDate) ){
		return;
	}
	
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

	const model = this.showConfigMaster.beforeCheckInCancellationRefundMainObjModel ? this.showConfigMaster.beforeCheckInCancellationRefundMainObjModel : this.editConfigMaster.beforeCheckInCancellationRefundMainObjModel
	const selectedDate = new Date(payload.effectiveDate).setHours(0, 0, 0, 0);
	const existingDate = new Date(model.effectiveDate).setHours(0, 0, 0, 0);	
	if ( selectedDate < new Date().setHours(0, 0, 0, 0) || (selectedDate <= existingDate && existingDate )) {
		this.notifyService.showInfo("The effective date must be after the last rule's effective date.", "");
		return;
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
	}else{
		payload.comments=this.comments;
		payload.ZoyBeforeCheckInCancellationInfo=payload.zoy_before_check_in_cancellation_info;
	}
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
		if( task === 'approve' && this.editConfigMaster.earlyCheckOutRuleDetails?.createdBy == this.userInfo.userEmail){
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
				this.editConfigMaster.earlyCheckOutRuleDetails.isApproved=true;
			}else{
				if(this.editConfigMaster.earlyCheckOutRuleDetails.isApproved){
					this.editConfigMaster.earlyCheckOutRuleDetails.early_check_out_id="";
				}
			}
			
			this.authService.checkLoginUserVlidaate();
				this.spinner.show();
				this.configMasterService.updateEarlyCheckOutRulesdDetails(this.editConfigMaster.earlyCheckOutRuleDetails).subscribe(res => {
					this.setRuleData('earlyCheckOutRuleDetails',res.data);
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
		this.setRuleData('earlyCheckOutRuleDetails',this.configMasterOrg.earlyCheckOutRuleDetails);
		this.earlyCheckOutRulesDisabled = true;
	  }
	  forceCheckoutSubmit(task:string): void {
		if( task === 'approve' && this.editConfigMaster.forceCheckOut?.createdBy == this.userInfo.userEmail){
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
				this.editConfigMaster.forceCheckOut.isApproved=true;
			}else{
				if(this.editConfigMaster.forceCheckOut.isApproved){
					this.editConfigMaster.forceCheckOut.forceCheckOutId="";
				}
			}
			this.authService.checkLoginUserVlidaate();
			this.spinner.show();
			this.configMasterService.updateForceCheckOutDetails(this.editConfigMaster.forceCheckOut).subscribe(res => {
				this.setRuleData('forceCheckOut',res.data);
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
		this.setRuleData('forceCheckOut',this.configMasterOrg.forceCheckOut);
		this.forceCheckoutDisabled = true;
	  }

		onDropdownChange(slab:any) {
			if (slab.slabFrom === 'Input') {
				slab.showInputBox = true;  
			  } else {
				slab.showInputBox = false;   
			  }
		  }

		  noRentalAgreementDisabled:boolean=true;
		  noRentalAgreementSubmit(task:string): void {
			if( task === 'approve' && this.editConfigMaster.noRentalAgreement?.createdBy == this.userInfo.userEmail){
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
					this.editConfigMaster.noRentalAgreement.isApproved=true;
				}else{
					if(this.editConfigMaster.noRentalAgreement.isApproved){
						this.editConfigMaster.noRentalAgreement.noRentalAgreementId="";
					}
				}
				this.authService.checkLoginUserVlidaate();
				this.spinner.show();
				this.configMasterService.updateNoRentalAgreement(this.editConfigMaster.noRentalAgreement).subscribe(res => {
					this.setRuleData('noRentalAgreement',res.data);
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
			this.setRuleData('noRentalAgreement',this.configMasterOrg.noRentalAgreement);
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
				  model.percentage = element.percentage;
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
				this.canShortSubmit=false;
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
			}
			
			removeShortTerm(shortTerm) {
			  shortTerm.isDelete = true;
			  this.canShortSubmit=false;
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
		if(task != 'reject'){

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
				  startDay = Number(startDay)>Number(term.start_day)?Number(term.start_day):Number(startDay) ;
				  endDay = Number(endDay)>Number(term.end_day)?Number(endDay):Number(term.end_day) ;
				
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
				  finalSubmitShortList.push(term);
				}
			  }
			
			  if (finalSubmitShortList.length < 1) {
				this.notifyService.showWarning("Please add durations","");
				return;
			  }
				let n=0;
			  this.shortTermDataList.forEach(m=>{
				let term=m;
				const ranges =this.shortTermDataList.filter(d=> endDay != Number(term.end_day) && Number(d.start_day) == (Number(term.end_day)+1));
			  if( ranges.length === 0 && endDay != Number(term.end_day) ){
			     n=++n;
				 return;
			   }
			  }) ;
			   if(n>0 || startDay !=1){
				this.notifyService.showWarning('The Short term duration period must be within the defined ranges of 1-'+endDay+' days.',"")
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
			const selectedDate = new Date(payload?.effectiveDate)?.setHours(0, 0, 0, 0);
			if(this.backUpshortTermData[compareIndex]?.effectiveDate){
				const existingDate = new Date(this.backUpshortTermData[compareIndex]?.effectiveDate)?.setHours(0, 0, 0, 0);	
				if ( selectedDate < new Date().setHours(0, 0, 0, 0) || (selectedDate <= existingDate && existingDate )) {
					this.notifyService.showInfo("The effective date must be after the last rule's effective date.", "");
					return;
				}
			}
			payload.zoy_short_term_dto_info=finalSubmitShortList;
		}{

		}
			  this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want Update ?')
			  .then(
				 (confirmed) =>{
				if(confirmed){
					this.spinner.show();
					this.configMasterService.submitShortTermData(payload).subscribe(data => {
					this.getDbSettingDetails();
					this.closeApproveRejectModel.nativeElement.click(); 
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

			approveOrRejectRule:string;
			task:string='';
			data:any;
	openApproveOrRejectModel(key:string,data:any){
		this.comments="";
		this.task='';
		this.submitted=false;
		this.key=key;
		this.approveOrRejectRule= this.configMasterService.rules.find(r=>r.key == key).name;
		this.data=data
	}
	key:string='';
	isApproveOrReject(task:string){
		this.task=task;
		this.submitted=true;
		if(this.task == 'reject' && !this.comments){
			return;
		}else{
			if(this.key=='beforeCheckInCancellationRefundMainObjModel'){
				this.beforeCheckInCRfUpDate(task);
			}else if(this.key=='shortTermMainModel'){
				this.submitShortTermData(task);
			}else{
				this.doApproveOrReject();
			}
		}
		
	}

doApproveOrReject() {	
	if(this.task === 'approve'){
		switch (this.key) {
			case 'tokenDetails':
			  this.tokenAdvancSubmit(this.task);
			  break;
		  
			case 'depositDetails':
			  this.securityDepositLimitsSubmit(this.task);
			  break;
		  
			case 'gstCharges':
			  this.gstChargesSubmit(this.task);
			  break;
		  
			case 'noRentalAgreement':
			  this.noRentalAgreementSubmit(this.task);
			  break;
		  
		  
			case 'cancellationAfterCheckInDetails':
			  this.autoCancellationSubmit(this.task);
			  break;
		  
			case 'otherCharges':
			  this.otherChargesSubmit(this.task);
			  break;
		  
			case 'forceCheckOut':
			  this.forceCheckoutSubmit(this.task);
			  break;
		  
			case 'earlyCheckOutRuleDetails':
			  this.earlyCheckOutRulesSubmit(this.task);
			  break;
		  
			case 'dataGrouping':
			  this.trendingPGSubmit(this.task);
			  break;

			case 'securityDepositDeadLineDetails':
			  this.securityDepositDeadLineSubmit(this.task);
			  break;
		  
			default:
			  console.warn(`Unhandled rule key: ${this.key}`);
			  break;
		  }
		  this.closeApproveRejectModel.nativeElement.click(); 	  
		
	}else{
		this.data.comments = this.comments; 
		let rule = this.configMasterService.rules.find(r=>r.name == this.approveOrRejectRule);
		this.authService.checkLoginUserVlidaate();
		this.spinner.show();
		this.configMasterService.ruleReject(this.data,rule.api).subscribe(res => {
			this.setRuleData(rule.key,res.data);
			this.notifyService.showSuccess(res.message, "");
			this.closeApproveRejectModel.nativeElement.click(); 
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

	}
	
	
	
		
			
			   
  }  