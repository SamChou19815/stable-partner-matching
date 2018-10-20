import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FreeTimeComponent } from './free-time.component';

const routes: Routes = [{ path: '', component: FreeTimeComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  declarations: []
})
export class FreeTimeRoutingModule {}
