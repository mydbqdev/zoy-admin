import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ManageOwnerComponent } from './managing-owner/components/managing-owner.component';
import { ZoyCodeComponent } from './zoy-code/components/zoy-code.component';


const routes: Routes = [
  { path: 'zoy-code', component: ZoyCodeComponent},
  { path: 'manage-owner', component: ManageOwnerComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppOwnerMenuRoutingModule { }