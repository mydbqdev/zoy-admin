import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LowRatingsComponent } from './low-ratting/component/low-ratings.component';

const routes: Routes = [
  { path:'low-ratings',component:LowRatingsComponent  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class RatingAndReviewsMenuRoutingModule { }
