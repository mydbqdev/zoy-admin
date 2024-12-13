import { Injectable } from '@angular/core';
import { Menu } from './menu';
import { UserService } from 'src/app/common/service/user.service';

@Injectable({
  providedIn: 'root',
})
export class MenuService {
  public userNameSession: string = "";
	public rolesArray: string[] = [];
  menusWithPermissions: Menu[]= [];

  constructor(private userService: UserService) {
		this.userNameSession = userService.getUsername();
		if (userService.getUserinfo() != undefined) {
			this.rolesArray = userService.getUserinfo().privilege;
		}
	}

  private menuList: Menu[] = [
    { name: "Activity Log", link: "/activity-log", permission: "DEFAULT_PERMISSION" },
    { name: "Bulk Upload", link: "/bulk-upload", permission: "BULK_UPLOAD_READ" },
    { name: "Change Password", link: "/profile", permission: "DEFAULT_PERMISSION" },
    { name: "Dashboard", link: "/home", permission: "DEFAULT_PERMISSION" },
    { name: "DB Master Configuration", link: "/db-master-configuration", permission: "DB_MASTER_CONFIGURATION_READ" },
    { name: "Help", link: "/help", permission: "DEFAULT_PERMISSION" },
    { name: "Home", link: "/home", permission: "DEFAULT_PERMISSION" },
    { name: "Managing Users", link: "/user-master", permission: "MANAGING_USERS_READ" },
    { name: "Manage Owners", link: "/manage-owner", permission: "MANAGING_OWNERS_READ" },
    { name: "Payment Approval", link: "/payment-approval", permission: "PAYMENT_APPROVAL_READ" },
    { name: "Permission Approval", link: "/permission-approval", permission: "PERMISSION_APPROVAL_READ" },
    { name: "Profile", link: "/profile", permission: "DEFAULT_PERMISSION" },
    { name: "Reports", link: "/report-list", permission: "REPORTS_READ" },
    { name: "Role and Permission", link: "/role-master", permission: "ROLE_AND_PERMISSION_READ" },
    { name: "Settings", link: "/settings", permission: "DEFAULT_PERMISSION" },
    { name: "Zoy Code", link: "/zoy-code", permission: "ZOYCODES_READ" },
    { name: "User Audit", link: "/user-audit", permission: "USER_AUDIT_READ" }
  ];
  

getAccessMenu(){
  if (this.userService.getUserinfo() != undefined) {
    this.rolesArray = this.userService.getUserinfo().privilege;
    this.menusWithPermissions= [];
  this.menuList.forEach(menu => {
    if ( (this.rolesArray.includes(menu.permission)||menu.permission===('DEFAULT_PERMISSION'))) {
      this.menusWithPermissions.push(menu);
    } else {
    }
  });}
}

getAllMenus(): Menu[] {
  this.getAccessMenu();
    return this.menusWithPermissions;
  }
}
