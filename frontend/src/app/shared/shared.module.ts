import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AngularFontAwesomeModule } from 'angular-font-awesome';
import { AlertComponent } from './alert/alert.component';
import { IconComponent } from './icon.component';
import { MaterialModule } from './material.module';
import { NotFoundComponent } from './not-found/not-found.component';
import { BlockingOverlayComponent } from './overlay/blocking-overlay.component';
import { LoadingOverlayComponent } from './overlay/loading-overlay.component';
import { UserCardComponent } from './user-card/user-card.component';
import { ProjectCardComponent } from './project-card/project-card.component';
import { StudentPublicInfoComponent } from './student-public-info/student-public-info.component';

@NgModule({
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MaterialModule, AngularFontAwesomeModule, HttpClientModule,
    RouterModule],
  exports: [CommonModule, FormsModule, ReactiveFormsModule, MaterialModule, AngularFontAwesomeModule, HttpClientModule,
    BlockingOverlayComponent, NotFoundComponent, AlertComponent, IconComponent, UserCardComponent,
    ProjectCardComponent, StudentPublicInfoComponent],
  declarations: [BlockingOverlayComponent, LoadingOverlayComponent, NotFoundComponent, AlertComponent, IconComponent,
    UserCardComponent, ProjectCardComponent, StudentPublicInfoComponent],
  providers: [HttpClient],
  entryComponents: [AlertComponent, LoadingOverlayComponent]
})
export class SharedModule {
}
