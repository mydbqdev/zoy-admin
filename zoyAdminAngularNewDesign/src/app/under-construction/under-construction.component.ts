import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../common/service/data.service';
import { SignupDetails } from '../common/shared/signup-details';
import { AuthService } from '../common/service/auth.service';
import { SidebarComponent } from '../components/sidebar/sidebar.component';

@Component({
  selector: 'app-under-construction',
  templateUrl: './under-construction.component.html',
  styleUrls: ['./under-construction.component.css']
})
export class UnderConstructionComponent implements OnInit {
  @ViewChild(SidebarComponent) sidemenuComp;
  userInfo:SignupDetails=new SignupDetails();
  isExpandSideBar:boolean=true;
  constructor(private dataService:DataService,private authService:AuthService) {
    this.authService.checkLoginUserVlidaate();
    this.dataService.getIsExpandSideBar.subscribe(name=>{
			this.isExpandSideBar=name;
		});
   }

  ngOnInit(): void {
    this.dataService.getUserDetails.subscribe(info=>{
			this.userInfo=info;
		}
		)
  }
  ngAfterViewInit(){
    this.sidemenuComp.expandMenu(0);
    this.sidemenuComp.activeMenu(0,'');
  }

}
