import { AfterViewInit, Component, ElementRef, OnInit, ViewChild, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { NavigationEnd } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { UserService } from 'src/app/common/service/user.service';
import { AuthService } from 'src/app/common/service/auth.service';
import { DataService } from 'src/app/common/service/data.service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import { SidebarComponent } from 'src/app/components/sidebar/sidebar.component';
import { MatTableDataSource } from '@angular/material/table';
import { DbMasterConfigurationService } from '../services/db-master-configuration.service';
import { DbSettingDataModel, DbSettingSubmitDataModel, settingTypeObjClmApiDetailsModel } from '../models/db-setting-models';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
	selector: 'app-db-master-configuration',
	templateUrl: './db-master-configuration.component.html',
	styleUrls: ['./db-master-configuration.component.css']
})
export class DbMasterConfigurationComponent implements OnInit, AfterViewInit {
  submittedPicture:boolean=false;
	public userNameSession: string = ""; 
	errorMsg: any = "";
	mySubscription: any;
	isExpandSideBar:boolean=true;
	@ViewChild(SidebarComponent) sidemenuComp;
	public rolesArray: string[] = [];

  settingType :string ='';
  settingTypeDetails:settingTypeObjClmApiDetailsModel=new settingTypeObjClmApiDetailsModel();
  settingTypeObjClmApiDetailsList: settingTypeObjClmApiDetailsModel[]=[];

  selectedsettingColumns :string[]=[];
  dbSettingDataList :any[]=[];
  dbSettingDataSource: MatTableDataSource<any>=new MatTableDataSource(this.dbSettingDataList);;
  dbSettingDataModel :DbSettingDataModel =new DbSettingDataModel();
  columnHeaders = {} ;
  submitDataModel:DbSettingSubmitDataModel=new DbSettingSubmitDataModel();
  // imgeURL2:any="assets/images/NotAvailable.jpg";
    
