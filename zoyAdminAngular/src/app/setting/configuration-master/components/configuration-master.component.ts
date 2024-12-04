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

	  constructor(private route: ActivatedRoute, private router: Router,private formBuilder: FormBuilder, private http: HttpClient, private userService: UserService,
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

	  hidden: boolean = true;
	  value: number = 20; 
	  tempValue: number = this.value; 
	
	  edit() {
		this.hidden = false;
	  }
	
	  submit() {
		this.value = this.tempValue;
		this.hidden = true;
	  }
	
	  reset() {
		this.tempValue = this.value;
		this.hidden = true;
	  }
	  
		isEditing: boolean = false;
		securityMinValue: number = 5000;
		securityMaxValue: number = 7000;

		tempMinValue: number = this.securityMinValue;
		tempMaxValue: number = this.securityMaxValue;

		startEditing() {
			this.isEditing = true;
		}

		submitValues() {
			this.securityMinValue = this.tempMinValue;
			this.securityMaxValue = this.tempMaxValue;
			this.isEditing = false;
		}

		resetValues() {
			this.tempMinValue = this.securityMinValue;
			this.tempMaxValue = this.securityMaxValue;
			this.isEditing = false;
		}

		gstHidden: boolean = true; 
		gstValue: number = 10;
		tempGSTValue: number = this.gstValue; 

		editGST() {
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

  }  