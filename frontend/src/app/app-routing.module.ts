import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { NotFoundComponent } from './shared/not-found/not-found.component';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent
  },
  {
    path: 'profile',
    loadChildren: './profile/profile.module#ProfileModule'
  },
  {
    path: 'free-time',
    loadChildren: './free-time/free-time.module#FreeTimeModule'
  },
  {
    path: 'courses',
    loadChildren: './course/course.module#CourseModule'
  },
  {
    path: 'partners',
    loadChildren: './partner/partner.module#PartnerModule'
  },
  {
    path: 'matching',
    loadChildren: './matching/matching.module#MatchingModule'
  },
  {
    path: '**',
    component: NotFoundComponent
  }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, {
      preloadingStrategy: PreloadAllModules
    })
  ],
  exports: [RouterModule],
  declarations: []
})
export class AppRoutingModule {
}
