import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MatchingComponent } from './matching.component';
import { MatchingCourseComponent } from './matching-course.component';

const routes: Routes = [
  { path: '', component: MatchingComponent },
  { path: 'course/:status/:id', component: MatchingCourseComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  declarations: []
})
export class MatchingRoutingModule {}
