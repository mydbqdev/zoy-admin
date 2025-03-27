import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LeadsComponent } from './leads/component/leads.component';
import { TicketsComponent } from './tickets/component/tickets.component';
const routes: Routes = [
  { path:'tickets',component:TicketsComponent  },
  { path: 'leads', component: LeadsComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class SupportMenuRoutingModule { }