import { Injectable } from '@angular/core';
import { Icon } from '../shared/icon';
import { NavDataList, NavGroup, NavItem } from './nav-data';

@Injectable({
  providedIn: 'root'
})
export class NavDataService {

  /**
   * The group for scheduler.
   * @type {NavGroup}
   */
  private readonly schedulerNavGroup: NavGroup = <NavGroup>{
    name: 'Scheduler', icon: Icon.ofMaterial('event_note'),
    children: [
      {
        name: 'Projects', icon: Icon.ofMaterial('event_available'),
        link: '/scheduler/projects'
      },
      {
        name: 'Events', icon: Icon.ofMaterial('event'), link: '/scheduler/events'
      },
      {
        name: 'Friends', icon: Icon.ofMaterial('group'), link: '/friends'
      },
      {
        name: 'Auto Scheduling', icon: Icon.ofMaterial('dashboard'), link: '/scheduler/auto'
      }
    ]
  };
  /**
   * Group for RSS Reader.
   * @type {NavGroup}
   */
  private readonly rssReaderGroup: NavGroup = <NavGroup>{
    name: 'RSS Reader', icon: Icon.ofMaterial('chrome_reader_mode'),
    children: [
      {
        name: 'Articles', icon: Icon.ofMaterial('library_books'),
        link: '/rss-reader/articles'
      },
      {
        name: 'Subscriptions', icon: Icon.ofMaterial('rss_feed'),
        link: '/rss-reader/subscriptions'
      },
    ]
  };
  /**
   * The group for playground
   * @type {NavGroup}
   */
  private readonly playgroundGroup: NavGroup = <NavGroup>{
    name: 'Playground', icon: Icon.ofMaterial('apps'),
    children: [
      <NavItem>{
        name: 'SAMPL', icon: Icon.ofMaterial('memory'),
        link: '/playground/sampl'
      },
      <NavItem>{
        name: 'Chunk Reader', icon: Icon.ofMaterial('speaker_notes'),
        link: '/playground/chunk-reader'
      },
      <NavItem>{
        name: 'TEN', icon: Icon.ofMaterial('grid_on'),
        link: '/playground/ten'
      }
    ]
  };
  /**
   * The nav data list for display.
   * @type {NavDataList}
   */
  readonly navDataList: NavDataList = new NavDataList(<NavGroup[]>[
    this.schedulerNavGroup, this.rssReaderGroup, this.playgroundGroup
  ]);

  constructor() {
  }

}
