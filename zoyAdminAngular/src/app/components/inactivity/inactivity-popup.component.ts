import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { InactivityService } from './inactivity.service';

@Component({
  selector: 'app-inactivity-popup',
  templateUrl: './inactivity-popup.component.html',
  styleUrls: ['./inactivity-popup.component.css']
})
export class InactivityPopupComponent implements OnInit {
  countdown$: Observable<number>;

  constructor(private inactivityService: InactivityService, private router: Router) {
    this.countdown$ = this.inactivityService.countdown$;
    console.log("  this.countdown$",  this.countdown$)
  }

  ngOnInit(): void {}

  stay() {
    this.inactivityService.resetInactivityTimer();
   // this.router.navigateByUrl('/dashboard'); // Replace with your route
  }

  logout() {
    this.inactivityService.logout();
  }
}
