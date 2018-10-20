import { Component, OnInit } from '@angular/core';
import { appCardData, ProjectCardData } from '../shared/project-card-data';
import { GlobalDataService } from '../shared/global-data.service';
import { GoogleUserService } from '../shared/google-user.service';
import { LoadingOverlayService } from '../shared/overlay/loading-overlay.service';
import { MatDialog } from '@angular/material';
import { shortDelay } from '../shared/util';

@Component({
  selector: 'app-partner',
  templateUrl: './partner.component.html',
  styleUrls: ['./partner.component.scss']
})
export class PartnerComponent implements OnInit {

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
