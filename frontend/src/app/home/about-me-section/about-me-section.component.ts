import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-home-about-me-section',
  templateUrl: './about-me-section.component.html',
  styleUrls: ['./about-me-section.component.css']
})
export class AboutMeSectionComponent implements OnInit {

  constructor() {
  }

  ngOnInit() {
  }

  // noinspection JSMethodCanBeStatic
  /**
   * A function to scroll to projects.
   */
  scrollToProjects(): void {
    const projects = document.getElementById('projects');
    if (projects != null) {
      projects.scrollIntoView();
    }
  }

}
