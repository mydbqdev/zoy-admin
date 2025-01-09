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
import { BeforeCheckInCancellationRefundModel, ConfigMasterModel, DataGroupingModel, EarlyCheckOutRuleDetails, OtherChargesModel, SecurityDepositDeadLineAndAutoCancellationModel, SecurityDepositLimitsModel, TokenDetailsModel} from '../models/config-master-model';
import { CdkDragDrop, CdkDropListGroup, moveItemInArray } from '@angular/cdk/drag-drop';
import { MatTable, MatTableDataSource } from '@angular/material/table';


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

	  triggerCondition : {'id':number,'cond_name':string}[] = []; //['==','>=','<=','>','<','!='];
	  triggerOn : {'id':number,'trigger_on':string}[]=[];//['PaidAmount','Rent','PaidAmount & Rent']; 

	  displayedColumns: string[] = ['trigger_condition', 'before_checkin_days','trigger_value','deduction_percentage','action'];
	  dataSource = new MatTableDataSource<BeforeCheckInCancellationRefundModel>([]);
	  @ViewChild(CdkDropListGroup) listGroup: CdkDropListGroup<HTMLElement[]>;
	  beforeCheckInCRDetails: BeforeCheckInCancellationRefundModel[] = [];
	  backUpBeforeCheckInCRList:BeforeCheckInCancellationRefundModel[]=[];
	  canSubmit:boolean = true;
	  @ViewChild('table', { static: true }) table: MatTable<BeforeCheckInCancellationRefundModel>;

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
		  this.getTriggerCondition();
		  this.getTriggerOn();
	  }
	  ngAfterViewInit() {
		  this.sidemenuComp.expandMenu(4);
		  this.sidemenuComp.activeMenu(4, 'configuration-master');
		  this.dataService.setHeaderName("Configuration Master");
	  }

	  isNotValidNumber(value: any): boolean {
		return value == null || isNaN(value);
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

	 tokenAdvancSubmit() {
		if(this.configMasterModel.tokenDetails.fixedToken == 0 || this.configMasterModel.tokenDetails.variableToken == 0 || !this.configMasterModel.tokenDetails.fixedToken || !this.configMasterModel.tokenDetails.variableToken ){
			return
		}
		this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want Update ?')
		.then(
		   (confirmed) =>{
			if(confirmed){
		this.spinner.show();
		this.authService.checkLoginUserVlidaate();
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
			});	}	
		}).catch(
			() => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
		);					
	  }
	
	  tokenAdvanceReset() {
		this.configMasterModel.tokenDetails = JSON.parse(JSON.stringify(this.configMasterOrg.tokenDetails));
		this.tokenAdvancDisabled = true;
	  }
	  
	  securityDepositLimitsSubmit() {
		if(this.configMasterModel.depositDetails.maximumDeposit == 0 || this.configMasterModel.depositDetails.minimumDeposit == 0 || !this.configMasterModel.depositDetails.maximumDeposit || !this.configMasterModel.depositDetails.minimumDeposit ){
			return ;
		}
		this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want Update ?')
		.then(
		   (confirmed) =>{
			if(confirmed){
				this.authService.checkLoginUserVlidaate();
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
			}).catch(
				() => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
			);								
	  	}
		
		securityDepositLimitsReset() {
			this.configMasterModel.depositDetails = JSON.parse(JSON.stringify(this.configMasterOrg.depositDetails));
			this.securityDepositLimitsDisabled = true;
		}
	securityDepositDeadLineSubmit() {
		this.authService.checkLoginUserVlidaate();
		if(!this.configMasterModel.securityDepositDeadLineDetails.auto_cancellation_day || !this.configMasterModel.securityDepositDeadLineDetails.deduction_percentage || !this.configMasterModel.securityDepositDeadLineDetails.trigger_condition|| !this.configMasterModel.securityDepositDeadLineDetails.trigger_value ){
			return ;
		}
		this.spinner.show();
		this.configMasterService.updateSecurityDepositDeadLineDetails(this.configMasterModel.securityDepositDeadLineDetails).subscribe(res => {
			this.configMasterOrg.securityDepositDeadLineDetails = Object.assign(new SecurityDepositDeadLineAndAutoCancellationModel(), res.data );
			this.configMasterModel.securityDepositDeadLineDetails = JSON.parse(JSON.stringify(this.configMasterOrg.securityDepositDeadLineDetails));
			this.securityDepositDeadLineDisabled = true;
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
	securityDepositDeadLineReset() {
		this.configMasterModel.securityDepositDeadLineDetails = JSON.parse(JSON.stringify(this.configMasterOrg.securityDepositDeadLineDetails));
		this.securityDepositDeadLineDisabled = true;
	}
	autoCancellationSubmit() {
		if(!this.configMasterModel.cancellationAfterCheckInDetails.auto_cancellation_day || !this.configMasterModel.cancellationAfterCheckInDetails.deduction_percentage || !this.configMasterModel.cancellationAfterCheckInDetails.trigger_condition|| !this.configMasterModel.cancellationAfterCheckInDetails.trigger_value ){
			return ;
		}
		this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want Update ?')
		.then(
		   (confirmed) =>{
			if(confirmed){
				this.authService.checkLoginUserVlidaate();
				this.spinner.show();
				this.configMasterService.updateAutoCancellationDetails(this.configMasterModel.cancellationAfterCheckInDetails).subscribe(res => {
					this.configMasterOrg.cancellationAfterCheckInDetails = Object.assign(new SecurityDepositDeadLineAndAutoCancellationModel(), res.data );
					this.configMasterModel.cancellationAfterCheckInDetails = JSON.parse(JSON.stringify(this.configMasterOrg.cancellationAfterCheckInDetails));
					this.autoCancellationDisabled = true;
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
	autoCancellationReset() {
		this.configMasterModel.cancellationAfterCheckInDetails = JSON.parse(JSON.stringify(this.configMasterOrg.cancellationAfterCheckInDetails));
		this.autoCancellationDisabled = true;
	}	
	otherChargesSubmit() {
		
		if(!this.configMasterModel.otherCharges.ownerDocumentCharges || !this.configMasterModel.otherCharges.tenantDocumentCharges ){
			return ;
		}
		this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want Update ?')
		.then(
		   (confirmed) =>{
			if(confirmed){
				this.authService.checkLoginUserVlidaate();
				this.spinner.show();
				this.configMasterService.updateOtherChargesDetails(this.configMasterModel.otherCharges).subscribe(res => {
					this.configMasterOrg.otherCharges = Object.assign(new OtherChargesModel(), res.data );
					this.configMasterModel.otherCharges = JSON.parse(JSON.stringify(this.configMasterOrg.otherCharges));
					this.otherChargesDisabled = true;
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
	  otherChargesReset() {
		this.configMasterModel.otherCharges = JSON.parse(JSON.stringify(this.configMasterOrg.otherCharges));
		this.otherChargesDisabled = true;
	}		  

	trendingPGSubmit() {
		if(this.configMasterModel.dataGrouping.considerDays == 0 ||  !this.configMasterModel.dataGrouping.considerDays ){
			return ;
		}
		this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want Update ?')
		.then(
		   (confirmed) =>{
			if(confirmed){
				this.authService.checkLoginUserVlidaate();
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
			}).catch(
				() => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
			);					
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
			});
			this.canSubmit=false;
		} 
		this.table.renderRows();
	  }

	  getBeforeCheckInCRData(){
		this.backUpBeforeCheckInCRList=[];
		this.configMasterOrg.cancellationBeforeCheckInDetails.forEach(element => {
			let model : BeforeCheckInCancellationRefundModel = new BeforeCheckInCancellationRefundModel();
			model.cancellation_id = element.cancellation_id; 
			model.before_checkin_days = element.before_checkin_days; 
			model.deduction_percentage = element.deduction_percentage; 
			model.priority = element.priority;
			model.trigger_condition = element.trigger_condition; 
			model.trigger_on = element.trigger_on; 
			model.trigger_value = element.trigger_value; 

			this.backUpBeforeCheckInCRList.push(model);
		});
		 
		  this.beforeCheckInCRDetails=JSON.parse(JSON.stringify(this.backUpBeforeCheckInCRList));
		  this.dataSource = new MatTableDataSource<BeforeCheckInCancellationRefundModel>(this.beforeCheckInCRDetails);
		  this.table.renderRows();
		}

	  beforeCheckInCRDatafReset(){
		this.beforeCheckInCRfSaveVali = false ;
		this.beforeCheckInCRfModel = new BeforeCheckInCancellationRefundModel();
		this.canSubmit=true;
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
	}  

 beforeCheckInCRfUpDate(){
		this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want Update ?')
		.then(
		   (confirmed) =>{
			if(confirmed){
				const filteredDetails = this.beforeCheckInCRDetails.filter(item => !item.isDelete);
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
			
			this.authService.checkLoginUserVlidaate();
			this.spinner.show();
			this.configMasterService.submitBeforeCheckInCRfDetails(this.beforeCheckInCRDetails).subscribe(res => {
			this.configMasterOrg.cancellationBeforeCheckInDetails = res.data;
			this.canSubmit = true;
			this.getBeforeCheckInCRData();
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
		if(this.checkDuplicateBCCR(row)){
			return;
		}
		row.isEdit = false;
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
		this.beforeCheckInCRDetails[i] = row ;
		this.dataSource.data[i] = row;
		this.canSubmit=false;
	  }

	beforeCheckInCRfDelete(row: BeforeCheckInCancellationRefundModel,n:number) {
		row.isDelete = true;
		if(!row.cancellation_id){
			this.beforeCheckInCRDetails.splice(n, 1)
		}
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
	
	  earlyCheckOutRulesSubmit() {
		if(!this.configMasterModel.earlyCheckOutRuleDetails.check_out_day || !this.configMasterModel.earlyCheckOutRuleDetails.deduction_percentage || !this.configMasterModel.earlyCheckOutRuleDetails.trigger_condition || !this.configMasterModel.earlyCheckOutRuleDetails.trigger_value ){
			return;
		}
		this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want Update ?')
		.then(
		   (confirmed) =>{
			if(confirmed){
				this.authService.checkLoginUserVlidaate();
				this.spinner.show();
				this.configMasterService.updateEarlyCheckOutRulesdDetails(this.configMasterModel.earlyCheckOutRuleDetails).subscribe(res => {
					this.configMasterOrg.earlyCheckOutRuleDetails = Object.assign(new EarlyCheckOutRuleDetails(), res.data );
					this.configMasterModel.earlyCheckOutRuleDetails = JSON.parse(JSON.stringify(this.configMasterOrg.earlyCheckOutRuleDetails));
					this.earlyCheckOutRulesDisabled = true;
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

	earlyCheckOutRuleReset() {
		this.configMasterModel.earlyCheckOutRuleDetails = JSON.parse(JSON.stringify(this.configMasterOrg.earlyCheckOutRuleDetails));
		this.earlyCheckOutRulesDisabled = true;
	  }

		getConfigMasterDetails(){
			this.authService.checkLoginUserVlidaate();
			this.spinner.show();
			this.configMasterService.getConfigMasterDetails().subscribe(res => {
			this.configMasterOrg =JSON.parse(JSON.stringify(res.data) );
			this.configMasterModel = JSON.parse(JSON.stringify(this.configMasterOrg));
			this.getBeforeCheckInCRData();
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

	

  }  