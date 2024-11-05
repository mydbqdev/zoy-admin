import { Component, OnInit } from '@angular/core';
import { DataService } from '../common/service/data.service';
import { SignupDetails } from '../common/shared/signup-details';
import { AuthService } from '../common/service/auth.service';

@Component({
  selector: 'app-under-construction',
  templateUrl: './under-construction.component.html',
  styleUrls: ['./under-construction.component.css']
})
export class UnderConstructionComponent implements OnInit {
  userInfo:SignupDetails=new SignupDetails();
  constructor(private dataService:DataService,private authService:AuthService) {
    this.authService.checkLoginUserVlidaate();
   }

  ngOnInit(): void {
    this.dataService.getUserDetails.subscribe(info=>{
			this.userInfo=info;
		}
		)
  }

}
