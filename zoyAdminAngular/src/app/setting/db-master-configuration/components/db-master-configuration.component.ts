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
import { MatTableDataSource } from '@angular/material/table';
import { DbMasterConfigurationService } from '../services/db-master-configuration.service';
import { DbSettingDataModel, DbSettingSubmitDataModel, settingTypeObjClmApiDetailsModel } from '../models/db-setting-models';
import { FormControl, FormGroup } from '@angular/forms';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';

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
  submitted:boolean= false ;
  isCreated :boolean=true;
  // shortTermData:ShortTermDataModel = new ShortTermDataModel();
  // shortTermDataList:ShortTermDataModel[] = [];
  // shortTermduration:number=0;
  @ViewChild('closeModel') closeModel: ElementRef;
	constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,private confirmationDialogService:ConfirmationDialogService,
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

  changeSettingType(){
    if(this.settingType == 'Amenities' ){
    this.resetChange();
    }
    this.settingTypeDetails = this.settingTypeObjClmApiDetailsList.find(t=>t.type == this.settingType);
    this.selectedsettingColumns = this.settingTypeDetails.columns ;
    this.getDbSettingDetails() ;
    // if(this.settingType == 'Short Term' ){
    //   this.getShortTermDuration() ;
    // }
   
  }

  // getShortTermDuration(){
  //     this.authService.checkLoginUserVlidaate();
  //    this.dbMasterConfigurationService.getShortTermDuration().subscribe(res => {
  //     if(res.data){
  //       this.shortTermduration = res.data?.rentingDurationDays | 0;
  //     }else{
  //       this.shortTermduration = 0;
  //     }
  //    }, error => {
  //    this.spinner.hide();
  //    if(error.status == 0) {
  //      this.notifyService.showError("Internal Server Error/Connection not established", "")
  //     }else if(error.status==401){
  //      console.error("Unauthorised");
  //    }else if(error.status==403){
  //    this.router.navigate(['/forbidden']);
  //    }else if (error.error && error.error.message) {
  //    this.errorMsg = error.error.message;
  //    console.log("Error:" + this.errorMsg);
  //    this.notifyService.showError(this.errorMsg, "");
  //    } else {
  //    if (error.status == 500 && error.statusText == "Internal Server Error") {
  //      this.errorMsg = error.statusText + "! Please login again or contact your Help Desk.";
  //    } else {
  //      let str;
  //      if (error.status == 400) {
  //      str = error.error.error;
  //      } else {
  //      str = error.error.message;
  //      str = str.substring(str.indexOf(":") + 1);
  //      }
  //      console.log("Error:" ,str);
  //      this.errorMsg = str;
  //    }
  //    if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
  //    }
  //  });
  //  }

   navigateMasterConfig(){
		if(this.rolesArray.includes('CONFIGURATION_MASTER_WRITE') || this.rolesArray.includes('CONFIGURATION_MASTER_APPROVAL_WRITE')){
			this.router.navigate(['/configuration-master']);
		}else{
			this.notifyService.showInfo("Please contact a higher-level admin","You do not have permission.");
		}
	  }
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

   
    createSetting(){
      this.submitted = false;
      this.isCreated = true;
      this.dbSettingDataModel = new DbSettingDataModel();
      this.previewUrl=false;
      this.previewUrlDueType=false;
      this.resetChange();
    }
    getElement(row:any){
      this.submitted = false;
      this.isCreated = false;
      this.previewUrl=false;
      this.previewUrlDueType=false;
      this.imgeURL2=null;
      this.imgeURLDueType2=null;
      const data =  JSON.parse(JSON.stringify(row));
      this.dbSettingDataModel = Object.assign(new DbSettingDataModel(),data);
  
      if (this.settingType === 'Rent Cycle' && this.dbSettingDataModel.cycle_name) {
        const cycleParts = this.dbSettingDataModel.cycle_name.split('-');
    
        if (cycleParts.length === 2) {
            this.dbSettingDataModel.cycle_first = cycleParts[0];
            this.dbSettingDataModel.cycle_second = cycleParts[1];
        }
      } 

      if(this.settingType ==='Amenities'){
        this.imgeURL2=this.dbSettingDataModel?.ameneties_image;
        this.fileUploadSizeStatus=false;
        this.fileUploadRatioStatus=false;
        this.fileUploadTypeStatus = false;
      }
      if(this.settingType ==='Due Type'){
        this.imgeURLDueType2=this.dbSettingDataModel?.due_image;
        this.fileUploadSizeStatus=false;
        this.fileUploadRatioStatus=false;
        this.fileUploadTypeStatus = false;
      }
    }  
    submitData(){
      if(this.settingType ==='Rental Agreement Document'){
      this.rentalAgreementDocumentSubmit();
      }else{
        this.submitted = true;
        this.withPhoto=false;
      if(this.validation()){      
        this.authService.checkLoginUserVlidaate();
        
        var form_data = new FormData();
        if(this.settingTypeDetails.api=='zoy_admin/ameneties' && this.fileData!=null){
          this.withPhoto=true;     
          form_data.append('amenetiesImage', this.fileData);
          form_data.append('ameneties', this.submitDataModel.ameneties);
          form_data.append('id', this.submitDataModel.id);
        }

        if(this.settingTypeDetails.api=='zoy_admin/dueType' && this.fileDataDueType!=null){
          this.withPhoto=true;     
          form_data.append('dueTypeImage', this.fileDataDueType);
          form_data.append('dueTypeName', this.submitDataModel.dueTypeName);
          form_data.append('id', this.submitDataModel.id);
        }
        let isCreatedMsg= this.isCreated ? ' create' :' update';
        this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want '+isCreatedMsg+'?')
        .then(
           (confirmed) =>{
          if(confirmed){
            this.spinner.show();
            this.dbMasterConfigurationService.submitData(this.submitDataModel,this.isCreated,this.settingTypeDetails.api,form_data,this.withPhoto).subscribe(data => {
            this.closeModel.nativeElement.click(); 
            this.getDbSettingDetails();
            this.resetChange();
            this.notifyService.showSuccess("Data has been "+isCreatedMsg+"d successfully", "")
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
}

  numberOnly(event): boolean {
    const charCode = (event.which) ? event.which : event.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
      return false;
    }
    return true;
  }
   
  withPhoto:boolean=false;
      validation():boolean{
        switch (this.settingType) {
          case 'Share Type':
                  if(this.dbSettingDataModel.share_type == null || this.dbSettingDataModel.share_type == '' || this.dbSettingDataModel.share_occupancy_count == null || this.dbSettingDataModel.share_occupancy_count == '' ){
                    return false;
                  }

                  const shareType = this.dbSettingDataList.filter(d=>d.share_id != this.dbSettingDataModel?.share_id && ( d?.share_type?.toLowerCase() === this.dbSettingDataModel?.share_type?.toLowerCase() || d.share_occupancy_count == this.dbSettingDataModel.share_occupancy_count) );
                  if(shareType.length>0){
                    this.notifyService.showError("Share type or share occupancy count is already available.", "");
                    return false;
                   }
                    this.submitDataModel.id=this.dbSettingDataModel?.share_id ;
                    this.submitDataModel.shareType=this.dbSettingDataModel.share_type ;
                    this.submitDataModel.shareOccupancyCount=this.dbSettingDataModel.share_occupancy_count ;
                  
            break;
          case 'Room Type':
              if(this.dbSettingDataModel.room_type_name == null || this.dbSettingDataModel.room_type_name == '' ){
                return false;
              }
              const roomType = this.dbSettingDataList.filter(d=>d.room_type_id != this.dbSettingDataModel?.room_type_id &&  d.room_type_name?.toLowerCase() === this.dbSettingDataModel.room_type_name?.toLowerCase()  );
              if(roomType.length>0){
               this.notifyService.showError("Room type is already available.", "");
               return false;
              }
                this.submitDataModel.id=this.dbSettingDataModel?.room_type_id ;
                this.submitDataModel.roomTypeName=this.dbSettingDataModel.room_type_name ;
              
          break;
          case 'Rent Cycle':
          
              if (this.dbSettingDataModel.cycle_first == null || this.dbSettingDataModel.cycle_first === '' || Number(this.dbSettingDataModel.cycle_first) < 1 || Number(this.dbSettingDataModel.cycle_first) > 31 ||
                  this.dbSettingDataModel.cycle_second == null || this.dbSettingDataModel.cycle_second === '' || Number(this.dbSettingDataModel.cycle_first) < 1 || Number(this.dbSettingDataModel.cycle_first) > 31) {
                  
                  return false;
                }
                  this.dbSettingDataModel.cycle_first =  Number(this.dbSettingDataModel.cycle_first) < 10 && !(this.dbSettingDataModel.cycle_first.toLowerCase().includes('0'))? ('0'+this.dbSettingDataModel.cycle_first) :this.dbSettingDataModel.cycle_first ;
                  this.dbSettingDataModel.cycle_second = Number(this.dbSettingDataModel.cycle_second) < 10 && !(this.dbSettingDataModel.cycle_second.toLowerCase().includes('0')) ? ('0'+this.dbSettingDataModel.cycle_second ):this.dbSettingDataModel.cycle_second;

                  this.dbSettingDataModel.cycle_name = `${this.dbSettingDataModel.cycle_first}-${this.dbSettingDataModel.cycle_second}`;
                  
                  this.submitDataModel.id = this.dbSettingDataModel?.cycle_id;
                  this.submitDataModel.rentCycleName = this.dbSettingDataModel.cycle_name;

                  const rentCycle = this.dbSettingDataList.filter(d=>d.cycle_id != this.dbSettingDataModel?.cycle_id &&  d.cycle_name?.toLowerCase() === this.dbSettingDataModel.cycle_name?.toLowerCase() );
                  if(rentCycle.length>0){
                    this.notifyService.showError("Rent Cycle is already available.", "");
                    return false;
                   }
              break;

          case 'PG Type':
                if ( this.dbSettingDataModel.pg_type_name == null || this.dbSettingDataModel.pg_type_name == '') {
                    return false;
                }
                const PGType = this.dbSettingDataList.filter(d=>d.pg_type_id != this.dbSettingDataModel?.pg_type_id &&  d.pg_type_name?.toLowerCase() === this.dbSettingDataModel.pg_type_name?.toLowerCase() );
                if(PGType.length>0){
                  this.notifyService.showError("PG Type is already available.", "");
                  return false;
                 }
                    this.submitDataModel.id = this.dbSettingDataModel?.pg_type_id;
                    this.submitDataModel.pgTypeName = this.dbSettingDataModel.pg_type_name;
                
          break;
          case 'Notification Mode':
                if ( this.dbSettingDataModel.notification_mod_name == null || this.dbSettingDataModel.notification_mod_name == '') {
                    return false;
                } 
                const notificationMode = this.dbSettingDataList.filter(d=>d.notification_mode_id != this.dbSettingDataModel?.notification_mode_id &&  d.notification_mod_name?.toLowerCase() === this.dbSettingDataModel.notification_mod_name?.toLowerCase() );
                if(notificationMode.length>0){
                  this.notifyService.showError("Notification Mode is already available.", "");
                  return false;
                 }
                    this.submitDataModel.id = this.dbSettingDataModel?.notification_mode_id;
                    this.submitDataModel.notificationModeName = this.dbSettingDataModel.notification_mod_name;
                
          break;
          case 'Factor':
                if ( this.dbSettingDataModel.factor_name == null || this.dbSettingDataModel.factor_name == '') {
                    return false;
                }
                const factor = this.dbSettingDataList.filter(d=>d.factor_id != this.dbSettingDataModel?.factor_id &&  d.factor_name?.toLowerCase() === this.dbSettingDataModel.factor_name?.toLowerCase() );
                if(factor.length>0){
                  this.notifyService.showError("Factor is already available.", "");
                  return false;
                 }
                    this.submitDataModel.id = this.dbSettingDataModel?.factor_id;
                    this.submitDataModel.factorName = this.dbSettingDataModel.factor_name;
                
          break;
          case 'Due Type':
            if (this.fileUploadRatioStatus || this.fileUploadSizeStatus || this.fileUploadTypeStatus || this.dbSettingDataModel.due_name == null || this.dbSettingDataModel.due_name == '' || (!this.isCreated && this.imgeURLDueType2==null) || (this.isCreated && (this.dbSettingDataModel.due_type_upload==null || this.dbSettingDataModel.due_type_upload==''))) {
                return false;
            } 
            const dueType = this.dbSettingDataList.filter(d=>d.due_type_id != this.dbSettingDataModel?.due_type_id &&  d.due_name?.toLowerCase() === this.dbSettingDataModel.due_name?.toLowerCase() );
            if(dueType.length>0){
              this.notifyService.showError("Due Type is already available.", "");
              return false;
             }
                this.submitDataModel.id = this.dbSettingDataModel?.due_type_id;
                this.submitDataModel.dueTypeName = this.dbSettingDataModel.due_name;
            
            break;
            case 'Currency Type':
              if (this.dbSettingDataModel.currency_name == null || this.dbSettingDataModel.currency_name == '') {
                  return false;
              } 
              const currencyType = this.dbSettingDataList.filter(d=>d.currency_id != this.dbSettingDataModel?.currency_id &&  d.currency_name?.toLowerCase() === this.dbSettingDataModel.currency_name?.toLowerCase() );
              if(currencyType.length>0){
                this.notifyService.showError("Currency Type is already available.", "");
                return false;
               }
                  this.submitDataModel.id = this.dbSettingDataModel?.currency_id;
                  this.submitDataModel.currencyName = this.dbSettingDataModel.currency_name;
              
              break;
          case 'Billing Type':
              if (this.dbSettingDataModel.billing_type_name == null || this.dbSettingDataModel.billing_type_name == '') {
                  return false;
              } 
              const billingType = this.dbSettingDataList.filter(d=>d.billing_type_id != this.dbSettingDataModel?.billing_type_id &&  d.billing_type_name?.toLowerCase() === this.dbSettingDataModel.billing_type_name?.toLowerCase() );
              if(billingType.length>0){
                this.notifyService.showError("Billing Type is already available.", "");
                return false;
               }
                  this.submitDataModel.id = this.dbSettingDataModel?.billing_type_id;
                  this.submitDataModel.billingTypeName = this.dbSettingDataModel.billing_type_name;
              
              break;
          case 'Amenities':
              if (this.fileUploadRatioStatus || this.fileUploadSizeStatus || this.fileUploadTypeStatus || this.dbSettingDataModel.ameneties_name == null || this.dbSettingDataModel.ameneties_name == '' || (!this.isCreated && this.imgeURL2==null) || (this.isCreated && (this.dbSettingDataModel.ameneties_upload==null || this.dbSettingDataModel.ameneties_upload==''))) {
                return false;
              } 
              const amenities = this.dbSettingDataList.filter(d=>d.ameneties_id != this.dbSettingDataModel?.ameneties_id &&  d.ameneties_name?.toLowerCase() === this.dbSettingDataModel.ameneties_name?.toLowerCase() );
              if(amenities.length>0){
                this.notifyService.showError("Amenities is already available.", "");
                return false;
               }
                  this.submitDataModel.id = this.dbSettingDataModel?.ameneties_id;
                  this.submitDataModel.ameneties = this.dbSettingDataModel.ameneties_name;
              
              break;
          case 'Floor Name':
                if ( this.dbSettingDataModel.floor_name == null || this.dbSettingDataModel.floor_name == '') {
                    return false;
                }
                const floorName = this.dbSettingDataList.filter(d=>d.floor_name_id != this.dbSettingDataModel?.floor_name_id &&  d.floor_name?.toLowerCase() === this.dbSettingDataModel.floor_name?.toLowerCase() );
                if(floorName.length>0){
                  this.notifyService.showError("Floor Name is already available.", "");
                  return false;
                 }
                    this.submitDataModel.id = this.dbSettingDataModel?.floor_name_id;
                    this.submitDataModel.floorName = this.dbSettingDataModel.floor_name;
                
               break;
          default:
            return false;
        }
      
        return true;
      }

      resetChange(){
        this.previewUrl=false;
        this.previewUrlDueType=false;
        this.formPicture.reset();
        this.fileUploadSizeStatus=false;
        this.fileUploadRatioStatus=false;
        this.fileUploadTypeStatus = false;
        this.submitted=false;
        this.fileData = null;
        this.fileDataDueType = null;
       this.fileWidth=0;
        this.fileHeight=0;
      }

      formPicture = new FormGroup({
        ImageInput : new FormControl(''),
        fileSource : new FormControl('')
        });
   
        fileData: File = null;
        fileDataDueType: File = null;
        previewUrl:any = null;
        previewUrlDueType:any = null;
      //  fileUploadProgress: string = null;
        uploadStatus:boolean=true;
        fileUploadSize:any;
        fileUploadSizeStatus:boolean=false;
        fileUploadRatioStatus:boolean=false;
        fileUploadTypeStatus:boolean=false;
        fileWidth:number;
        fileHeight:number;
        imgeURL2: string = null;
        imgeURLDueType2: string = null;
       onFileChanged(event,type:string) {

        this.fileUploadSizeStatus = false;
        this.fileUploadRatioStatus = false;
        this.fileUploadTypeStatus = false;

        this.previewUrl=false;
        this.previewUrlDueType=false;
        if(event.target.files.length>0){
          if(!this.isCreated && this.imgeURL2==null && type!='duetype'){
            this.imgeURL2='';
          }
          if(!this.isCreated && this.imgeURL2==null && type=='duetype'){
            this.imgeURLDueType2='';
          }
          const file=event.target.files[0];

          if (!file.type.startsWith('image/')) {
            this.fileUploadTypeStatus = true;
            return;  
          }

          this.formPicture.patchValue({
          fileSource:file
          });
          if(type!='duetype'){
          this.fileData = <File>event.target.files[0];
          }else if(type=='duetype'){
            this.fileDataDueType = <File>event.target.files[0];
          }
          this.fileUploadSize=file.size/1024;
          if (this.fileUploadSize <= 50) {
            const img = new Image();
            img.onload = () => {
                this.fileWidth = img.width;
                this.fileHeight = img.height;

                if (this.fileWidth <= 75 && this.fileHeight <= 75) {
                    this.fileUploadRatioStatus = false;
                    this.fileUploadSizeStatus = false;
                    if(type!='duetype'){
                    this.preview();
                    }else if(type=='duetype'){
                      this.previewDueType();
                    }
                } else {
                    this.fileUploadRatioStatus = true;
                    this.fileUploadSizeStatus = false;
                    // alert('Image dimensions must be exactly 50px by 50px.');
                }
            };
            img.src = URL.createObjectURL(file);
        } else {
            this.fileUploadSizeStatus = true;
            this.fileUploadRatioStatus = false;
            // alert('File size must be 50 KB or less.');
        }
        }else{
          if(!this.isCreated){
            this.imgeURL2=null;
            this.imgeURLDueType2=null;
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
      
      previewDueType() {
        // Show preview 
        var mimeType = this.fileDataDueType.type;
        if (mimeType.match(/image\/*/) == null) {
          return;
        }
        this.uploadStatus=false;
        var reader = new FileReader();      
        reader.readAsDataURL(this.fileDataDueType); 
        reader.onload = (_event) => { 
          this.previewUrlDueType = reader.result; 
          var img=new Image();
          img.onload=()=>{
            this.fileHeight=img.height;
            this.fileWidth=img.width;
          }
        }
      } 


  isNotValidNumber(value: any): boolean {
      return  0 == value || value === undefined || value === null || isNaN(value);
   }

  convertToNumber(value: any): number {
    return Number(value);
  }

  //  backUpshortTermDataList :ShortTermDataModel[]=[];
  //    getShortTermList(data:any){
  //     this.backUpshortTermDataList = [];
  //     data.forEach(element => {
  //       let model : ShortTermDataModel = new ShortTermDataModel();
  //       model.zoy_pg_short_term_master_id = element.zoy_pg_short_term_master_id; 
  //       model.start_day = element.start_day; 
  //       model.end_day = element.end_day;  
   
  //       this.backUpshortTermDataList.push(model);
  //     });
       
  //       this.shortTermDataList=JSON.parse(JSON.stringify(this.backUpshortTermDataList));
   
  //     }

  //     addShortTermVali:boolean=false
  //  addShortTerm() {
  //   this.addShortTermVali = true;
  //   if(!this.shortTermData.start_day|| this.shortTermData.start_day>this.shortTermduration || Number(this.shortTermData.start_day)===0 
  //       || !this.shortTermData.end_day|| this.shortTermData.end_day>this.shortTermduration || Number(this.shortTermData.end_day)===0
  //       || Number(this.shortTermData.start_day) >= Number(this.shortTermData.end_day)){
  //         return;
  //       }
  //   this.shortTermDataList.push(JSON.parse(JSON.stringify(this.shortTermData)));
  //   this.addShortTermVali = false;
  //   this.shortTermData = new ShortTermDataModel();
  //  }

  //  modifyShortTerm(shortTerm) {
  //    if(!shortTerm.start_day || Number(shortTerm.start_day)>this.shortTermduration || Number(shortTerm.start_day)===0 
  //       || !shortTerm.end_day || Number(shortTerm.end_day)>this.shortTermduration || Number(shortTerm.end_day)===0
  //       || Number(shortTerm.start_day) >= Number(shortTerm.end_day)){
  //     return;
  //   }
  //   shortTerm.isEdit = false;
  // }
  
  // removeShortTerm(shortTerm) {
  //   console.log("shortTerm",shortTerm)
  //   shortTerm.isDelete = true;
  // }
  
  // undoShortTermDelete(shortTerm) {
  //   shortTerm.isDelete = false;
  // }
  
  // undoEditShortTermItem(i:number) {
  //   this.shortTermDataList[i]=JSON.parse(JSON.stringify(this.backUpshortTermDataList[i]));
  // }

  // shortTermDataListReset(){
  //   this.shortTermDataList=JSON.parse(JSON.stringify(this.backUpshortTermDataList));
  // }
  // submitShortTerm:boolean = false;
  // submitShortTermData() {   
  //   let finalSubmitShortList = [];
  //   this.submitShortTerm = true;
  //   let startDay = this.shortTermduration;
  //   let endDay = 0;
   
  //   for (let i = 0; i < this.shortTermDataList.length; i++) {
  //     const term = this.shortTermDataList[i];

  //     if (!term.isDelete) {
  //       startDay = startDay>term.start_day?term.start_day:startDay ;
  //       endDay = endDay>term.end_day?endDay:term.end_day ;

  //       if (term.isEdit) {
  //         this.notifyService.showWarning("Save if term is being edited.","")
  //         return; 
  //       }
 
  //       for (let j = i + 1; j < this.shortTermDataList.length; j++) {
  //         const otherTerm = this.shortTermDataList[j];
          
  //         if (!(Number(term.end_day) < Number(otherTerm.start_day) || Number(term.start_day) > Number(otherTerm.end_day))) {
  //           this.notifyService.showWarning("The Short term duration period must not Overlapp.","")
  //           return;
  //         }

          
  //       }

  //       const ranges = this.shortTermDataList.filter(d=> Number(d.start_day) == (Number(term.end_day)+1));
  //       if(term.end_day != this.shortTermduration && ranges.length === 0 ){
  //          this.notifyService.showWarning('The Short term duration period must be within the defined ranges of 1-'+this.shortTermduration+' days.',"")
  //          return;
  //        }

  //       finalSubmitShortList.push(term);
  //     }
  //   }
  
  //   if (finalSubmitShortList.length < 1) {
  //     this.notifyService.showWarning("Please add durations","");
  //     return;
  //   }
   
  //   if( Number(startDay) != 1 || Number(endDay) !=this.shortTermduration){
  //     this.notifyService.showInfo('The Short term duration period must be within the defined ranges of 1-'+this.shortTermduration+' days.', "");
  //     return
  //   }

  //   if (JSON.stringify(finalSubmitShortList) === JSON.stringify(this.backUpshortTermDataList)) {
  //     this.notifyService.showInfo("Short term slabs details are already up to date.", "");
  //     return;
  //   }
  //   this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want Update ?')
  //   .then(
  //      (confirmed) =>{
  //     if(confirmed){
  //         this.dbMasterConfigurationService.submitShortTermData(finalSubmitShortList).subscribe(data => {
  //         this.closeModel.nativeElement.click(); 
  //         this.getShortTermList(data);
  //         this.dbSettingDataList=Object.assign([],data);
  //         this.dbSettingDataSource = new MatTableDataSource(this.dbSettingDataList);
  //         this.resetChange();
  //         this.submitShortTerm = false;
  //         this.spinner.hide();
  //         }, error => {
  //         this.spinner.hide();
  //         if(error.status == 0) {
  //           this.notifyService.showError("Internal Server Error/Connection not established", "")
  //         }else if(error.status==401){
  //           console.error("Unauthorised");
  //         }else if(error.status==403){
  //         this.router.navigate(['/forbidden']);
  //         }else if (error.error && error.error.message) {
  //         this.errorMsg = error.error.message;
  //         console.log("Error:" + this.errorMsg);
  //         this.notifyService.showError(this.errorMsg, "");
  //         } else {
  //         if (error.status == 500 && error.statusText == "Internal Server Error") {
  //           this.errorMsg = error.statusText + "! Please login again or contact your Help Desk.";
  //         } else {
  //           let str;
  //           if (error.status == 400) {
  //           str = error.error.error;
  //           } else {
  //           str = error.error.message;
  //           str = str.substring(str.indexOf(":") + 1);
  //           }
  //           console.log("Error:" ,str);
  //           this.errorMsg = str;
  //         }
  //         if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
  //         }
  //       });  
  //     }	
  //   }).catch(
  //     () => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)')
  //   );	   
  // }

  documentfile:File;
  documentVali:boolean=false;
  downloadProgress:boolean=false;
  onDocumentChanged(event: any) {
		this.documentVali=false;
		this.documentfile=event.target.files[0]; 
		const fileType = this.documentfile.type;
		 if(fileType !== 'application/vnd.openxmlformats-officedocument.wordprocessingml.document') {
			this.documentVali=true;
		 }else{
			this.documentVali=false;
		 }
	}

    rentalAgreementDocumentSubmit(){
      this.submitted=true;
      if(!this.documentfile || !this.dbSettingDataModel.rental_agreement_document || this.documentVali){
        return;
      }
      let isCreatedMsg= this.isCreated ? 'upload' :' update';
      var form_data = new FormData();
      form_data.append('document', this.documentfile);
      form_data.append('id', this.isCreated ?'0':this.dbSettingDataModel.rental_agreement_document_id);
      this.confirmationDialogService.confirm('Confirmation!!', 'are you sure you want '+isCreatedMsg+'?')
      .then(
        (confirmed) =>{
        if(confirmed){
          this.spinner.show();
          this.dbMasterConfigurationService.rentalAgreementDocumentSubmit(form_data).subscribe(data => {
          this.closeModel.nativeElement.click(); 
          this.getDbSettingDetails();
          this.resetChange();
          this.notifyService.showSuccess(data.message, "")
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
        
  downloadRentalAgreementDocument(url: string) {
      this.downloadProgress=true
      const link = document.createElement('a');
      link.href = url;
      link.download = "Rental_Agreement_Document.docx";
      link.target = '_blank';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      this.downloadProgress=false
    }

}

  