  @ViewChild('closeModel') closeModel: ElementRef;
	constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,
		private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService,
    private dbMasterConfigurationService:DbMasterConfigurationService) {
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
    this.columnHeaders = dbMasterConfigurationService.columnHeaders;
    this.settingTypeObjClmApiDetailsList = dbMasterConfigurationService.settingTypeObjClmApiDetails;

    this.settingTypeDetails = this.settingTypeObjClmApiDetailsList[0];
    this.selectedsettingColumns = this.settingTypeDetails.columns ;
    this.settingType = this.settingTypeDetails.type ;
    this.getDbSettingDetails() ;
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
		this.sidemenuComp.activeMenu(4,'db-master-configuration');
		this.dataService.setHeaderName("DB Master Configuration");

	}

  submitted:boolean= false ;

  changeSettingType(){
    if("settingType == 'Ameneties' "){
    this.resetChange();
    }
    this.settingTypeDetails = this.settingTypeObjClmApiDetailsList.find(t=>t.type == this.settingType);
    this.selectedsettingColumns = this.settingTypeDetails.columns ;
    this.getDbSettingDetails() ;
  }

 
    isCreated :boolean=true;
    getDbSettingDetails(){
       this.authService.checkLoginUserVlidaate();
      this.spinner.show();
      this.dbMasterConfigurationService.getDbSettingDetails(this.settingTypeDetails.api).subscribe(data => {
        this.dbSettingDataList=Object.assign([],data);
        this.dbSettingDataSource = new MatTableDataSource(this.dbSettingDataList);
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
        str = error.error;
        } else {
        str = error.message;
        str = str.substring(str.indexOf(":") + 1);
        }
        console.log("Error:" + str);
        this.errorMsg = str;
      }
      if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
      }
    });
    
    }

   
    createSetting(){
      this.submitted = false;
      this.isCreated = true;
      this.dbSettingDataModel = new DbSettingDataModel();
    }
    getElement(row:any){
      this.submitted = false;
      this.isCreated = false;
      const data =  JSON.parse(JSON.stringify(row));
      this.dbSettingDataModel = Object.assign(new DbSettingDataModel(),data);
  
      if (this.settingType === 'Rent Cycle' && this.dbSettingDataModel.cycle_name) {
        const cycleParts = this.dbSettingDataModel.cycle_name.split('-');
    
        if (cycleParts.length === 2) {
            this.dbSettingDataModel.cycle_first = cycleParts[0];
            this.dbSettingDataModel.cycle_second = cycleParts[1];
        }
      } 
    }  
    submitData(){
      this.submitted = true;

     if(this.validation()){      
      this.authService.checkLoginUserVlidaate();
      this.spinner.show();
      this.dbMasterConfigurationService.submitData(this.submitDataModel,this.isCreated,this.settingTypeDetails.api).subscribe(data => {
      this.closeModel.nativeElement.click(); 
      this.getDbSettingDetails();
      this.resetChange();
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
        str = error.error;
        } else {
        str = error.message;
        str = str.substring(str.indexOf(":") + 1);
        }
        console.log("Error:" + str);
        this.errorMsg = str;
      }
      if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
      }
    });
  }
}
    

  numberOnly(event): boolean {
    const charCode = (event.which) ? event.which : event.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
      return false;
    }
    return true;
  }
      
      validation():boolean{
        switch (this.settingType) {
          case 'Share Type':
                  if(this.dbSettingDataModel.share_type == null || this.dbSettingDataModel.share_type == '' || this.dbSettingDataModel.share_occupancy_count == null || this.dbSettingDataModel.share_occupancy_count == '' ){
                    return false;
                  }else{
                    this.submitDataModel.id=this.dbSettingDataModel?.share_id ;
                    this.submitDataModel.shareType=this.dbSettingDataModel.share_type ;
                    this.submitDataModel.shareOccupancyCount=this.dbSettingDataModel.share_occupancy_count ;
                  }
            break;
          case 'Room Type':
              if(this.dbSettingDataModel.room_type_name == null || this.dbSettingDataModel.room_type_name == '' ){
                return false;
              }else{
                this.submitDataModel.id=this.dbSettingDataModel?.room_type_id ;
                this.submitDataModel.roomTypeName=this.dbSettingDataModel.room_type_name ;
              }
          break;
          case 'Rent Cycle':
          
              if (this.dbSettingDataModel.cycle_first == null || this.dbSettingDataModel.cycle_first == '' || 
                  this.dbSettingDataModel.cycle_second == null || this.dbSettingDataModel.cycle_second == '') {
                  
                  return false;
              } else {
                  this.dbSettingDataModel.cycle_first = Number(this.dbSettingDataModel.cycle_first) < 10 ? ('0'+this.dbSettingDataModel.cycle_first) :this.dbSettingDataModel.cycle_first ;
                  this.dbSettingDataModel.cycle_second = Number(this.dbSettingDataModel.cycle_second) < 10 ? ('0'+this.dbSettingDataModel.cycle_second ):this.dbSettingDataModel.cycle_second;

                  this.dbSettingDataModel.cycle_name = `${this.dbSettingDataModel.cycle_first}-${this.dbSettingDataModel.cycle_second}`;
                  
                  this.submitDataModel.id = this.dbSettingDataModel?.cycle_id;
                  this.submitDataModel.rentCycleName = this.dbSettingDataModel.cycle_name;
              }
              break;

          case 'PG Type':
                if ( this.dbSettingDataModel.pg_type_name == null || this.dbSettingDataModel.pg_type_name == '') {
                    return false;
                } else {
                    this.submitDataModel.id = this.dbSettingDataModel?.pg_type_id;
                    this.submitDataModel.pgTypeName = this.dbSettingDataModel.pg_type_name;
                }
          break;
          case 'Notification Mode':
                if ( this.dbSettingDataModel.notification_mod_name == null || this.dbSettingDataModel.notification_mod_name == '') {
                    return false;
                } else {
                    this.submitDataModel.id = this.dbSettingDataModel?.notification_mode_id;
                    this.submitDataModel.notificationModeName = this.dbSettingDataModel.notification_mod_name;
                }
          break;
          case 'Factor':
                if ( this.dbSettingDataModel.factor_name == null || this.dbSettingDataModel.factor_name == '') {
                    return false;
                } else {
                    this.submitDataModel.id = this.dbSettingDataModel?.factor_id;
                    this.submitDataModel.factorName = this.dbSettingDataModel.factor_name;
                }
          break;
          case 'Due Type':
            if (this.dbSettingDataModel.due_type_name == null || this.dbSettingDataModel.due_type_name == '') {
                return false;
            } else {
                this.submitDataModel.id = this.dbSettingDataModel?.due_type_id;
                this.submitDataModel.dueTypeName = this.dbSettingDataModel.due_type_name;
            }
            break;
            case 'Currency Type':
              if (this.dbSettingDataModel.currency_name == null || this.dbSettingDataModel.currency_name == '') {
                  return false;
              } else {
                  this.submitDataModel.id = this.dbSettingDataModel?.currency_id;
                  this.submitDataModel.currencyName = this.dbSettingDataModel.currency_name;
              }
              break;
          case 'Billing Type':
              if (this.dbSettingDataModel.billing_type_name == null || this.dbSettingDataModel.billing_type_name == '') {
                  return false;
              } else {
                  this.submitDataModel.id = this.dbSettingDataModel?.billing_type_id;
                  this.submitDataModel.billingTypeName = this.dbSettingDataModel.billing_type_name;
              }
              break;
          case 'Ameneties':
            this.submittedPicture=true;
            var form_data = new FormData();
            if(this.formPicture.invalid){
              return;
              }
		        form_data.append('image', this.fileData);
              if (this.dbSettingDataModel.ameneties_name == null || this.dbSettingDataModel.ameneties_name == '') {
                  return false;
              } else {
                  this.submitDataModel.id = this.dbSettingDataModel?.ameneties_id;
                  this.submitDataModel.ameneties = this.dbSettingDataModel.ameneties_name;
              }
              break;
          default:
            return false;
        }
      
        return true;
      }

      resetChange(){
        this.previewUrl=false;
        this.formPicture.reset();
        this.fileUploadSizeStatus=false;
        this.submittedPicture=false;
        this.fileData = null;
      }

      formPicture = new FormGroup({
        ImageInput : new FormControl('', [Validators.required]),
        fileSource : new FormControl('', [Validators.required])
        });
   
        fileData: File = null;
        previewUrl:any = null;
        fileUploadProgress: string = null;
        uploadStatus:boolean=true;
        fileUploadSize:any;
        fileUploadSizeStatus:boolean=false;
        fileWidth:number;
        fileHeight:number;
        imgeURL2: string = null;
       onFileChanged(event) {
        this.previewUrl=false;
        if(event.target.files.length>0){
          const file=event.target.files[0];
          this.formPicture.patchValue({
          fileSource:file
          });
          this.fileData = <File>event.target.files[0];
          this.fileUploadSize=file.size/1024;
          if (this.fileUploadSize <= 50) {
            const img = new Image();
            img.onload = () => {
                this.fileWidth = img.width;
                this.fileHeight = img.height;

                if (this.fileWidth <= 75 && this.fileHeight <= 75) {
                    this.fileUploadSizeStatus = false;
                    this.preview();
                } else {
                    this.fileUploadSizeStatus = true;
                    // alert('Image dimensions must be exactly 50px by 50px.');
                }
            };
            img.src = URL.createObjectURL(file);
        } else {
            this.fileUploadSizeStatus = true;
            // alert('File size must be 50 KB or less.');
        }
        }
      }
      preview() {
        // Show preview 
        var mimeType = this.fileData.type;
        if (mimeType.match(/image\/*/) == null) {
          return;
        }
        this.uploadStatus=false;
        var reader = new FileReader();      
        reader.readAsDataURL(this.fileData); 
        reader.onload = (_event) => { 
          this.previewUrl = reader.result; 
          var img=new Image();
          img.onload=()=>{
            this.fileHeight=img.height;
            this.fileWidth=img.width;
          }
        }
      } 
}

  
