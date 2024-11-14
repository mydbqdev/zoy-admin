import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserMasterComponent } from './user-master/components/user-master.component';
import { RoleMasterComponent } from './role-master/components/role-master.component';
import { PermissionApprovalComponent } from './permission-approval/component/permission-approval.component';
import { ZoyCodeComponent } from './owners/zoy-code/zoy-code.component';

const routes: Routes = [
  {	path: 'role-master', component: UserMasterComponent },
  { path:'user-master',component:RoleMasterComponent  },
  { path: 'permission-approval', component: PermissionApprovalComponent},
  { path: 'zoy-code', component: ZoyCodeComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppSettingMenuRoutingModule { }
