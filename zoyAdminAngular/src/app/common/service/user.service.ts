import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UserInfo } from '../shared/model/userinfo.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http : HttpClient) {
  }
  public username :string;
  public dbquser:boolean;
  public userinfo:UserInfo;
  public sessionTime:Date = null;
  public loggedOut:boolean;
  

  getUsername(){
      return this.username;
  }

  setUsername(_username:string){
      this.username=_username;
  }

  getUserinfo(){
      return this.userinfo;
  }

  setUserinfo(_userinfo:UserInfo){
      this.userinfo=_userinfo;
  }  

  getSessionTime(){
    return this.sessionTime;
  }

  setSessionTime(sessionTime:Date){
    this.sessionTime=sessionTime;
  }

  isLoggedOut(){
    return this.loggedOut;
  }

  setLoggedOut(loggedOut:boolean){
    this.loggedOut=loggedOut;
  }  
}
