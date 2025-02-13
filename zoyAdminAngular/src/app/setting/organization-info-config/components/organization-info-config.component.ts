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
  imgeURL2:any="data:image/jpeg;base64,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";
  previewUrl:any="data:image/jpeg;base64,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";
  editInfo:boolean;
  editOrg:boolean;
  public lastPageSize:number=0;
  pageSize: number = 5; 
  pageSizeOptions: number[] = [5,10, 20, 50]; 
  totalProduct: number = 0; 
  displayedColumns: string[] = ['type', 'addressLineOne', 'city', 'state', 'pinCode','contactNumberOne','emailOne','action'];
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
  branchType:string[]=['Head Branch','Sub Branch ']
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
        console.log(res);
        this.orgMainBranchInfo = res.data;
        this.mainBranchInfo = JSON.parse(JSON.stringify(this.orgMainBranchInfo)) ;
        if(!res.logo && res.logo?.size!=0){ 
          const blob = new Blob([new Uint8Array(res.logo)], { type: 'image/png' });
          const reader = new FileReader();
          reader.onload = (e) => {
            this.imgeURL2 = e.target?.result as string;
          };
          
          reader.readAsDataURL(blob);
          // const reader =new FileReader();
          
          // reader.readAsDataURL(new Blob([res.logo]));
          // reader.onload=(e)=>this.imgeURL2=e.target.result; 
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
       this.spinner.show();
       this.organizationInfoConfigService.getOrganizationBranchInfo().subscribe(res => {
        console.log(res.data)
      if(res.data !=null && res.data?.length>0){
        this.totalProduct = res.data.length
        this.orginalFetchData = JSON.parse(JSON.stringify(res.data));
        this.ELEMENT_DATA = Object.assign([],this.orginalFetchData);
        this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
      }else{
        this.totalProduct = 0;
        this.orginalFetchData = [];
        this.ELEMENT_DATA = Object.assign([]);
        this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
      }
      this.lastPageSize=this.pageSize;
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;
      console.log()
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
              this.previewUrl = reader.result;  // Set preview URL
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
        const pinCodePattern = /^[0-9]{5}$/;  
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

      submitBranchInfo(){
        this.submittedBranchInfo=true;
         console.log("this.infoModel",this.infoModel);
         if (!this.infoModel.type || !this.infoModel.type || 
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
    
         this.authService.checkLoginUserVlidaate();
         this.spinner.show();
         this.organizationInfoConfigService.submitBranchInfo(this.infoModel).subscribe(res => {
          console.log(res.data)
          if(res.data !=null && res.data?.length>0){
            this.totalProduct = res.data.length
            this.orginalFetchData = JSON.parse(JSON.stringify(res.data));
            this.ELEMENT_DATA = Object.assign([],this.orginalFetchData);
            this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
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
        this.mainBranchInfo = JSON.parse(JSON.stringify(this.orgMainBranchInfo)) 
       }


       submitMainBranchInfo(){
        this.submittedMainInfo = true ;
        if (!this.mainBranchInfo.companyName || !this.mainBranchInfo.gstNumber || 
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
         if(this.fileUploadSizeStatus){
          form_data.append("companyLogo",this.selectedLogo);
         }else{
          form_data.append("companyLogo",this.mainBranchInfo.logo);
         }
         this.spinner.show();
         this.organizationInfoConfigService.submitMainBranchInfo(form_data).subscribe(res => {
          this.orgMainBranchInfo = res.data;
          this.mainBranchInfo = JSON.parse(JSON.stringify(this.orgMainBranchInfo)) ;
          if(!res.logo && res.logo?.size!=0){ 
            const reader =new FileReader();
            reader.readAsDataURL(new Blob([res.logo]));
            reader.onload=(e)=>this.imgeURL2=e.target.result as string; 
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
