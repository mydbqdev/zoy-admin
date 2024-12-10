import { Injectable, NgZone } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserActivityService {
  private lastActionTimeSubject = new BehaviorSubject<number>(Date.now());
  public lastActionTime$ = this.lastActionTimeSubject.asObservable();

  constructor(private ngZone: NgZone) {
    this.listenForUserActivity();
  }

  private listenForUserActivity() {
    this.ngZone.runOutsideAngular(() => {
      document.addEventListener('mousemove', this.updateLastActionTime.bind(this));
      document.addEventListener('click', this.updateLastActionTime.bind(this));
      document.addEventListener('keydown', this.updateLastActionTime.bind(this));
      document.addEventListener('scroll', this.updateLastActionTime.bind(this));
    });
  }

  private updateLastActionTime() {
    this.lastActionTimeSubject.next(Date.now()); 
  }

  getTimeSinceLastAction(): number {
    const lastActionTime = this.lastActionTimeSubject.value;
    return Date.now() - lastActionTime; 
  }
}
