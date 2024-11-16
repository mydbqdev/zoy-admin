import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserMasterComponent } from './user-master/components/user-master.component';
import { PermissionApprovalComponent } from './permission-approval/component/permission-approval.component';

const routes: Routes = [
  { path:'user-master',component:UserMasterComponent  },
  { path: 'permission-approval', component: PermissionApprovalComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppUsersMenuRoutingModule { }
