import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserMasterComponent } from './user-master/components/user-master.component';
import { RoleMasterComponent } from './role-master/components/role-master.component';
const routes: Routes = [

  {
	path: 'role-master', component: UserMasterComponent,
		
  },
  {
	path:'user-master',component:RoleMasterComponent
},
  
 
	
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppSettingMenuRoutingModule { }
