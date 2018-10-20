import { Component, OnInit } from '@angular/core';
import { GoogleUserService } from '../../shared/google-user.service';

@Component({
  selector: 'app-home-about-me-section',
  templateUrl: './about-me-section.component.html',
  styleUrls: ['./about-me-section.component.css']
})
export class AboutMeSectionComponent implements OnInit {

  signedIn = false;

  constructor(private googleUserService: GoogleUserService) {
    (async () => {
      const signedIn = await this.googleUserService.isSignedInPromise();
      if (signedIn) {
        this.signedIn = true;
      }
    })();
  }

  ngOnInit() {
  }

  /**
   * Let the user sign in.
   */
  signIn(): void {
    this.googleUserService.signIn();
  }

  private reportSignIn(): void {
    this.signedIn = true;
  }

}
