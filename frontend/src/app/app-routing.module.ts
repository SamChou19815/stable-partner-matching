import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { NotFoundComponent } from './shared/not-found/not-found.component';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent
  },
  /*,
  {
    path: 'friends',
    loadChildren: 'app/friends/friends.module#FriendsModule'
  },
  {
    path: 'scheduler',
    loadChildren: 'app/scheduler/scheduler.module#SchedulerModule'
  },
  {
    path: 'rss-reader',
    loadChildren: 'app/rss-reader/rss-reader.module#RssReaderModule'
  },
  {
    path: 'playground/sampl',
    loadChildren: 'app/sampl/sampl.module#SamplModule'
  },
  {
    path: 'playground/chunk-reader',
    loadChildren: 'app/chunk-reader/chunk-reader.module#ChunkReaderModule'
  },
  {
    path: 'playground/ten',
    loadChildren: 'app/ten/ten.module#TenModule'
  },*/
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
