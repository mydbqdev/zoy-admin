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
import { OwnerDetails } from '../models/owner-details-model';
import { MatTableDataSource } from '@angular/material/table';
import { FormBuilder } from '@angular/forms';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';
import { GenerateZoyCodeService } from '../../service/zoy-code.service';
import { UserInfo } from 'src/app/common/shared/model/userinfo.service';

@Component({
  selector: 'app-managing-owner-details',
  templateUrl: './managing-owner-details.component.html',
  styleUrl: './managing-owner-details.component.css'
})
export class OwnerDetailsComponent implements OnInit, AfterViewInit {
	displayedColumns: string[] = ['zoy_code', 'owner_name', 'email_id', 'mobile_no','noof_properties', 'status','action'];
	public ELEMENT_DATA:OwnerDetails[]=[];
	orginalFetchData:OwnerDetails[]=[];
	searchText:string='';
	dataSource:MatTableDataSource<OwnerDetails>=new MatTableDataSource<OwnerDetails>();
	columnSortDirectionsOg: { [key: string]: string | null } = {
	  zoy_code: null,
	  owner_name: null,
	  email_id: null,
	  noof_properties: null,
	  status: null
	};
	stopPropagation(event: MouseEvent): void {
		event.stopPropagation();
	  }
	generateZCode : OwnerDetails=new OwnerDetails();
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

	  pageSize: number = 10; 
	  pageSizeOptions: number[] = [10, 20, 50]; 
	  totalProduct: number = 0;
	  userInfo:UserInfo=new UserInfo();
	  constructor(private generateZoyCodeService : GenerateZoyCodeService,private route: ActivatedRoute, private router: Router,private formBuilder: FormBuilder, private http: HttpClient, private userService: UserService,
		  private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService, private confirmationDialogService:ConfirmationDialogService) {
			  this.authService.checkLoginUserVlidaate();
			  this.userNameSession = userService.getUsername();
		  //this.defHomeMenu=defMenuEnable;
		  this.userInfo=this.userService.getUserinfo();
		  if (userService.getUserinfo() != undefined) {
			  this.rolesArray = userService.getUserinfo().privilege;
		  }else{
			  this.dataService.getUserDetails.subscribe(name=>{
				  this.rolesArray =name.privilege;
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
		  //this.getZoyOwnerList()
	  }
	  ngAfterViewInit() {
		  this.sidemenuComp.expandMenu(2);
		  this.sidemenuComp.activeMenu(2, 'manage-owner');
		  this.dataService.setHeaderName("Manage Owner Details");
	  }
	  
	  collaspeList = [
		{ id: 1, name: 'left1', selected: true },
		{ id: 2, name: 'left2', selected: false },
	  ];
	  collaspeListRight = [
		{ id: 1, name: 'right1', selected: false },
		{ id: 2, name: 'right2', selected: false },
		{ id: 3, name: 'right3', selected: false },
	  ];
	   // Toggle the selected status for a button
	   collaspeExpandPanel(status: any,side:string): void {
		status.selected = !status.selected;
         if(side!='left1' && this.collaspeList[1].selected){
			this.collaspeList[0].selected=false;
		 }
		 if(side!='left2' && this.collaspeList[0].selected){
			this.collaspeList[1].selected=false;
		 }
	  }
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
		 if(side=='right3' && this.collaspeListRight[2].selected){
			this.collaspeListRight[0].selected=false;
			this.collaspeListRight[1].selected=false;
		 }
	  }
  }  