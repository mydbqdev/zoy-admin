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
import { GenerateZoyCodeService } from '../../service/zoy-code.service';
import { UserInfo } from 'src/app/common/shared/model/userinfo.service';
import { FloorInformation, PgOwnerData, PgOwnerPropertyInformation, Room } from '../models/owner-full-details';
import { ZoyOwnerService } from '../../service/zoy-owner.service';

@Component({
  selector: 'app-managing-owner-details',
  templateUrl: './managing-owner-details.component.html',
  styleUrl: './managing-owner-details.component.css'
})
export class OwnerDetailsComponent implements OnInit, AfterViewInit {
	
	public userNameSession: string = "";
	  errorMsg: any = "";
	  mySubscription: any;
	  isExpandSideBar:boolean=true;
	  @ViewChild(SidebarComponent) sidemenuComp;
	  public rolesArray: string[] = [];
	  submitted=false;
	  userInfo:UserInfo=new UserInfo();
	  owenerId :string ='' ;
	  pgOwnerData : PgOwnerData = new PgOwnerData();
	  roomList:Room[]=[]
	  roomArrayList:any[]=[];
	  floorInfo:FloorInformation = new FloorInformation();
	  floor_id:string='';
	  property_id:string='';
	  property_status:string='';

	  propertyInfo :PgOwnerPropertyInformation =new PgOwnerPropertyInformation();
	  totalRecord:number=0;
	  pageSizeOptions: number[] = [10, 25, 50];
	  pageSize = 10;
	  constructor(private generateZoyCodeService : GenerateZoyCodeService,private route: ActivatedRoute, private router: Router,private formBuilder: FormBuilder, private http: HttpClient, private userService: UserService,private zoyOwnerService :ZoyOwnerService,
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
		  this.dataService.getOwenerId.subscribe(id=>{
			if(id == null || id == undefined || id == ''){
				if(localStorage.getItem('ownerInfo')){
					this.owenerId = localStorage.getItem('ownerInfo') ;
				}else{
					this.router.navigate(['/manage-owner']);
				}
			}else{
				this.owenerId=id;
			}
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
		  this.sidemenuComp.expandMenu(2);
		  this.sidemenuComp.activeMenu(2, 'manage-owner');
		  this.dataService.setHeaderName("Manage Owner Details");
		  
		  this.getZoyOwnerDetails();
	  }
	  
	  collaspeList = [
		{ id: 1, name: 'left1', selected: true },
		{ id: 2, name: 'left2', selected: false },
	  ];
	  collaspeListRight = [
		{ id: 1, name: 'right1', selected: false },
		{ id: 2, name: 'right2', selected: false },
		{ id: 3, name: 'right3', selected: false },
		{ id: 4, name: 'right4', selected: false },
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
			this.collaspeListRight[3].selected=false;
		 }
		 if(side=='right2' && this.collaspeListRight[1].selected){
			this.collaspeListRight[0].selected=false;
			this.collaspeListRight[2].selected=false;
			this.collaspeListRight[3].selected=false;
		 }
		 if(side=='right3' && this.collaspeListRight[2].selected){
			this.collaspeListRight[0].selected=false;
			this.collaspeListRight[1].selected=false;
			this.collaspeListRight[3].selected=false;
		 }
		 if(side=='right4' && this.collaspeListRight[3].selected){
			this.collaspeListRight[0].selected=false;
			this.collaspeListRight[1].selected=false;
			this.collaspeListRight[2].selected=false;
		 }
	  }

	  pdfSource: string = "";
	  readViewFileForApproval() {
		this.spinner.show();
		  this.pdfSource = '/assets/sample_files/sample.pdf';
		  this.spinner.hide();
	  }
	  
	  selectProperty(){
		this.propertyInfo = this.pgOwnerData.pg_owner_property_information.find(info=>info.property_id == this.property_id);
		this.property_status = this.propertyInfo.status ;
		if(this.propertyInfo.floor_information.length>0){

			this.floorInfo = this.propertyInfo.floor_information[0];
			this.floor_id = this.floorInfo.floor_id; 
			this.showRooms();
		}
		
	  }
	  selectFloor(){
			this.floorInfo = this.propertyInfo.floor_information.find(f=>f.floor_id == this.floor_id);
			this.showRooms();
	  }
	 
 	showRooms(){
		this.roomArrayList=[];
		 for(let i=0;i<this.floorInfo.rooms?.length;i=i+3){
					this.roomList=[];
					for(let j=i;j<i+3 && j<this.floorInfo.rooms.length;j++){
						this.roomList.push(this.floorInfo.rooms[j]);
					}
					this.roomArrayList.push(this.roomList);
				 }
	  }

	  availableBedsArray(n:number) {
		const num = Number(n);
		return new Array(num); 
	  }
	
	  unavailableBedsArray(room:any) {
		const num = Number(room.number_of_beds) - Number(room.beds_available) ;
		return new Array(num); 
	  }

	  getZoyOwnerDetails(){
		this.authService.checkLoginUserVlidaate();
		this.spinner.show();
		this.zoyOwnerService.getZoyOwnerDetails(this.owenerId).subscribe(res => {
		this.pgOwnerData = res?.data ;
		if(this.pgOwnerData.pg_owner_property_information.length>0){
			this.property_id =this.pgOwnerData.pg_owner_property_information[0].property_id; 
			this.propertyInfo = this.pgOwnerData.pg_owner_property_information.find(info=>info.property_id == this.property_id);
			this.property_status = this.propertyInfo.status ;
			if(this.propertyInfo.floor_information.length>0){
				this.floorInfo = this.propertyInfo.floor_information[0];
				this.floor_id = this.floorInfo.floor_id; 
				this.showRooms();
			}
			
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

	doActiveteDeactiveteOwner(doActiveteDeactiveteOwner:any,status:string,ownerName:string){
		this.authService.checkLoginUserVlidaate();		
		this.confirmationDialogService.confirm('Confirmation!!', 'Are you sure to '+( status=='Active'?'deactivated':'activated' )+' to the owner - '+ownerName+' ?')
		.then((confirmed) =>{
		   if(confirmed){
			   // backend call here
			   this.notifyService.showSuccess("Api is not implemented. will do soon", "");   
			}
		}).catch(() => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')); 
		}

		transactionHeader:string="";
	  selectTransaction(selectTab:number,header:string){
		//this.selectedTab=selectTab;
		this.transactionHeader=header;
	}
  }  