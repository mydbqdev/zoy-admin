import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminVendorManagementComponent } from './admin-vendor-management/component/admin-vendor-management.component';

const routes: Routes = [
  { path: 'admin-vendor-management', component: AdminVendorManagementComponent},
  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class VendorMenuRoutingModule { }
