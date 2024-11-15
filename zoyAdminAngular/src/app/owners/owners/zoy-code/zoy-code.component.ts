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
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { ZoyData } from '../models/zoy-code-model';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'app-zoy-code',
  templateUrl: './zoy-code.component.html',
  styleUrl: './zoy-code.component.css'
})
export class ZoyCodeComponent implements OnInit, AfterViewInit {
  pageSize: number = 10; 
  pageSizeOptions: number[] = [10, 20, 50]; 
  totalProduct: number = 0; 
  displayedColumns: string[] = ['zoyCode', 'ownerName', 'email', 'contact','date', 'status'];
  public ELEMENT_DATA:ZoyData[];
  dataSource:MatTableDataSource<ZoyData>=new MatTableDataSource<ZoyData>();
  columnSortDirectionsOg: { [key: string]: string | null } = {
    zoyCode: null,
    ownerName: null,
    email: null,
    contact:null,
    date: null,
	status: null
  };
  public userNameSession: string = "";
	errorMsg: any = "";
	mySubscription: any;
	isExpandSideBar:boolean=true;
	@ViewChild(SidebarComponent) sidemenuComp;
	@ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;
	public rolesArray: string[] = [];
	
	constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient, private userService: UserService,
		private spinner: NgxSpinnerService, private authService:AuthService,private dataService:DataService,private notifyService: NotificationService) {
		this.userNameSession = userService.getUsername();
		//this.defHomeMenu=defMenuEnable;
	
		if (userService.getUserinfo() != undefined) {
			this.rolesArray = userService.getUserinfo().privilege;
		}else{
			this.dataService.getUserDetails.subscribe(name=>{
				this.rolesArray =name.privilege;
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
	selectedModel: string = 'generate';

	// Method to update the selected button
	selectButton(button: string): void {
	  this.selectedModel = button;

	  if(this.selectedModel =='generated'){
     this.getZoyCodeDetails();
	  }else{
		
	  }

	}
	ngOnDestroy() {
		if (this.mySubscription) {
			this.mySubscription.unsubscribe();
		}
	}
	ngOnInit() {
		if (this.userNameSession == null || this.userNameSession == undefined || this.userNameSession == '') {
			this.router.navigate(['/']);
		}
	}
	ngAfterViewInit() {
		this.sidemenuComp.expandMenu(2);
		this.sidemenuComp.activeMenu(2, 'zoy-code');
		this.dataService.setHeaderName("Zoy Code");
	}

	test(){
		this.notifyService.showNotification("Success","");
	}

	getZoyCodeDetails(){
    // this.authService.checkLoginUserVlidaate();
    this.ELEMENT_DATA = Object.assign([],mockData);
    this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
    this.dataSource.sort = this.sort;
	this.dataSource.paginator = this.paginator;

//   this.spinner.show();
//   this.userMasterService.getUserList().subscribe(data => {
//     this.ELEMENT_DATA = Object.assign([],data);
//     this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
//     this.dataSource.sort = this.sort;
// 		this.dataSource.paginator = this.paginator;
//     this.spinner.hide();

//  }, error => {
//   this.spinner.hide();
//   if(error.status==403){
//     this.router.navigate(['/forbidden']);
//   }else if (error.error && error.error.message) {
//     this.errorMsg = error.error.message;
//     console.log("Error:" + this.errorMsg);
//     this.notifyService.showError(this.errorMsg, "");
//   } else {
//     if (error.status == 500 && error.statusText == "Internal Server Error") {
//       this.errorMsg = error.statusText + "! Please login again or contact your Help Desk.";
//     } else {
//       let str;
//       if (error.status == 400) {
//         str = error.error;
//       } else {
//         str = error.message;
//         str = str.substring(str.indexOf(":") + 1);
//       }
//       console.log("Error:" + str);
//       this.errorMsg = str;
//     }
//     if(error.status !== 401 ){this.notifyService.showError(this.errorMsg, "");}
//   }
// });

}
	
}


export const mockData = [
	{
	  zoyCode: 'ZC001',
	  ownerName: 'John Doe',
	  email: 'john.doe@example.com',
	  contact: '123-456-7890',
	  date: '2023-11-01',
	  status: 'Registered'
	},
	{
	  zoyCode: 'ZC002',
	  ownerName: 'Jane Smith',
	  email: 'jane.smith@example.com',
	  contact: '098-765-4321',
	  date: '2023-11-02',
	  status: 'Pending'
	},
	{
	  zoyCode: 'ZC003',
	  ownerName: 'Michael Johnson',
	  email: 'michael.j@example.com',
	  contact: '456-123-7890',
	  date: '2023-11-03',
	  status: 'Registered'
	},
	{
	  zoyCode: 'ZC004',
	  ownerName: 'Emily Davis',
	  email: 'emily.davis@example.com',
	  contact: '321-654-0987',
	  date: '2023-11-04',
	  status: 'Pending'
	},
	{
	  zoyCode: 'ZC005',
	  ownerName: 'William Brown',
	  email: 'william.brown@example.com',
	  contact: '213-546-8790',
	  date: '2023-11-05',
	  status: 'Registered'
	},
	{
	  zoyCode: 'ZC006',
	  ownerName: 'Olivia Taylor',
	  email: 'olivia.t@example.com',
	  contact: '765-432-1098',
	  date: '2023-11-06',
	  status: 'Pending'
	},
	{
	  zoyCode: 'ZC007',
	  ownerName: 'James Wilson',
	  email: 'james.wilson@example.com',
	  contact: '876-543-2109',
	  date: '2023-11-07',
	  status: 'Registered'
	},
	{
	  zoyCode: 'ZC008',
	  ownerName: 'Sophia Martinez',
	  email: 'sophia.m@example.com',
	  contact: '234-567-8901',
	  date: '2023-11-08',
	  status: 'Pending'
	},
	{
	  zoyCode: 'ZC009',
	  ownerName: 'Benjamin Garcia',
	  email: 'benjamin.g@example.com',
	  contact: '345-678-9012',
	  date: '2023-11-09',
	  status: 'Registered'
	},
	{
	  zoyCode: 'ZC010',
	  ownerName: 'Emma Anderson',
	  email: 'emma.anderson@example.com',
	  contact: '456-789-0123',
	  date: '2023-11-10',
	  status: 'Pending'
	}
  ];
  
