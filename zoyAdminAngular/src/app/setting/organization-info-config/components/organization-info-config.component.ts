import { AfterViewInit, Component, ElementRef, inject, OnInit, ViewChild } from '@angular/core';
import { FormBuilder} from '@angular/forms';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { AuthService } from 'src/app/common/service/auth.service';
import { DataService } from 'src/app/common/service/data.service';
import { UserService } from 'src/app/common/service/user.service';
import { ConfirmationDialogService } from 'src/app/common/shared/confirm-dialog/confirm-dialog.service';
import { NotificationService } from 'src/app/common/shared/message/notification.service';
import { SidebarComponent } from 'src/app/components/sidebar/sidebar.component';
import { OrganizationInfoConfigService } from '../services/organization-info-service';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { OrganizationBranchInfoModel, OrganizationMainBranchInfoModel } from '../models/organization-info-config-model';

@Component({
  selector: 'app-organization-info-config',
  templateUrl: './organization-info-config.component.html',
  styleUrl: './organization-info-config.component.css'
})
export class OrganizationInfoConfigComponent implements OnInit, AfterViewInit {
  public userNameSession: string = "";
  errorMsg: any = "";
  mySubscription: any;
  isExpandSideBar:boolean=true;
  @ViewChild(SidebarComponent) sidemenuComp;
  public rolesArray: string[] = [];
  imgeURL2:any="";
  previewUrl:any="";
  editInfo:boolean;
  editOrg:boolean;
  public lastPageSize:number=0;
  pageSize: number = 5; 
  pageSizeOptions: number[] = [5,10, 20, 50]; 
  totalProduct: number = 0; 
  displayedColumns: string[] = ['type', 'addressLineOne', 'city', 'state', 'pinCode','contactNumberOne','emailOne','status','action'];
  public ELEMENT_DATA:OrganizationBranchInfoModel[];
  orginalFetchData:OrganizationBranchInfoModel[]=[];
  dataSource:MatTableDataSource<OrganizationBranchInfoModel>=new MatTableDataSource<OrganizationBranchInfoModel>();
  searchText :string;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild('closeModel') closeModel : ElementRef;
  private _liveAnnouncer = inject(LiveAnnouncer);
  orgMainBranchInfo:OrganizationMainBranchInfoModel=new OrganizationMainBranchInfoModel();
  sortActive:string='';
  sortDirection:string='';
  selectedLogo: File | null = null;
  fileData: File | null = null;
  fileUploadSizeStatus: boolean = true;
  task:string='';
  submittedBranchInfo:boolean;
  submittedMainInfo:boolean;
  infoModel:OrganizationBranchInfoModel = new OrganizationBranchInfoModel();
  branchType:string[]=['Head Office','Branch Office'];
  status:string[]=['Open','On Hold','Closed'];
  OrganizationMainBranchInfoModel
  mainBranchInfo:OrganizationMainBranchInfoModel = new OrganizationMainBranchInfoModel();
  constructor(private route: ActivatedRoute, private router: Router,private formBuilder: FormBuilder, private dataService: DataService, private organizationInfoConfigService :OrganizationInfoConfigService,private userService:UserService,
      private spinner: NgxSpinnerService, private authService:AuthService,private notifyService: NotificationService, private confirmationDialogService:ConfirmationDialogService) {
        this.authService.checkLoginUserVlidaate();
        this.userNameSession = userService.getUsername();

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
      this.getOrganizationMailBranchInfo();
      this.getOrganizationBranchInfo();
     
    }
    ngAfterViewInit() {
      this.sidemenuComp.expandMenu(4);
      this.sidemenuComp.activeMenu(4, 'organization-info-config');
      this.dataService.setHeaderName("Organization Info Configuration");
    }
    
