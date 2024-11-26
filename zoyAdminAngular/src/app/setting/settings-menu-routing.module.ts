import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RoleMasterComponent } from './role-master/components/role-master.component';
import { DbMasterConfigurationComponent } from './db-master-configuration/components/db-master-configuration.component';

const routes: Routes = [
  { path: 'role-master', component: RoleMasterComponent},
  { path: 'db-master-configuration', component: DbMasterConfigurationComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppSettingMenuRoutingModule { }
