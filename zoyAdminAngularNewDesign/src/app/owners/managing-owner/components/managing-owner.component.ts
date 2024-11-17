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
  selector: 'app-managing-owner',
  templateUrl: './managing-owner.component.html',
  styleUrl: './managing-owner.component.css'
})
export class ManageOwnerComponent implements OnInit, AfterViewInit {
  pageSize: number = 10; 
  pageSizeOptions: number[] = [10, 20, 50]; 
  totalProduct: number = 0; 
  displayedColumns: string[] = ['zoyCode', 'ownerName', 'email', 'contact','date', 'status','action'];
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
			this.authService.checkLoginUserVlidaate();
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

	ngOnDestroy() {
		if (this.mySubscription) {
			this.mySubscription.unsubscribe();
		}
	}
	ngOnInit() {
		//if (this.userNameSession == null || this.userNameSession == undefined || this.userNameSession == '') {
		//	this.router.navigate(['/']);
		//}
		this.getZoyCodeDetails();
	}
	ngAfterViewInit() {
		this.sidemenuComp.expandMenu(2);
		this.sidemenuComp.activeMenu(2, 'manage-owner');
		this.dataService.setHeaderName("Manage Owner");
	}

	getZoyCodeDetails(){
    // this.authService.checkLoginUserVlidaate();
    this.ELEMENT_DATA = Object.assign([],mockData);
    this.dataSource =new MatTableDataSource(this.ELEMENT_DATA);
    this.dataSource.sort = this.sort;
	this.dataSource.paginator = this.paginator;
}
	
}


export const mockData = [
	{
	  zoyCode: 'ZC001',
	  ownerName: 'John Doe',
	  email: 'john.doe@example.com',
	  contact: '123-456-7890',
	  date: '02',
	  status: 'Registered'
	},
	{
	  zoyCode: 'ZC002',
	  ownerName: 'Jane Smith',
	  email: 'jane.smith@example.com',
	  contact: '098-765-4321',
	  date: '01',
	  status: 'Pending'
	},
	{
	  zoyCode: 'ZC003',
	  ownerName: 'Michael Johnson',
	  email: 'michael.j@example.com',
	  contact: '456-123-7890',
	  date: '03',
	  status: 'Registered'
	},
	{
	  zoyCode: 'ZC004',
	  ownerName: 'Emily Davis',
	  email: 'emily.davis@example.com',
	  contact: '321-654-0987',
	  date: '01',
	  status: 'Pending'
	},
	{
	  zoyCode: 'ZC005',
	  ownerName: 'William Brown',
	  email: 'william.brown@example.com',
	  contact: '213-546-8790',
	  date: '02',
	  status: 'Registered'
	},
	{
	  zoyCode: 'ZC006',
	  ownerName: 'Olivia Taylor',
	  email: 'olivia.t@example.com',
	  contact: '765-432-1098',
	  date: '02',
	  status: 'Pending'
	},
	{
	  zoyCode: 'ZC007',
	  ownerName: 'James Wilson',
	  email: 'james.wilson@example.com',
	  contact: '876-543-2109',
	  date: '03',
	  status: 'Registered'
	},
	{
	  zoyCode: 'ZC008',
	  ownerName: 'Sophia Martinez',
	  email: 'sophia.m@example.com',
	  contact: '234-567-8901',
	  date: '08',
	  status: 'Pending'
	},
	{
	  zoyCode: 'ZC009',
	  ownerName: 'Benjamin Garcia',
	  email: 'benjamin.g@example.com',
	  contact: '345-678-9012',
	  date: '09',
	  status: 'Registered'
	},
	{
	  zoyCode: 'ZC010',
	  ownerName: 'Emma Anderson',
	  email: 'emma.anderson@example.com',
	  contact: '456-789-0123',
	  date: '10',
	  status: 'Pending'
	}
  ];
  
