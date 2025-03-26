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
    { name: "Activity Log", link: "/activity-log", permission: "DEFAULT_PERMISSION", icon:"fas fa-th-list" },
    { name: "Bulk Upload", link: "/bulk-upload", permission: "BULK_UPLOAD_READ", icon:"fas fa-fw fa-door-open" },
    { name: "Tenant", link: "/tenants", permission: "TENANTSZOY_READ", icon:"fas fa-fw fa-door-open" },
    { name: "Change Password", link: "/profile", permission: "DEFAULT_PERMISSION", icon:"fas fa-th-list" },
    { name: "Dashboard", link: "/home", permission: "DEFAULT_PERMISSION", icon:"fas fa-th-list"},
    { name: "Initial Configuration", link: "/db-master-configuration", permission: "DB_MASTER_CONFIGURATION_READ", icon:"fas fa-fw fa-cogs" },
    { name: "Master Configuration", link: "/configuration-master", permission: "CONFIGURATION_MASTER_READ", icon:"fas fa-fw fa-cogs" },
    { name: "Master Configuration Approval", link: "/configuration-master", permission: "CONFIGURATION_MASTER_APPROVAL_READ", icon:"fas fa-fw fa-cogs" },
    { name: "Help", link: "/help", permission: "DEFAULT_PERMISSION", icon:"fas fa-th-list"},
    { name: "Home", link: "/home", permission: "DEFAULT_PERMISSION", icon:"fas fa-th-list"},
    { name: "Managing Users", link: "/user-master", permission: "MANAGING_USERS_READ", icon:"fas fa-fw fa-users-cog" },
    { name: "Manage Owners", link: "/manage-owner", permission: "MANAGING_OWNERS_READ", icon:"fas fa-fw fa-users" },
    { name: "Payment Approval", link: "/payment-approval", permission: "PAYMENT_APPROVAL_READ", icon:"fas fa-fw fa-money-check" },
    { name: "Locked User", link: "/locked-user", permission: "LOCKED_USER_READ", icon:"fas fa-fw fa-users-cog" },
    { name: "Permission Approval", link: "/permission-approval", permission: "PERMISSION_APPROVAL_READ", icon:"fas fa-fw fa-users-cog" },
    { name: "Profile", link: "/profile", permission: "DEFAULT_PERMISSION", icon:"fas fa-th-list" },
    { name: "Role and Permission", link: "/role-master", permission: "ROLE_AND_PERMISSION_READ", icon:"fas fa-fw fa-cogs"},
    { name: "Settings", link: "/settings", permission: "DEFAULT_PERMISSION", icon:"fas fa-th-list" },
    { name: "Owner Registration", link: "/zoy-code", permission: "ZOYCODES_READ", icon:"fas fa-fw fa-users" },
    { name: "User Audit", link: "/user-audit", permission: "USER_AUDIT_READ", icon:"fas fa-fw fa-cogs" },
    { name: "Organization Information", link: "/organization-info-config", permission: "ORGANIZATION_INFO_CONFIG_READ", icon:"fas fa-fw fa-cogs" },
    { name: "Tenant Reports", link: "/tenant-reports", permission: "TENANT_REPORTS_READ", icon:"fas fa-chart-bar" },
    { name: "Owner Reports", link: "/owner-reports", permission: "OWNER_REPORTS_READ", icon:"fas fa-chart-bar" },
    { name: "Finance Reports", link: "/finance-reports", permission: "FINANCE_REPORTS_READ", icon:"fas fa-chart-bar" },
    { name: "Support Reports", link: "/support-reports", permission: "SUPPORT_REPORTS_READ", icon:"fas fa-chart-bar" },
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
getAllMenuList(): Menu[] {
    return this.menuList;
  }

getAllMenus(): Menu[] {
  this.getAccessMenu();
    return this.menusWithPermissions;
  }

  private authorization:{ key: string; screen: string; order: number }[] = [
    { key: "ZOYCODES", screen: "Zoy Code" ,order:1.1 },
    { key: "MANAGING OWNERS", screen: "Manage Owners" ,order:1.2 },
    { key: "MANAGING USERS", screen: "Managing Users" ,order:2.1 },
    { key: "PERMISSION APPROVAL", screen: "Permission Approval" ,order:2.2 },
    { key: "LOCKED USER", screen: "Locked User" ,order:2.3 },
    { key: "TENANTSZOY", screen: "Tenants" ,order:3.1 },
    { key: "BULK UPLOAD", screen: "Bulk Upload" ,order:3.2 },
    { key: "REPORTS", screen: "Reports" ,order:4.1 },
    { key: "PAYMENT APPROVAL", screen: "Payment Approval" ,order:4.2 },
    { key: "TICKETS", screen: "Tickets" ,order:5.1 },
    { key: "ROLE AND PERMISSION", screen: "Role & Permission" ,order:6.1 },
    { key: "DB MASTER CONFIGURATION", screen: "Initial Configuration" ,order:6.2 },
    { key: "CONFIGURATION MASTER", screen: "Master Configuration" ,order:6.3 },
    { key: "CONFIGURATION MASTER APPROVAL", screen: "Master Configuration Approval" ,order:6.3 },
    { key: "USER AUDIT", screen: "User Audit" ,order:6.4 },
    { key: "ORGANIZATION INFO CONFIG", screen: "Organization Information" ,order:6.5 },
    { key: "TENANT REPORTS", screen: "Tenant Reports" ,order:7.1 },
    { key: "OWNER REPORTS", screen: "Owner Reports" ,order:7.2 },
    { key: "FINANCE REPORTS", screen: "Finance Reports" ,order:7.3 },
    { key: "SUPPORT REPORTS", screen: "Support Reports" ,order:7.4 },
  ];

  getAllAuthorization():any {
      return this.authorization;
    }
}

