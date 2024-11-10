import { Component, OnInit, Output } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { User } from '../shared/user';

@Component({
  selector: 'app-startup',
  templateUrl: './startup.component.html',
  styleUrls: ['./startup.component.css']
})
export class StartupComponent implements OnInit {

  public errorMsg ="";
  userInfo :User;
  username :string;
  constructor(private authService:AuthService,public router:Router,public route:ActivatedRoute) { }

  ngOnInit() {
  this.authService.checkLoginUser();
  }

  @Output()
  get getMessage():string{
    return this.authService.message;
  }

}
