 import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
 import { Injectable } from '@angular/core';
 import { AuthService } from './auth.service';

@Injectable({providedIn:'root'})
 export class AuthGuard implements CanActivate{
    constructor(private router:Router,private authService:AuthService){} 
    canActivate(next:ActivatedRouteSnapshot,state:RouterStateSnapshot){
     return this.authService.isLoggedIn(state.url);
    }
 }