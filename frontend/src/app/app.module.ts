import { NgModule } from '@angular/core';
import { BrowserModule, Title } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ServiceWorkerModule } from '@angular/service-worker';
import { environment } from '../environments/environment';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AboutMeRowComponent } from './home/about-me-section/about-me-row/about-me-row.component';
import { AboutMeSectionComponent } from './home/about-me-section/about-me-section.component';
import { HomeComponent } from './home/home.component';
import { NavModule } from './nav/nav.module';
import { SharedModule } from './shared/shared.module';
import { NgxJsonLdModule } from '@ngx-lite/json-ld';
import { AngularFireAuthModule } from '@angular/fire/auth';
import { AngularFireModule } from '@angular/fire';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    AboutMeRowComponent,
    AboutMeSectionComponent
  ],
  imports: [
    BrowserModule.withServerTransition({ appId: 'stable-pm' }), NavModule, BrowserAnimationsModule,
    AngularFireModule.initializeApp(environment.firebase), AngularFireAuthModule, NgxJsonLdModule,
    ServiceWorkerModule.register('/ngsw-worker.js', { enabled: false }),
    AppRoutingModule,
    SharedModule
  ],
  providers: [Title],
  bootstrap: [AppComponent]
})
export class AppModule {
}
