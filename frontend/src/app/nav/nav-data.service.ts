import { Injectable } from '@angular/core';
import { Icon } from '../shared/icon';
import { NavDataList, NavGroup, NavItem } from './nav-data';

@Injectable({
  providedIn: 'root'
})
export class NavDataService {

  /**
   * The group for scheduler.
   */
  private readonly appGroup: NavGroup = <NavGroup>{
    name: 'App', icon: Icon.ofMaterial('event_note'),
    children: [
      {
        name: 'Profile', icon: Icon.ofMaterial('profile'),
        link: '/scheduler/projects'
      },
      {
        name: 'Courses', icon: Icon.ofMaterial('school'), link: '/scheduler/events'
      },
      {
        name: 'Partners', icon: Icon.ofMaterial('group'), link: '/friends'
      },
      {
        name: 'Matching', icon: Icon.ofMaterial('dashboard'), link: '/scheduler/auto'
      }
    ]
  };
  /**
   * The nav data list for display.
   */
  readonly navDataList: NavDataList = new NavDataList(<NavGroup[]>[this.appGroup]);

  constructor() {
  }

}
