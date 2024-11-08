import { AfterViewInit, Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { DataService } from 'src/app/common/service/data.service';
import { UserService } from 'src/app/common/service/user.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit,AfterViewInit {
  userNameSession: any;
  mySubscription: any;
  headerName:string='';
  constructor( private userService: UserService, private router: Router,private dataService:DataService) {
    this.userNameSession = userService.getUsername();
    this.router.routeReuseStrategy.shouldReuseRoute = function () {
      return false;
    };

    this.mySubscription = this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        // Trick the Router into believing it's last link wasn't previously loaded
        this.router.navigated = false;
      }
    });
    this.dataService.getHeaderName.subscribe(name=>{
			this.headerName=name;
		});
    console.log("HeaderComponent >>this.headerName",this.headerName);
  }
  ngAfterViewInit(): void {
    
  }
  ngOnInit(): void {
    if(this.userNameSession==null || this.userNameSession==undefined || this.userNameSession==''){
     // this.router.navigate(['/']);
      }
  }
  ngOnDestroy() {
    if (this.mySubscription) {
      this.mySubscription.unsubscribe();
    }
  }

}
