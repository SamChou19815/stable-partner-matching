import { Component, HostBinding, Input, OnInit } from '@angular/core';
import { Icon } from './icon';

/**
 * [IconComponent] is used to display an icon that can vary between different icon providers.
 */
@Component({
  selector: 'app-icon',
  template: `
    <mat-icon *ngIf="icon.type === 'material'">{{icon.name}}</mat-icon>
    <fa *ngIf="icon.type === 'font-awesome'" [name]="icon.name" size="2x"></fa>
  `,
  styleUrls: []
})
export class IconComponent implements OnInit {

  /**
   * The icon to be displayed.
   * @type {Icon}
   */
  @Input() icon: Icon = { name: 'home', type: 'material' };

  @HostBinding('style.display') display = 'inline-block';

  constructor() {
  }

  ngOnInit() {
  }

}
