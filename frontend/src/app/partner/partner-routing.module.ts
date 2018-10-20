import { NgModule } from '@angular/core';
import { PartnerComponent } from './partner.component';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [{ path: '', component: PartnerComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  declarations: []
})
export class PartnerRoutingModule {}
