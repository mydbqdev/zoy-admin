import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AllClosedTicketsComponent } from './all-closed-tickets/component/all-closed-tickets.component';
import { AllTicketsComponent } from './all-tickets/component/all-tickets.component';
import { LeadsComponent } from './leads/component/leads.component';
import { MyClosedTicketsComponent } from './my-closed-tickets/component/my-closed-tickets.component';
import { TicketsComponent } from './my-tickets/component/tickets.component';

const routes: Routes = [
  { path:'tickets',component:TicketsComponent  },
  { path:'my-closed-tickets',component:MyClosedTicketsComponent  },
  { path:'all-tickets',component:AllTicketsComponent  },
  { path:'closed-tickets',component:AllClosedTicketsComponent  },
  { path: 'leads', component: LeadsComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class SupportMenuRoutingModule { }