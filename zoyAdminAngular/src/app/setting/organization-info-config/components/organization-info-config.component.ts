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
import { OrganizationBranchInfoModel } from '../models/organization-info-config-model';

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
  pageSize: number = 10; pageSizeOptions: number[] = [10, 20, 50]; 
  totalProduct: number = 0; 
  displayedColumns: string[] = ['type', 'addressLine1', 'city', 'state', 'pincode','contactNumber1','emailId1','action'];
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
      this.getOrganizationBranchInfo();
     
    }
    ngAfterViewInit() {
      this.sidemenuComp.expandMenu(4);
      this.sidemenuComp.activeMenu(4, 'organization-info-config');
      this.dataService.setHeaderName("Organization Info Configuration");
    }

    
    getOrganizationBranchInfo(){
      this.searchText ='';
      this.orginalFetchData = JSON.parse(JSON.stringify(this.mockdata)) ;
      this.totalProduct =this.mockdata.length;
      this.ELEMENT_DATA = Object.assign([],this.mockdata);
      this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;
      return;
       this.authService.checkLoginUserVlidaate();
       this.spinner.show();
       this.organizationInfoConfigService.getConfigMasterDetails().subscribe(data => {
        
      if(data !=null && data.length>0){
    
        this.ELEMENT_DATA = Object.assign([],data);
         this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
      }else{
        this.ELEMENT_DATA = Object.assign([]);
         this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
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

    mockdata= [
      {
        "companyProfileId": "CP123456789",
        "type": "Head Office",
        "addressLine1": "123 Business Park Road",
        "city": "Dindea City",
        "state": "Dindea State",
        "pincode": "456789",
        "emailId1": "contact@companydindea.com",
        "contactNumber1": "+1234567890",
        "emailId2": "support@companydindea.com",
        "contactNumber2": "+0987654321"
      },
      {
        "companyProfileId": "CP987654321",
        "type": "Branch Office",
        "addressLine1": "456 Corporate Plaza",
        "city": "Techville",
        "state": "Dindea State",
        "pincode": "123456",
        "emailId1": "branch@companydindea.com",
        "contactNumber1": "+1987654321",
        "emailId2": "info@companydindea.com",
        "contactNumber2": "+1234567899"
      },
      {
        "companyProfileId": "CP564738291",
        "type": "Regional Office",
        "addressLine1": "789 Industrial Avenue",
        "city": "Industrial Town",
        "state": "Dindea State",
        "pincode": "789123",
        "emailId1": "regional@companydindea.com",
        "contactNumber1": "+1122334455",
        "emailId2": "contact@companydindea.com",
        "contactNumber2": "+9988776655"
      },
      {
        "companyProfileId": "CP1122334455",
        "type": "Sales Office",
        "addressLine1": "101 Sales Square",
        "city": "Dindea City",
        "state": "Dindea State",
        "pincode": "654321",
        "emailId1": "sales@companydindea.com",
        "contactNumber1": "+1144556677",
        "emailId2": "support@companydindea.com",
        "contactNumber2": "+1222333444"
      },
      {
        "companyProfileId": "CP5566778899",
        "type": "Customer Service",
        "addressLine1": "202 Service Lane",
        "city": "Service City",
        "state": "Dindea State",
        "pincode": "321654",
        "emailId1": "service@companydindea.com",
        "contactNumber1": "+1239876543",
        "emailId2": "help@companydindea.com",
        "contactNumber2": "+1298765432"
      },
        {
          "companyProfileId": "CP123456789",
          "type": "Head Office",
          "addressLine1": "123 Business Park Road",
          "city": "Dindea City",
          "state": "Dindea State",
          "pincode": "456789",
          "emailId1": "contact@companydindea.com",
          "contactNumber1": "+1234567890",
          "emailId2": "support@companydindea.com",
          "contactNumber2": "+0987654321"
        },
        {
          "companyProfileId": "CP987654321",
          "type": "Branch Office",
          "addressLine1": "456 Corporate Plaza",
          "city": "Techville",
          "state": "Dindea State",
          "pincode": "123456",
          "emailId1": "branch@companydindea.com",
          "contactNumber1": "+1987654321",
          "emailId2": "info@companydindea.com",
          "contactNumber2": "+1234567899"
        },
        {
          "companyProfileId": "CP564738291",
          "type": "Regional Office",
          "addressLine1": "789 Industrial Avenue",
          "city": "Industrial Town",
          "state": "Dindea State",
          "pincode": "789123",
          "emailId1": "regional@companydindea.com",
          "contactNumber1": "+1122334455",
          "emailId2": "contact@companydindea.com",
          "contactNumber2": "+9988776655"
        },
        {
          "companyProfileId": "CP1122334455",
          "type": "Sales Office",
          "addressLine1": "101 Sales Square",
          "city": "Dindea City",
          "state": "Dindea State",
          "pincode": "654321",
          "emailId1": "sales@companydindea.com",
          "contactNumber1": "+1144556677",
          "emailId2": "support@companydindea.com",
          "contactNumber2": "+1222333444"
        },
        {
          "companyProfileId": "CP5566778899",
          "type": "Customer Service",
          "addressLine1": "202 Service Lane",
          "city": "Service City",
          "state": "Dindea State",
          "pincode": "321654",
          "emailId1": "service@companydindea.com",
          "contactNumber1": "+1239876543",
          "emailId2": "help@companydindea.com",
          "contactNumber2": "+1298765432"
        },
        {
          "companyProfileId": "CP6688994455",
          "type": "Support Office",
          "addressLine1": "303 Support Road",
          "city": "Supportville",
          "state": "Dindea State",
          "pincode": "987654",
          "emailId1": "support@companydindea.com",
          "contactNumber1": "+1012345678",
          "emailId2": "care@companydindea.com",
          "contactNumber2": "+1112233445"
        },
        {
          "companyProfileId": "CP4455778899",
          "type": "Warehouse",
          "addressLine1": "404 Industrial Road",
          "city": "Logistics City",
          "state": "Dindea State",
          "pincode": "568934",
          "emailId1": "warehouse@companydindea.com",
          "contactNumber1": "+1234567891",
          "emailId2": "logistics@companydindea.com",
          "contactNumber2": "+2345678902"
        },
        {
          "companyProfileId": "CP9900112233",
          "type": "Marketing Office",
          "addressLine1": "505 Marketing Hub",
          "city": "Marketville",
          "state": "Dindea State",
          "pincode": "234567",
          "emailId1": "marketing@companydindea.com",
          "contactNumber1": "+1010101010",
          "emailId2": "promo@companydindea.com",
          "contactNumber2": "+1020304050"
        },
        {
          "companyProfileId": "CP6677889900",
          "type": "Training Center",
          "addressLine1": "606 Training Lane",
          "city": "Educity",
          "state": "Dindea State",
          "pincode": "123789",
          "emailId1": "training@companydindea.com",
          "contactNumber1": "+2020304050",
          "emailId2": "education@companydindea.com",
          "contactNumber2": "+2121212121"
        },
        {
          "companyProfileId": "CP2233445566",
          "type": "Product Design Office",
          "addressLine1": "707 Design Boulevard",
          "city": "Creative City",
          "state": "Dindea State",
          "pincode": "445566",
          "emailId1": "design@companydindea.com",
          "contactNumber1": "+3031323344",
          "emailId2": "innovation@companydindea.com",
          "contactNumber2": "+3132334455"
        },
        {
          "companyProfileId": "CP8877665544",
          "type": "Legal Office",
          "addressLine1": "808 Legal Plaza",
          "city": "Lawcity",
          "state": "Dindea State",
          "pincode": "654987",
          "emailId1": "legal@companydindea.com",
          "contactNumber1": "+4040506070",
          "emailId2": "attorney@companydindea.com",
          "contactNumber2": "+5050607071"
        },
        {
          "companyProfileId": "CP3344556677",
          "type": "IT Support",
          "addressLine1": "909 Tech Lane",
          "city": "Techopolis",
          "state": "Dindea State",
          "pincode": "323423",
          "emailId1": "it@companydindea.com",
          "contactNumber1": "+6060708090",
          "emailId2": "support@companydindea.com",
          "contactNumber2": "+7070809091"
        },
        {
          "companyProfileId": "CP1100223344",
          "type": "Accounting Office",
          "addressLine1": "1010 Finance Road",
          "city": "Financtown",
          "state": "Dindea State",
          "pincode": "224455",
          "emailId1": "accounts@companydindea.com",
          "contactNumber1": "+8080910112",
          "emailId2": "billing@companydindea.com",
          "contactNumber2": "+9091022233"
        },
        {
          "companyProfileId": "CP1199776655",
          "type": "Research Office",
          "addressLine1": "1212 Research Street",
          "city": "Innovation City",
          "state": "Dindea State",
          "pincode": "112233",
          "emailId1": "research@companydindea.com",
          "contactNumber1": "+1213141516",
          "emailId2": "data@companydindea.com",
          "contactNumber2": "+1314151617"
        },
        {
          "companyProfileId": "CP2233667799",
          "type": "Customer Relations",
          "addressLine1": "1313 Customer Drive",
          "city": "Relatown",
          "state": "Dindea State",
          "pincode": "445577",
          "emailId1": "relations@companydindea.com",
          "contactNumber1": "+1415161718",
          "emailId2": "clients@companydindea.com",
          "contactNumber2": "+1516171819"
        },
        {
          "companyProfileId": "CP7755889922",
          "type": "Inventory Office",
          "addressLine1": "1414 Stock Lane",
          "city": "Stockport",
          "state": "Dindea State",
          "pincode": "556677",
          "emailId1": "inventory@companydindea.com",
          "contactNumber1": "+1617181920",
          "emailId2": "stocks@companydindea.com",
          "contactNumber2": "+1718192021"
        },
        {
          "companyProfileId": "CP8899776655",
          "type": "Event Office",
          "addressLine1": "1515 Event Plaza",
          "city": "Eventcity",
          "state": "Dindea State",
          "pincode": "667788",
          "emailId1": "events@companydindea.com",
          "contactNumber1": "+1819202122",
          "emailId2": "organize@companydindea.com",
          "contactNumber2": "+1920212223"
        },
        {
          "companyProfileId": "CP6655334422",
          "type": "Quality Assurance",
          "addressLine1": "1616 Quality Road",
          "city": "Qacity",
          "state": "Dindea State",
          "pincode": "778899",
          "emailId1": "quality@companydindea.com",
          "contactNumber1": "+2021222324",
          "emailId2": "assurance@companydindea.com",
          "contactNumber2": "+2122232425"
        },
        {
          "companyProfileId": "CP2233441100",
          "type": "Compliance Office",
          "addressLine1": "1717 Law Lane",
          "city": "Compliancetown",
          "state": "Dindea State",
          "pincode": "334455",
          "emailId1": "compliance@companydindea.com",
          "contactNumber1": "+2223242526",
          "emailId2": "audit@companydindea.com",
          "contactNumber2": "+2324252627"
        }
      
      
    ]
    
        announceSortChange(sortState: Sort): void {
         
        }

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
      filterData($event: KeyboardEvent){
        if ($event.keyCode === 13) {
        if(this.searchText==''){
          this.ELEMENT_DATA = Object.assign([],this.orginalFetchData);
          this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
    
        }else{
          const pagedData = Object.assign([],this.orginalFetchData.filter(data =>
          	data.addressLine1.toLowerCase().includes(this.searchText.toLowerCase()) || data.city.toLowerCase().includes(this.searchText.toLowerCase()) || data.contactNumber1?.toLowerCase().includes(this.searchText.toLowerCase()) 
            || data.emailId1?.toLowerCase().includes(this.searchText.toLowerCase()) || data.pincode?.toLowerCase().includes(this.searchText.toLowerCase()) || data.state?.toLowerCase().includes(this.searchText.toLowerCase()) || data.type?.toLowerCase().includes(this.searchText.toLowerCase())
          ));
          this.ELEMENT_DATA = Object.assign([],pagedData);
          this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
          
        }
        
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        }
      }
}
