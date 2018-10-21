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
    name: 'BigRedMatch', icon: Icon.ofMaterial('android'),
    children: [
      { name: 'Profile', icon: Icon.ofMaterial('person'), link: '/profile' },
      { name: 'Courses', icon: Icon.ofMaterial('school'), link: '/courses' },
      { name: 'Partners', icon: Icon.ofMaterial('group'), link: '/partners' }
      // { name: 'Matching', icon: Icon.ofMaterial('person_add'), link: '/matching' }
    ]
  };
  /**
   * The nav data list for display.
   */
  readonly navDataList: NavDataList = new NavDataList(<NavGroup[]>[this.appGroup]);

  constructor() {
  }

}
