import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserMasterComponent } from './user-master/components/user-master.component';
import { PermissionApprovalComponent } from './permission-approval/component/permission-approval.component';
import { LockedUserComponent } from './locked-user/component/locked-user.component';
import { AdminVendorManagementComponent } from './admin-vendor-management/component/admin-vendor-management.component';

const routes: Routes = [
  { path:'user-master',component:UserMasterComponent  },
  { path: 'permission-approval', component: PermissionApprovalComponent},
  { path: 'locked-user', component: LockedUserComponent},
  { path: 'admin-vendor-management', component: AdminVendorManagementComponent},
  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppUsersMenuRoutingModule { }
