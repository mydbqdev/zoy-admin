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
		{ id: 3, name: 'right3', selected: false },
        { id: 4, name: 'right4', selected: false },
        { id: 5, name: 'right5', selected: false },
        { id: 6, name: 'right6', selected: false },
	  ];
	  // Toggle the selected status for a button
	  collaspeExpandPanelRight(status: any,side:string): void {
		status.selected = !status.selected;
         if(side=='right1' && this.collaspeListRight[0].selected){
			this.collaspeListRight[1].selected=false;
			this.collaspeListRight[2].selected=false;
            this.collaspeListRight[3].selected=false;
			this.collaspeListRight[4].selected=false;
            this.collaspeListRight[5].selected=false;
		 }
		 if(side=='right2' && this.collaspeListRight[1].selected){
			this.collaspeListRight[0].selected=false;
			this.collaspeListRight[2].selected=false;
            this.collaspeListRight[3].selected=false;
			this.collaspeListRight[4].selected=false;
            this.collaspeListRight[5].selected=false;
		 }
		 if(side=='right3' && this.collaspeListRight[2].selected){
			this.collaspeListRight[0].selected=false;
			this.collaspeListRight[1].selected=false;
            this.collaspeListRight[3].selected=false;
			this.collaspeListRight[4].selected=false;
            this.collaspeListRight[5].selected=false;
		 }
         if(side=='right4' && this.collaspeListRight[3].selected){
			this.collaspeListRight[0].selected=false;
            this.collaspeListRight[1].selected=false;
			this.collaspeListRight[2].selected=false;
            this.collaspeListRight[4].selected=false;
            this.collaspeListRight[5].selected=false;
		 }
         if(side=='right5' && this.collaspeListRight[4].selected){
			this.collaspeListRight[0].selected=false;
			this.collaspeListRight[1].selected=false;
            this.collaspeListRight[2].selected=false;
			this.collaspeListRight[3].selected=false;
            this.collaspeListRight[5].selected=false;
		 }
         if(side=='right6' && this.collaspeListRight[5].selected){
			this.collaspeListRight[0].selected=false;
			this.collaspeListRight[1].selected=false;
            this.collaspeListRight[2].selected=false;
			this.collaspeListRight[3].selected=false;
            this.collaspeListRight[4].selected=false;
		 }
	  }

  }  