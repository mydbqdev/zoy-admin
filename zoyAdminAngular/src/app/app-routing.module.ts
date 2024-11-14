import { RouterModule, Routes } from '@angular/router';
import { SigninComponent } from './components/signin/signin.component';
import { StartupComponent } from './common/startup/startup.component';
import { NgModule } from '@angular/core';
import { SignupComponent } from './components/signup/signup.component';
import { UnderConstructionComponent } from './under-construction/under-construction.component';
import { HomeComponent } from './components/home/home.component';
import { UserListComponent } from './user-management/user-list/user-list.component';
import { ReportListComponent } from './reports/component/report-list.component';
import { RoleMasterComponent } from './setting/role-master/components/role-master.component';
import { UserMasterComponent } from './setting/user-master/components/user-master.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { PermissionApprovalComponent } from './setting/permission-approval/component/permission-approval.component';
import { ZoyCodeComponent } from './setting/owners/zoy-code/zoy-code.component';

const routes: Routes = [
	{path:'',redirectTo:'/startup',pathMatch:'full'},
	{path:'startup',component:StartupComponent,pathMatch:'full'},
	{ path: 'signup', component: SignupComponent },
	{ path: 'signin', component: SigninComponent },
	{ path: 'under-construction', component:UnderConstructionComponent},
	{ path: 'home', component: HomeComponent },
	{ path: 'user-list', component: UserListComponent },
	{ path: 'report-list', component: ReportListComponent },
	{ path: 'role-master', component: RoleMasterComponent },	
	{ path: 'user-master', component: UserMasterComponent },
	{ path: 'forgot-password', component: ForgotPasswordComponent },
	{ path: 'permission-approval', component: PermissionApprovalComponent },
	{ path: 'zoy-code', component: ZoyCodeComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule { }