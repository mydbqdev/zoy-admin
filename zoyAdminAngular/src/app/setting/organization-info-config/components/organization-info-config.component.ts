import { AfterViewInit, Component, ElementRef, inject, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
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
import { MatSort, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { UserDetails } from 'src/app/user-management/user-master/models/register-details';
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
  imgeURL2:any="assets/images/NotAvailable.jpg";
  editInfo:boolean;
  editOrg:boolean;
  public lastPageSize:number=0;
  pageSize: number = 10; 
  pageSizeOptions: number[] = [10, 20, 50]; 
  totalProduct: number = 0; 
  displayedColumns: string[] = ['type', 'addressLineOne', 'city', 'state', 'pinCode','contactNumberOne','emailOne','action'];
  public ELEMENT_DATA:OrganizationBranchInfoModel[];
  orginalFetchData:OrganizationBranchInfoModel[];
  dataSource:MatTableDataSource<OrganizationBranchInfoModel>=new MatTableDataSource<OrganizationBranchInfoModel>();
  searchText :string;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild('closeModal') closeModal : ElementRef;
   private _liveAnnouncer = inject(LiveAnnouncer);
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
          const reader =new FileReader();
          reader.readAsDataURL(new Blob([res.logo]));
          reader.onload=(e)=>this.imgeURL2=e.target.result; 
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
        this.totalProduct = this.mockdata.length
        this.ELEMENT_DATA = Object.assign([],this.mockdata);
        this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
      }else{
        this.totalProduct = 0;
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

    mockdata= [
      {
        "companyProfileId": "CP123456789",
        "type": "Head Office",
        "addressLineOne": "123 Business Park Road",
        "city": "Dindea City",
        "state": "Dindea State",
        "pinCode": "456789",
        "emailOne": "contact@companydindea.com",
        "contactNumberOne": "+1234567890",
        "emailId2": "support@companydindea.com",
        "contactNumber2": "+0987654321"
      },
      {
        "companyProfileId": "CP987654321",
        "type": "Branch Office",
        "addressLineOne": "456 Corporate Plaza",
        "city": "Techville",
        "state": "Dindea State",
        "pinCode": "123456",
        "emailOne": "branch@companydindea.com",
        "contactNumberOne": "+1987654321",
        "emailId2": "info@companydindea.com",
        "contactNumber2": "+1234567899"
      },
      {
        "companyProfileId": "CP564738291",
        "type": "Regional Office",
        "addressLineOne": "789 Industrial Avenue",
        "city": "Industrial Town",
        "state": "Dindea State",
        "pinCode": "789123",
        "emailOne": "regional@companydindea.com",
        "contactNumberOne": "+1122334455",
        "emailId2": "contact@companydindea.com",
        "contactNumber2": "+9988776655"
      },
      {
        "companyProfileId": "CP1122334455",
        "type": "Sales Office",
        "addressLineOne": "101 Sales Square",
        "city": "Dindea City",
        "state": "Dindea State",
        "pinCode": "654321",
        "emailOne": "sales@companydindea.com",
        "contactNumberOne": "+1144556677",
        "emailId2": "support@companydindea.com",
        "contactNumber2": "+1222333444"
      },
      {
        "companyProfileId": "CP5566778899",
        "type": "Customer Service",
        "addressLineOne": "202 Service Lane",
        "city": "Service City",
        "state": "Dindea State",
        "pinCode": "321654",
        "emailOne": "service@companydindea.com",
        "contactNumberOne": "+1239876543",
        "emailId2": "help@companydindea.com",
        "contactNumber2": "+1298765432"
      },
        {
          "companyProfileId": "CP123456789",
          "type": "Head Office",
          "addressLineOne": "123 Business Park Road",
          "city": "Dindea City",
          "state": "Dindea State",
          "pinCode": "456789",
          "emailOne": "contact@companydindea.com",
          "contactNumberOne": "+1234567890",
          "emailId2": "support@companydindea.com",
          "contactNumber2": "+0987654321"
        },
        {
          "companyProfileId": "CP987654321",
          "type": "Branch Office",
          "addressLineOne": "456 Corporate Plaza",
          "city": "Techville",
          "state": "Dindea State",
          "pinCode": "123456",
          "emailOne": "branch@companydindea.com",
          "contactNumberOne": "+1987654321",
          "emailId2": "info@companydindea.com",
          "contactNumber2": "+1234567899"
        },
        {
          "companyProfileId": "CP564738291",
          "type": "Regional Office",
          "addressLineOne": "789 Industrial Avenue",
          "city": "Industrial Town",
          "state": "Dindea State",
          "pinCode": "789123",
          "emailOne": "regional@companydindea.com",
          "contactNumberOne": "+1122334455",
          "emailId2": "contact@companydindea.com",
          "contactNumber2": "+9988776655"
        },
        {
          "companyProfileId": "CP1122334455",
          "type": "Sales Office",
          "addressLineOne": "101 Sales Square",
          "city": "Dindea City",
          "state": "Dindea State",
          "pinCode": "654321",
          "emailOne": "sales@companydindea.com",
          "contactNumberOne": "+1144556677",
          "emailId2": "support@companydindea.com",
          "contactNumber2": "+1222333444"
        },
        {
          "companyProfileId": "CP5566778899",
          "type": "Customer Service",
          "addressLineOne": "202 Service Lane",
          "city": "Service City",
          "state": "Dindea State",
          "pinCode": "321654",
          "emailOne": "service@companydindea.com",
          "contactNumberOne": "+1239876543",
          "emailId2": "help@companydindea.com",
          "contactNumber2": "+1298765432"
        },
        {
          "companyProfileId": "CP6688994455",
          "type": "Support Office",
          "addressLineOne": "303 Support Road",
          "city": "Supportville",
          "state": "Dindea State",
          "pinCode": "987654",
          "emailOne": "support@companydindea.com",
          "contactNumberOne": "+1012345678",
          "emailId2": "care@companydindea.com",
          "contactNumber2": "+1112233445"
        },
        {
          "companyProfileId": "CP4455778899",
          "type": "Warehouse",
          "addressLineOne": "404 Industrial Road",
          "city": "Logistics City",
          "state": "Dindea State",
          "pinCode": "568934",
          "emailOne": "warehouse@companydindea.com",
          "contactNumberOne": "+1234567891",
          "emailId2": "logistics@companydindea.com",
          "contactNumber2": "+2345678902"
        },
        {
          "companyProfileId": "CP9900112233",
          "type": "Marketing Office",
          "addressLineOne": "505 Marketing Hub",
          "city": "Marketville",
          "state": "Dindea State",
          "pinCode": "234567",
          "emailOne": "marketing@companydindea.com",
          "contactNumberOne": "+1010101010",
          "emailId2": "promo@companydindea.com",
          "contactNumber2": "+1020304050"
        },
        {
          "companyProfileId": "CP6677889900",
          "type": "Training Center",
          "addressLineOne": "606 Training Lane",
          "city": "Educity",
          "state": "Dindea State",
          "pinCode": "123789",
          "emailOne": "training@companydindea.com",
          "contactNumberOne": "+2020304050",
          "emailId2": "education@companydindea.com",
          "contactNumber2": "+2121212121"
        },
        {
          "companyProfileId": "CP2233445566",
          "type": "Product Design Office",
          "addressLineOne": "707 Design Boulevard",
          "city": "Creative City",
          "state": "Dindea State",
          "pinCode": "445566",
          "emailOne": "design@companydindea.com",
          "contactNumberOne": "+3031323344",
          "emailId2": "innovation@companydindea.com",
          "contactNumber2": "+3132334455"
        },
        {
          "companyProfileId": "CP8877665544",
          "type": "Legal Office",
          "addressLineOne": "808 Legal Plaza",
          "city": "Lawcity",
          "state": "Dindea State",
          "pinCode": "654987",
          "emailOne": "legal@companydindea.com",
          "contactNumberOne": "+4040506070",
          "emailId2": "attorney@companydindea.com",
          "contactNumber2": "+5050607071"
        },
        {
          "companyProfileId": "CP3344556677",
          "type": "IT Support",
          "addressLineOne": "909 Tech Lane",
          "city": "Techopolis",
          "state": "Dindea State",
          "pinCode": "323423",
          "emailOne": "it@companydindea.com",
          "contactNumberOne": "+6060708090",
          "emailId2": "support@companydindea.com",
          "contactNumber2": "+7070809091"
        },
        {
          "companyProfileId": "CP1100223344",
          "type": "Accounting Office",
          "addressLineOne": "1010 Finance Road",
          "city": "Financtown",
          "state": "Dindea State",
          "pinCode": "224455",
          "emailOne": "accounts@companydindea.com",
          "contactNumberOne": "+8080910112",
          "emailId2": "billing@companydindea.com",
          "contactNumber2": "+9091022233"
        },
        {
          "companyProfileId": "CP1199776655",
          "type": "Research Office",
          "addressLineOne": "1212 Research Street",
          "city": "Innovation City",
          "state": "Dindea State",
          "pinCode": "112233",
          "emailOne": "research@companydindea.com",
          "contactNumberOne": "+1213141516",
          "emailId2": "data@companydindea.com",
          "contactNumber2": "+1314151617"
        },
        {
          "companyProfileId": "CP2233667799",
          "type": "Customer Relations",
          "addressLineOne": "1313 Customer Drive",
          "city": "Relatown",
          "state": "Dindea State",
          "pinCode": "445577",
          "emailOne": "relations@companydindea.com",
          "contactNumberOne": "+1415161718",
          "emailId2": "clients@companydindea.com",
          "contactNumber2": "+1516171819"
        },
        {
          "companyProfileId": "CP7755889922",
          "type": "Inventory Office",
          "addressLineOne": "1414 Stock Lane",
          "city": "Stockport",
          "state": "Dindea State",
          "pinCode": "556677",
          "emailOne": "inventory@companydindea.com",
          "contactNumberOne": "+1617181920",
          "emailId2": "stocks@companydindea.com",
          "contactNumber2": "+1718192021"
        },
        {
          "companyProfileId": "CP8899776655",
          "type": "Event Office",
          "addressLineOne": "1515 Event Plaza",
          "city": "Eventcity",
          "state": "Dindea State",
          "pinCode": "667788",
          "emailOne": "events@companydindea.com",
          "contactNumberOne": "+1819202122",
          "emailId2": "organize@companydindea.com",
          "contactNumber2": "+1920212223"
        },
        {
          "companyProfileId": "CP6655334422",
          "type": "Quality Assurance",
          "addressLineOne": "1616 Quality Road",
          "city": "Qacity",
          "state": "Dindea State",
          "pinCode": "778899",
          "emailOne": "quality@companydindea.com",
          "contactNumberOne": "+2021222324",
          "emailId2": "assurance@companydindea.com",
          "contactNumber2": "+2122232425"
        },
        {
          "companyProfileId": "CP2233441100",
          "type": "Compliance Office",
          "addressLineOne": "1717 Law Lane",
          "city": "Compliancetown",
          "state": "Dindea State",
          "pinCode": "334455",
          "emailOne": "compliance@companydindea.com",
          "contactNumberOne": "+2223242526",
          "emailId2": "audit@companydindea.com",
          "contactNumber2": "+2324252627"
        }
      
      
    ]
    
      

        sortActive:string='';
        sortDirection:string='';
    sortChange(sortActive: string, sortDirection: string){
   
      this.sortDirection = this.sortActive === sortActive ? (this.sortDirection == 'desc' ?'asc':'desc'):sortDirection;
      this.sortActive=sortActive ;
      console.log("sortActive",sortActive,"sortDirection",sortDirection)
      const sorterdList = this.sorting();
      this.ELEMENT_DATA = JSON.parse(JSON.stringify(sorterdList));
      this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
    }

    sorting( ): OrganizationBranchInfoModel[] {
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
        if ($event.keyCode === 13) {
        this.filterData();
        }
      }

      filterData(){
        
        if(this.searchText==''){
          this.ELEMENT_DATA = Object.assign([],this.orginalFetchData);
          this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
    
        }else{
          
          const pagedData = Object.assign([], this.orginalFetchData.filter(data =>
            data.addressLineOne.toLowerCase().includes(this.searchText.toLowerCase()) || 
            data.city.toLowerCase().includes(this.searchText.toLowerCase()) || 
            data.contactNumberOne?.toLowerCase().includes(this.searchText.toLowerCase()) || 
            data.emailOne?.toLowerCase().includes(this.searchText.toLowerCase()) || 
            data.pinCode?.toLowerCase().includes(this.searchText.toLowerCase()) || 
            data.state?.toLowerCase().includes(this.searchText.toLowerCase()) || 
            data.type?.toLowerCase().includes(this.searchText.toLowerCase()) ||
            data.addressLineTwo?.toLowerCase().includes(this.searchText.toLowerCase()) ||
            data.addressLineThree?.toLowerCase().includes(this.searchText.toLowerCase()) ||
            data.emailTwo?.toLowerCase().includes(this.searchText.toLowerCase()) ||
            data.contactNumberTwo?.toLowerCase().includes(this.searchText.toLowerCase())
          ));
          
          this.ELEMENT_DATA = Object.assign([],pagedData);
          this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
          
        }

        console.log("this.paginator.pageIndex",this.paginator.pageIndex,this.pageSize);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
      }

      pageChanged(event:any){
       // this.reportDataSource=new MatTableDataSource<any>();
        if(this.lastPageSize!=event.pageSize){
          this.paginator.pageIndex=0;
          event.pageIndex=0;
           }
         this.pageSize=event.pageSize;
         this.filterData();
        // this.getReportDetails(this.paginator.pageIndex , event.pageSize,this.sortActive,this.sortDirection);
         }

      task:string='';
      openModel(element:any,task:string){
        this.submittedBranchInfo=false;
        this.task=task;
        this.infoModel = JSON.parse(JSON.stringify(element))
      }

      submittedBranchInfo:boolean;
      submittedMainInfo:boolean;
      infoModel:OrganizationBranchInfoModel = new OrganizationBranchInfoModel();
      branchType:string[]=['Head Branch','Sub Branch ']
      OrganizationMainBranchInfoModel
      mainBranchInfo:OrganizationMainBranchInfoModel = new OrganizationMainBranchInfoModel();
      

      submitBranchInfo(){
        this.submittedBranchInfo=true;
         this.authService.checkLoginUserVlidaate();
         this.spinner.show();
         this.organizationInfoConfigService.submitBranchInfo(this.infoModel).subscribe(res => {
          console.log(res.data)
          if(res.data !=null && res.data?.length>0){
            this.totalProduct = res.data.length
            this.ELEMENT_DATA = Object.assign([],res.data);
            this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
          }else{
            this.totalProduct = 0;
            this.ELEMENT_DATA = Object.assign([]);
            this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
          }
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
       orgMainBranchInfo:OrganizationMainBranchInfoModel=new OrganizationMainBranchInfoModel();
       editMainBranchInfo(){
        this.submittedMainInfo = false ;
        this.editOrg=!this.editOrg ;
        this.mainBranchInfo = JSON.parse(JSON.stringify(this.orgMainBranchInfo)) 
       }


       submitMainBranchInfo(){
        this.submittedMainInfo = true ;
         this.authService.checkLoginUserVlidaate();
         const model =JSON.stringify(this.mainBranchInfo);
			   var form_data = new FormData();

			   form_data.append('companyProfile', model);
			//	form_data.append("companyLogo",this.upfile);

         this.spinner.show();
         this.organizationInfoConfigService.submitMainBranchInfo(form_data).subscribe(res => {
          this.orgMainBranchInfo = res.data;
          this.mainBranchInfo = JSON.parse(JSON.stringify(this.orgMainBranchInfo)) ;
          if(!res.logo && res.logo?.size!=0){ 
            const reader =new FileReader();
            reader.readAsDataURL(new Blob([res.logo]));
            reader.onload=(e)=>this.imgeURL2=e.target.result; 
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
}
