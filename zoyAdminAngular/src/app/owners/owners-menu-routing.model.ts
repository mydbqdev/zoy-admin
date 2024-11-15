import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ZoyCodeComponent } from './zoy-code/components/zoy-code.component';



const routes: Routes = [
  { path: 'zoy-code', component: ZoyCodeComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppOwnerMenuRoutingModule { }