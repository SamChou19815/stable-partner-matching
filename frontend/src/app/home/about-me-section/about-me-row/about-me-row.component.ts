import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-home-about-me-row',
  template: '<ng-content></ng-content>',
  styleUrls: ['./about-me-row.component.css']
})
export class AboutMeRowComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
