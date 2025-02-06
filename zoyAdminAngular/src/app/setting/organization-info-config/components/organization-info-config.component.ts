import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
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

@Component({
  selector: 'app-organization-info-config',
  standalone: true,
  imports: [],
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

 constructor(private route: ActivatedRoute, private router: Router,private formBuilder: FormBuilder, private dataService: DataService, private OrganizationInfoConfigService :OrganizationInfoConfigService,private userService:UserService,
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
     
    }
    ngAfterViewInit() {
      this.sidemenuComp.expandMenu(4);
      this.sidemenuComp.activeMenu(4, 'organization-info-config');
      this.dataService.setHeaderName("Organization Info Configuration");
    }
}
