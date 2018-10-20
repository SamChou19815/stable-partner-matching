import { Component, OnInit } from '@angular/core';
import { shortDelay } from '../shared/util';
import { MatDialog } from '@angular/material';
import { LoadingOverlayService } from '../shared/overlay/loading-overlay.service';
import { GoogleUserService } from '../shared/google-user.service';
import { appCardData, ProjectCardData } from '../shared/project-card-data';
import { GlobalDataService } from '../shared/global-data.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {

  readonly appIntro: ProjectCardData = appCardData;
  /**
   * Whether the user has logged in.
   */
  isUserLoggedIn = false;

  constructor(private dataService: GlobalDataService,
              private googleUserService: GoogleUserService,
              private loadingService: LoadingOverlayService,
              private dialog: MatDialog) {
  }

  ngOnInit() {
    shortDelay(async () => {
      const ref = this.loadingService.open();
      this.isUserLoggedIn = await this.googleUserService.isSignedInPromise();
      if (!this.isUserLoggedIn) {
        ref.close();
        return;
      }
      this.dataService.initializeApp().then(ref.close);
    });
  }

  signIn(): void {
    this.googleUserService.signIn();
  }

  get isInitialized(): boolean {
    return !this.dataService.initData.isNotInitialized;
  }

}
