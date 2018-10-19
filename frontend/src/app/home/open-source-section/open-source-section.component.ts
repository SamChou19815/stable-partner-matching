import { Component, OnInit } from '@angular/core';
import { OpenSourceData } from './open-source-data';

@Component({
  selector: 'app-home-open-source-section',
  templateUrl: './open-source-section.component.html',
  styleUrls: ['./open-source-section.component.scss']
})
export class OpenSourceSectionComponent implements OnInit {

  /**
   * A list of projects to display.
   */
  readonly projects: OpenSourceData[] = [
    <OpenSourceData>{
      name: 'TypedStore', logo: '/assets/app-icons/typed-store.png',
      intro: 'A declarative and type-safe client library for GCP Datastore.',
      link: 'https://github.com/SamChou19815/typed-store'
    },
    <OpenSourceData>{
      name: 'TypedStore', logo: '/assets/app-icons/kinetics.png',
      intro: 'An opinionated library to quickly bootstrap your backend on GCP.',
      link: 'https://github.com/SamChou19815/kinetics'
    },
    <OpenSourceData>{
      name: 'Badges for GCP Cloud Build', logo: '/assets/app-icons/badges-4-gcp-cloud-build.png',
      intro: 'Automatically generating badges for build status on GCP Cloud Build.',
      link: 'https://github.com/SamChou19815/badges-4-gcp-cloud-build'
    }
  ];

  constructor() {
  }

  ngOnInit() {
  }

}
