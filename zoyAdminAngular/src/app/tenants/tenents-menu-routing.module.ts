import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TenantProfileComponent } from './tenant-profile/component/tenant-profile.component';
import { TenantsComponent } from './tenants/component/tenants.component';

const routes: Routes = [
  { path: 'tenants', component: TenantsComponent },
  { path: 'tenantprofile', component: TenantProfileComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class TenantMenuRoutingModule { }
