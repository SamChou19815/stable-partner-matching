import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Icon } from '../../shared/icon';
import { NavGroup } from '../nav-data';

@Component({
  selector: 'app-nav-group',
  templateUrl: './nav-group.component.html',
  styleUrls: ['./nav-group.component.css']
})
export class NavGroupComponent implements OnInit {

  /**
   * Whether the group will display all children navigation items.
   * @type {boolean}
   */
  doesShowChild = true;
  /**
   * The navigation group to display.
   * @type {NavGroup}
   */
  @Input() group: NavGroup = { name: '', icon: Icon.ofDummy, children: [] };
  /**
   * Emit when a navigation item is clicked.
   * @type {EventEmitter<undefined>}
   */
  @Output() navClicked = new EventEmitter<undefined>();

  constructor() {
  }

  ngOnInit() {
  }

}
