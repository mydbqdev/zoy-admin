import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RoleMasterComponent } from './role-master/components/role-master.component';
import { DbMasterConfigurationComponent } from './db-master-configuration/components/db-master-configuration.component';
import { ConfigurationMasterComponent } from './configuration-master/components/configuration-master.component';

const routes: Routes = [
  { path: 'role-master', component: RoleMasterComponent},
  { path: 'db-master-configuration', component: DbMasterConfigurationComponent},
  { path: 'configuration-master', component: ConfigurationMasterComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppSettingMenuRoutingModule { }
