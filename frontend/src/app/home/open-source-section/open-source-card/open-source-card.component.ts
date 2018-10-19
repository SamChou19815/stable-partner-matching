import { Component, Input, OnInit } from '@angular/core';
import { OpenSourceData } from '../open-source-data';

@Component({
  selector: 'app-home-open-source-card',
  templateUrl: './open-source-card.component.html',
  styleUrls: ['./open-source-card.component.css']
})
export class OpenSourceCardComponent implements OnInit {

  /**
   * The data to display.
   */
  @Input() data: OpenSourceData = <OpenSourceData> { name: '', logo: '', intro: '', link: '' };

  constructor() {
  }

  ngOnInit() {
  }

}
