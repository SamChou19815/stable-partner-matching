import { NgModule } from '@angular/core';
import { FreeTimeRoutingModule } from './free-time-routing.module';
import { SharedModule } from '../shared/shared.module';
import { FreeTimeComponent } from './free-time.component';

@NgModule({
  imports: [SharedModule, FreeTimeRoutingModule],
  declarations: [FreeTimeComponent]
})
export class FreeTimeModule {}
