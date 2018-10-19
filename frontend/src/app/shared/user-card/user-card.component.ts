import { Component, Input, OnInit } from '@angular/core';
import { GoogleUser } from '../google-user';

@Component({
  selector: 'app-user-card',
  templateUrl: './user-card.component.html',
  styleUrls: ['./user-card.component.css']
})
export class UserCardComponent implements OnInit {

  /**
   * The user information to display.
   * @type {GoogleUser}
   */
  @Input() user: GoogleUser = GoogleUser.defaultDummyUser;

  constructor() {
  }

  ngOnInit() {
  }

  /**
   * Returns the css for profile picture.
   *
   * @returns {string} the css for profile picture.
   */
  get userProfilePictureCss(): string {
    return `url(${this.user.picture})`;
  }

}