    getOrganizationMailBranchInfo(){
       this.authService.checkLoginUserVlidaate();
       this.spinner.show();
       this.organizationInfoConfigService.getOrganizationMailBranchInfo().subscribe(res => {
        this.orgMainBranchInfo = res.data;
        this.mainBranchInfo = JSON.parse(JSON.stringify(this.orgMainBranchInfo)) ;
        if(res.data?.logo && res.data?.logo.length > 0){ 
          const blob = new Blob([new Uint8Array(res.data.logo)], { type: 'image/png' });
          const reader = new FileReader();
          reader.onload = (e) => {
            this.imgeURL2 = e.target?.result as string;
          };
          reader.readAsDataURL(blob);  
          }else{
          this.imgeURL2="assets/images/NotAvailable.jpg";
          }
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


    getOrganizationBranchInfo(){
       this.authService.checkLoginUserVlidaate();
       this.organizationInfoConfigService.getOrganizationBranchInfo().subscribe(res => {
      if(res.data !=null && res.data?.length>0){
        this.totalProduct = res.data.length
        this.orginalFetchData = JSON.parse(JSON.stringify(res.data));
        this.ELEMENT_DATA = Object.assign([],this.orginalFetchData);
        this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
        this.sortChange('type','desc');
      }else{
        this.totalProduct = 0;
        this.orginalFetchData = [];
        this.ELEMENT_DATA = Object.assign([]);
        this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
      }
      this.lastPageSize=this.pageSize;
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;
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

    sortChange(sortActive: string, sortDirection: string){
      this.sortDirection = this.sortActive === sortActive ? (this.sortDirection == 'desc' ?'asc':'desc'):sortDirection;
      this.sortActive=sortActive ;
      const sorterdList = this.sorting();
      this.ELEMENT_DATA = JSON.parse(JSON.stringify(sorterdList));
      this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;
    }

    sorting(): OrganizationBranchInfoModel[] {
      const list = this.ELEMENT_DATA;
          return list.sort((a, b) => {
              let comparison = 0;
              if(a[this.sortActive]){
              comparison = a[this.sortActive].localeCompare(b[this.sortActive]); 
              }
              if (this.sortDirection === 'desc') {
                  comparison = -comparison;
              }
              return comparison;
          });
      }

      searchBy($event: KeyboardEvent){
        console.log("$event.keyCode",$event.keyCode)
        if ($event.keyCode === 13) {
        this.filterData();
        }
      }

      filterData(){
        if(this.searchText==''){
          this.ELEMENT_DATA = JSON.parse(JSON.stringify(this.orginalFetchData));
          this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
        }else{
          const searchText = this.searchText ? this.searchText.toLocaleLowerCase() : ''; 
          const pagedData = Object.assign([], this.orginalFetchData?.filter(data =>
            (data.addressLineOne?.toLowerCase())?.includes(searchText) || 
            (data.city?.toLowerCase())?.includes(searchText) || 
            (data.contactNumberOne?.toLowerCase())?.includes(searchText) || 
            (data.emailOne?.toLowerCase())?.includes(searchText) || 
            (data.status?.toLowerCase())?.includes(searchText) || 
            (data.pinCode?.toLowerCase())?.includes(searchText) || 
            (data.state?.toLowerCase())?.includes(searchText) || 
            (data.type?.toLowerCase())?.includes(searchText) ||
            (data.addressLineTwo?.toLowerCase())?.includes(searchText) ||
            (data.addressLineThree?.toLowerCase())?.includes(searchText) ||
            (data.emailTwo?.toLowerCase())?.includes(searchText) ||
            (data.contactNumberTwo?.toLowerCase())?.includes(searchText)
          ));
          
          this.ELEMENT_DATA = JSON.parse(JSON.stringify(pagedData)) ;
          this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
          
        }

        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
      }

      pageChanged(event:any){
        if(this.lastPageSize!=event.pageSize){
          this.paginator.pageIndex=0;
          event.pageIndex=0;
           }
         this.pageSize=event.pageSize;
         this.filterData();
         }

       
        resetFileData(): void {
          this.previewUrl = false;
          this.fileUploadSizeStatus = true;
          this.selectedLogo = null;
          this.fileData = null;
        }

        preview(): void {
          const mimeType = this.fileData?.type;
          if (mimeType && mimeType.match(/image\/*/) != null) {
            const reader = new FileReader();
            reader.readAsDataURL(this.fileData!);
            reader.onload = () => {
              this.previewUrl = reader.result;  
            };
          }
        }
        onFileChanged(event: any): void {
          this.previewUrl = false;  // Reset preview before a new file is selected
          const file = event.target.files[0];
      
          if (file) {
            this.selectedLogo = file;
            this.fileData = file;
      
            const fileSizeInKB = file.size / 1024;
            if (!(file.type === 'image/jpeg' || file.type === 'image/png')) {
              this.fileUploadSizeStatus = false;  
            } else if (fileSizeInKB <= 100) {
              this.fileUploadSizeStatus = true; 
              this.preview();
            } else {
              this.fileUploadSizeStatus = false;  
            }
          }
        }

      openModel(element:any,task:string){
        this.submittedBranchInfo=false;
        this.task=task;
        this.infoModel = JSON.parse(JSON.stringify(element?element:new OrganizationBranchInfoModel() ))
      }
  
      validateEmailFormat(email:string): boolean {
        const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        if (!emailPattern.test(email)) {
          return true;  
        } else {
          return false; 
        }
      }
      numberOnly(event): boolean {
        const charCode = (event.which) ? event.which : event.keyCode;
        if (charCode > 31 && (charCode < 48 || charCode > 57)) {
          return false;
        }
        return true;
      }
      validatePinCode(pinCode: string): boolean {
        const pinCodePattern = /^[0-9]{6}$/;  
        return !pinCodePattern.test(pinCode);
      }
      validatecontactNumber(contactNumber: string): boolean {
        const pinCodePattern = /^[0-9]{10}$/;  
        return !pinCodePattern.test(contactNumber);
      }
      validateUrl(url) {
        const urlPattern = /^(https?|ftp):\/\/[^\s/$.?#].[^\s]*$/i;
        return !urlPattern.test(url);
      }
      validatePanNumber(panNumber) {
        const panRegex = /^[A-Z]{5}[0-9]{4}[A-Z]{1}$/;
        return !panRegex.test(panNumber);
      }
      validateGstNumber(gstNumber) {
        const gstRegex = /^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[0-9]{1}[Z]{1}[A-Z0-9]{1}$/;
        return !gstRegex.test(gstNumber); 
    }

      submitBranchInfo(){
        this.submittedBranchInfo=true;
         if (!this.infoModel.type || !this.infoModel.type || !this.infoModel.status ||
              !this.infoModel.pinCode || this.validatePinCode(this.infoModel?.pinCode) ||
              !this.infoModel.city || !this.infoModel.state || 
              !this.infoModel.addressLineOne || !this.infoModel.addressLineTwo || !this.infoModel.addressLineThree || 
              !this.infoModel.emailOne || this.validateEmailFormat(this.infoModel.emailOne) || 
              !this.infoModel.emailTwo || this.validateEmailFormat(this.infoModel.emailTwo) || 
               this.infoModel.emailOne === this.infoModel.emailTwo ||
              !this.infoModel.contactNumberOne || this.validatecontactNumber(this.infoModel.contactNumberOne) || 
              !this.infoModel.contactNumberTwo || this.validatecontactNumber(this.infoModel.contactNumberTwo) || 
              this.infoModel.contactNumberOne === this.infoModel.contactNumberTwo 
            ){
            return;
          }

          if(this.infoModel.type === 'Head Office' && this.orginalFetchData.filter(org=>org.companyProfileId != this.infoModel.companyProfileId && org.type == this.infoModel.type).length>0 ){
            this.notifyService.showError('Head Office already exists',"");
            return;
          }

          if(this.orginalFetchData.filter(org=>org.companyProfileId != this.infoModel.companyProfileId && org.contactNumberOne == this.infoModel.contactNumberOne).length>0 ){
            this.notifyService.showError('Contact Number One already exists',"");
            return;
          }
    
         this.authService.checkLoginUserVlidaate();
         this.spinner.show();
         this.organizationInfoConfigService.submitBranchInfo(this.infoModel).subscribe(res => {
          if(this.infoModel.companyProfileId){
            this.notifyService.showSuccess("",this.infoModel.type+" has been updated successfully");
          }else{
            this.notifyService.showSuccess("",this.infoModel.type+" has been created successfully");
          }
        
          if(res.data !=null && res.data?.length>0){
            this.totalProduct = res.data.length
            this.orginalFetchData = JSON.parse(JSON.stringify(res.data));
            this.ELEMENT_DATA = Object.assign([],this.orginalFetchData);
            this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
            this.sortChange('type','desc');
          }else{
            this.totalProduct = 0;
            this.orginalFetchData = Object.assign([]);
            this.ELEMENT_DATA = Object.assign([]);
            this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
          }
          this.closeModel.nativeElement.click(); 
          this.submittedBranchInfo=false;
          this.lastPageSize=this.pageSize;
          this.dataSource.sort = this.sort;
          this.dataSource.paginator = this.paginator;
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
      
       editMainBranchInfo(){
        this.submittedMainInfo = false ;
        this.editOrg=!this.editOrg ;
        this.mainBranchInfo = JSON.parse(JSON.stringify(this.orgMainBranchInfo)) ;
       }

       cancelMainBranchInfo(){
        this.editOrg=!this.editOrg ;
        this.resetFileData();
       }

      onPincodeChange(event: any) {
        const pincode = event.target.value;
        if (pincode && pincode.length === 6) {
          this. getCityAndState(pincode);
        } else {
          this.infoModel.city = '';
          this.infoModel.state = '';
        }
      }

      getCityAndState(pincode){
        this.organizationInfoConfigService.getCityAndState(pincode).subscribe(res => {
        if (res.results && res.results?.length > 0 ) {
          const addressComponents = res.results[0].address_components;
          this.infoModel.city = this.extractCity(addressComponents);
          this.infoModel.state = this.extractState(addressComponents);
        } else {
          this.infoModel.city = '';
          this.infoModel.state = '';
        }      
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
        }
      );

      }

      extractCity(components: any[]) {
        for (const component of components) {
          if (component.types.includes('locality')) {
            return component.long_name;
          }
        }
        return '';
      }
    
      extractState(components: any[]) {
        for (const component of components) {
          if (component.types.includes('administrative_area_level_1')) {
            return component.long_name;
          }
        }
        return '';
      }


       submitMainBranchInfo(){
        this.submittedMainInfo = true ;
        if (!this.mainBranchInfo.companyName || !this.mainBranchInfo.gstNumber || this.validateGstNumber(this.mainBranchInfo.gstNumber) ||
          !this.mainBranchInfo.panNumber || this.validatePanNumber(this.mainBranchInfo.panNumber) ||
          !this.mainBranchInfo.url || this.validateUrl(this.mainBranchInfo.url) || !this.fileUploadSizeStatus
        ){
          return;
        }
         this.authService.checkLoginUserVlidaate();
         const data={"company_name":this.mainBranchInfo.companyName,"gst_number":this.mainBranchInfo.gstNumber,
                  "pan_number":this.mainBranchInfo.panNumber,"url":this.mainBranchInfo.url}
         const model =JSON.stringify(data);
			   var form_data = new FormData();

			   form_data.append('companyProfile', model);
         if(this.fileUploadSizeStatus && this.selectedLogo){
          form_data.append("companyLogo",this.selectedLogo);
         } else if(this.orgMainBranchInfo.logo){
          const byteArray = new Uint8Array(this.orgMainBranchInfo.logo.map(value => value < 0 ? 256 + value : value));
          const blob = new Blob([byteArray], { type: 'image/png' });

          form_data.append("companyLogo",  blob, 'companyLogo.png');
         }else{
          this.notifyService.showError("","Please select a logo to upload.")
         return;
         }
         this.spinner.show();
         this.organizationInfoConfigService.submitMainBranchInfo(form_data).subscribe(res => {
          this.notifyService.showSuccess("","Organization Basic Information has been updated successfully");
          this.orgMainBranchInfo = res.data;
          this.mainBranchInfo = JSON.parse(JSON.stringify(this.orgMainBranchInfo)) ;
          if(res.data?.logo && res.data?.logo.length > 0){ 
            const blob = new Blob([new Uint8Array(res.data.logo)], { type: 'image/png' });
            const reader = new FileReader();
            reader.onload = (e) => {
              this.imgeURL2 = e.target?.result as string;
            };
            reader.readAsDataURL(blob);  
            }else{
            this.imgeURL2="assets/images/NotAvailable.jpg";
            }
           this.submittedMainInfo = false ;
           this.editOrg =false;
           this.resetFileData();
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
}
