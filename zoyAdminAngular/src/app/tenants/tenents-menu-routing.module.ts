import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BulkUploadComponent } from './bulk-upload/component/bulk-upload.component';
import { TenantProfileComponent } from './tenant-profile/component/tenant-profile.component';
import { TenantsComponent } from './tenants/component/tenants.component';

const routes: Routes = [
  { path:'bulk-upload',component:BulkUploadComponent  },
  { path: 'tenants', component: TenantsComponent },
  { path: 'tenantprofile', component: TenantProfileComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class TenantMenuRoutingModule { }
