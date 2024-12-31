import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AlertNotificationComponent } from './component/alert-notification.component';

const routes: Routes = [
  { path: 'alert-notification', component: AlertNotificationComponent},

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppNotificationRoutingModule {}