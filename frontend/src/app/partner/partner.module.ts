import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { PartnerRoutingModule } from './partner-routing.module';
import { PartnerComponent } from './partner.component';

@NgModule({
  imports: [SharedModule, PartnerRoutingModule],
  declarations: [PartnerComponent]
})
export class PartnerModule { }
