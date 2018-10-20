import { NgModule } from '@angular/core';
import { CourseComponent } from './course.component';
import { SharedModule } from '../shared/shared.module';
import { CourseRoutingModule } from './course-routing.module';

@NgModule({
  imports: [SharedModule, CourseRoutingModule],
  declarations: [CourseComponent]
})
export class CourseModule {}
