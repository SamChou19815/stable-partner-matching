import { Component, Input, OnInit } from '@angular/core';
import { Icon } from '../../shared/icon';
import { NavItem } from '../nav-data';

@Component({
  selector: 'app-nav-item',
  templateUrl: './nav-item.component.html',
  styleUrls: ['./nav-item.component.css']
})
export class NavItemComponent implements OnInit {

  /**
   * A simple navigation item to display.
   * @type {NavItem}
   */
  @Input() item: NavItem = { name: '', icon: Icon.ofDummy, link: '' };
  /**
   * Whether the item is displayed as a child in a group.
   * @type {boolean}
   */
  @Input() isChild = false;

  constructor() {
  }

  ngOnInit() {
  }

}
