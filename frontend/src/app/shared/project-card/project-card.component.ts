import { Component, Input, OnInit } from '@angular/core';
import { ProjectCardData } from '../project-card-data';
import { GoogleUserService } from '../google-user.service';

@Component({
  selector: 'app-project-card',
  templateUrl: './project-card.component.html',
  styleUrls: ['./project-card.component.css']
})
export class ProjectCardComponent implements OnInit {

  /**
   * The data to display.
   * @type {ProjectCardData}
   */
  @Input() data: ProjectCardData = <ProjectCardData>{ name: '', logo: '' };
  /**
   * Whether sign in is enabled, which defaults to false.
   * @type {boolean}
   */
  @Input() signInEnabled = false;

  constructor(private googleUserService: GoogleUserService) {
  }

  ngOnInit() {
  }

  /**
   * Let the user sign in.
   */
  signIn(): void {
    this.googleUserService.signIn();
  }

}
