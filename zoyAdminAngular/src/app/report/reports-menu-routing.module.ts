import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FinanceReportsComponent } from './finance-reports/component/finance-reports.component';
import { OwnerReportsComponent } from './owner-reports/component/owner-reports.component';
import { SupportReportsComponent } from './support-reports/component/support.reports.component';
import { TenantReportsComponent } from './tenant-reports/component/tenant-reports.component';

const routes: Routes = [
  { path:'finance-reports',component:FinanceReportsComponent  },
  { path:'owner-reports',component: OwnerReportsComponent  },
  { path:'support-reports',component:SupportReportsComponent  },
  { path:'tenant-reports',component:TenantReportsComponent  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class ReportsMenuRoutingModule { }
