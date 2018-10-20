import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { MatchingRoutingModule } from './matching-routing.module';
import { MatchingComponent } from './matching.component';

@NgModule({
  imports: [SharedModule, MatchingRoutingModule],
  declarations: [MatchingComponent]
})
export class MatchingModule { }
