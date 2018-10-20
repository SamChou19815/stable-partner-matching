import { Component, OnInit } from '@angular/core';
import { shortDelay } from '../shared/util';
import { MatDialog } from '@angular/material';
import { LoadingOverlayService } from '../shared/overlay/loading-overlay.service';
import { GoogleUserService } from '../shared/google-user.service';
import { appCardData, ProjectCardData } from '../shared/project-card-data';

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

  constructor(private dataService: SchedulerDataService,
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
      this.dataService.initializedSchedulerApp().then(ref.close);
    });
  }

  /**
   * Let the user sign in.
   */
  signIn(): void {
    this.googleUserService.signIn();
  }

  /**
   * Returns whether the data is initialized.
   *
   * @returns {boolean} whether the data is initialized.
   */
  get isInitialized(): boolean {
    return !this.dataService.schedulerData.isNotInitialized;
  }

}
