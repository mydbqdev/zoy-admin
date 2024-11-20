import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BulkUploadComponent } from './bulk-upload/component/bulk-upload.component';
import { TenantsComponent } from './tenants/tenants.component';

const routes: Routes = [
  { path:'bulk-upload',component:BulkUploadComponent  },
	{ path: 'tenants', component: TenantsComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class TenantMenuRoutingModule { }
