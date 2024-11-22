import { RouterModule, Routes } from '@angular/router';
import { SigninComponent } from './components/signin/signin.component';
import { StartupComponent } from './common/startup/startup.component';
import { NgModule } from '@angular/core';
import { UnderConstructionComponent } from './under-construction/under-construction.component';
import { HomeComponent } from './components/home/home.component';
import { ReportListComponent } from './finance/reports/component/report-list.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { ProfileComponent } from './profile/profile/profile.component';
import { ActivityLogComponent } from './profile/activity-log/activity-log.component';
import { SettingsComponent } from './profile/settings/settings.component';
import { TicketsComponent } from './supports/tickets/tickets.component';
import { PaymentApprovalComponent } from './finance/payment-appoval/component/payment-approval.component';

const routes: Routes = [
	{path:'',redirectTo:'/startup',pathMatch:'full'},
	{path:'startup',component:StartupComponent,pathMatch:'full'},
	{ path: 'signin', component: SigninComponent },
	{ path: 'under-construction', component:UnderConstructionComponent},
	{ path: 'home', component: HomeComponent },
	{ path: 'report-list', component: ReportListComponent },
	{ path: 'forgot-password', component: ForgotPasswordComponent },
	{ path: 'profile', component: ProfileComponent },
	{ path: 'activity-log', component: ActivityLogComponent },
	{ path: 'settings', component: SettingsComponent },
	{ path: 'tickets', component: TicketsComponent },
	{path: 'payment-approval', component: PaymentApprovalComponent}
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule { }