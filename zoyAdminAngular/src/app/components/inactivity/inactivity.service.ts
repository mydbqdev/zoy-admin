import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class InactivityService {
  private timeoutDuration = 1 * 60 * 1000; // 5 minutes in milliseconds
  private countdownTimer: any;
  private countdownSubject: BehaviorSubject<number> = new BehaviorSubject<number>(this.timeoutDuration / 1000); // in seconds
  public countdown$: Observable<number> = this.countdownSubject.asObservable();
  
  private userInactiveTimeout: any;

  constructor(private router: Router) {}

  startInactivityTimer() {
    // Reset any previous timer
    this.resetInactivityTimer();

    // Start a new timer for 5 minutes (300 seconds)
    this.userInactiveTimeout = setTimeout(() => {
      this.showInactivityPopup();
    }, this.timeoutDuration);

    // Start the countdown timer
   // this.startCountdown();
  }

  resetInactivityTimer() {
    if (this.userInactiveTimeout) {
      clearTimeout(this.userInactiveTimeout);
    }

    if (this.countdownTimer) {
      clearInterval(this.countdownTimer);
    }

    this.countdownSubject.next(this.timeoutDuration / 1000); // Reset countdown to 5 minutes
  }

  startCountdown() {
    this.countdownTimer = setInterval(() => {
      let remainingTime = this.countdownSubject.getValue() - 1;
      if (remainingTime <= 0) {
        clearInterval(this.countdownTimer);
        this.logout(); // Force logout if time expires
      } else {
        console.log(this.countdownSubject," this.countdownSubject.next(remainingTime);", remainingTime)
        this.countdownSubject.next(remainingTime)
      }
    }, 1000); // Update every second
  }

  showInactivityPopup() {
    // Show the inactivity popup (to be handled in a component)
    // You can emit an event to show the popup here.
    // For now, you can call a method to navigate to the login page after timeout or if the user doesn't act.
    this.router.navigate(['/signin']); // Redirect to the logout page after inactivity
  }

  logout() {
    // Implement logout action, clear session or token, and redirect to login page.
 //   localStorage.removeItem('userToken'); // Example: clearing user token
    this.router.navigate(['/signin']); // Redirect to login page
  }
}
